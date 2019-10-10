package com.littlec.sdk.net;

import com.littlec.sdk.net.callback.ProgressListener;

/**
 * Created by zhangguoqiong on 2016/7/26.
 */
 public interface DownloadTaskListener extends ProgressListener {
    void onPrepare(DownloadTask downloadTask);

    void onStart(DownloadTask downloadTask);

    void onDownloading(DownloadTask downloadTask);

    void onPause(DownloadTask downloadTask);

    void onCancel(DownloadTask downloadTask);

    void onCompleted(DownloadTask downloadTask);

    void onError(DownloadTask downloadTask, int errorCode);

    int DOWNLOAD_ERROR_FILE_NOT_FOUND = -1;
    int DOWNLOAD_ERROR_IO_ERROR = -2;
}
