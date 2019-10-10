/* Project: android_im_sdk
 * 
 * File Created at 2016/10/11
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

import android.os.Parcel;
import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.database.entity.ConversationEntity;

import static com.littlec.sdk.biz.chat.entity.LCMessage.ContentType.forNumber;

/**
 * @Type com.littlec.sdk.common
 * @User user
 * @Desc 会话实体类
 * @Date 2016/10/11
 * @Version
 */
public class LCConversation implements Parcelable {
    private ConversationEntity conversationEntity;
    public  LCConversation(ConversationEntity entity){
        this.conversationEntity=entity;
    }
    public ConversationEntity getConversationEntity(){
        if(conversationEntity==null){
            conversationEntity=new ConversationEntity();
        }
        return conversationEntity;
    }

    public String getConversationId(){
        if(conversationEntity==null){
            return null;
        }
        return conversationEntity.get_recipientId();
    }
    public int getChatType(){
        if(conversationEntity==null){
            return -1;
        }
        return conversationEntity.getChattype();
    }
    public int getUnreadMsgCount(){
        if(conversationEntity==null){
            return 0;
        }
        return conversationEntity.getUnreadCount();
    }
    public int getAllMsgCount(){
        if(conversationEntity==null){
            return 0;
        }
        return conversationEntity.getTotalCount();
    }
    public long getTime(){
        if(conversationEntity==null){
            return 0;
        }
        return conversationEntity.getDate();
    }

    public LCMessage.ContentType getMsgContentType(){
        return forNumber(conversationEntity.getMsgContentType());
    }

    public String getMsgContent(){
        return conversationEntity.getMsgContent();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.conversationEntity, flags);
    }

    protected LCConversation(Parcel in) {
        this.conversationEntity = in.readParcelable(ConversationEntity.class.getClassLoader());
    }

    public static final Creator<LCConversation> CREATOR = new Creator<LCConversation>() {
        @Override
        public LCConversation createFromParcel(Parcel source) {
            return new LCConversation(source);
        }

        @Override
        public LCConversation[] newArray(int size) {
            return new LCConversation[size];
        }
    };
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/11 user creat
 */