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
 * @Desc 视频消息消息体
 * @Date 2016/8/1
 * @Version
 */
public class LCVideoMessageBody extends LCFileMessageBody {
    private int duration;
    private String thumbnailUrl;
    private int width;
    private int height;
    private String thumbPath;

    public LCVideoMessageBody() {
    }

    public LCVideoMessageBody(File paramFile, int duration) {
        if (paramFile != null) {
            this.localPath = paramFile.getAbsolutePath();
            this.fileName = paramFile.getName();
            this.fileLength = paramFile.length();
        }
        this.duration = duration;
    }

    public LCVideoMessageBody(String localPath) {
        this.localPath = localPath;
    }

    //    private String localThumbnailPath;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    //    public String getLocalThumbnailPath() {
    //        return localThumbnailPath;
    //    }
    //
    //    public void setLocalThumbnailPath(String localThumbnailPath) {
    //        this.localThumbnailPath = localThumbnailPath;
    //    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.duration);
        dest.writeString(this.thumbnailUrl);
        //        dest.writeString(this.localThumbnailPath);
    }

    protected LCVideoMessageBody(Parcel in) {
        super(in);
        this.duration = in.readInt();
        this.thumbnailUrl = in.readString();
        //        this.localThumbnailPath = in.readString();
    }

    public static final Parcelable.Creator<LCVideoMessageBody> CREATOR = new Parcelable.Creator<LCVideoMessageBody>() {
        @Override
        public LCVideoMessageBody createFromParcel(Parcel source) {
            return new LCVideoMessageBody(source);
        }

        @Override
        public LCVideoMessageBody[] newArray(int size) {
            return new LCVideoMessageBody[size];
        }
    };

    @Override
    public String toString() {
        return super.toString() + ",duration=" + duration + ",thumbnailUrl" + thumbnailUrl
                + ",width=" + width + ",height=" + height;
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
