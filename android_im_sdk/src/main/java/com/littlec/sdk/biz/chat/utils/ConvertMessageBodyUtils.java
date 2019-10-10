/* Project: android_im_sdk
 * 
 * File Created at 2016/8/23
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.utils;

import android.text.TextUtils;

import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCGwMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCReadReceiptMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCVideoMessageBody;
import com.littlec.sdk.database.entity.MediaEntity;
import com.fingo.littlec.proto.css.Msg;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/23
 * @Version
 */

public class ConvertMessageBodyUtils {
    public static MediaEntity messageBodyToFileMessageExtention(LCMessageBody messageBody) {
        MediaEntity fileMessageExtention;
        if(messageBody instanceof LCTextMessageBody){
            fileMessageExtention=new MediaEntity();
            fileMessageExtention.setContent(((LCTextMessageBody)messageBody).getMessageContent());
            return fileMessageExtention;
        }
        else if (messageBody instanceof LCImageMessageBody) {
            fileMessageExtention = new MediaEntity();
            fileMessageExtention.setFileName(((LCImageMessageBody) messageBody).getFileName());
            fileMessageExtention.setFileLength(((LCImageMessageBody) messageBody).getFileLength());
            fileMessageExtention
                    .setOriginalLink(((LCImageMessageBody) messageBody).getOriginalUri());
            fileMessageExtention.setSmallLink(((LCImageMessageBody) messageBody).getSmallUri());
            fileMessageExtention.setMiddleLink(((LCImageMessageBody) messageBody).getMiddleUri());
            fileMessageExtention.setLargeLink(((LCImageMessageBody) messageBody).getLargeUri());
            fileMessageExtention.setIsOrigin(((LCImageMessageBody) messageBody).getisOrigin());
            return fileMessageExtention;
        }
       else if (messageBody instanceof LCAudioMessageBody) {
            fileMessageExtention = new MediaEntity();
            fileMessageExtention.setFileName(((LCAudioMessageBody) messageBody).getFileName());
            fileMessageExtention.setFileLength(((LCAudioMessageBody) messageBody).getFileLength());
            fileMessageExtention
                    .setOriginalLink(((LCAudioMessageBody) messageBody).getOriginalUri());
            fileMessageExtention.setDuration(((LCAudioMessageBody) messageBody).getDuration());
            return fileMessageExtention;
        }
       else if (messageBody instanceof LCVideoMessageBody) {
            fileMessageExtention = new MediaEntity();
            fileMessageExtention.setFileName(((LCVideoMessageBody) messageBody).getFileName());
            fileMessageExtention.setFileLength(((LCVideoMessageBody) messageBody).getFileLength());
            fileMessageExtention
                    .setOriginalLink(((LCVideoMessageBody) messageBody).getOriginalUri());
            fileMessageExtention
                    .setSmallLink(((LCVideoMessageBody) messageBody).getThumbnailUrl());
            fileMessageExtention.setDuration(((LCVideoMessageBody) messageBody).getDuration());
            return fileMessageExtention;
        }

        else if (messageBody instanceof LCLocationMessageBody) {
            fileMessageExtention = new MediaEntity();
            fileMessageExtention.setFileName(((LCLocationMessageBody) messageBody).getFileName());
            fileMessageExtention
                    .setFileLength(((LCLocationMessageBody) messageBody).getFileLength());
            fileMessageExtention.setLatitude(((LCLocationMessageBody) messageBody).getLatitude());
            fileMessageExtention.setLongitude(((LCLocationMessageBody) messageBody).getLongitude());
            fileMessageExtention.setAddress(((LCLocationMessageBody) messageBody).getAddress());
            fileMessageExtention
                    .setLocationDes(((LCLocationMessageBody) messageBody).getLocation_desc());
            fileMessageExtention
                    .setOriginalLink(((LCLocationMessageBody) messageBody).getOriginalUri());
            return fileMessageExtention;
        }
        else if(messageBody instanceof LCFileMessageBody){
        }
        else if(messageBody instanceof LCATMessageBody){

        }else if(messageBody instanceof LCReadReceiptMessageBody){

        }else if(messageBody instanceof LCCustomMessageBody){

        }else if(messageBody instanceof LCGwMessageBody){
            fileMessageExtention=new MediaEntity();
            fileMessageExtention.setContent(((LCGwMessageBody)messageBody).getMessageContent());
            fileMessageExtention.setData1(((LCGwMessageBody)messageBody).getNotification());
            return fileMessageExtention;
        }
        return null;

    }
    public static LCMessageBody fileExtentionToMessageBody(String msgId,int contentType,MediaEntity entity){
        if(entity==null){
            return null;
        }
        switch(contentType){
            case Msg.EMsgContentType.TEXT_VALUE:
                LCTextMessageBody   textBody=new LCTextMessageBody(entity.getContent());
                textBody.setContentType(LCMessage.ContentType.TXT);
                return textBody;
            case Msg.EMsgContentType.IMAGE_VALUE:
                LCImageMessageBody  imageMessageBody=new LCImageMessageBody();
                imageMessageBody.setFileName(entity.getFileName());
                imageMessageBody.setFileLength(entity.getFileLength());
                imageMessageBody.setLocalPath(entity.getContent());
                imageMessageBody.setOriginalUri(entity.getOriginalLink());
                imageMessageBody.setSmallUri(entity.getSmallLink());
                imageMessageBody.setMiddleUri(entity.getMiddleLink());
                imageMessageBody.setLargeUri(entity.getLargeLink());
                imageMessageBody.setThumbPath(entity.getThumbPath());
                imageMessageBody.setContentType(LCMessage.ContentType.IMAGE);
                imageMessageBody.setisOrigin(entity.getIsOrigin());
               return imageMessageBody;
            case Msg.EMsgContentType.VIDEO_VALUE:
                LCVideoMessageBody videoMessageBody=new LCVideoMessageBody();
                videoMessageBody.setFileName(entity.getFileName());
                videoMessageBody.setFileLength(entity.getFileLength());
                videoMessageBody.setLocalPath(entity.getContent());
                videoMessageBody.setOriginalUri(entity.getOriginalLink());
                videoMessageBody.setThumbnailUrl(entity.getSmallLink());
                videoMessageBody.setDuration(entity.getDuration());
                videoMessageBody.setThumbPath(entity.getThumbPath());
                videoMessageBody.setContentType(LCMessage.ContentType.VIDEO);
                return videoMessageBody;
            case Msg.EMsgContentType.AUDIO_VALUE:
                LCAudioMessageBody audioMessageBody=new LCAudioMessageBody();
                audioMessageBody.setFileName(entity.getFileName());
                audioMessageBody.setFileLength(entity.getFileLength());
                audioMessageBody.setLocalPath(entity.getContent());
                audioMessageBody.setOriginalUri(entity.getOriginalLink());
                audioMessageBody.setDuration(entity.getDuration());
                audioMessageBody.setContentType(LCMessage.ContentType.AUDIO);
                return audioMessageBody;
            case Msg.EMsgContentType.LOCATION_VALUE:
                LCLocationMessageBody locationMessageBody=new LCLocationMessageBody();
                locationMessageBody.setFileName(entity.getFileName());
                locationMessageBody.setFileLength(entity.getFileLength());
                locationMessageBody.setLocalPath(entity.getContent());
                locationMessageBody.setOriginalUri(entity.getOriginalLink());
                locationMessageBody.setLocation_desc(entity.getLocationDes());
                locationMessageBody.setAddress(entity.getAddress());
                locationMessageBody.setLatitude(entity.getLatitude());
                locationMessageBody.setLongitude(entity.getLongitude());
                locationMessageBody.setContentType(LCMessage.ContentType.LOCATION);
               return locationMessageBody;
            case Msg.EMsgContentType.AT_VALUE:
                break;
            case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                String guid=entity.getContent();
                LCReadReceiptMessageBody receiptMessageBody=null;
                if(!TextUtils.isEmpty(msgId)) {
                    //这里存储的是 msg_id
                     receiptMessageBody = new LCReadReceiptMessageBody(msgId);
                    receiptMessageBody.setContentType(LCMessage.ContentType.READ_RECEIPT);
                }
               return receiptMessageBody;
            case Msg.EMsgContentType.FILE_VALUE+1:
                break;
            case Msg.EMsgContentType.CUSTOM_VALUE+2:
                LCGwMessageBody lcGwMessageBody=new LCGwMessageBody(entity.getContent(),entity.getData1());
                lcGwMessageBody.setContentType(LCMessage.ContentType.GwMsg);
                return lcGwMessageBody;
            case Msg.EMsgContentType.CUSTOM_VALUE:
                LCTextMessageBody   textBody1=new LCTextMessageBody(entity.getContent());
                textBody1.setContentType(LCMessage.ContentType.TXT);
                return textBody1;
            default:
                break;

        }
    return null;
    }




}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/23 zhangguoqiong creat
 */
