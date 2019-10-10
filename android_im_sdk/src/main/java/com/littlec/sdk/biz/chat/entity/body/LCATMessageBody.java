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

import java.util.List;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc @消息实体
 * @Date 2016/8/12
 * @Version
 */
public class LCATMessageBody extends LCMessageBody  {
    private List<String> at_members;
    private String text="";
    private boolean atAll=false;

    public LCATMessageBody(String text,boolean atAll){
        if(text==null){
            text="";
        }
        this.text=text;
        this.atAll=atAll;
    }

    public LCATMessageBody(String text, List<String> at_members) {
        if(text==null){
            text="";
        }
        this.text = text;
        this.at_members = at_members;
    }

    public List<String> getAt_members() {
        return at_members;
    }

    public void setAt_members(List<String> at_members) {
        this.at_members = at_members;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAtAll(boolean atAll){
        this.atAll=atAll;
    }

    public boolean getAtAll(){
        return atAll;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(super.contentType == null ? -1 : this.contentType.value());
        dest.writeStringList(this.at_members);
        dest.writeString(this.text);
    }

    public LCATMessageBody() {
    }

    protected LCATMessageBody(Parcel in) {
        int tmpContentType = in.readInt();
        super.contentType = tmpContentType == -1 ? null : LCMessage.ContentType.values()[tmpContentType];
        this.at_members = in.createStringArrayList();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<LCATMessageBody> CREATOR = new Parcelable.Creator<LCATMessageBody>() {
        @Override
        public LCATMessageBody createFromParcel(Parcel source) {
            return new LCATMessageBody(source);
        }

        @Override
        public LCATMessageBody[] newArray(int size) {
            return new LCATMessageBody[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("text=" + text);
        for (String str : at_members) {
            sb.append(",at_member=" + str);
        }
        return sb.toString();
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
