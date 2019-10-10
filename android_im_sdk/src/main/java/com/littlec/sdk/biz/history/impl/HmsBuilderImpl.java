/* Project: android_im_sdk
 *
 * File Created at 2016/8/5
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.history.impl;

import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.utils.LCLogger;

import java.util.List;

/**
 * @Type com.littlec.sdk.chat.core.builder
 * @User user
 * @Desc hms模块构造器
 * @Date 2016/8/5
 * @Version
 */
public class HmsBuilderImpl implements ILCBuilder {
    private LCLogger Logger = LCLogger.getLogger(HmsBuilderImpl.class.getName());
    private String service_name = "littlec-chat";
    private String methodName;
    private LCMessage.ChatType chatType;
    private String targetUserName;
    private long beginGuid;
    private int limit;
    private long guid;
    private boolean recOrSend = false;
    private boolean remove_total_session = false;
    private List<Long> guidList;

    /**
     * @Title: HmsBuilderImpl <br>
     * @Description: 改构造器第一个变量定义为serviceName<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/5 17:23
     */
    public HmsBuilderImpl(String methodName, LCMessage.ChatType type, String targetUserName, long beginGuid, int limit) {
        this.methodName = methodName;
        this.chatType = type;
        this.targetUserName = targetUserName;
        this.beginGuid = beginGuid;
        this.limit = limit;
    }

    public HmsBuilderImpl(String methodName, long guid) {
        this.methodName = methodName;
        this.guid = guid;
    }

    public HmsBuilderImpl(String methodName, long guid, boolean recOrSend) {
        this.methodName = methodName;
        this.guid = guid;
        this.recOrSend = recOrSend;
    }

    public HmsBuilderImpl(String methodName, LCMessage.ChatType chatType, boolean remove_total_session, String targetUserName, List<Long> guidList) {
        this.methodName = methodName;
        this.chatType = chatType;
        this.remove_total_session = remove_total_session;
        this.targetUserName = targetUserName;
        this.guidList = guidList;
    }

    private Chat.ChatHistoryMessageGetRequest buildChatHistoryMessageRequest() {
        Chat.ChatHistoryMessageGetRequest.Builder requestBuilder = Chat.ChatHistoryMessageGetRequest
                .newBuilder();
        requestBuilder.setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey());
        requestBuilder.setFromUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
        requestBuilder.setBeginGuid(beginGuid);
        requestBuilder.setLimit(limit);
        requestBuilder.setToUsername(targetUserName);
        return requestBuilder.build();
    }

    private Chat.ChatMessageRemoveRequest buildChatMessageRemoveRequest() {
        Chat.ChatMessageRemoveRequest.Builder request = Chat.ChatMessageRemoveRequest.newBuilder()
                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
                .setFromUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
        request.setToUsername(targetUserName);
        if (guidList != null)
            request.addAllGuid(guidList);
        if (remove_total_session) {
            request.setRemoveSession(true);
        } else {
            request.setRemoveSession(false);
        }
        return request.build();
    }


    private Chat.MessageSyncRequest buildMessageSyncRequest() {
        Chat.MessageSyncRequest request = Chat.MessageSyncRequest.newBuilder().setGuid(guid)
                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName()).setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
                .setSor(recOrSend).build();
        return request;
    }

    private Chat.SyncSendGUIDRequest buildSyncServerGuidRequest() {
        Chat.SyncSendGUIDRequest request = Chat.SyncSendGUIDRequest.newBuilder()
                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())
                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey()).build();
        return request;
    }


    @Override
    public Connector.UnaryRequest buildUnaryRequest() {
        Connector.UnaryRequest.Builder requestBuilder = Connector.UnaryRequest.newBuilder();
        requestBuilder.setServiceName(service_name);
        requestBuilder.setMethodName(methodName);
        if ("chatMessageRemove".equals(methodName)) {
            requestBuilder.setData(buildChatMessageRemoveRequest().toByteString());
        } else if ("chatHistoryMessageGet".equals(methodName)) {
            requestBuilder.setData(buildChatHistoryMessageRequest().toByteString());
        } else if ("syncSendGUID".equals(methodName)) {
            requestBuilder.setData(buildSyncServerGuidRequest().toByteString());
        } else if ("messageSync".equals(methodName)) {
            requestBuilder.setData(buildMessageSyncRequest().toByteString());
        }
        return requestBuilder.build();
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/5 user creat
 */
