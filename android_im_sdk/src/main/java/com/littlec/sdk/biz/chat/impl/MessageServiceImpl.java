/* Project: android_im_sdk
 *
 * File Created at 2016/8/29
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.impl;

import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.fingo.littlec.proto.css.CssErrorCode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.chat.IMessageCmdService;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.listener.AynMsgResponseListener;
import com.littlec.sdk.biz.chat.utils.MsgSendResultParser;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.connect.repeater.ExcTaskManager;
import com.littlec.sdk.connect.util.LCDirector;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.littlec.sdk.utils.sp.UserInfoSP;

import java.util.ArrayList;
import java.util.List;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc 消息模块
 * @Date 2016/8/29
 * @Version
 */
public class MessageServiceImpl implements IMessageCmdService {
    private static final String TAG = "MessageServiceImpl";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);

    public MessageServiceImpl() {
        super();
    }

    public synchronized void sendPacket(final LCMessage message) {
        Logger.d("sendPacket");//首次发送
        /****无网络的情况下直接加入异常队列******/
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            //存入重发表
            if (message.contentType() != LCMessage.ContentType.RETRACT) {
                ExcTaskManager.getInstance().putExceptionPacket(message);
            } else {
                DispatchController.getInstance().onError(message,
                        LCError.COMMON_SERVER_INNER_ERROR.getValue(),
                        LCError.COMMON_SERVER_INNER_ERROR.getDesc());
            }
            return;
        }
        if (message.LCMessageEntity().getContentType() != LCMessage.ContentType.READ_RECEIPT_NUM
                && message.LCMessageEntity().getContentType() != LCMessage.ContentType.RETRACT_NUM) {
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }
        ILCBuilder builder = new MessageBuilderImpl(message);
        Connector.UnaryRequest request = LCDirector.constructUnaryRequest(builder);
        LCGrpcManager.getInstance().sendAynRequest(request, new AynMsgResponseListener() {
            @Override
            public void onNext(Chat.ChatMessageResponse chatResponse) {
                if (chatResponse.getRet() == CssErrorCode.ErrorCode.OK) {
                    if (chatResponse.getGuid() == 0) {
                        Logger.e("sendAynRequest ,chatResponse msg guid==0" + chatResponse.toString());
                    }
                    MsgSendResultParser.handleOnNextSucess(chatResponse, message);
                    UserInfoSP.putGuid(LCChatConfig.UserInfo.SEND_GUID, chatResponse.getGuid());
                    DispatchController.getInstance().onSuccess(message);
                } else if (chatResponse.getRet() == CssErrorCode.ErrorCode.CHAT_RECEIVER_NOT_EXIST
                       || chatResponse.getRet() == CssErrorCode.ErrorCode.CSS_BUNDLE_NOT_EXISTS
                        /*
                        || chatResponse.getRet() == CssErrorCode.ErrorCode.GROUP_NOT_EXIST
                        || chatResponse.getRet() == CssErrorCode.ErrorCode.GROUP_REQUESTER_NOT_IN_GROUP*/) {
                    Logger.e("sendAynRequest onNext chatResponse ,code name:" + chatResponse.getRet().toString());
                    ExcTaskManager.handleSendError(message.getMsgId(), message, chatResponse.getRet());
                } else {
                    ExcTaskManager.getInstance().putExceptionPacket(message);
                }
            }

            @Override
            public void onError(Throwable t) {
                Logger.e("send message onError");
                if (t instanceof InvalidProtocolBufferException) {
                    //从缓存中删除
                    ExcTaskManager.getInstance().removeExceptionTask(message.getMsgId());
                    //从数据库中删除
                    ExcTaskManager.getInstance().removeDBExceptionTask(message.getMsgId());
                    message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
                    if (message.LCMessageEntity().getContentType() != LCMessage.ContentType.READ_RECEIPT_NUM) {
                        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
                        GetDataFromDB.insertOrUpdateConversationEntity(message, true);
                    }
                    DispatchController.getInstance().onError(message, LCError.MESSAGE_PARSE_ERROR.getValue(), LCError.MESSAGE_PARSE_ERROR.getDesc());
                } else {
                    ExcTaskManager.getInstance().putExceptionPacket(message);
                }
            }

            @Override
            public void onCompleted() {

            }
        });

    }

    /**
     * 请求FAQ数据
     * @param faq_id
     * @param input
     * @return
     * @throws LCException
     */
    @Override
    public Chat.FAQListResp getFAQUnit(String faq_id, String input,String language)
            throws LCException {
        List<Chat.FAQListItem> faqListItems = new ArrayList<>();
        Chat.FAQListResp msgGetResponse = null;
        ILCBuilder builder=null;
        builder = new MessageBuilderImpl("fAQListRequest",faq_id, input,language);
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance()
                .sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                msgGetResponse = Chat.FAQListResp.parseFrom(unaryResponse.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return msgGetResponse;
    }

    /**
     * 获取客服ID
     * @param country_code
     * @param language
     * @return
     * @throws LCException
     */
    @Override
    public Chat.CustomerServiceResp getCustomerService(String country_code, String language)
            throws LCException {
        Chat.CustomerServiceResp msgGetResponse = null;
        ILCBuilder builder=null;
        builder = new MessageBuilderImpl("customerServiceRequest",country_code, language);
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance()
                .sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                msgGetResponse = Chat.CustomerServiceResp.parseFrom(unaryResponse.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return msgGetResponse;
    }

    /**
     * 检测是否在聊天中
     * @return
     * @throws LCException
     */
    @Override
    public Chat.CheckBundleExistsResp checkBundleExists()
            throws LCException {
        Chat.CheckBundleExistsResp msgGetResponse = null;
        ILCBuilder builder=null;
        builder = new MessageBuilderImpl("checkBundleExists","", "");
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance()
                .sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                msgGetResponse = Chat.CheckBundleExistsResp.parseFrom(unaryResponse.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return msgGetResponse;
    }
    //    public void setToken(String regId, String pushAppSecret, String packName) throws LCException {
//        ILCBuilder builder = new PushBuilderImpl("setToken", regId, pushAppSecret, packName);
//        Connector.UnaryResponse response = LCGrpcManager.getInstance().sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != CssErrorCode.ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        Push.CommonResponse commonResponse = null;
//        try {
//            commonResponse = Push.CommonResponse.parseFrom(response.getData());
//            if (commonResponse != null) {
//                int resultCode = commonResponse.getRet().getNumber();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, commonResponse.getRet().toString());
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//
//    }
//
//    public void clearToken() throws LCException {
//        ILCBuilder builder = new PushBuilderImpl("clearToken");
//        Connector.UnaryResponse response = null;
//        try {
//            response = LCGrpcManager.getInstance()
//                    .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        } catch (Exception e) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        if (response.getRet() != CssErrorCode.ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        Push.CommonResponse commonResponse = null;
//        try {
//            commonResponse = Push.CommonResponse.parseFrom(response.getData());
//            if (commonResponse != null) {
//                int resultCode = commonResponse.getRet().getNumber();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, commonResponse.getRet().toString());
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//    }
//

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/29 user creat
 */
