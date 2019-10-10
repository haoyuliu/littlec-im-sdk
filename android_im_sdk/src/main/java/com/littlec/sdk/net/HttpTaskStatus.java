package com.littlec.sdk.net;

/**
 * Created by zhangguoqiong on 2016/7/26.
 */
class HttpTaskStatus {
    public static final int HTTP_STATUS_INIT = -1;
    public static final int HTTP_STATUS_PREPARE = 0;
    public static final int HTTP_STATUS_START = 1;
    public static final int HTTP_STATUS_DOWNLOADING = 2;
    public static final int HTTP_STATUS_CANCEL = 3;
    public static final int HTTP_STATUS_ERROR = 4;
    public static final int HTTP_STATUS_COMPLETED = 5;
    public static final int HTTP_STATUS_PAUSE = 6;
}
