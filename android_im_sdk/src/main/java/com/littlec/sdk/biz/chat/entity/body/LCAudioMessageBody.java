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

import java.io.File;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc audio message body
 * @Date 2016/8/1
 * @Version
 */
public class LCAudioMessageBody extends LCFileMessageBody {
    private int duration = -1;

    public LCAudioMessageBody() {

    }

    public LCAudioMessageBody(String localPath) {
        this.localPath = localPath;

    }
    public LCAudioMessageBody(String localPath,int duration) {
        this.localPath = localPath;
        this.duration=duration;

    }

    public LCAudioMessageBody(File paramFile, int duration) {
        if (paramFile != null) {
            this.localPath = paramFile.getAbsolutePath();
            this.fileLength = paramFile.length();
            this.fileName = paramFile.getName();
        }
        this.duration = duration;
    }

    public LCAudioMessageBody(String fileName, String localPath, int duration) {
        this.fileName = fileName;
        this.localPath = originalUri;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.duration);
    }

    protected LCAudioMessageBody(Parcel in) {
        super(in);
        this.duration = in.readInt();
    }

    public static final Parcelable.Creator<LCAudioMessageBody> CREATOR = new Parcelable.Creator<LCAudioMessageBody>() {
        @Override
        public LCAudioMessageBody createFromParcel(Parcel source) {
            return new LCAudioMessageBody(source);
        }

        @Override
        public LCAudioMessageBody[] newArray(int size) {
            return new LCAudioMessageBody[size];
        }
    };

    @Override
    public String toString() {
        return super.toString() + ",duration=" + duration;
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
