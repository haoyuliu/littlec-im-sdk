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
 * @Desc 位置消息体
 * @Date 2016/8/1
 * @Version
 */
public class LCLocationMessageBody extends LCFileMessageBody {
    private String address;
    private double latitude;
    private double longitude;
    private int width;
    private int height;
    private String location_desc;

    public LCLocationMessageBody(double latitude, double longitude, String address, File file,String location_desc) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location_desc=location_desc;
        if (file != null) {
            this.localPath = file.getAbsolutePath();
            this.fileName = file.getName();
            this.fileLength = file.length();
        }
    }

    public LCLocationMessageBody() {

    }

    public String getLocation_desc() {
        return location_desc;
    }

    public void setLocation_desc(String location_desc) {
        this.location_desc = location_desc;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address){this.address=address;}


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

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.location_desc);
    }

    protected LCLocationMessageBody(Parcel in) {
        super(in);
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.width = in.readInt();
        this.height = in.readInt();
        this.location_desc = in.readString();
    }

    public static final Parcelable.Creator<LCLocationMessageBody> CREATOR = new Parcelable.Creator<LCLocationMessageBody>() {
        @Override
        public LCLocationMessageBody createFromParcel(Parcel source) {
            return new LCLocationMessageBody(source);
        }

        @Override
        public LCLocationMessageBody[] newArray(int size) {
            return new LCLocationMessageBody[size];
        }
    };

    @Override
    public String toString() {
        return super.toString() + ",address=" + address + ",latitude=" + latitude + ",longitude="
                + longitude + ",width=" + width + ",height=" + height + ",location_desc="
                + location_desc;
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
