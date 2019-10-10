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
 * @Desc file message body
 * @Date 2016/8/1
 * @Version
 */
public class LCFileMessageBody extends LCMessageBody implements Parcelable {
    protected String fileName;
    protected long fileLength;
    protected String localPath;
    protected String originalUri;

    public LCFileMessageBody() {
    }
    public LCFileMessageBody(String localPath){
        this.localPath=localPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;

    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;

    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;

    }

    public String getContent() {
        return getLocalPath();
    }

    @Override
    public String toString() {
        return "fileName=" + fileName + ",fileLength=" + fileLength + ",localPath=" + localPath
                + ",originalUri=" + originalUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(super.contentType == null ? -1 : this.contentType.value());
        dest.writeString(this.fileName);
        dest.writeLong(this.fileLength);
        dest.writeString(this.localPath);
        dest.writeString(this.originalUri);
    }

    protected LCFileMessageBody(Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.fileName = in.readString();
        this.fileLength = in.readLong();
        this.localPath = in.readString();
        this.originalUri = in.readString();
    }

    public static final Parcelable.Creator<LCFileMessageBody> CREATOR = new Parcelable.Creator<LCFileMessageBody>() {
        @Override
        public LCFileMessageBody createFromParcel(Parcel source) {
            return new LCFileMessageBody(source);
        }

        @Override
        public LCFileMessageBody[] newArray(int size) {
            return new LCFileMessageBody[size];
        }
    };
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/1 user creat
 */
