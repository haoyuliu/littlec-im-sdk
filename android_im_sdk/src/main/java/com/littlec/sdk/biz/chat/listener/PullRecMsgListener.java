/* Project: android_im_sdk
 *
 * File Created at 2016/8/18
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

import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.chat.utils.MsgReceiveParser;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;

import io.grpc.stub.StreamObserver;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc 拉接收消息监听
 * @Date 2016/8/18
 * @Version
 */
public class PullRecMsgListener implements StreamObserver<Connector.UnaryResponse> {
    private LCLogger Logger = LCLogger.getLogger("PullRecMsgListener");

    @Override
    public void onNext(Connector.UnaryResponse unaryResponse) {
        Logger.d("recv onNext");
        MsgReceiveParser.parseUnaryResponse(unaryResponse, true);
    }

    @Override
    public void onError(Throwable t) {
        Logger.e(t);
//        t.printStackTrace();
        System.out.println(CommonUtils.getExcPrintStackTrace(t));
    }

    @Override
    public void onCompleted() {
        Logger.e("recv onCompleted");
        DispatchController.getInstance().sendPullMessage(DispatchController.PullMessageMethodType.pullCompleted, true, true);
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/18 user creat
 */