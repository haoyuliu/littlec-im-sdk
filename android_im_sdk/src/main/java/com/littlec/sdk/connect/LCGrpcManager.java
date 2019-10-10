/* Project: android_im_sdk
 *
 * File Created at 2016/7/29
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect;

import com.fingo.littlec.connector.service.css.ConnectorServiceGrpc;
import com.fingo.littlec.connector.service.css.ConnectorServiceGrpc.ConnectorServiceBlockingStub;
import com.fingo.littlec.connector.service.css.ConnectorServiceGrpc.ConnectorServiceStub;
import com.fingo.littlec.proto.css.Connector;
import com.fingo.littlec.proto.css.Connector.UnaryResponse;
import com.fingo.littlec.proto.css.CssErrorCode;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.chat.listener.AynMsgResponseListener;
import com.littlec.sdk.biz.chat.listener.AynMsgResponseObserver;
import com.littlec.sdk.biz.chat.listener.PullRecMsgListener;
import com.littlec.sdk.biz.chat.listener.PullSendMsgListener;
import com.littlec.sdk.biz.history.impl.HmsBuilderImpl;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.core.ConnectBuilderImpl;
import com.littlec.sdk.connect.core.IConnectBuilder;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.connect.listener.PacketListener;
import com.littlec.sdk.connect.util.LCDirector;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCSingletonFactory;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

/**
 * @Type com.littlec.sdk.chat
 * @User user
 * @Desc Provide GRPC imanager
 * @Date 2016/7/29
 * @Version
 */
public class LCGrpcManager {
    private static final String TAG = "LCGrpcManager";
    private static final int TIMEOUT = 8;

    private ConnectorServiceBlockingStub blockingStub;
    private ConnectorServiceStub asyncStub;

    private StreamObserver<Connector.SessionRequest> requestObserver;
    private PacketListener<Connector.SessionNotify> responseObserver;

    private StreamObserver<Connector.UnaryResponse> pullRecObserver;
    private StreamObserver<Connector.UnaryResponse> pullSendObserver;


    public static LCGrpcManager getInstance() {
        return LCSingletonFactory.getInstance(LCGrpcManager.class);
    }

    private LCGrpcManager() {
        ManagedChannel channel = RpcChannelManager.getInstance().initChannel(LCChatConfig.ServerConfig.getConnectAddress(), true);
        blockingStub = ConnectorServiceGrpc.newBlockingStub(channel);
        asyncStub = ConnectorServiceGrpc.newStub(channel);
        pullRecObserver = new PullRecMsgListener();
        pullSendObserver = new PullSendMsgListener();
        LCLogger.getLogger(TAG).d("new LCGrpcManager,done");
    }


    /**
     * @Title: initConnection <br>
     * @Description: 初始化流 <br>
     * @throws: 2016/8/29 16:38
     */
    public synchronized void initConnection(LCCommonCallBack initCallBack) {//异步的方法，又被多处调用
        LCLogger.getLogger(TAG).e("initConnection");
        if (requestObserver != null) {
            LCLogger.getLogger(TAG).d("do requestObserver.onCompleted()");
            try {
                requestObserver.onCompleted();//正常关闭流//may be IllegalStateException:call already half-closed
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ManagedChannel channel = RpcChannelManager.getInstance().initChannel(LCChatConfig.ServerConfig.getConnectAddress(), true);
            LCLogger.getLogger(TAG).d("address:" + LCChatConfig.ServerConfig.getConnectAddress());
            LCLogger.getLogger(TAG).d("newBlockingStub");
            blockingStub = ConnectorServiceGrpc.newBlockingStub(channel);
            LCLogger.getLogger(TAG).d("newStub");
            asyncStub = ConnectorServiceGrpc.newStub(channel);
            if (responseObserver != null) {
                DispatchController.getInstance().unregister(DispatchController.CallBackType.PULLMessageCallBack, responseObserver);
                responseObserver.onDestroy();
                responseObserver = null;
            }
            responseObserver = new PacketListener();// 如果是ping三次失败，Chanel关闭新建了，那么这个observer也应该失效了，否则发送的时候Channel shutdownNow invoked
            responseObserver.setLoginCallback(initCallBack);
            LCLogger.getLogger(TAG).d("new PacketObserver," + responseObserver.toString());
            DispatchController.getInstance().register(DispatchController.CallBackType.PULLMessageCallBack, responseObserver);
            LCLogger.getLogger(TAG).d("sendSessionRequest");
            if (asyncStub == null) {
                LCLogger.getLogger(TAG).e("sendSessionRequest asyncStub is null!!!");
                if (initCallBack != null) {
                    initCallBack.onFailed(LCError.COMMON_INIT_FAIL.getValue(), LCError.COMMON_INIT_FAIL.getDesc());
                }
            } else {
                requestObserver = asyncStub.sendSessionRequest(responseObserver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (initCallBack != null) {
                initCallBack.onFailed(LCError.COMMON_INIT_FAIL.getValue(), LCError.COMMON_INIT_FAIL.getDesc());
            }
        }
    }

    private ManagedChannel getChannel() {
        return RpcChannelManager.getInstance().getChannel();
    }

    /**
     * @Title:doLogin doLogin <br>
     * @Description: send login request<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/26 10:32
     */
    public void doLogin(LCCommonCallBack loginCallback, Connector.SessionRequest.ESessionRequestType type) {
        if (responseObserver == null) {
            LCLogger.getLogger(TAG).e("packetListener is null,please check the reason");
            loginCallback.onFailed(LCError.COMMON_CONTENT_NULL.getValue(), LCError.COMMON_CONTENT_NULL.getDesc());
            return;
        }
        responseObserver.setLoginCallback(loginCallback);
        LCLogger.getLogger(TAG).d("do requestObserver.onNext login, type:" + type);
        Connector.SessionRequest sessionRequest;
        sessionRequest = LCDirector.constructLoginRequest(new ConnectBuilderImpl(), type);
        requestObserver.onNext(sessionRequest);
    }


    public void sendPing(String msgId) {
        LCLogger.getLogger(TAG).d("sendPing");
        if (requestObserver != null) {
            IConnectBuilder builder = new ConnectBuilderImpl();
            LCLogger.getLogger(TAG).d("do requestObserver.onNext ping");
            try {
                requestObserver.onNext(LCDirector.constructPingRequest(builder, msgId));//IllegalStateException:call was half-closed
            } catch (Exception e) {
                LCLogger.getLogger(TAG).d("do requestObserver.onNext error " + e);
            }
        }
    }

    /**
     * @Title: synMessage <br>
     * @Description: 同步历史消息 <br>
     * @param: recOrSend 为0 同步接收列表里面的消息 recOrSend为1 同步发送列表里面的消息 <br>
     * @param: guid <br>
     * @return: <br>
     * @throws: 2016/9/18 18:59
     */
    public void synMessage(long guid, boolean recOrSend) {
        ILCBuilder builder = new HmsBuilderImpl("messageSync", guid, !recOrSend);
        asyncUnaryRequest(LCDirector.constructUnaryRequest(builder),
                recOrSend ? pullRecObserver : pullSendObserver);

    }

    public UnaryResponse synGuid() {
        ILCBuilder builder = new HmsBuilderImpl("syncSendGUID", 0);
        return sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
    }

    /**
     * @Title: sendUnaryRequest <br>
     * @Description:发送Unary请求 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/31 14:08
     */
    public UnaryResponse sendUnaryRequest(Connector.UnaryRequest request) {
        return sendUnaryRequestWithDeadline(request, TIMEOUT);
    }

    private static Connector.UnaryResponse buildErrorResponse() {
        return Connector.UnaryResponse.newBuilder().setMethodName("Exception").setServiceName("Exception").setRet(CssErrorCode.ErrorCode.SESSION_STATUS_ERROR).build();
    }

    public Connector.UnaryResponse sendUnaryRequestWithDeadline(Connector.UnaryRequest request, int timeout) {
        if (getChannel() == null) {
            LCLogger.getLogger(TAG).e("channel is null");
            return buildErrorResponse();
        } else {
            if (getChannel().isShutdown()) {
                LCLogger.getLogger(TAG).e("channel not null，but the channel is shutdown");
                return buildErrorResponse();
            }
        }
        try {
            LCLogger.getLogger(TAG).d("blockingStub.withDeadlineAfter " + timeout + ",method:" + (request == null ? "null" : request.getMethodName()));
            return blockingStub.withDeadlineAfter(timeout, TimeUnit.SECONDS).sendUnaryRequest(request);
        } catch (StatusRuntimeException srException) {
            LCLogger.getLogger(TAG).e("StatusRuntimeException:" + srException);
            srException.printStackTrace();
            if (srException.getStatus().getCode() == Status.DEADLINE_EXCEEDED.getCode()) {
                //发送超时，进行重连todo

            } else if (srException.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                shutdownChannel();
            }
            return buildErrorResponse();
        } catch (Exception e) {
            LCLogger.getLogger(TAG).e(e.getMessage(), e);
            return buildErrorResponse();
        }
    }

    public void asyncUnaryRequest(Connector.UnaryRequest request, StreamObserver<Connector.UnaryResponse> observer) {
        if (getChannel() == null) {
            if (observer != null)
                observer.onError(new Throwable("channel is null"));
            return;
        } else {
            if (getChannel().isShutdown()) {
                if (observer != null)
                    observer.onError(new Throwable("channel is shutDown"));
                return;
            }
        }
        LCLogger.getLogger(TAG).d("asyncStub.withDeadlineAfter " + TIMEOUT + ",method:" + request.getMethodName());
        asyncStub.withDeadlineAfter(TIMEOUT, TimeUnit.SECONDS).sendUnaryRequest(request, observer);
    }

    /**
     * @Title: sendAynsRequest <br>
     * @Description: 发送异步请求 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/10/31 11:40
     */
    public void sendAynRequest(Connector.UnaryRequest request, AynMsgResponseListener listener) {
        AynMsgResponseObserver aynsListener = new AynMsgResponseObserver();
        aynsListener.setAynResponseListener(listener);
        asyncUnaryRequest(request, aynsListener);
    }

    /*
     *关闭channel
     */
    public synchronized void shutdownChannel() {
        if (requestObserver != null) {
            LCLogger.getLogger(TAG).d("do requestObserver.onCompleted()");
            try {
                requestObserver.onCompleted();//may be IllegalStateException:call already half-closed
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestObserver = null;
        }
        RpcChannelManager.getInstance().shutdownChannel();
    }

    /**
     * @Title: doLogout <br>
     * @Description: 登出接口 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/26 10:31
     */
    public void doLogout(LCCommonCallBack callback) {
        if (responseObserver == null) {
            if (callback != null) {
                callback.onFailed(LCError.COMMON_LOGOUT_NOT_LOGIN.getValue(), LCError.COMMON_LOGOUT_NOT_LOGIN.getDesc());
            }
            return;
        }
        responseObserver.setLoginCallback(null);
        responseObserver.setLogoutCallback(callback);
        LCLogger.getLogger(TAG).d("do requestObserver.onNext logout");
        requestObserver.onNext(LCDirector.constructLogoutRequest(new ConnectBuilderImpl()));
    }


    public void onDestroy() {
        shutdownChannel();
        blockingStub = null;
        asyncStub = null;
        if (responseObserver != null) {
            DispatchController.getInstance().unregister(DispatchController.CallBackType.PULLMessageCallBack, responseObserver);
            responseObserver.onDestroy();
            responseObserver = null;
        }
        pullRecObserver = null;
        pullSendObserver = null;
        DispatchController.getInstance().onDestroy();
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/29 user creat
 */
