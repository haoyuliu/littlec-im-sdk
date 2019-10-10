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
package com.littlec.sdk.biz.chat.entity.body;

import android.os.Parcel;
import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.LCMessage;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc 回执消息实体
 * @Date 2016/8/12
 * @Version
 */
public class LCReadReceiptMessageBody extends LCMessageBody {
    private String receipt_msgId;
    public LCReadReceiptMessageBody(){

    }
    public LCReadReceiptMessageBody(String msgId) {
        this.receipt_msgId= msgId;
    }

    public void setReceipt_msgId(String receipt_guid) {
        this.receipt_msgId = receipt_guid;
    }

    public String getReceipt_msgId() {
        return receipt_msgId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(super.contentType == null ? -1 : this.contentType.value());
        dest.writeString(this.receipt_msgId);
    }

    protected LCReadReceiptMessageBody(Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.receipt_msgId = in.readString();
    }

    public static final Parcelable.Creator<LCReadReceiptMessageBody> CREATOR = new Parcelable.Creator<LCReadReceiptMessageBody>() {
        @Override
        public LCReadReceiptMessageBody createFromParcel(Parcel source) {
            return new LCReadReceiptMessageBody(source);
        }

        @Override
        public LCReadReceiptMessageBody[] newArray(int size) {
            return new LCReadReceiptMessageBody[size];
        }
    };

    @Override
    public String toString() {
        return "receipt_msgId=" + receipt_msgId;
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
