/* Project: android_im_sdk
 *
 * File Created at 2016/8/1
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.body.LCMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.database.entity.MessageEntity;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.utils.CommonUtils;

import java.util.List;

import static com.littlec.sdk.biz.chat.entity.LCMessage.Direct.RECEIVE;
import static com.littlec.sdk.biz.chat.entity.LCMessage.Direct.SEND;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc 消息实体
 * @Date 2016/8/1
 * @Version
 */
public class LCMessage implements Parcelable, Cloneable {
    public static final int PREPARED = -1;// 准备好了
    private List<String> userList;
    private LCMessageBody messageBody = null;
    private MessageEntity messageEntity = null;
    private int jimao_left_num = 0;//鸡毛信剩余数量

    public LCMessage(MessageEntity messageEntity, LCMessageBody body) {
        this.messageEntity = messageEntity;
        this.messageBody = body;
    }

    public enum ChatType {
        Chat(Msg.EMsgType.CHAT_MSG_VALUE),//0
        GroupChat(Msg.EMsgType.GROUP_MSG_VALUE),//1,
        GwChat(Msg.EMsgType.MSGGW_MSG_VALUE),//3
        MultiChat(Msg.EMsgType.MULTI_MSG_VALUE), //5
        RobotChat(Msg.EMsgType.MULTI_MSG_VALUE + 1),//6
        PrivateChat(Msg.EMsgType.PRIVATE_MSG_VALUE);//7
        //        GroupSignal(Msg.EMsgType.GROUP_SIGNAL_VALUE);
        private int value;

        ChatType(int value) {
            this.value = value;
        }

        public static ChatType forNumber(int value) {
            switch (value) {
                case Msg.EMsgType.CHAT_MSG_VALUE:
                    return Chat;
                case Msg.EMsgType.GROUP_MSG_VALUE:
                    return GroupChat;
                case Msg.EMsgType.MULTI_MSG_VALUE:
                    return MultiChat;
                case Msg.EMsgType.MSGGW_MSG_VALUE:
                    return GwChat;
                case Msg.EMsgType.MULTI_MSG_VALUE + 1:
                    return RobotChat;
                case Msg.EMsgType.PRIVATE_MSG_VALUE:
                    return PrivateChat;
                default:
                    return null;
            }
        }

        public int value() {
            return value;
        }
    }

    public enum Direct {
        SEND(1),
        RECEIVE(0);
        private int value;

        Direct(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Direct forNumber(int value) {
            switch (value) {
                case 1:
                    return SEND;
                case 0:
                    return RECEIVE;
                default:
                    return null;
            }
        }
    }

    public enum Status {
        MSG_SEND_PROGRESS(0),
        MSG_SEND_SUCCESS(1),
        MSG_SEND_FAIL(2),
        MSG_RECEIVED(3),
        MSG_RECEIVED_FAIL(4),
        MSG_READ(5),
        MSG_UNREAD(6),
        MSG_POST_FILE_FAIL(7);
        private int value;

        Status(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Status forNumber(int value) {
            switch (value) {
                case 0:
                    return MSG_SEND_PROGRESS;
                case 1:
                    return MSG_SEND_SUCCESS;
                case 2:
                    return MSG_SEND_FAIL;
                case 3:
                    return MSG_RECEIVED;
                case 4:
                    return MSG_RECEIVED_FAIL;
                case 5:
                    return MSG_READ;
                case 6:
                    return MSG_UNREAD;
                case 7:
                    return MSG_POST_FILE_FAIL;
                default:
                    return null;
            }
        }
    }

    public enum ContentType {
        TXT(Msg.EMsgContentType.TEXT_VALUE),
        IMAGE(Msg.EMsgContentType.IMAGE_VALUE),
        VIDEO(Msg.EMsgContentType.VIDEO_VALUE),
        AUDIO(Msg.EMsgContentType.AUDIO_VALUE),
        LOCATION(Msg.EMsgContentType.LOCATION_VALUE),
        AT(Msg.EMsgContentType.AT_VALUE),
        READ_RECEIPT(Msg.EMsgContentType.READ_RECEIPT_VALUE),
        FILE(Msg.EMsgContentType.FILE_VALUE),
        RETRACT(Msg.EMsgContentType.RETRACT_VALUE),
        CUSTOM(Msg.EMsgContentType.CUSTOM_VALUE),
        JIMAO_READED(Msg.EMsgContentType.JIMAO_READED_VALUE),
        JIMAO_SENDED(Msg.EMsgContentType.JIMAO_SENDED_VALUE),
        SIGNAL(Msg.EMsgContentType.CUSTOM_VALUE + 3),
        GwMsg(Msg.EMsgContentType.CUSTOM_VALUE + 4),
        ROBOT_TEXT(Msg.EMsgContentType.CUSTOM_VALUE + 5),
        ROBOT_LINK(Msg.EMsgContentType.CUSTOM_VALUE + 6),
        ROBOT_NEWS(Msg.EMsgContentType.CUSTOM_VALUE + 7),
        ROBOT_COOK_BOOK(Msg.EMsgContentType.CUSTOM_VALUE + 8),
        CUSTOM_NO_APNS(Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE),
        CUSTOM_FAMILY(Msg.EMsgContentType.CUSTOM_FAMILY_VALUE);
        public static final int TXT_NUM = 0;
        public static final int IMAGE_NUM = 1;
        public static final int VIDEO_NUM = 2;
        public static final int AUDIO_NUM = 3;
        public static final int LOCATION_NUM = 4;
        public static final int AT_NUM = 5;
        public static final int READ_RECEIPT_NUM = 6;
        public static final int FILE_NUM = 7;
        public static final int RETRACT_NUM = 8;
        public static final int CUSTOM_NUM = 9;
        public static final int JIMAO_READED_NUM = 10;
        public static final int JIMAO_SENDED_NUM = 11;
        public static final int SIGNAL_NUM = 12;
        public static final int GwMsg_NUM = 13;
        public static final int ROBOT_TEXT_NUM = 14;
        public static final int ROBOT_LINK_NUM = 15;
        public static final int ROBOT_NEWS_NUM = 16;
        public static final int ROBOT_COOK_BOOK_NUM = 17;
        public static final int CUSTOM_NO_APNS_NUM = Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE;

        private int value;

        ContentType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static ContentType forNumber(int value) {
            switch (value) {
                case Msg.EMsgContentType.TEXT_VALUE:
                    return TXT;
                case Msg.EMsgContentType.IMAGE_VALUE:
                    return IMAGE;
                case Msg.EMsgContentType.VIDEO_VALUE:
                    return VIDEO;
                case Msg.EMsgContentType.AUDIO_VALUE:
                    return AUDIO;
                case Msg.EMsgContentType.LOCATION_VALUE:
                    return LOCATION;
                case Msg.EMsgContentType.AT_VALUE:
                    return AT;
                case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                    return READ_RECEIPT;
                case Msg.EMsgContentType.FILE_VALUE:
                    return FILE;
                case Msg.EMsgContentType.RETRACT_VALUE:
                    return RETRACT;
                case Msg.EMsgContentType.CUSTOM_VALUE:
                    return CUSTOM;
                case Msg.EMsgContentType.JIMAO_READED_VALUE:
                    return JIMAO_READED;
                case Msg.EMsgContentType.JIMAO_SENDED_VALUE:
                    return JIMAO_SENDED;
                case 12:
                    return SIGNAL;
                case 13:
                    return GwMsg;
                case 14:
                    return ROBOT_TEXT;
                case 15:
                    return ROBOT_LINK;
                case 16:
                    return ROBOT_NEWS;
                case 17:
                    return ROBOT_COOK_BOOK;
                case Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE:
                    return CUSTOM_NO_APNS;
                case Msg.EMsgContentType.CUSTOM_FAMILY_VALUE:
                    return CUSTOM_FAMILY;
                default:
                    return null;
            }
        }

    }

    public MessageEntity LCMessageEntity() {
        return messageEntity;
    }

    public ChatType chatType() {
        if (messageEntity != null) {
            return ChatType.forNumber(messageEntity.getChatType());
        }
        return ChatType.Chat;
    }


    public Direct direct() {
        if (messageEntity != null) {
            return Direct.forNumber(messageEntity.getSendOrRecv());
        }
        return Direct.SEND;
    }

    public String getMsgId() {
        if (messageEntity != null) {
            return messageEntity.getMsgId();
        }
        return null;
    }

    public void setMsgId(String msgId) {
        if (messageEntity != null) {
            messageEntity.setMsgId(msgId);
        }
    }

    public Status status() {
        if (messageEntity != null) {
            return Status.forNumber(messageEntity.getStatus());
        }
        return Status.MSG_SEND_FAIL;
    }

    public String getFrom() {
        if (messageEntity != null) {
            return messageEntity.getFrom();
        }
        return null;
    }

    public String getFromNick() {
        if (messageEntity != null) {
            return messageEntity.getFromNick();
        }
        return null;
    }

    public void setFromNick(String fromNick) {
        if (messageEntity != null) {
            messageEntity.setFromNick(fromNick);
        }
    }
    public String getTo() {
        if (messageEntity != null) {
            return messageEntity.getTo();
        }
        return null;
    }

    public ContentType contentType() {
        if (messageEntity != null) {
            return ContentType.forNumber(messageEntity.getContentType());
        }
        return ContentType.TXT;
    }

    public long getCreateTime() {
        if (messageEntity != null) {
            return messageEntity.getCreateTime();
        }
        return 0;
    }

    public boolean read() {
        if (messageEntity != null) {
            return messageEntity.getRead();
        }
        return false;
    }

    public boolean burnAfterRead() {
        if (messageEntity != null) {
            return messageEntity.getBurnAfterRead();
        }
        return false;
    }

    public String getConversationId() {
        if (messageEntity != null) {
            return messageEntity.getConversationId();
        }
        return null;
    }

    public long getMediaId() {
        if (messageEntity != null) {
            return messageEntity.getMediaId();
        }
        return 0;
    }

    public String getExtra() {
        if (messageEntity != null) {
            return messageEntity.getExtra();
        }
        return null;
    }


    public LCMessageBody LCMessageBody() {
        return messageBody;
    }

    private void setChatType(ChatType type) {
        if (messageEntity != null) {
            messageEntity.setChatType(type.value());
        }

    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<String> getUserList() {
        return userList;
    }

    public LCMessage(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }

    public static LCMessage initMessageWithBody(LCMessageBody body, ChatType chatType,
                                                String toChatUserName) {
        LCMessage message = createSendMessageBody(body.getContentType());
        message.setChatType(chatType);
        message.messageEntity.setTo(toChatUserName);
        message.messageEntity.setJiMaoFlag(false);
        message.addBody(body);
        return message;
    }

    public static LCMessage initMessageWithBodyToRobot(LCMessageBody body, ChatType chatType) {
        LCMessage message = createSendMessageBody(body.getContentType());
        message.setChatType(chatType);
        message.messageEntity.setJiMaoFlag(false);
        message.messageEntity.setSendOrRecv(RECEIVE.value());
        message.messageEntity.setTo("001");
        message.messageEntity.setExtra(((LCTextMessageBody) body).getMessageContent());
        message.addBody(body);
        return message;
    }

    public static LCMessage initMessageWithBody(LCMessageBody body, ChatType chatType,
                                                String toChatUserName, boolean jiMaoFlag) {
        LCMessage message = createSendMessageBody(body.getContentType());
        message.setChatType(chatType);
        message.messageEntity.setTo(toChatUserName);
        message.messageEntity.setJiMaoFlag(jiMaoFlag);
        message.addBody(body);
        return message;
    }

    public static LCMessage initMessageWithBody(LCMessageBody body, ChatType chatType,
                                                List<String> userList) {
        LCMessage message = createSendMessageBody(body.getContentType());
        message.setUserList(userList);
        message.messageEntity.setTo(CommonUtils.getStringFromUserList(userList));
        message.setChatType(chatType);
        message.messageEntity.setJiMaoFlag(false);
        message.addBody(body);
        return message;

    }

    private static LCMessage createSendMessageBody(ContentType contentType) {
        MessageEntity messageDb = new MessageEntity();
        messageDb.setContentType(contentType.value());
        messageDb.setSendOrRecv(SEND.value());
        messageDb.setRead(false);
        messageDb.setFrom(UserInfoSP.getString(LCChatConfig.UserInfo.USERNAME, ""));
        messageDb.setFromNick(UserInfoSP.getString(LCChatConfig.UserInfo.NICK, ""));
        messageDb.setMsgId(CommonUtils.getUUID());
        return new LCMessage(messageDb);
    }


    private void addBody(LCMessageBody messageBody) {
        this.messageBody = messageBody;
    }


    public interface Creator<T> {
        T createFromParcel(Parcel var1);

        T[] newArray(int var1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.messageBody, flags);
        dest.writeParcelable(this.messageEntity, flags);
        dest.writeInt(this.jimao_left_num);
    }

    protected LCMessage(Parcel in) {
        this.messageBody = in.readParcelable(LCMessageBody.class.getClassLoader());
        this.messageEntity = in.readParcelable(MessageEntity.class.getClassLoader());
        this.jimao_left_num = in.readInt();
    }

    public void setJimao_left_num(int jimao_left_num) {
        this.jimao_left_num = jimao_left_num;
    }

    public int getJimao_left_num() {
        return jimao_left_num;
    }


    public static final Parcelable.Creator<LCMessage> CREATOR = new Parcelable.Creator<LCMessage>() {
        @Override
        public LCMessage createFromParcel(Parcel source) {
            return new LCMessage(source);
        }

        @Override
        public LCMessage[] newArray(int size) {
            return new LCMessage[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    @Override
    public String toString() {
        StringBuilder entityString = new StringBuilder();
        if (messageEntity != null) {
            entityString.append("chatType:").append(messageEntity.getChatType()).append(",")
                    .append("to:").append(messageEntity.getTo()).append(",")
                    .append("from:").append(messageEntity.getFrom()).append(",")
                    .append("fromNick:").append(messageEntity.getFromNick()).append(",")
                    .append("msgId:").append(messageEntity.getMsgId()).append(",")
                    .append("guid:").append(messageEntity.getGuid()).append(",")
                    .append("contentType:").append(+messageEntity.getContentType()).append(",")
                    .append("read:").append(messageEntity.getRead()).append(",")
                    .append("conversationId:").append(messageEntity.getConversationId());
        }
        if (messageBody != null) {
            entityString.append(" ;").append(messageBody.toString());
        }
        return entityString.toString();
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/1 user creat
 */
