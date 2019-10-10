package com.littlec.sdk.net;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: HttpClient
 * Description:  httpclient配置初始化类
 * Creator: user
 * Date: 2016/7/18 10:12
 */
class HttpClient {

    private static class OkHttpClientHolder {
        private static final OkHttpClient INSTANCE = new OkHttpClient();
    }

    private HttpClient() {
    }

    private static final int TIMEOUT = 30 * 1000;

    public static OkHttpClient getInstance() {
        OkHttpClient client = OkHttpClientHolder.INSTANCE;
        client.setConnectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        return client;
    }

    /**
     * MethodName: 初始化连接参数 <br>
     * Description:  <br>
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/15 10:35
     */
    private void init(long connectTimeout, int readTimeout, int writeTimeout) {

    }

}
