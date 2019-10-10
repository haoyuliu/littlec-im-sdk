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

import android.os.Parcelable;

import com.littlec.sdk.biz.chat.entity.LCMessage;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc
 * @Date 2016/8/1
 * @Version
 */
public abstract class LCMessageBody implements Parcelable {
    protected LCMessage.ContentType contentType;

    public static LCMessageBody createTxtMessageBody(String content) {
        LCMessageBody messageBody = new LCTextMessageBody(content);
        messageBody.setContentType(LCMessage.ContentType.TXT);
        return messageBody;
    }
    public static LCMessageBody createGwMessageBody(String content,String notification) {
        LCMessageBody messageBody = new LCGwMessageBody(content,notification);
        return messageBody;
    }

    public static LCMessageBody createLocationMessageBody(double latitude, double longtitude,
                                                          String locationAddress,
                                                          String screenShotFilePath,
                                                          String location_desc) {
        LCMessageBody messageBody = new LCLocationMessageBody(latitude, longtitude, locationAddress,
                new File(screenShotFilePath), location_desc);
        messageBody.setContentType(LCMessage.ContentType.LOCATION);
        return messageBody;
    }

    public static LCMessageBody createImageMessageBody(String localPath,boolean isOrigin) {
        LCImageMessageBody imageMessageBody = new LCImageMessageBody(localPath,isOrigin);
        imageMessageBody.setContentType(LCMessage.ContentType.IMAGE);
        return imageMessageBody;
    }

    public static LCMessageBody createAudioMessageBody(String localPath,int duration) {
        LCMessageBody messageBody = new LCAudioMessageBody(localPath,duration);
        messageBody.setContentType(LCMessage.ContentType.AUDIO);
        return messageBody;
    }

    public static LCMessageBody createVideoMessageBody(String localPath) {
        LCVideoMessageBody videoMessageBody = new LCVideoMessageBody(localPath);
        videoMessageBody.setContentType(LCMessage.ContentType.VIDEO);
        return videoMessageBody;
    }
    public static LCMessageBody createVideoMessageBody(String localPath,int duration) {
        LCVideoMessageBody videoMessageBody = new LCVideoMessageBody(localPath);
        videoMessageBody.setDuration(duration);
        videoMessageBody.setContentType(LCMessage.ContentType.VIDEO);
        return videoMessageBody;
    }

    public static LCMessageBody createFileMessageBody(String localPath) {
        LCFileMessageBody fileMessageBody = new LCFileMessageBody(localPath);
        fileMessageBody.setContentType(LCMessage.ContentType.FILE);
        return fileMessageBody;
    }

    public static LCMessageBody createAtMessageBody(String text, List<String> userList) {
        LCATMessageBody atMessageBody = new LCATMessageBody(text, userList);
        atMessageBody.setContentType(LCMessage.ContentType.AT);
        return atMessageBody;
    }

    public static LCMessageBody createAtAllMessageBody(String text) {
        LCATMessageBody atMessageBody = new LCATMessageBody(text, true);
        atMessageBody.setContentType(LCMessage.ContentType.AT);
        return atMessageBody;
    }

    public static LCMessageBody createReadReceiptMessageBody(String msgId) {
        LCReadReceiptMessageBody receiptMessageBody = new LCReadReceiptMessageBody(msgId);
        receiptMessageBody.setContentType(LCMessage.ContentType.READ_RECEIPT);
        return receiptMessageBody;
    }

    public static LCMessageBody createRetractMessageBody(String msgId) {
        LCReadReceiptMessageBody receiptMessageBody = new LCReadReceiptMessageBody(msgId);
        receiptMessageBody.setContentType(LCMessage.ContentType.RETRACT);
        return receiptMessageBody;
    }

    public static LCMessageBody createCustomMessage(Map<String, String> map, String notification) {
        if (map == null) {
            throw new NullPointerException();
        }
        LCCustomMessageBody customMessageBody = new LCCustomMessageBody(map, notification);
        customMessageBody.setContentType(LCMessage.ContentType.CUSTOM);
        return customMessageBody;
    }

    public static LCMessageBody createCustomNoApnsMessage(Map<String, String> map, String notification) {
        if (map == null) {
            throw new NullPointerException();
        }
        LCCustomNoApnsMessageBody customMessageBody = new LCCustomNoApnsMessageBody(map, notification);
        customMessageBody.setContentType(LCMessage.ContentType.CUSTOM_NO_APNS);
        return customMessageBody;
    }
    public static LCMessageBody createFamilyCustomMessage(Map<String, String> map, String notification) {
        if (map == null) {
            throw new NullPointerException();
        }
        LCFamilyCustomMessageBody customMessageBody = new LCFamilyCustomMessageBody(map, notification);
        customMessageBody.setContentType(LCMessage.ContentType.CUSTOM_FAMILY);
        return customMessageBody;
    }

    public LCMessage.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(LCMessage.ContentType contentType) {
        this.contentType = contentType;
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
