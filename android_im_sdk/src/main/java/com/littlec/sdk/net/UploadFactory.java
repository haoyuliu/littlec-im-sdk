/* Project: android_im_sdk
 * 
 * File Created at 2016/8/15
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

import com.littlec.sdk.utils.LCSingletonFactory;

/**
 * @Type com.littlec.sdk.utils.http
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/15
 * @Version
 */

public class UploadFactory {
    public static UploadManager getUploadManager() {
        return LCSingletonFactory.getInstance(UploadManager.class);
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/15 zhangguoqiong creat
 */
