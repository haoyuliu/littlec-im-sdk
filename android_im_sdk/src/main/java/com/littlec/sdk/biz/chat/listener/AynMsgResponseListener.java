/* Project: android_im_sdk
 * 
 * File Created at 2016/10/31
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

import com.fingo.littlec.proto.css.Chat;

/**
 * @Type com.littlec.sdk.chat.core.repeater
 * @User user
 * @Desc 异步消息回调
 * @Date 2016/10/31
 * @Version
 */
public interface AynMsgResponseListener {
    void onNext(Chat.ChatMessageResponse response);
    void onError(Throwable t);
    void onCompleted();
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/31 user creat
 */