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
package com.littlec.sdk.biz.chat.entity.body;

import android.os.Parcel;
import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.LCMessage;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc 文本消息消息体
 * @Date 2016/8/1
 * @Version
 */
public class LCTextMessageBody extends LCMessageBody {
    private String messageContent;
    public static final Parcelable.Creator<LCTextMessageBody> CREATOR = new Parcelable.Creator<LCTextMessageBody>() {
        public LCTextMessageBody createFromParcel(Parcel in) {
            return new LCTextMessageBody(in);
        }

        public LCTextMessageBody[] newArray(int size) {
            return new LCTextMessageBody[size];
        }
    };

    private LCTextMessageBody(Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.messageContent = in.readString();
    }

    public LCTextMessageBody(String message) {
        this.messageContent = message;
    }

    public String getMessageContent() {
        return messageContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(super.contentType == null ? -1 : this.contentType.value());
        parcel.writeString(this.messageContent);
    }

    @Override
    public String toString() {
        return "messageContent=" + messageContent;
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
