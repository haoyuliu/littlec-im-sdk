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
package com.littlec.sdk.connect.util;

import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.CssErrorCode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.littlec.sdk.connect.LCConnectManager;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCReadReceiptMessageBody;
import com.littlec.sdk.connect.repeater.ExcTaskManager;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.littlec.sdk.utils.sp.SdkInfoSp;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.fingo.littlec.proto.css.Enum;
import com.fingo.littlec.proto.css.Ntf;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.NetworkMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Type com.littlec.sdk.chat.core.parser
 * @User user
 * @Desc
 * @Date 2016/8/29
 * @Version
 */
public class NotificationParser {
    private static final String TAG = "NotificationParser";
    private final static LCLogger Logger = LCLogger.getLogger(TAG);

    public static void loginNtfParser(com.google.protobuf.ByteString str,
                                      LCCommonCallBack loginCallback) {
        if (loginCallback == null) {
            Logger.e("loginCallback null");
            return;
        }
        if (str.isEmpty()) {
            loginCallback.onFailed(LCError.COMMON_LOGIN_RESPONSE_NULL.getValue(),
                    LCError.COMMON_LOGIN_RESPONSE_NULL.getDesc());
        }
        try {
            Connector.LoginResponse response = Connector.LoginResponse.parseFrom(str);
            if (response != null) {
                if (response.getRet() != CssErrorCode.ErrorCode.OK) {
                    loginCallback.onFailed(response.getRet().getNumber(),
                            response.getRet().toString());
                    UserInfoSP.removeString(LCChatConfig.UserInfo.PASSWORD);
                } else {
                    LCChatConfig.LCChatGlobalStorage.getInstance()
                            .setLoginUserName(response.getUserInfos().getUsername());
                    SdkInfoSp.modifiedFileName(response.getUserInfos().getPhone(),
                            LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE);
                    UserInfoSP.putString(LCChatConfig.UserInfo.USERNAME,
                            response.getUserInfos().getUsername());
                    UserInfoSP.putString(LCChatConfig.UserInfo.NICK,
                            response.getUserInfos().getNick());
                    UserInfoSP.putString(LCChatConfig.UserInfo.PHONE,
                            response.getUserInfos().getPhone());
                    UserInfoSP.setBoolean(LCChatConfig.UserInfo.LOGIN_FLAG, true);
                    UserInfoSP.putString(LCChatConfig.UserInfo.APPKEY,
                            LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey());
                    UserInfoSP.removeString(LCChatConfig.UserInfo.APPPASSWD);//为了清除旧数据，passwd之类的要么加密要么不保存
                    UserInfoSP.removeString(LCChatConfig.UserInfo.PASSWORD);//为了清除旧数据，passwd之类的要么加密要么不保存
                    SdkInfoSp.modifiedFileName(response.getUserInfos().getPhone(),
                            LCChatConfig.SdkInfo.PREFS_SDK_INFO_PROFILE);

                    DBFactory.getDBManager().initDataBase(LCChatConfig.LCChatGlobalStorage.getInstance().getContext());
                    /*********************登录成功后，处理异常消息,注意顺序一定不能颠倒，一定要先执行connect模块才能抛出成功回调***/
                    ExcTaskManager.getInstance().startExcModule();
                    LCSingletonFactory.getInstance(LCConnectManager.class).startPing();
                    Logger.d("loginCallback.onSuccess()");
                    loginCallback.onSuccess();
                    //开启网络监控模块
                    NetworkMonitor.registerReceiver(LCChatConfig.LCChatGlobalStorage.getInstance().getContext());
                    if (LCClient.getInstance().accountManager().getSyncMsgListener() != null) {
                        LCChatConfig.LCChatGlobalStorage.getInstance().setSyncMsgFlag(true);
                        LCClient.getInstance().accountManager().getSyncMsgListener().onSyncIng();
                    }
                    /*********************登录成功后,同步guid,guid同步成功再同步发送消息表和接收消息表**********************/
                    LCChatConfig.LCChatGlobalStorage.getInstance().setSynGuidFlag(true);
                    //登录成功后把接收guid置为0，只同步最新的离线消息
                    UserInfoSP.clearGuid(LCChatConfig.UserInfo.REV_GUID);
                    DispatchController.getInstance().sendPullMessage(DispatchController.PullMessageMethodType.pullAllMessage, false, true);
                     if(UserInfoSP.getLong(LCChatConfig.UserInfo.SEND_GUID)==0){
                    Connector.UnaryResponse synGuidResponse = LCGrpcManager.getInstance().synGuid();
                    if (parseSyncServerGUIDResponse(synGuidResponse)) {
                        DispatchController.getInstance().sendPullMessage(DispatchController.PullMessageMethodType.pullAllMessage, false, false);
                    }
                    }else {
                         DispatchController.getInstance().sendPullMessage(DispatchController.PullMessageMethodType.pullAllMessage, false, false);
                     }

                }
            }
            Logger.d("ret " + response.getRet().getNumber() + " user_infos "
                    + response.getUserInfos().getAppkey() + " nick "
                    + response.getUserInfos().getNick());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            loginCallback.onFailed(LCError.COMMON_LOGIN_PARSE_FAILED.getValue(),
                    LCError.COMMON_LOGIN_PARSE_FAILED.getDesc());
        }

    }

    public static void logoutNtfParser(com.google.protobuf.ByteString str,
                                       LCCommonCallBack logoutCallback) {
        if (logoutCallback == null) {
            Logger.e("logoutCallback null");
            return;
        }
        try {
            Connector.LogoutResponse response = Connector.LogoutResponse.parseFrom(str);
            if (response.getRet() != CssErrorCode.ErrorCode.OK) {
                logoutCallback.onFailed(response.getRet().getNumber(),
                        response.getRet().toString());
                return;
            } else {
                logoutCallback.onSuccess();
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            logoutCallback.onFailed(LCError.COMMON_LOGOUT_PARSE_FAILED.getValue(),
                    LCError.COMMON_LOGOUT_PARSE_FAILED.getDesc());
        }

    }

    /**
     * @Title: pingNtfParser<br>
     * @Description: 解析ping包 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/1 18:55
     */
    public static void pingNtfParser(com.google.protobuf.ByteString str) {
        Connector.PingResponse response = null;
        try {
            response = Connector.PingResponse.parseFrom(str);
            String msgId = response.getMsgId();
            LCSingletonFactory.getInstance(LCConnectManager.class).removeMsgId(msgId);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public static void retractNotificationParser(com.google.protobuf.ByteString str) {
        try {
            Ntf.RetractNotification retractNotification = Ntf.RetractNotification.parseFrom(str);
            if (!retractNotification.getFromUsername()
                    .equals(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())) {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setFrom(retractNotification.getFromUsername());
                messageEntity.setGuid(retractNotification.getRetractGuid());
                messageEntity.setTo(retractNotification.getUri());
                messageEntity.setChatType(retractNotification.getMsgTypeValue());
                messageEntity.setSendOrRecv(LCMessage.Direct.RECEIVE.value());
                messageEntity.setContentType(LCMessage.ContentType.RETRACT.value());
                //            messageEntity.setChatType(LCMessage.ChatType.GroupChat.value());
                messageEntity.setMsgId(CommonUtils.getUUID());
                messageEntity.setCreateTime(CommonUtils.getCurrentTime());
                String retract_Msg_Id = GetDataFromDB
                        .queryMsgIdByGuid(retractNotification.getRetractGuid());
                MessageEntity messageEntityOld = DBFactory.getDBManager().getDBMessageService()
                        .queryBuilder().where(MessageEntityDao.Properties.MsgId.eq(retract_Msg_Id))
                        .unique();
                if (messageEntityOld != null) {
                    if (messageEntityOld.getMediaEntity() != null)
                        DBFactory.getDBManager().getDBMediaService()
                                .delete(messageEntityOld.getMediaEntity());
                    DBFactory.getDBManager().getDBMessageService().queryBuilder()
                            .where(MessageEntityDao.Properties.MsgId.eq(retract_Msg_Id)).buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                LCReadReceiptMessageBody retractBody = new LCReadReceiptMessageBody(retract_Msg_Id);
                LCMessage message = new LCMessage(messageEntity, retractBody);
                MediaEntity entity = new MediaEntity();
                entity.setContent(retractNotification.getFromUsername() + "撤回了一条消息");
                //            ConversationEntity conversationEntity = GetDataFromDB
                //                    .insertOrUpdateConversationEntity(message);
                DBFactory.getDBManager().getDBMediaService().insert(entity);
                message.LCMessageEntity().setMediaEntity(entity);
                if (message.chatType() == LCMessage.ChatType.Chat)
                    message.LCMessageEntity()
                            .setConversationId(retractNotification.getFromUsername());
                else if (message.chatType() == LCMessage.ChatType.GroupChat)
                    message.LCMessageEntity().setConversationId(retractNotification.getUri());
                //            message.LCMessageEntity().setConversationId(conversationEntity.getRecipientAddress());
                DBFactory.getDBManager().getDBMessageService()
                        .insertOrReplace(message.LCMessageEntity());
                List<LCMessage> messagesList = new ArrayList<>();
                messagesList.add(message);
                //            String guid_name = LCChatConfig.UserInfo.REV_GUID;
                //            UserInfoSP.putLong(guid_name, retractNotification.getRetractGuid());
                ConversationEntity conversationEntity = DBFactory.getDBManager()
                        .getDBConversationService()
                        .load(message.LCMessageEntity().getConversationId());
                if (conversationEntity != null) {
                    conversationEntity
                            .setMsgContent(message.LCMessageEntity().getMediaEntity().getContent());
                    conversationEntity.setMsgContentType(LCMessage.ContentType.RETRACT.value());
                }

                DBFactory.getDBManager().getDBConversationService().update(conversationEntity);
                DispatchController.getInstance().onReceivedChatMessage(messagesList);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            Logger.e(e);
        }

    }

    public static void kickNotificationParser(com.google.protobuf.ByteString str) {
        try {
            Ntf.KickNotification kickNotification = Ntf.KickNotification.parseFrom(str);
            Enum.EClientType clientType = kickNotification.getLoginClientType();
            DispatchController.getInstance().onAccountConflict(LCClient.ClientType.forNumber(clientType.getNumber()));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    public static boolean parseSyncServerGUIDResponse(Connector.UnaryResponse unaryResponse) {
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            return false;
        } else {
            try {
                Chat.SyncSendGUIDResponse syncServerGUIDResponse = Chat.SyncSendGUIDResponse
                        .parseFrom(unaryResponse.getData());
                if (syncServerGUIDResponse.getRet() != CssErrorCode.ErrorCode.OK) {
                    return false;
                } else {
                    long sendGuid = syncServerGUIDResponse.getSendGuid();
                    UserInfoSP.putGuid(LCChatConfig.UserInfo.SEND_GUID, sendGuid);
                    return true;
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/29 user creat
 */
