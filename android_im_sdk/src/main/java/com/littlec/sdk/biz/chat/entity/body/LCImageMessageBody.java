/* Project: android_im_sdk
 * 
 * File Created at 2016/8/4
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

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc image message body
 * @Date 2016/8/4
 * @Version
 */
public class LCImageMessageBody extends LCFileMessageBody {
    private boolean isOrigin = false;
    private String middleUri;
    private String smallUri;
    private String largeUri;
    private int small_width;
    private int small_height;
    private int middle_width;
    private int middle_height;
    private String thumbPath;

    public void setisOrigin(boolean isOrigin) {
        this.isOrigin = isOrigin;
    }

    public boolean getisOrigin() {
        return isOrigin;
    }

    public String getMiddleUri() {
        return middleUri;
    }

    public void setMiddleUri(String middleUri) {
        this.middleUri = middleUri;
    }

    public String getSmallUri() {
        return smallUri;
    }

    public void setSmallUri(String smallUri) {
        this.smallUri = smallUri;
    }

    public String getLargeUri() {
        return largeUri;
    }

    public void setLargeUri(String largeUri) {
        this.largeUri = largeUri;
    }

    public int getSmall_width() {
        return small_width;
    }

    public void setSmall_width(int small_width) {
        this.small_width=small_width;
    }
    public int getMiddle_width() {
        return middle_width;
    }

    public void setMiddle_width(int middle_width) {
        this.middle_width=middle_width;
    }

    public int getSmall_height() {
        return small_height;
    }

    public void setSmall_height(int small_height) {
        this.small_height = small_height;
    }
    public int getMiddle_height() {
        return middle_height;
    }

    public void setMiddle_height(int middle_height) {
        this.middle_height = middle_height;
    }
    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
    public LCImageMessageBody(String localPath,boolean isOrigin) {
        this.localPath = localPath;
        this.isOrigin=isOrigin;

    }

    public LCImageMessageBody() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.isOrigin ? (byte) 1 : (byte) 0);
        dest.writeString(this.middleUri);
        dest.writeString(this.smallUri);
        dest.writeString(this.largeUri);
        dest.writeInt(this.small_width);
        dest.writeInt(this.small_height);
        dest.writeInt(this.middle_width);
        dest.writeInt(this.middle_height);

    }

    protected LCImageMessageBody(Parcel in) {
        super(in);
        this.isOrigin = in.readByte() != 0;
        this.middleUri = in.readString();
        this.smallUri = in.readString();
        this.largeUri = in.readString();
        this.small_width = in.readInt();
        this.small_height = in.readInt();
        this.middle_width = in.readInt();
        this.middle_height = in.readInt();
    }

    public static final Creator<LCImageMessageBody> CREATOR = new Creator<LCImageMessageBody>() {
        @Override
        public LCImageMessageBody createFromParcel(Parcel source) {
            return new LCImageMessageBody(source);
        }

        @Override
        public LCImageMessageBody[] newArray(int size) {
            return new LCImageMessageBody[size];
        }
    };

    @Override
    public String toString() {
        return super.toString() + ",middleUri=" + middleUri + ",smallUri=" + smallUri + ",largeUri="
                + largeUri + ",small_width=" + small_width + ",small_height=" + small_height + ",isOrigin=" + isOrigin;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/4 user creat
 */
