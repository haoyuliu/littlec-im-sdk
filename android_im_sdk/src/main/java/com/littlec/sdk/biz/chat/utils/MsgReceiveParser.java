/* Project: android_im_sdk
 *
 * File Created at 2016/8/12
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.utils;

import android.text.TextUtils;

import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.fingo.littlec.proto.css.CssErrorCode;
import com.fingo.littlec.proto.css.Msg;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomNoApnsMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFamilyCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCReadReceiptMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCVideoMessageBody;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.IdWorker;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.sp.UserInfoSP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Type com.littlec.sdk.chat.core.parser
 * @User user
 * @Desc 解析拉取到的消息（在线消息和历史消息）
 * @Date 2016/8/12
 * @Version
 */
public class MsgReceiveParser {
    private static final String TAG = "MsgReceiveParser";
    private final static LCLogger logger = LCLogger.getLogger(TAG);

    public static void parseUnaryResponse(Connector.UnaryResponse response, boolean recOrSend) {
        logger.d("----parse msg----parseUnaryResponse serviceName:" + response.getServiceName() + ",methoName:" + response.getMethodName());
        long beginTime = CommonUtils.getCurrentTime();
//        logger.d("parseMsgGetResponse beginTime:" + beginTime);
        try {
            parseMsgGetResponse(response, recOrSend);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            logger.e("parseMsgGetResponse error ." + e);
        }
        long endTime = CommonUtils.getCurrentTime();
        logger.d("parseMsgGetResponse cost time:" + (endTime - beginTime));
    }

    private static MessageEntity getMessageEntityByGuid(Long guid) {
        if (guid == null) {
            return null;
        }
        List<MessageEntity> list = DBFactory.getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.Guid.eq(guid))
                .limit(1)
                .list();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * @Title: <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/18 19:42
     */
    private static void parseMsgGetResponse(Connector.UnaryResponse unaryResponse, boolean recOrSend) throws InvalidProtocolBufferException {
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            logger.e("ret not ok ");
            return;
        }
        Chat.MsgGetResponse response = Chat.MsgGetResponse.parseFrom(unaryResponse.getData());
        if (response == null) {
            logger.e("MsgGetResponse is null");
            return;
        }
        if (response.getRet() != CssErrorCode.ErrorCode.OK) {
            logger.e("errorCode: " + response.getRet() + " errorReason: " + response.getRetValue());
            return;
        } else {
            List<Msg.MessageUnit> list = response.getDataList();
            if (list == null && list.size() == 0) {
                logger.e("list is empty ,no message");
                return;
            }
            logger.d("pull result,messageList size before parse:" + list.size());
            List<LCMessage> messageList = parseMessageUnit(list, false, recOrSend);
            /**********************更新guid*************************/
            logger.d("pull result,messageList size after parse:" + messageList.size());
                /*  LCMessageListener messageListener = LCClient.getInstance().messageManagerInner()
                        .getListener();*/
            MediaEntity fileMessageExtention;
            List<LCMessage> abolishMessage = new ArrayList<>();
            //临时变量用于数据库批量插入
            List<MessageEntity> messageEntityList = new ArrayList<>();
            List<ConversationEntity> conversationEntityList = new ArrayList<>();
            HashMap<String, MediaEntity> hashMap = new HashMap<>();//需要重新赋值mediaId的消息数据,key为msgId,value为mediaEntity
            for (int size = messageList.size() - 1; size >= 0; size--) {
                LCMessage message = messageList.get(size);
                /************消息体里面的guid才是已读回执对应的消息标识**************/
                if (message.LCMessageEntity().getContentType() == Msg.EMsgContentType.READ_RECEIPT_VALUE) {
                    LCReadReceiptMessageBody body = (LCReadReceiptMessageBody) message.LCMessageBody();
                    String guid = body.getReceipt_msgId();
                    if (TextUtils.isEmpty(guid)) {
                        logger.e("guid is empty");
                        continue;
                    }
                    logger.d("read receipt type msg ,receiptMsgGuid:" + guid);
                    long read_receipt_guid = Long.parseLong(guid);
                    MessageEntity read_receipt_msg_entity = GetDataFromDB.queryMessageEntityByGuid(read_receipt_guid);
                    if (read_receipt_msg_entity == null) {
                        logger.e("read_receipt_message is null");
                        continue;
                    }
                    read_receipt_msg_entity.setRead(true);
                    DBFactory.getDBManager().getDBMessageService().update(read_receipt_msg_entity);
                    continue;
                }
                MessageEntity messageEntity = getMessageEntityByGuid(message.LCMessageEntity().getGuid());
                if (messageEntity != null
                        && messageEntity.getContentType() == LCMessage.ContentType.RETRACT.value()
                        && messageEntity.getSendOrRecv() == LCMessage.Direct.RECEIVE.value()) {
                    continue;
                }
                ConversationEntity conversationEntity = GetDataFromDB.insertOrUpdateConversationEntity(message, false);
                if (conversationEntity != null)
                    conversationEntityList.add(conversationEntity);

                message.LCMessageEntity().setConversationId(conversationEntity != null ? conversationEntity.get_recipientId() : "");
                if (message.direct() == LCMessage.Direct.RECEIVE) {
                    message.LCMessageEntity().setStatus(LCMessage.Status.MSG_RECEIVED.value());
                }
                fileMessageExtention = ConvertMessageBodyUtils.messageBodyToFileMessageExtention(message.LCMessageBody());
                if (fileMessageExtention != null) {
                    hashMap.put(message.getMsgId(), fileMessageExtention);
                }
//                MessageEntity messageEntity = getMessageEntityByGuid(message.LCMessageEntity().getGuid());
                if (messageEntity == null || message.getFrom().equals(message.getTo())) {
                    logger.d("----parse msg---- messageEntity is null:guid is" + message.LCMessageEntity().getGuid());
                    messageEntityList.add(message.LCMessageEntity());
//                    DBFactory.getDBManager().getDBMessageService().insertOrReplace(message.LCMessageEntity());
                    /**数据库里面不存在才回调出去，相当于数据库去重一次**/
                    abolishMessage.add(message);
                } else {
                    logger.d("----parse msg---- messageEntity not null:guid is" + message.LCMessageEntity().getGuid());
                    if (messageEntity.getContentType() != LCMessage.ContentType.RETRACT_NUM
                            && messageEntity.getSendOrRecv() == LCMessage.Direct.RECEIVE.value()
                            && message.contentType() == LCMessage.ContentType.RETRACT) {
                        if (messageEntity.getMediaEntity() != null) {
                            DBFactory.getDBManager().getDBMediaService().delete(messageEntity.getMediaEntity());
                        }
                        DBFactory.getDBManager().getDBMessageService().queryBuilder()
                                .where(MessageEntityDao.Properties.Guid
                                        .eq(message.LCMessageEntity().getGuid()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();
                        MediaEntity entity = new MediaEntity();
                        entity.setContent(message.getFrom() + "撤回了一条消息");
                        DBFactory.getDBManager().getDBMediaService().insert(entity);
                        message.LCMessageEntity().setMediaEntity(entity);
                        ConversationEntity conversationEntity2 = DBFactory.getDBManager()
                                .getDBConversationService()
                                .load(message.LCMessageEntity().getConversationId());
                        conversationEntity2.setMsgContent(message.LCMessageEntity().getMediaEntity().getContent());
                        DBFactory.getDBManager().getDBConversationService().update(conversationEntity2);
                        DBFactory.getDBManager().getDBMessageService().insertOrReplace(message.LCMessageEntity());
                    }
                }
            }
            if (LCChatConfig.LCChatGlobalStorage.getInstance().getSyncMsgFlag() && recOrSend) {
                if (LCClient.getInstance().accountManager().getSyncMsgListener() != null) {
                    logger.d("----parse msg---- syncMsgFinish");
                    LCClient.getInstance().accountManager().getSyncMsgListener().onSyncFinished();
                    LCChatConfig.LCChatGlobalStorage.getInstance().setSyncMsgFlag(false);
                }
            }
            logger.d("----parse msg---- begin to check and insert data");
            //批量插入数据库
            if (!hashMap.isEmpty()) {
                DBFactory.getDBManager().getDBMediaService().insertOrReplaceInTx(hashMap.values());
                if (!messageEntityList.isEmpty()) {
                    for (String key : hashMap.keySet()) {
                        for (MessageEntity messageEntity : messageEntityList) {
                            if (messageEntity.getMsgId().equals(key)) {
                                messageEntity.setMediaId(hashMap.get(key).getId());
                                continue;
                            }
                        }
                    }
                }
            }
            if (!messageEntityList.isEmpty()) {
                DBFactory.getDBManager().getDBMessageService().insertOrReplaceInTx(messageEntityList);
            }
            if (!conversationEntityList.isEmpty()) {
                DBFactory.getDBManager().getDBConversationService().insertOrReplaceInTx(conversationEntityList);
            }


            logger.d("----parse msg---- finish db operation");
            if (abolishMessage.isEmpty()) {
                logger.e("----parse msg---- abolishMessage is empty");
                return;
            }
            logger.e("----parse msg---- abolishMessage size:" + abolishMessage.size());
            //存储最大的guid
            Long guid = abolishMessage.get(abolishMessage.size() - 1).LCMessageEntity().getGuid();
            logger.d("----parse msg---- recOrSend:" + recOrSend + ",saveGuid:" + guid);
            UserInfoSP.putGuid(recOrSend ? LCChatConfig.UserInfo.REV_GUID : LCChatConfig.UserInfo.SEND_GUID, guid);
            DispatchController.getInstance().onReceivedChatMessage(abolishMessage);
        }


    }

    private static void parseEachUnit(Msg.MessageUnit unit, Map<String, LCMessage> msgMap, List<LCMessage> messageListBySelf, boolean isHistory) {
        switch (unit.getMsgType()) {
            case CHAT_MSG:
                if (!unit.getIsRetracted()) {
                    LCMessage chatMessage = parseChatMessage(unit.getData());
                    if (chatMessage == null || TextUtils.isEmpty(chatMessage.LCMessageEntity().getMsgId())) {
                        logger.e("chat Message null or msgid empty");
                        break;
                    }
                    String msgId = chatMessage.LCMessageEntity().getMsgId();
                    if (!msgMap.containsKey(msgId)) {
                        msgMap.put(msgId, chatMessage);
                    } else {
                        logger.e("in chat ,msgMap key has the same msgId key:" + msgId);
                    }
                }
                break;
            case PRIVATE_MSG:

                break;
            case GROUP_MSG:

                break;

            case MSGGW_MSG:
                LCMessage message = parseMsgGw(unit.getData());
                if (message == null || TextUtils.isEmpty(message.LCMessageEntity().getMsgId())) {
                    logger.e("msg gw Message null or msgid empty");
                    break;
                }
                String msgId = message.LCMessageEntity().getMsgId();
                if (!msgMap.containsKey(msgId)) {
                    msgMap.put(msgId, message);
                } else {
                    logger.e("in msg gw, msgMap key has the same msgId key:" + msgId);
                }
                break;
            case SYSTEM_MSG:
                break;
            case MULTI_MSG:

                break;
            default:
                break;
        }
    }

    /**
     * @Title: parseMessageUnit <br>
     * @Description: 解析各种消息类型<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/30 8:37
     */
    public synchronized static List<LCMessage> parseMessageUnit(List<Msg.MessageUnit> list, boolean isHistory, boolean recOrSend) {
        if (list == null)
            return null;

        Map<String, LCMessage> msgMap = new LinkedHashMap<>();
        List<LCMessage> messageListBySelf = new ArrayList<>();//自己发送的群消息
        for (Msg.MessageUnit unit : list) {
            try {
                parseEachUnit(unit, msgMap, messageListBySelf, isHistory);
            } catch (Exception e) {
                //防止解析异常
                logger.d("parseEachUnit error" + e);
            }
        }
        /************************消息去重 包括拉取消息去重和数据库去重***************************/
        List<LCMessage> resultList = new ArrayList<>();
        for (String key : msgMap.keySet()) {
            resultList.add(msgMap.get(key));
        }
        //自己发的群消息拉到后单独保存guid
        if (!messageListBySelf.isEmpty()) {
            Long guid = messageListBySelf.get(0).LCMessageEntity().getGuid();
            logger.d("parseMessageUnit ----parse msg---- recOrSend:" + recOrSend + ",saveGuid:" + guid);
            UserInfoSP.putGuid(recOrSend ? LCChatConfig.UserInfo.REV_GUID : LCChatConfig.UserInfo.SEND_GUID, guid);
        }
        return resultList;
    }

    private static LCMessage parseChatMessage(ByteString data) {
        try {
            Chat.ChatMessage chatMessage = Chat.ChatMessage.newBuilder().mergeFrom(data).build();
            //basic message of chatmessage
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setTo(chatMessage.getToUsername());
            messageEntity.setFrom(chatMessage.getFromUsername());
            messageEntity.setFromNick(chatMessage.getFromNick());
            messageEntity.setJiMaoFlag(chatMessage.getIsJimao());
            if (chatMessage.getFromUsername().equals(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())) {
                messageEntity.setSendOrRecv(LCMessage.Direct.SEND.value());
            } else {
                messageEntity.setSendOrRecv(LCMessage.Direct.RECEIVE.value());
            }
            if (chatMessage.getToUsername().equals(chatMessage.getFromUsername())) {
                messageEntity.setMsgId(CommonUtils.getUUID());
            } else {
                messageEntity.setMsgId(chatMessage.getMsgId());
            }
            messageEntity.setContentType(chatMessage.getMsgContentTypeValue());
            if (chatMessage.getMsgContentType() == Msg.EMsgContentType.RETRACT) {
                logger.d("parseChatMessage retract type msg");
                Msg.RetractMessage temp = Msg.RetractMessage.newBuilder().mergeFrom(chatMessage.getData()).build();
                messageEntity.setGuid(temp.getRetractGuid());
                messageEntity.setExtra((messageEntity.getSendOrRecv() == LCMessage.Direct.SEND.value() ? "你" : chatMessage.getFromUsername()) + "撤回了一条消息");
            } else {
                messageEntity.setGuid(chatMessage.getGuid());
            }
            messageEntity.setCreateTime(IdWorker.getInstance().restore(chatMessage.getGuid()));
            messageEntity.setChatType(LCMessage.ChatType.Chat.value());
            messageEntity.setStatus(LCMessage.Status.MSG_SEND_SUCCESS.value());

            //parse message body
            LCMessageBody body = parseMessageContent(messageEntity, chatMessage.getData());
            if (body != null) {
                return new LCMessage(messageEntity, body);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e);
        }
        return null;
    }


    private static LCMessage parseMsgGw(ByteString data) {
        Msg.MsggwMessage msggwMessage;
        LCMessage message = null;
        try {
            msggwMessage = Msg.MsggwMessage.newBuilder().mergeFrom(data).build();
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setFrom(msggwMessage.getFrom());
            messageEntity.setGuid(msggwMessage.getGuid());
            messageEntity.setMsgId(CommonUtils.getUUID());
            messageEntity.setTo(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
            messageEntity.setSendOrRecv(LCMessage.Direct.RECEIVE.value());
            messageEntity.setCreateTime(IdWorker.getInstance().restore(msggwMessage.getGuid()));
            messageEntity.setChatType(LCMessage.ChatType.GwChat.value());
            messageEntity.setContentType(LCMessage.ContentType.GwMsg_NUM);
            LCMessageBody body = LCMessageBody.createGwMessageBody(msggwMessage.getContent(), msggwMessage.getNotification());
            message = new LCMessage(messageEntity, body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return message;
    }


    private static LCMessageBody parseMessageContent(MessageEntity messageEntity, ByteString data) {
        if (messageEntity == null || data == null)
            return null;
        LCMessage.ContentType contentType = LCMessage.ContentType.forNumber(messageEntity.getContentType());
        if (contentType == null) {
            logger.e("unknow type ,retun null!!");
            return null;
        }
        try {
            switch (contentType) {
                case TXT:
                    Msg.TextMessage textMessage = Msg.TextMessage.parseFrom(data);
                    LCTextMessageBody messageBody = new LCTextMessageBody(textMessage.getText());
                    return messageBody;
                case IMAGE:
                    Msg.ImageMessage imageMessage = Msg.ImageMessage.parseFrom(data);
                    LCImageMessageBody imageMessageBody = new LCImageMessageBody();
                    imageMessageBody.setisOrigin(imageMessage.getIsOrigin());
                    imageMessageBody.setOriginalUri(imageMessage.getOriginLink());
                    imageMessageBody.setLargeUri(imageMessage.getBigLink());
                    imageMessageBody.setMiddleUri(imageMessage.getMiddleLink());
                    imageMessageBody.setSmallUri(imageMessage.getSmallLink());
                    imageMessageBody.setFileName(imageMessage.getFileName());
                    imageMessageBody.setFileLength(imageMessage.getFileLength());
                    imageMessageBody.setSmall_width(imageMessage.getSmallWidth());
                    imageMessageBody.setSmall_height(imageMessage.getSmallHeight());
                    imageMessageBody.setMiddle_width(imageMessage.getMiddleWidth());
                    imageMessageBody.setMiddle_height(imageMessage.getMiddleHeight());
                    return imageMessageBody;
                case VIDEO:
                    Msg.VideoMessage videoMessage = Msg.VideoMessage.parseFrom(data);
                    LCVideoMessageBody videoMessageBody = new LCVideoMessageBody();
                    videoMessageBody.setFileName(videoMessage.getFileName());
                    videoMessageBody.setOriginalUri(videoMessage.getVideoLink());
                    videoMessageBody.setThumbnailUrl(videoMessage.getVideoScreenShortLink());
                    videoMessageBody.setFileLength(videoMessage.getFileLength());
                    videoMessageBody.setDuration(videoMessage.getDuration());
                    videoMessageBody.setHeight(videoMessage.getHeight());
                    videoMessageBody.setWidth(videoMessage.getWidth());
                    return videoMessageBody;
                case AUDIO:
                    Msg.AudioMessage audioMsg = Msg.AudioMessage.parseFrom(data);
                    LCAudioMessageBody audioMessageBody = new LCAudioMessageBody();
                    audioMessageBody.setFileName(audioMsg.getFileName());
                    audioMessageBody.setOriginalUri(audioMsg.getAudioLink());
                    audioMessageBody.setFileLength(audioMsg.getFileLength());
                    audioMessageBody.setDuration(audioMsg.getDuration());
                    return audioMessageBody;
                case LOCATION:
                    Msg.LocationMessage locationMessage = Msg.LocationMessage.parseFrom(data);
                    LCLocationMessageBody locationBody = new LCLocationMessageBody();
                    locationBody.setFileName(locationMessage.getFileName());
                    locationBody.setOriginalUri(locationMessage.getOriginLink());
                    locationBody.setFileLength(locationMessage.getFileLength());
                    locationBody.setWidth(locationMessage.getWidth());
                    locationBody.setHeight(locationMessage.getHeight());
                    locationBody.setAddress(locationMessage.getLocationAddress());
                    locationBody.setLongitude(locationMessage.getLongitude());
                    locationBody.setLatitude(locationMessage.getLatitude());
                    locationBody.setLocation_desc(locationMessage.getLocationDesc());
                    return locationBody;
                case AT:
                    Msg.AtMessage atMessage = Msg.AtMessage.parseFrom(data);
                    LCATMessageBody atBody = new LCATMessageBody();
                    atBody.setAt_members(atMessage.getAtMemberList());
                    atBody.setText(atMessage.getText());
                    return atBody;
                case READ_RECEIPT:
                    /***已读消息回执 消息体里面的 guid才是对应消息的标识   本身的msgId guid 都不能作为标识**/
                    Msg.ReadReceiptMessage readReceiptMessage = Msg.ReadReceiptMessage.parseFrom(data);
                    long guid = readReceiptMessage.getReceiptGuid();
                    LCReadReceiptMessageBody readReceiptMessageBody = new LCReadReceiptMessageBody(String.valueOf(guid));
                    return readReceiptMessageBody;
                case FILE:
                    break;
                case RETRACT:
                    LCReadReceiptMessageBody retractBody = new LCReadReceiptMessageBody(messageEntity.getMsgId());
                    return retractBody;
                case CUSTOM:
                    Msg.CustomMessage customMessage = Msg.CustomMessage.parseFrom(data);
                    String notification = customMessage.getNotification();
                    List<Msg.CustomEntity> customEntities = customMessage.getCustomEntityList();
                    HashMap<String, String> map = new HashMap<>();
                    for (Msg.CustomEntity entity : customEntities) {
                        map.put(entity.getKey(), entity.getValue());
                    }
                    LCCustomMessageBody customMessageBody = new LCCustomMessageBody(map, notification);
                    messageEntity.setExtra(customMessageBody.getContent());
                    return customMessageBody;
                case CUSTOM_NO_APNS:
                    Msg.CustomNoApnsMessage customNoApnsMessage = Msg.CustomNoApnsMessage.parseFrom(data);
                    notification = customNoApnsMessage.getNotification();
                    customEntities = customNoApnsMessage.getCustomEntityList();
                    map = new HashMap<>();
                    for (Msg.CustomEntity entity : customEntities) {
                        map.put(entity.getKey(), entity.getValue());
                    }
                    LCCustomNoApnsMessageBody customNoApnsMessageBody = new LCCustomNoApnsMessageBody(map, notification);
                    messageEntity.setExtra(customNoApnsMessageBody.getContent());
                    return customNoApnsMessageBody;
                case CUSTOM_FAMILY:
                    Msg.CustomMessage familyCustomMessage = Msg.CustomMessage.parseFrom(data);
                    notification = familyCustomMessage.getNotification();
                    customEntities = familyCustomMessage.getCustomEntityList();
                    map = new HashMap<>();
                    for (Msg.CustomEntity entity : customEntities) {
                        map.put(entity.getKey(), entity.getValue());
                    }
                    LCFamilyCustomMessageBody familyCustomMessageBody = new LCFamilyCustomMessageBody(map, notification);
                    messageEntity.setExtra(familyCustomMessageBody.getContent());
                    return familyCustomMessageBody;


            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            logger.e(e);
        }
        return null;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/12 user creat
 */
