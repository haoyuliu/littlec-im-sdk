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

import com.google.protobuf.InvalidProtocolBufferException;
import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;

import io.grpc.stub.StreamObserver;

/**
 * @Type com.littlec.sdk.chat.core.launcher.impl
 * @User user
 * @Desc 消息异步发送回调，目前正常流程的消息用同步，异常模块的消息用异步
 * @Date 2016/10/31
 * @Version
 */
public class AynMsgResponseObserver implements StreamObserver<Connector.UnaryResponse> {
    private static final String TAG = "AynMsgResponseObserver";
    private static final LCLogger logger = LCLogger.getLogger(TAG);
    private AynMsgResponseListener listener;

    public void setAynResponseListener(AynMsgResponseListener listener) {
        this.listener = listener;
    }

    @Override
    public void onNext(Connector.UnaryResponse response) {
        try {
            Chat.ChatMessageResponse chatResponse = Chat.ChatMessageResponse
                    .parseFrom(response.getData());
//            String msgId = chatResponse.getMsgId();
            if (listener != null) {
                listener.onNext(chatResponse);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        if (listener != null) {
            listener.onError(t);
        }
    }

    @Override
    public void onCompleted() {
        logger.d("onCompleted");
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/31 user creat
 */