/* Project: android_im_sdk
 * 
 * File Created at 2016/9/21
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.listener;

import com.littlec.sdk.biz.chat.entity.LCMessage;

/**
 * @Type com.littlec.sdk
 * @User user
 * @Desc 消息发送回调
 * @Date 2016/9/21
 * @Version
 */
public interface LCMessageSendCallBack {
    void onSuccess(LCMessage message);

    void onError(LCMessage message, int code, String errorString);

    void onProgress(LCMessage message, int progress);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/21 user creat
 */