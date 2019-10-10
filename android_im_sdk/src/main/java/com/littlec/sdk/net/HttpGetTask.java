package com.littlec.sdk.net;

import android.content.Context;

import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.net.callback.ProgressListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ClassName: HttpGetTask
 * Description:  实现get请求
 * Creator: user
 * Date: 2016/7/17 22:27
 */
public class HttpGetTask extends HttpBaseTask {

    public HttpGetTask(String url) {
        setUrl(url);
    }

    public HttpGetTask() {
    }

    public static HttpGetTask newBuilder() {
        return LCSingletonFactory.getInstance(HttpGetTask.class);
    }

    @Override
    protected Request buildRequest() {
        Request request = new Request.Builder().url(getUrl()).build();
        return request;
    }

    @Override
    public Response doSynsExcute() {
        Response response = null;
        try {
            response = client.newCall(buildRequest()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void doAynsExcute() {
        client.newCall(buildRequest()).enqueue(getCallBack());
    }

    public void doGetFileAynsExcute(final ProgressListener progressListener,
                                    Callback callback) {

        client.networkInterceptors().add(new Interceptor() {
            //下载进度拦截器
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        setCallback(callback);
        doAynsExcute();
    }

    public void downloadFile(String url, String savePath, final ProgressListener progressListener) {
        final File file = new File(savePath);
        setUrl(url);
        doGetFileAynsExcute(progressListener, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (progressListener != null)
                    progressListener.onError(e.toString());
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response != null) {
                    //下载完成，保存数据到文件
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int hasRead = 0;
                    while ((hasRead = is.read(buf)) > 0) {
                        fos.write(buf, 0, hasRead);
                    }
                    fos.close();
                    is.close();
                    System.out.println("下载成功");
                    if (progressListener != null)
                        progressListener.onSuccess();
                    client.networkInterceptors().clear();
                }
            }
        });
    }

    public void downloadFile(Context context, String saveDirPath, String id,
                             final DownloadTaskListener downloadTaskListener) {
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), downloadTaskListener))
                        .build();
            }
        });
        DownloadManager downloadManager = DownloadManager.getInstance(client, context);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setId(id);
        downloadTask.setUrl(getUrl());
        downloadTask.setFileName(id + ".jpg");
        downloadTask.setSaveDirPath(saveDirPath + "/");
        downloadManager.addDownloadTask(downloadTask, downloadTaskListener);

    }

    public void pauseTask(Context context, String id) {
        DownloadManager downloadManager = DownloadManager.getInstance(client, context);
        downloadManager.pause(id);
    }

    public void resumeTask(Context context, String id) {
        DownloadManager downloadManager = DownloadManager.getInstance(client, context);
        downloadManager.resume(id);
    }

}
