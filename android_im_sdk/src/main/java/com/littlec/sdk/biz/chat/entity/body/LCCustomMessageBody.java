/* Project: android_im_sdk
 * 
 * File Created at 2016/9/27
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc 自定义消息体
 * @Date 2016/9/27
 * @Version
 */
public class LCCustomMessageBody extends LCMessageBody implements Parcelable ,Serializable{
    private static final long serialVersionUID =-2227837795673906488L;

    private Map<String, String> map;

    private String notification="";

    private JSONObject jsonObject=new JSONObject();

    public LCCustomMessageBody(Map<String, String> map,String notification) {
        this.map = map;
        this.notification=notification;
        String key;
        for(Map.Entry<String,String> entry:map.entrySet()){
            key=entry.getKey();
            try {
                jsonObject.put(key,map.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public String getContent() {
        if (jsonObject != null)
        {
            return jsonObject.toString();
        }else{
            return null;
        }
    }

    public String getNotification(){
        return notification;
    }

    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(map);
    }

    protected LCCustomMessageBody(Parcel in) {
        this.map = in.readHashMap(Map.class.getClassLoader());
    }

    public static final Creator<LCCustomMessageBody> CREATOR = new Creator<LCCustomMessageBody>() {
        @Override
        public LCCustomMessageBody createFromParcel(Parcel source) {
            return new LCCustomMessageBody(source);
        }

        @Override
        public LCCustomMessageBody[] newArray(int size) {
            return new LCCustomMessageBody[size];
        }
    };
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/27 user creat
 */
