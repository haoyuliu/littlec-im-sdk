/* Project: android_im_sdk
 *
 * File Created at 2016/8/3
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

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCVideoMessageBody;
import com.littlec.sdk.config.LCChatConfig;
import com.fingo.littlec.proto.css.Enum;
import com.fingo.littlec.proto.css.Msg;
import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.sp.UserInfoSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Type com.littlec.sdk.chat.core.builder
 * @User user
 * @Desc 聊天服务构造器
 * @Date 2016/8/3
 * @Version
 */
public class MessageBuilderImpl implements ILCBuilder {
    private static final String TAG = "MessageBuilderImpl";
    private LCLogger Logger = LCLogger.getLogger(TAG);
    private String service_name = "littlec-chat";
    private LCMessage message;
    private String methodName;
    private String parameter1;
    private String parameter2;
    private String language;
    public MessageBuilderImpl(LCMessage message) {
        this.message = message;
        this.methodName = getMethodName(message.chatType().value());
    }

    public MessageBuilderImpl(String methodName, String  parameter1, String  parameter2) {
        this.methodName = methodName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }
    public MessageBuilderImpl(String methodName, String  parameter1, String  parameter2,String language) {
        this.methodName = methodName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.language = language;
    }
    private Chat.ChatMessageRequest buildChatMessage() {
        Chat.ChatMessageRequest.Builder chatMessageBuilder = Chat.ChatMessageRequest.newBuilder()
                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
                .setToUsername(message.LCMessageEntity().getTo().toLowerCase())
                .setFromNick(message.LCMessageEntity().getFromNick())
                .setFromUsername(message.LCMessageEntity().getFrom())
                .setMsgId(message.LCMessageEntity().getMsgId())
                .setBurnAfterRead(message.LCMessageEntity().getBurnAfterRead())
                .setFromClientType(Enum.EClientType.ANDROID)
                .setIsJimao(message.LCMessageEntity().getJiMaoFlag());

        switch (message.LCMessageEntity().getContentType()) {
            case Msg.EMsgContentType.TEXT_VALUE:
                chatMessageBuilder.setData(buildTextMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.TEXT);
                break;
            case Msg.EMsgContentType.IMAGE_VALUE:
                chatMessageBuilder.setData(buildImageMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.IMAGE);
                break;
            case Msg.EMsgContentType.VIDEO_VALUE:
                chatMessageBuilder.setData(buildVideoMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.VIDEO);
                break;
            case Msg.EMsgContentType.AUDIO_VALUE:
                chatMessageBuilder.setData(buildAudioMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.AUDIO);
                break;
            case Msg.EMsgContentType.LOCATION_VALUE:
                chatMessageBuilder.setData(buildLocationMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.LOCATION);
                break;
            case Msg.EMsgContentType.FILE_VALUE:
                chatMessageBuilder.setData(buildFileMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.FILE);
                break;
            case Msg.EMsgContentType.AT_VALUE:
                chatMessageBuilder.setData(buildAtMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.AT);
                break;
            case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                chatMessageBuilder.setData(buildReadReceiptMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.READ_RECEIPT);
                break;
            case Msg.EMsgContentType.RETRACT_VALUE:
                chatMessageBuilder.setData(buildRetractMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.RETRACT);
                break;
            case Msg.EMsgContentType.CUSTOM_VALUE:
                chatMessageBuilder.setData(buildCustomMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.CUSTOM);
                break;
            case Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE:
                chatMessageBuilder.setData(buildCustomMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.CUSTOM_NO_APNS);
                break;
            case Msg.EMsgContentType.CUSTOM_FAMILY_VALUE:
                chatMessageBuilder.setData(buildCustomMessage().toByteString());
                chatMessageBuilder.setMsgContentType(Msg.EMsgContentType.CUSTOM_FAMILY);
                break;
            default:
                break;

        }

        return chatMessageBuilder.build();
    }

    private Chat.PrivateMessageRequest buildPrivateMessage() {
        Chat.PrivateMessageRequest.Builder builder = Chat.PrivateMessageRequest.newBuilder()
                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
                .setToUsername(message.LCMessageEntity().getTo().toLowerCase())
                .setFromNick(message.LCMessageEntity().getFromNick())
                .setFromUsername(message.LCMessageEntity().getFrom())
                .setMsgId(message.LCMessageEntity().getMsgId())
                .setBurnAfterRead(message.LCMessageEntity().getBurnAfterRead())
                .setFromClientType(Enum.EClientType.ANDROID)
                .setIsJimao(message.LCMessageEntity().getJiMaoFlag());

        switch (message.LCMessageEntity().getContentType()) {
            case Msg.EMsgContentType.TEXT_VALUE:
                builder.setData(buildTextMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.TEXT);
                break;
            case Msg.EMsgContentType.IMAGE_VALUE:
                builder.setData(buildImageMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.IMAGE);
                break;
            case Msg.EMsgContentType.VIDEO_VALUE:
                builder.setData(buildVideoMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.VIDEO);
                break;
            case Msg.EMsgContentType.AUDIO_VALUE:
                builder.setData(buildAudioMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.AUDIO);
                break;
            case Msg.EMsgContentType.LOCATION_VALUE:
                builder.setData(buildLocationMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.LOCATION);
                break;
            case Msg.EMsgContentType.FILE_VALUE:
                builder.setData(buildFileMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.FILE);
                break;
            case Msg.EMsgContentType.AT_VALUE:
                builder.setData(buildAtMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.AT);
                break;
            case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                builder.setData(buildReadReceiptMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.READ_RECEIPT);
                break;
            case Msg.EMsgContentType.RETRACT_VALUE:
                builder.setData(buildRetractMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.RETRACT);
                break;
            case Msg.EMsgContentType.CUSTOM_VALUE:
                builder.setData(buildCustomMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.CUSTOM);
                break;
            case Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE:
                builder.setData(buildCustomMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.CUSTOM_NO_APNS);
                break;
            case Msg.EMsgContentType.CUSTOM_FAMILY_VALUE:
                builder.setData(buildCustomMessage().toByteString());
                builder.setMsgContentType(Msg.EMsgContentType.CUSTOM_FAMILY);
                break;
            default:
                break;

        }

        return builder.build();
    }


    @Override
    public Connector.UnaryRequest buildUnaryRequest() {
        Connector.UnaryRequest request = null;
        if (methodName.equals("sendChat")) {
            service_name = "littlec-chat";
            request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
                    .setRequestId(message.getMsgId()).setMethodName(methodName)
                    .setData(buildChatMessage().toByteString()).build();
        } else if (methodName.equals("sendPrivate")) {
            service_name = "littlec-chat";
            request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
                    .setRequestId(message.getMsgId()).setMethodName(methodName)
                    .setData(buildPrivateMessage().toByteString()).build();
        }else if (methodName.equals("fAQListRequest")){
            service_name = "littlec-chat";
            request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
                    .setMethodName(methodName)
                    .setData(buildFAQMessage().toByteString()).build();
        }else if (methodName.equals("customerServiceRequest")){
            service_name = "littlec-chat";
            request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
                    .setMethodName(methodName)
                    .setData(buildCSMessage().toByteString()).build();
        }else if (methodName.equals("checkBundleExists")){
            service_name = "littlec-chat";
            request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
                    .setMethodName(methodName)
                    .setData(buildCheckBundleExistsMessage().toByteString()).build();
        }
        return request;
    }

    /**
     * @Title: buildTextMessage <br>
     * @Description: 文本data生成<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/4 19:17
     */
    private Msg.TextMessage buildTextMessage() {
        LCTextMessageBody body = (LCTextMessageBody) message.LCMessageBody();
        return Msg.TextMessage.newBuilder().setText(body.getMessageContent())
                .setBurnAfterRead(false).build();
    }

    //设置是否发送原图

    /**
     * @Title: buildImageMessage <br>
     * @Description: 构建image message<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/5 11:49
     */
    private Msg.ImageMessage buildImageMessage() {
        LCImageMessageBody body = (LCImageMessageBody) message.LCMessageBody();
        return Msg.ImageMessage.newBuilder().setFileName(body.getFileName())
                .setOriginLink(body.getOriginalUri()).setBigLink(body.getLargeUri())
                .setMiddleLink(body.getMiddleUri()).setSmallLink(body.getSmallUri())
                .setFileLength(body.getFileLength()).setSmallWidth(body.getSmall_width())
                .setSmallHeight(body.getSmall_height())
                .setMiddleWidth(body.getMiddle_width()).setMiddleHeight(body.getMiddle_height())
                .setIsOrigin(body.getisOrigin()).build();
    }

    /**
     * @Title: buildVideoMessage <br>
     * @Description: 构建video message body  <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/5 11:48
     */
    private Msg.VideoMessage buildVideoMessage() {
        LCVideoMessageBody body = (LCVideoMessageBody) message.LCMessageBody();
        return Msg.VideoMessage.newBuilder().setFileName(body.getFileName())
                .setVideoLink(body.getOriginalUri()).setVideoScreenShortLink(body.getThumbnailUrl())
                .setFileLength(body.getFileLength()).setDuration(body.getDuration())
                .setWidth(body.getWidth()).setHeight(body.getHeight()).build();
    }

    /**
     * @Title: buildAudioMessage <br>
     * @Description: 构建audio message<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/5 11:48
     */
    private Msg.AudioMessage buildAudioMessage() {
        LCAudioMessageBody body = (LCAudioMessageBody) message.LCMessageBody();
        return Msg.AudioMessage.newBuilder().setFileName(body.getFileName())
                .setAudioLink(body.getOriginalUri()).setFileLength(body.getFileLength())
                .setDuration(body.getDuration()).build();
    }

    /**
     * @Title: buildLocationMessage <br>
     * @Description: 构建位置消息体 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/11 15:21
     */
    private Msg.LocationMessage buildLocationMessage() {
        LCLocationMessageBody body = (LCLocationMessageBody) message.LCMessageBody();
        return Msg.LocationMessage.newBuilder().setFileName(body.getFileName())
                .setOriginLink(body.getOriginalUri()).setFileLength(body.getFileLength())
                .setWidth(body.getWidth()).setHeight(body.getHeight())
                .setLongitude(body.getLongitude()).setLatitude(body.getLatitude())
                .setLocationDesc(body.getLocation_desc()).setLocationAddress(body.getAddress())
                .build();
    }

    /**
     * @Title: buildFileMessage <br>
     * @Description: 构建文件消息 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/23 10:49
     */
    private Msg.FileMessage buildFileMessage() {
        LCFileMessageBody body = (LCFileMessageBody) message.LCMessageBody();
        return Msg.FileMessage.newBuilder().setFileName(body.getFileName())
                .setFileLink(body.getOriginalUri()).setFileLength(body.getFileLength()).build();
    }

    /**
     * @Title: buildAtMessage <br>
     * @Description: 构建@消息体 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/17 12:02
     */
    private Msg.AtMessage buildAtMessage() {
        LCATMessageBody body = (LCATMessageBody) message.LCMessageBody();
        if (body.getAtAll()) {
            return Msg.AtMessage.newBuilder().setAtAll(true).setText(body.getText()).build();
        } else {
            return Msg.AtMessage.newBuilder().setText(body.getText())
                    .addAllAtMember(body.getAt_members()).build();
        }
    }

    /**
     * @Title: buildReceiptMessage <br>
     * @Description: 构建回执消息体 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/17 12:02
     */
    private Msg.ReadReceiptMessage buildReadReceiptMessage() {
        if (message != null) {
            return Msg.ReadReceiptMessage.newBuilder()
                    .setReceiptGuid(message.LCMessageEntity().getGuid()).build();
        }
        return null;
    }

    /**
     * @Title: buildRetractMessage<br>
     * @Description: 构建撤回消息体<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/26 12:42
     */
    private Msg.RetractMessage buildRetractMessage() {
        if (message != null) {
            return Msg.RetractMessage.newBuilder()
                    .setRetractGuid(message.LCMessageEntity().getGuid()).build();
        }
        return null;
    }

    /**
     * @Title: buildCustomMessage <br>
     * @Description: 构建自定义消息体 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/26 17:07
     */
    private Msg.CustomMessage buildCustomMessage() {
        LCCustomMessageBody customMessageBody = (LCCustomMessageBody) message.LCMessageBody();
        Msg.CustomMessage.Builder builder = Msg.CustomMessage.newBuilder();
        if (customMessageBody.getNotification() != null) {
            builder.setNotification(customMessageBody.getNotification());
        }
        Map<String, String> map = customMessageBody.getMap();
        List<Msg.CustomEntity> list = new ArrayList<>();
        String value;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Msg.CustomEntity.Builder customEntityBuilder = Msg.CustomEntity.newBuilder();
            customEntityBuilder.setKey(entry.getKey());
            value = entry.getValue();
            if (value == null) {
                customEntityBuilder.setValue("");
            } else {
                customEntityBuilder.setValue(entry.getValue());
            }
            Msg.CustomEntity customEntity = customEntityBuilder.build();
            list.add(customEntity);
        }

        builder.addAllCustomEntity(list);
        return builder.build();

    }

    /**
     * @Title: getMethodName <br>
     * @Description: 获取不同类型的方法名 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/25 9:59
     */
    private String getMethodName(int type) {
        String str = "methodName";
        switch (type) {
            case Msg.EMsgType.CHAT_MSG_VALUE:
                str = "sendChat";
                break;
            case Msg.EMsgType.PRIVATE_MSG_VALUE:
                str = "sendPrivate";
                break;
            case Msg.EMsgType.GROUP_MSG_VALUE:
                str = "sendGroupMessage";
                break;
            case Msg.EMsgType.MULTI_MSG_VALUE:
                str = "sendMulti";
                break;
            default:
                break;
        }
        return str;
    }

    /**
     * 创建请求FAQ接口数据
     * @return
     */
    private Chat.FAQListReq buildFAQMessage() {
        Chat.FAQListReq.Builder chatMessageBuilder = Chat.FAQListReq.newBuilder()
                .setUserId(UserInfoSP.getString(LCChatConfig.UserInfo.USERNAME, ""))
                .setFaqId(parameter1)
                .setInput(parameter2)
                .setLanguage(language);
        return chatMessageBuilder.build();
    }

    /**
     * 客服接口
     * @return
     */
    private Chat.CustomerServiceReq buildCSMessage() {
        Chat.CustomerServiceReq.Builder chatMessageBuilder = Chat.CustomerServiceReq.newBuilder()
                .setUserId(UserInfoSP.getString(LCChatConfig.UserInfo.USERNAME, ""))
                .setCountryCode(parameter1)
                .setLanguage(parameter2);
        return chatMessageBuilder.build();
    }

    /**
     * 检测是否在聊天中接口
     * @return
     */
    private Chat.CheckBundleExistsReq buildCheckBundleExistsMessage() {
        Chat.CheckBundleExistsReq.Builder chatMessageBuilder = Chat.CheckBundleExistsReq.newBuilder()
                .setUserId(UserInfoSP.getString(LCChatConfig.UserInfo.USERNAME, ""));
        return chatMessageBuilder.build();
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/3 user creat
 */
