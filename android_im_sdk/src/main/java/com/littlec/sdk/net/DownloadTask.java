package com.littlec.sdk.net;

import android.text.TextUtils;
import android.util.Log;

import com.littlec.sdk.database.dao.DownloadDBEntityDao;
import com.littlec.sdk.database.entity.DownloadDBEntity;

import com.littlec.sdk.utils.LCLogger;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangguoqiong on 2016/7/26.
 */
 class DownloadTask implements Runnable {
    LCLogger logger = LCLogger.getLogger("httpDownload");
    private DownloadDBEntity dbEntity;
    private DownloadDBEntityDao downloadDao;
    private DownloadManager downloadManager;
    private OkHttpClient client;
    private String id;
    private long totalSize;
    private long completedSize;
    //private float percent;
    private String url;
    private String saveDirPath;
    private RandomAccessFile file;
    private int UPDATE_SIZE = 50 * 1024; // 每50k更新一次
    private int downloadStatus = HttpTaskStatus.HTTP_STATUS_INIT;
    private String fileName; // 保存的文件名字
    private List<DownloadTaskListener> listeners = new ArrayList<>();

    public DownloadTask() {

    }

    @Override
    public void run() {
        downloadStatus = HttpTaskStatus.HTTP_STATUS_PREPARE;
        onPrepare();
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            dbEntity = downloadDao.load(id);
            file = new RandomAccessFile(saveDirPath + fileName, "rwd");
            if (dbEntity != null) {
                completedSize = dbEntity.getCompletedSize();
                totalSize = dbEntity.getTotalSize();
            }
            if (file.length() < completedSize) {
                completedSize = file.length();
                logger.d(completedSize);
            }
            long fileLength = file.length();
            if (fileLength != 0 && totalSize <= fileLength) {
                downloadStatus = HttpTaskStatus.HTTP_STATUS_COMPLETED;
                totalSize = completedSize = fileLength;
                dbEntity = new DownloadDBEntity(id, totalSize, completedSize, url, saveDirPath,
                        fileName, downloadStatus);
                downloadDao.insertOrReplace(dbEntity);
                onCompleted();
                return;
            }
            downloadStatus = HttpTaskStatus.HTTP_STATUS_START;
            onStart();
            Request request = new Request.Builder().url(url)
                    .header("RANGE", "bytes=" + completedSize + "-") //  断点续传添加请求头
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    downloadStatus = HttpTaskStatus.HTTP_STATUS_DOWNLOADING;
                    if (totalSize <= 0) {
                        totalSize = responseBody.contentLength();
                        dbEntity.setTotalSize(totalSize);
                        downloadDao.update(dbEntity);
                        logger.d(totalSize);
                    }
                    if (TextUtils.isEmpty(response.header("Content-Range"))) {
                        //返回的没有Content-Range 不支持断点下载 需要重新下载
                        File alreadyDownloadedFile = new File(saveDirPath + fileName);
                        if (alreadyDownloadedFile.exists()) {
                            alreadyDownloadedFile.delete();
                        }
                        file = new RandomAccessFile(saveDirPath + fileName, "rwd");
                        completedSize = 0;
                    }
                    file.seek(completedSize);
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[2 * 1024];
                    int length = 0;
                    int buffOffset = 0;
                    if (dbEntity == null) {
                        dbEntity = new DownloadDBEntity(id, totalSize, 0L, url, saveDirPath,
                                fileName, downloadStatus);
                        downloadDao.insertOrReplace(dbEntity);
                    }
                    while ((length = bis.read(buffer)) > 0
                            && downloadStatus != HttpTaskStatus.HTTP_STATUS_CANCEL
                            && downloadStatus != HttpTaskStatus.HTTP_STATUS_PAUSE) {
                        file.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;
                        if (buffOffset >= UPDATE_SIZE) {
                            buffOffset = 0;
                            dbEntity.setCompletedSize(completedSize);
                            downloadDao.update(dbEntity);
                            Log.d("onDownloading1", dbEntity.toString());
                            onDownloading();
                        }
                    }
                    dbEntity.setCompletedSize(completedSize);
                    downloadDao.update(dbEntity);
                    Log.d("onDownloading2", dbEntity.toString());
                    onDownloading();
                }
            } else {
                downloadStatus = HttpTaskStatus.HTTP_STATUS_ERROR;
                onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            }

        } catch (FileNotFoundException e) {
            downloadStatus = HttpTaskStatus.HTTP_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND);
            return;
            //            e.printStackTrace();
        } catch (IOException e) {
            downloadStatus = HttpTaskStatus.HTTP_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            return;
        } finally {
            dbEntity.setCompletedSize(completedSize);
            downloadDao.update(dbEntity);
            Log.d("onDownloadComplete", dbEntity.toString());
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (totalSize == completedSize)
            downloadStatus = HttpTaskStatus.HTTP_STATUS_COMPLETED;
        dbEntity.setDownloadStatus(downloadStatus);
        downloadDao.update(dbEntity);
        Log.d("onDownloadComplete2", dbEntity.toString());

        switch (downloadStatus) {
            case HttpTaskStatus.HTTP_STATUS_COMPLETED:
                onCompleted();
                break;
            case HttpTaskStatus.HTTP_STATUS_PAUSE:
                onPause();
                break;
            case HttpTaskStatus.HTTP_STATUS_CANCEL:
                downloadDao.delete(dbEntity);
                File temp = new File(saveDirPath + fileName);
                if (temp.exists())
                    temp.delete();
                onCancel();
                break;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPercent() {
        return totalSize == 0 ? 0 : completedSize * 100 / totalSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long toolSize) {
        this.totalSize = toolSize;
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void setDownloadDao(DownloadDBEntityDao downloadDao) {
        this.downloadDao = downloadDao;
    }

    public void setDbEntity(DownloadDBEntity dbEntity) {
        this.dbEntity = dbEntity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttpClient(OkHttpClient client) {
        this.client = client;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void cancel() {
        setDownloadStatus(HttpTaskStatus.HTTP_STATUS_CANCEL);
        File temp = new File(saveDirPath + fileName);
        if (temp.exists())
            temp.delete();
    }

    public void pause() {
        setDownloadStatus(HttpTaskStatus.HTTP_STATUS_PAUSE);
    }

    private void onPrepare() {
        for (DownloadTaskListener listener : listeners) {
            listener.onPrepare(this);
        }
    }

    private void onStart() {
        for (DownloadTaskListener listener : listeners) {
            listener.onStart(this);
        }
    }

    private void onDownloading() {
        Log.d("onDownloading", id + "listener size:" + listeners.size());
        for (DownloadTaskListener listener : listeners) {
            listener.onDownloading(this);
        }
    }

    private void onCompleted() {
        for (DownloadTaskListener listener : listeners) {
            listener.onCompleted(this);
        }
    }

    private void onPause() {
        for (DownloadTaskListener listener : listeners) {
            listener.onPause(this);
        }
    }

    private void onCancel() {
        for (DownloadTaskListener listener : listeners) {
            listener.onCancel(this);
        }
    }

    private void onError(int errorCode) {
        for (DownloadTaskListener listener : listeners) {
            listener.onError(this, errorCode);
        }
    }

    public void addDownloadListener(DownloadTaskListener listener) {
        listeners.add(listener);
    }

    public void removeDownloadListener(DownloadTaskListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public void removeAllDownloadListener() {
        this.listeners.clear();
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DownloadTask that = (DownloadTask) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static DownloadTask parse(DownloadDBEntity entity) {
        DownloadTask task = new DownloadTask();
        task.setDownloadStatus(entity.getDownloadStatus());
        task.setId(entity.getDownloadId());
        task.setUrl(entity.getUrl());
        task.setFileName(entity.getFileName());
        task.setSaveDirPath(entity.getSaveDirPath());
        task.setCompletedSize(entity.getCompletedSize());
        task.setDbEntity(entity);
        task.setTotalSize(entity.getTotalSize());
        return task;
    }
}
