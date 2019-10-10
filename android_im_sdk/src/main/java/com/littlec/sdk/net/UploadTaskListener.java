/* Project: android_im_sdk
 * 
 * File Created at 2016/8/2
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.net;

import com.littlec.sdk.net.callback.ProgressListener;

/**
 * @Type com.littlec.sdk.utils.http
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/2
 * @Version
 */

 interface UploadTaskListener extends ProgressListener {
    void onPrepare(UploadTask uploadTask);

    void onStart(UploadTask uploadTask);

    void onDownloading(UploadTask uploadTask);

    void onPause(UploadTask uploadTask);

    void onCancel(UploadTask uploadTask);

    void onCompleted(UploadTask uploadTask);

    void onError(UploadTask uploadTask, int errorCode);

    int UPLOAD_ERROR_FILE_NOT_FOUND = -1;
    int UPLOAD_ERROR_IO_ERROR = -2;
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/2 zhangguoqiong creat
 */
