/* Project: android_im_sdk
 * 
 * File Created at 2016/11/16
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
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/11/16
 * @Version
 */

public class LCGwMessageBody extends LCMessageBody {
    private String messageContent;
    private String notification;
    public static final Parcelable.Creator<LCGwMessageBody> CREATOR = new Parcelable.Creator<LCGwMessageBody>() {
        public LCGwMessageBody  createFromParcel(Parcel in) {
            return new LCGwMessageBody (in);
        }

        public LCGwMessageBody [] newArray(int size) {
            return new LCGwMessageBody [size];
        }
    };

    private LCGwMessageBody (Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.messageContent = in.readString();
        this.notification=in.readString();
    }

    public LCGwMessageBody (String content,String notification) {
        this.messageContent = content;
        this.notification=notification;
    }

    public String getMessageContent() {
        return messageContent;
    }
    public String getNotification(){
        return notification;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(super.contentType == null ? -1 : this.contentType.value());
        parcel.writeString(this.messageContent);
        parcel.writeString(this.notification);
    }

    @Override
    public String toString() {
        return "messageContent=" + messageContent+"notification"+notification;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/11/16 zhangguoqiong creat
 */
