package com.littlec.sdk.net;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;

/**
 * ClassName: HttpBaseTask
 * Description:
 * Creator: user
 * Date: 2016/7/17 22:14
 */
abstract class HttpBaseTask {
    protected static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private String url;
    private Callback callback;
    private File file;
    private RequestBody requestBody;
    protected OkHttpClient client = HttpClient.getInstance();

    protected abstract Request buildRequest();

    protected abstract Response doSynsExcute();

    protected abstract void doAynsExcute();

    protected HttpBaseTask setUrl(String url) {
        this.url = url;
        return this;
    }

    protected String getUrl() {
        return url;
    }

    public HttpBaseTask setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    protected Callback getCallBack() {
        return callback;
    }

    protected HttpBaseTask setFile(File file) {
        this.file = file;
        return this;
    }

    protected File getFile() {
        return file;
    }

    protected HttpBaseTask setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    protected RequestBody getRequestBody() {
        return requestBody;
    }

}
