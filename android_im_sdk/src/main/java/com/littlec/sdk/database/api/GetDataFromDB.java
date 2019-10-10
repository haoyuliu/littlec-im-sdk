/* Project: android_im_sdk
 *
 * File Created at 2016/9/23
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.database.api;

import android.text.TextUtils;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCGwMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.utils.ConvertMessageBodyUtils;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.FriendReqDBEntityDao;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ContactEntity;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.FriendReqDBEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.littlec.sdk.database.DBFactory.getDBManager;
import static com.littlec.sdk.lang.LCError.MESSAGE_EXIST_ERROR;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/9/23
 * @Version
 */

public class GetDataFromDB {
    private static LCLogger Logger = LCLogger.getLogger(GetDataFromDB.class.getName());

    public static MessageEntity cloneMessageEntity(MessageEntity messageEntity) {
        MessageEntity messageEntity1 = new MessageEntity();
        messageEntity1.setMsgId(messageEntity.getMsgId());
        messageEntity1.setSendOrRecv(messageEntity.getSendOrRecv());
        messageEntity1.setChatType(messageEntity.getChatType());
        messageEntity1.setFrom(messageEntity.getFrom());
        messageEntity1.setFromNick(messageEntity.getFromNick());
        messageEntity1.setTo(messageEntity.getTo());
        messageEntity1.setContentType(messageEntity.getContentType());
        messageEntity1.setStatus(messageEntity.getStatus());
        messageEntity1.setMediaId(messageEntity.getMediaId());
        messageEntity1.setCreateTime(CommonUtils.getCurrentTime());
        messageEntity1.setConversationId(messageEntity.getConversationId());
        return messageEntity1;
    }

    public static LCMessage getMessageFromDB(String msgId) {
        try {
            if (TextUtils.isEmpty(msgId)) {
                return null;
            }
            MessageEntity messageEntity = getDBManager().getDBMessageService().queryBuilder()
                    .where(MessageEntityDao.Properties.MsgId.eq(msgId)).unique();
            if (messageEntity == null) {
                return null;
            }
            MediaEntity mediaEntity = messageEntity.getMediaEntity();
            LCMessageBody messageBody = null;
            if (messageEntity.getContentType() == LCMessage.ContentType.AT.value()) {
                String result = messageEntity.getExtra();
                JSONObject jsonObject = new JSONObject(result);
                messageBody = new LCATMessageBody(jsonObject.optString("content"), null);
            } else {
                messageBody = ConvertMessageBodyUtils.fileExtentionToMessageBody(
                        messageEntity.getMsgId(), messageEntity.getContentType(), mediaEntity);
            }
            LCMessage message = new LCMessage(messageEntity, messageBody);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Title: generateNewMessageByMsgId <br>
     * @Description: 通过msgId查询到消息，并且将内容拷贝到新消息中 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/2 18:23
     */
    public static LCMessage createNewMessageByMsgId(LCMessage.ChatType chatType,
                                                    String toChatUserName, String msgId)
            throws LCException {
        MessageEntity messageEntity = getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.MsgId.eq(msgId)).unique();
        if (messageEntity == null) {
            throw new LCException(MESSAGE_EXIST_ERROR);
        }
        MessageEntity cloneMessageEntity = GetDataFromDB.cloneMessageEntity(messageEntity);
        if (cloneMessageEntity.getSendOrRecv() == LCMessage.Direct.RECEIVE.value())
            cloneMessageEntity
                    .setFrom(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
        cloneMessageEntity.setTo(toChatUserName);
        cloneMessageEntity.setMsgId(CommonUtils.getUUID());
        cloneMessageEntity.setChatType(chatType.value());
        MediaEntity mediaEntity = messageEntity.getMediaEntity();
        LCMessageBody messageBody = ConvertMessageBodyUtils.fileExtentionToMessageBody(
                cloneMessageEntity.getMsgId(), cloneMessageEntity.getContentType(), mediaEntity);
        LCMessage message = new LCMessage(cloneMessageEntity, messageBody);
        return message;
    }

    public static List<LCMessage> getAllMessage(String conversationId) {
        try {
            List<MessageEntity> messageEntities = DBFactory.getDBManager().getDBMessageService()
                    .queryBuilder().where(MessageEntityDao.Properties.ConversationId.eq(conversationId))
                    .orderAsc(MessageEntityDao.Properties.CreateTime).list();
            if (messageEntities == null)
                return null;
            List<LCMessage> list = new ArrayList<>();
            for (MessageEntity messageEntity : messageEntities) {
                MediaEntity mediaEntity = messageEntity.getMediaEntity();
                LCMessageBody messageBody = null;
                if (messageEntity.getContentType() == LCMessage.ContentType.AT.value()) {
                    String result = messageEntity.getExtra();
                    JSONObject jsonObject = new JSONObject(result);
                    messageBody = new LCATMessageBody(jsonObject.optString("content"), null);
                } else {
                    messageBody = ConvertMessageBodyUtils.fileExtentionToMessageBody(
                            messageEntity.getMsgId(), messageEntity.getContentType(), mediaEntity);
                }
                LCMessage message = new LCMessage(messageEntity, messageBody);
                list.add(message);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<LCMessage> getAllMessage() {
        try {
            List<MessageEntity> messageEntities = DBFactory.getDBManager().getDBMessageService()
                    .queryBuilder()
                    .orderAsc(MessageEntityDao.Properties.CreateTime).list();
            if (messageEntities == null)
                return null;
            List<LCMessage> list = new ArrayList<>();
            for (MessageEntity messageEntity : messageEntities) {
                MediaEntity mediaEntity = messageEntity.getMediaEntity();
                LCMessageBody messageBody = null;
                if (messageEntity.getContentType() == LCMessage.ContentType.AT.value()) {
                    String result = messageEntity.getExtra();
                    JSONObject jsonObject = new JSONObject(result);
                    messageBody = new LCATMessageBody(jsonObject.optString("content"), null);
                } else {
                    messageBody = ConvertMessageBodyUtils.fileExtentionToMessageBody(
                            messageEntity.getMsgId(), messageEntity.getContentType(), mediaEntity);
                }
                LCMessage message = new LCMessage(messageEntity, messageBody);
                list.add(message);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static MessageEntity queryMessageEntityByGuid(long guid) {
        List<MessageEntity> list = getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.Guid.eq(guid)).list();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public static long queryGuidByMsgId(String msgId) {
        MessageEntity messageEntity = DBFactory.getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.MsgId.eq(msgId)).unique();
        if (messageEntity == null)
            return -1;
        return messageEntity.getGuid();
    }

    public static MessageEntity queryMessageEntityByMsgId(String msgId) {
        MessageEntity messageEntity = getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.MsgId.eq(msgId)).unique();

        return messageEntity;
    }

    public static String queryMsgIdByGuid(long guid) {
        MessageEntity messageEntity = getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.Guid.eq(guid)).unique();
        if (messageEntity == null)
            return null;
        return messageEntity.getMsgId();
    }

    public static FriendReqDBEntity queryFriendReqDBEntityByRegId(String regId) {
        FriendReqDBEntity friendReqDBEntity = getDBManager().getDBFriendReqService().queryBuilder()
                .where(FriendReqDBEntityDao.Properties.RegId.eq(regId)).unique();
        return friendReqDBEntity;

    }

    public static void insertMessageByFriendEntity(FriendReqDBEntity friendReqDBEntity) {
        MessageEntity messageEntity = new MessageEntity();
        MediaEntity mediaEntity = new MediaEntity();
        if (!TextUtils.isEmpty(friendReqDBEntity.getRemark()))
            mediaEntity.setContent(friendReqDBEntity.getRemark());
        else
            mediaEntity.setContent("我请求添加你为好友");
        getDBManager().getDBMediaService().insertOrReplace(mediaEntity);
        messageEntity.setMediaEntity(mediaEntity);
        messageEntity.setFrom(friendReqDBEntity.getFromUserName());
        messageEntity.setFromNick(friendReqDBEntity.getFromNick());
        messageEntity.setTo(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
        messageEntity.setChatType(LCMessage.ChatType.Chat.value());
        messageEntity.setContentType(LCMessage.ContentType.TXT.value());
        messageEntity.setSendOrRecv(LCMessage.Direct.RECEIVE.value());
        messageEntity.setCreateTime(CommonUtils.getCurrentTime());
        ConversationEntity conversationEntity = insertOrUpdateConversationEntity(messageEntity);
        messageEntity.setConversationId(conversationEntity.get_recipientId());
        getDBManager().getDBMessageService().insertOrReplace(messageEntity);
    }

    public static ConversationEntity insertOrUpdateConversationEntity(LCMessage message, boolean insertDataToDB) {
        //        if(message.chatType()== LCMessage.ChatType.GwChat){
        //            return null;
        //        }
        if (message == null || message.LCMessageEntity() == null
                || message.LCMessageEntity().getTo() == null) {
            return null;
        }
        String conversationId;
        if (message.direct().equals(LCMessage.Direct.RECEIVE) &&
                (message.chatType().equals(LCMessage.ChatType.Chat)
                        || message.chatType().equals(LCMessage.ChatType.PrivateChat)
                        || message.chatType().equals(LCMessage.ChatType.GwChat)
                        || message.chatType().equals(LCMessage.ChatType.MultiChat))) {
            conversationId = message.getFrom();
        } else {
            conversationId = message.getTo();
        }
        ConversationEntity conversationEntity = getDBManager().getDBConversationService()
                .load(conversationId);
        if (conversationEntity == null) {
            Logger.e("conversation is null");
            insertDataToDB = true;
            conversationEntity = new ConversationEntity(conversationId);
        }
        if (message.LCMessageBody() instanceof LCTextMessageBody) {
            conversationEntity.setMsgContent(
                    ((LCTextMessageBody) message.LCMessageBody()).getMessageContent());
        }
        if (message.LCMessageBody() instanceof LCGwMessageBody) {
            conversationEntity
                    .setMsgContent(((LCGwMessageBody) message.LCMessageBody()).getMessageContent());
        }
        if (message.LCMessageBody() instanceof LCATMessageBody) {
            if (((LCATMessageBody) message.LCMessageBody()).getAt_members() != null) {
//                if (((LCATMessageBody) message.LCMessageBody()).getAt_members()
//                        .contains(LCClient.getInstance().getCurrentUser().getUserName()))
//                    conversationEntity.setMsgContent(message.getFromNick() + "@了你");
//                else
                conversationEntity
                        .setMsgContent(((LCATMessageBody) message.LCMessageBody()).getText());
            }
        }
        if (message.contentType() == LCMessage.ContentType.RETRACT) {
            if (message.direct() == LCMessage.Direct.SEND)
                conversationEntity.setMsgContent("你撤回了一条消息");
            else
                conversationEntity.setMsgContent(message.getFrom() + "撤回了一条消息");
        }
        if (message.contentType() == LCMessage.ContentType.SIGNAL) {
            conversationEntity.setMsgContent(message.getExtra());
        }
        conversationEntity.setMsgStatus(message.LCMessageEntity().getStatus());
        conversationEntity.setDate(message.LCMessageEntity().getCreateTime());
        conversationEntity.setMsgContentType(message.LCMessageEntity().getContentType());

        /***************如果为收到的消息，则未读+1**************/
        if (message.LCMessageEntity().getSendOrRecv() == LCMessage.Direct.RECEIVE.value()
                && message.contentType() != LCMessage.ContentType.RETRACT) {
            if (!LCChatConfig.LCChatGlobalStorage.getInstance().getIsInChat(conversationId)) {
                Logger.e("set conversation unread:" + (conversationEntity.getUnreadCount() + 1));
                conversationEntity.setUnreadCount(conversationEntity.getUnreadCount() + 1);
            }
        }
        conversationEntity.setTotalCount(conversationEntity.getTotalCount() + 1);
        conversationEntity.setChattype(message.LCMessageEntity().getChatType());
        if (insertDataToDB)
            getDBManager().getDBConversationService().insertOrReplace(conversationEntity);
        return conversationEntity;
    }

    public static ConversationEntity insertOrUpdateConversationEntity(MessageEntity messageEntity) {
        ConversationEntity conversationEntity;
        String conversationId;
        /*******************需要注意 会话id只有当 收到消息且是单聊的时候 才设置对方***/
        if (messageEntity.getSendOrRecv() == LCMessage.Direct.RECEIVE.value() &&
                (messageEntity.getChatType() == LCMessage.ChatType.Chat.value()
                        || messageEntity.getChatType() == LCMessage.ChatType.PrivateChat.value())) {
            conversationId = messageEntity.getFrom();
        } else {
            conversationId = messageEntity.getTo();
        }
        conversationEntity = getDBManager().getDBConversationService().load(conversationId);

        if (conversationEntity == null) {
            conversationEntity = new ConversationEntity(conversationId);
        }
        if (messageEntity.getMediaEntity() != null) {
            conversationEntity.setMsgContent(messageEntity.getMediaEntity().getContent());
        }
        conversationEntity.setMsgStatus(messageEntity.getStatus());
        conversationEntity.setDate(messageEntity.getCreateTime());
        conversationEntity.setMsgContentType(messageEntity.getContentType());
        conversationEntity.setChattype(messageEntity.getChatType());
        /***************如果为收到的消息，则未读+1**************/
        if (messageEntity.getSendOrRecv() == LCMessage.Direct.RECEIVE.value()) {
            if (!LCChatConfig.LCChatGlobalStorage.getInstance().getIsInChat(conversationId))
                conversationEntity.setUnreadCount(conversationEntity.getUnreadCount() + 1);
        }
        conversationEntity.setTotalCount(conversationEntity.getTotalCount() + 1);
        getDBManager().getDBConversationService().insertOrReplace(conversationEntity);
        return conversationEntity;
    }

    public static boolean queryContactWithUserName(String userName) {
        ContactEntity contactEntity = DBFactory.getDBManager().getDBContactService().load(userName);
        if (contactEntity == null) {
            return false;
        } else {
            return true;
        }

    }


}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/23 zhangguoqiong creat
 */
