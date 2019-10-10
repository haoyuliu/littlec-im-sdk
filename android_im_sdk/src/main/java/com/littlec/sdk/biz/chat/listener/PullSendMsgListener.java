/* Project: android_im_sdk
 * 
 * File Created at 2016/11/10
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

import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.chat.utils.MsgReceiveParser;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;

import io.grpc.stub.StreamObserver;

/**
 * @Type com.littlec.sdk.chat.core.launcher.impl
 * @User user
 * @Desc 拉取发送消息接听
 * @Date 2016/11/10
 * @Version
 */
 public class PullSendMsgListener implements StreamObserver<Connector.UnaryResponse>{
    private LCLogger Logger=LCLogger.getLogger("PullSendMsgListener");

    @Override
    public void onNext(Connector.UnaryResponse unaryResponse) {
        Logger.d("send onNext");
        MsgReceiveParser.parseUnaryResponse(unaryResponse,false);
    }

    @Override
    public void onError(Throwable t) {
        Logger.e(t);
    }

    @Override
    public void onCompleted() {
        Logger.e("send onCompleted");
        DispatchController.getInstance().sendPullMessage(DispatchController.PullMessageMethodType.pullCompleted,true,false);
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/11/10 user creat
 */