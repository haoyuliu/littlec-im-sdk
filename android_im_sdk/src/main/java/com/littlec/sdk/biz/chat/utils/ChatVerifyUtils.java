/* Project: android_im_sdk
 * 
 * File Created at 2016/8/2
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

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCReadReceiptMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCVideoMessageBody;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.ImageUtils;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCPathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Type com.littlec.sdk.utils
 * @User user
 * @Desc 所有消息的校验类
 * @Date 2016/8/2
 * @Version
 */
public class ChatVerifyUtils extends AccountVerifyUtils {
    private static LCLogger logger = LCLogger.getLogger("ChatVerifyUtils");

    /** 
     * @Title: verifyChatParam <br>
     * @Description: 负责整个聊天的参数校验 <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/8/25 9:11
     */
    public static boolean verifyChatParam(LCMessage message) {
        if (LCClient.getInstance().messageManagerInner().getCallBack() == null) {
            throw new NullPointerException("Parameter cannot be null");
        }
        if (!checkChatType(message)) {
            return false;
        }
        message.LCMessageEntity().setCreateTime(CommonUtils.getCurrentTime());
        message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_PROGRESS.value());
        switch (message.LCMessageEntity().getContentType()) {
            case Msg.EMsgContentType.TEXT_VALUE:
                if (!checkTextBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.IMAGE_VALUE:
                if (!checkImageBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.VIDEO_VALUE:
                if (!checkVideoBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.AUDIO_VALUE:
                if (!checkAudioBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.LOCATION_VALUE:
                if (!checkLocationBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.FILE_VALUE:
                if (!checkFileBody(message))
                    return false;
                break;
            case Msg.EMsgContentType.AT_VALUE:
                if (!checkAtBody(message))
                    return false;
                break;

            case Msg.EMsgContentType.RETRACT_VALUE:
            case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                if (!checkRetractAndReadReceiptBody(message)) {
                    return false;
                }
                break;
            case Msg.EMsgContentType.CUSTOM_VALUE:
            case Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE:
                if (!checkCustomBody(message)) {
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }

    /**
     * @Title: checkChatType <br>
     * @Description: 检查会话类型 <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/8/25 9:11
     */
    private static boolean checkChatType(LCMessage message) {
        switch (message.LCMessageEntity().getChatType()) {
            case Msg.EMsgType.CHAT_MSG_VALUE:
            case Msg.EMsgType.PRIVATE_MSG_VALUE:
                if (!checkUserName(message.LCMessageEntity().getTo())) {
                    DispatchController.getInstance().onError(message,
                            LCError.ACCOUNT_USERNAME_ILLEGAL.getValue(),
                            LCError.ACCOUNT_USERNAME_ILLEGAL.getDesc());
                    return false;
                }
                break;
            case Msg.EMsgType.GROUP_MSG_VALUE:
                if (TextUtils.isEmpty(message.LCMessageEntity().getTo())) {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_CHAT_GROUP_ID_ILLEGAL.getValue(),
                            LCError.MESSAGE_CHAT_GROUP_ID_ILLEGAL.getDesc());
                    return false;
                }
                break;
            case Msg.EMsgType.MULTI_MSG_VALUE:
                List<String> userList = message.getUserList();
                if (userList == null || userList.size() < 1) {
                    String toUserName = message.LCMessageEntity().getTo();
                    if (!TextUtils.isEmpty(toUserName)) {
                        userList = new ArrayList<>();
                        userList.add(toUserName);
                        message.setUserList(userList);
                        return true;
                    }
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_MULTI_TARGET_USERNAME_NULL.getValue(),
                            LCError.MESSAGE_MULTI_TARGET_USERNAME_NULL.getDesc());
                    return false;
                }
                if (userList.size() > 200) {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_MAX_NUM_LIMIT.getValue(),
                            LCError.MESSAGE_MAX_NUM_LIMIT.getDesc());
                    return false;
                }
                for (String userName : userList) {
                    if (!checkUserName(userName)) {
                        DispatchController.getInstance().onError(message,
                                LCError.MESSAGE_MULTI_TARGET_USERNAME_NULL.getValue(),
                                LCError.MESSAGE_MULTI_TARGET_USERNAME_NULL.getDesc());
                        return false;
                    }
                }
                break;
            default:
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_CHAT_TYPE_WRONG.getValue(),
                        LCError.MESSAGE_CHAT_TYPE_WRONG.getDesc());
                return false;
        }
        return true;

    }

    private static boolean checkFileformat(int fileMode, String filePath, LCMessage message) {
//        filePath = filePath.toLowerCase();
        File file = new File(filePath);
        if (!file.exists()) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_FILE_PATH_EMPTY.getValue(),
                    LCError.MESSAGE_FILE_PATH_EMPTY.getDesc());
            logger.e(LCError.MESSAGE_FILE_PATH_EMPTY);
            return false;
        }

        if (file.length() <= 0) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_FILE_TOO_SMALL.getValue(),
                    LCError.MESSAGE_FILE_TOO_SMALL.getDesc());
            logger.e(LCError.MESSAGE_FILE_TOO_SMALL);
            return false;
        }
//        if (file.length() >= 15 * 1024 * 1024) {
//            DispatchController.getInstance().onError(message,
//                    LCError.MESSAGE_FILE_TOO_LARGE.getValue(),
//                    LCError.MESSAGE_FILE_TOO_LARGE.getDesc());
//            logger.e(LCError.MESSAGE_FILE_TOO_LARGE);
//            return false;
//        }
        filePath=filePath.toLowerCase();
        switch (fileMode) {
            case Msg.EMsgContentType.IMAGE_VALUE:
                if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")
                        || filePath.endsWith(".png") || filePath.endsWith(".bmp")
                        || filePath.endsWith(".gif")) {
                    return true;
                } else {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getValue(),
                            LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getDesc());
                    return false;
                }
            case Msg.EMsgContentType.LOCATION_VALUE:
                if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")
                        || filePath.endsWith(".png") || filePath.endsWith(".bmp")) {
                    return true;
                } else {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getValue(),
                            LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getDesc());
                    return false;
                }

            case Msg.EMsgContentType.AUDIO_VALUE:
                if (filePath.endsWith(".mp3") || filePath.endsWith(".amr")) {
                    return true;
                } else {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_FILE_AUDIO_TYPE_ILLEGAL.getValue(),
                            LCError.MESSAGE_FILE_AUDIO_TYPE_ILLEGAL.getDesc());
                    return false;
                }

            case Msg.EMsgContentType.VIDEO_VALUE:
                if (filePath.endsWith(".rmvb") || filePath.endsWith(".avi")
                        || filePath.endsWith(".rm") || filePath.endsWith(".mpg")
                        || filePath.endsWith(".mpeg") || filePath.endsWith(".mov")
                        || filePath.endsWith(".wmv") || filePath.endsWith(".asf")
                        || filePath.endsWith(".mp4") || filePath.endsWith(".3gp")) {
                    return true;

                } else {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_FILE_VIDEO_TYPE_ILLEGAL.getValue(),
                            LCError.MESSAGE_FILE_VIDEO_TYPE_ILLEGAL.getDesc());
                    return false;
                }

            case Msg.EMsgContentType.FILE_VALUE:
                if (filePath.endsWith(".bmp") || filePath.endsWith(".gif")
                        || filePath.endsWith(".jpeg") || filePath.endsWith(".jpeg2000")
                        || filePath.endsWith(".jp2") || filePath.endsWith(".tiff")
                        || filePath.endsWith(".psd") || filePath.endsWith(".png")
                        || filePath.endsWith(".svg") || filePath.endsWith(".pcx")
                        || filePath.endsWith(".dxf") || filePath.endsWith(".wmf")
                        || filePath.endsWith(".emf") || filePath.endsWith(".lic")
                        || filePath.endsWith(".fli") || filePath.endsWith(".flc")
                        || filePath.endsWith(".eps") || filePath.endsWith(".tga")
                        || filePath.endsWith(".jpg") || filePath.endsWith(".wma")
                        || filePath.endsWith(".mp3") || filePath.endsWith(".wav")
                        || filePath.endsWith(".mid") || filePath.endsWith(".mp1")
                        || filePath.endsWith(".mp2") || filePath.endsWith(".amr")
                        || filePath.endsWith(".wma") || filePath.endsWith(".m4a")
                        || filePath.endsWith(".aac") || filePath.endsWith(".rmvb")
                        || filePath.endsWith(".avi") || filePath.endsWith(".rm")
                        || filePath.endsWith(".mpg") || filePath.endsWith(".mpeg")
                        || filePath.endsWith(".mov") || filePath.endsWith(".wmv")
                        || filePath.endsWith(".asf") || filePath.endsWith(".dat")
                        || filePath.endsWith(".asx") || filePath.endsWith(".wvx")
                        || filePath.endsWith(".mpe") || filePath.endsWith(".mpa")
                        || filePath.endsWith(".mp4") || filePath.endsWith(".3gp")
                        || filePath.endsWith(".apk") || filePath.endsWith(".ipa")
                        || filePath.endsWith(".doc") || filePath.endsWith(".docx")
                        || filePath.endsWith(".ppt") || filePath.endsWith(".pptx")
                        || filePath.endsWith(".xls") || filePath.endsWith(".xlsx")
                        || filePath.endsWith(".txt") || filePath.endsWith(".plist")
                        || filePath.endsWith(".vcf")) {
                    return true;
                } else {
                    DispatchController.getInstance().onError(message,
                            LCError.MESSAGE_FILE_TYPE_ILLEGAL.getValue(),
                            LCError.MESSAGE_FILE_TYPE_ILLEGAL.getDesc());

                    return false;
                }

        }

        return false;
    }

    public static boolean isGifImage(String filePath) {
        FileInputStream is = null;
        byte[] b = new byte[10];
        int l = -1;
        try {
            is = new FileInputStream(filePath);
            l = is.read(b);
            is.close();
        } catch (Exception e) {
            return false;
        }
        if (l == 10) {
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];
            //			byte b3 = b[3];
            //			byte b6 = b[6];
            //			byte b7 = b[7];
            //			byte b8 = b[8];
            //			byte b9 = b[9];
            if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
                return true;
            }
            //			else if(b1 == (byte)'P' && b2 == (byte)'N' && b3 == (byte)'G') {
            //				return true;
            //			}
            //			else if(b6 == (byte)'J' && b7 == (byte)'F' && b8 == (byte)'I' && b9 == (byte)'F') {
            //				return true;
            //			}
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean checkTextBody(LCMessage message) {
        LCTextMessageBody messageBody = (LCTextMessageBody) message.LCMessageBody();
        if (messageBody.getMessageContent() == null
                || messageBody.getMessageContent().length() == 0) {
            DispatchController.getInstance().onError(message,
                    LCError.COMMON_CONTENT_NULL.getValue(), LCError.COMMON_CONTENT_NULL.getDesc());
            return false;
        }
        if (messageBody.getMessageContent().length() > 1000) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_TXT_OVER_LENGTH.getValue(),
                    LCError.MESSAGE_TXT_OVER_LENGTH.getDesc());
            return false;
        }
        return true;

    }

    private static boolean checkImageBody(LCMessage message) {
        String ImageFilePath = ((LCImageMessageBody) message.LCMessageBody()).getLocalPath();
        Boolean isOrigin = ((LCImageMessageBody) message.LCMessageBody()).getisOrigin();
        if (TextUtils.isEmpty(ImageFilePath) || ImageFilePath.equals("/")) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_CONTENT_TYPE_NULL.getValue(),
                    LCError.MESSAGE_FILE_PATH_EMPTY.getDesc());
            return false;
        }
        if (ChatVerifyUtils.checkFileformat(Msg.EMsgContentType.IMAGE_VALUE, ImageFilePath,
                message)) {
            File file = new File(ImageFilePath);
            String fileName = file.getName();
            ((LCImageMessageBody) message.LCMessageBody()).setFileName(fileName);
            ((LCImageMessageBody) message.LCMessageBody()).setFileLength(file.length());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(ImageFilePath, options); // 此时返回的bitmap为null
            //	if(options.outWidth > 7680 || options.outHeight > 4320) {// 8K分辨率(7680*4320)，
            //		callback.onError(message, CMChatConstant.ErrorDesc.ERROR_FILE_RESOLUTION_TOO_LARGE);
            //		logger.e(CMChatConstant.ErrorDesc.ERROR_FILE_RESOLUTION_TOO_LARGE);
            //		return;
            //	}
            if (options.outWidth <= 0 || options.outHeight <= 0) {// 可能不是图片
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_FILE_PIC_ILLEGAL.getValue(),
                        LCError.MESSAGE_FILE_PIC_ILLEGAL.getDesc());
                logger.e(LCError.MESSAGE_FILE_PIC_ILLEGAL);
                return false;
            }

            if (!isOrigin) {
                if (!ImageFilePath.toLowerCase().endsWith(".gif")) {
                    if (LCPathUtil.getInstance().getImagePath() != null) {
                        if (options.outWidth > 1024 || options.outHeight > 1024) {
                            ImageUtils.compressPicture(ImageFilePath,
                                    ImageUtils.localPathToSmallPath(ImageFilePath));
                        } else {
                            ((LCImageMessageBody) message.LCMessageBody()).setisOrigin(true);
                        }
                    } else {
                        ((LCImageMessageBody) message.LCMessageBody()).setisOrigin(true);
                    }
                } else {
                    ((LCImageMessageBody) message.LCMessageBody()).setisOrigin(true);
                }
            }

            /*int degree = ImageUtil.readPictureDegree(ImageFilePath);
            boolean shouldRotate = (degree % 180 == 0 ? false : true);// 0，90，180，270，360
            ((LCImageMessageBody) message.LCMessageBody())
                    .setHeight(shouldRotate ? options.outWidth : options.outHeight);
            ((LCImageMessageBody) message.LCMessageBody())
                    .setWidth(shouldRotate ? options.outHeight : options.outWidth);*/
            /*if(ChatVerifyUtils.isGifImage(filePath)) {
                message.LCMessagedb().setContentType(Msg.EMsgType.EMSG_TYPE_GIF_VALUE);
            }*/
        } else {
            return false;
        }
        return true;
    }

    private static boolean checkVideoBody(LCMessage message) {
        String videoFilePath = ((LCFileMessageBody) message.LCMessageBody()).getLocalPath();
        if (ChatVerifyUtils.checkFileformat(Msg.EMsgContentType.VIDEO_VALUE, videoFilePath,
                message)) {
            File file = new File(videoFilePath);
            if(((LCVideoMessageBody)message.LCMessageBody()).getDuration()==0){
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                if (file != null) {
                    mediaPlayer.setDataSource(videoFilePath);
                    mediaPlayer.prepare();
                    ((LCVideoMessageBody) message.LCMessageBody())
                            .setDuration(mediaPlayer.getDuration() / 1000==0?1:mediaPlayer.getDuration() / 1000);
                }
            } catch (Exception e) {
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_VIDEO_ANALUSIS_ERROR.getValue(),
                        LCError.MESSAGE_VIDEO_ANALUSIS_ERROR.getDesc());
                return false;

            }}
            String fileName = file.getName();
            ((LCVideoMessageBody) message.LCMessageBody()).setFileName(fileName);
            ((LCVideoMessageBody) message.LCMessageBody()).setFileLength(file.length());
        } else {

            return false;
        }
        return true;
    }

    private static boolean checkAudioBody(LCMessage message) {
        String audioFilepath = ((LCFileMessageBody) message.LCMessageBody()).getLocalPath();
        if (ChatVerifyUtils.checkFileformat(Msg.EMsgContentType.AUDIO_VALUE, audioFilepath,
                message)) {
            File file = new File(audioFilepath);
            if(((LCAudioMessageBody)message.LCMessageBody()).getDuration()==0){
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                if (file != null) {
                    mediaPlayer.setDataSource(audioFilepath);
                    mediaPlayer.prepare();
                    if (mediaPlayer.getDuration() / 1000 >= 180) {
                        DispatchController.getInstance().onError(message,
                                LCError.MESSAGE_AUDIO_DURATION_ILLEGAL.getValue(),
                                LCError.MESSAGE_AUDIO_DURATION_ILLEGAL.getDesc());
                        return false;
                    }
                    ((LCAudioMessageBody) message.LCMessageBody())
                            .setDuration(mediaPlayer.getDuration() / 1000==0?1:mediaPlayer.getDuration() / 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_AUDIO_ANALUSIS_ERROR.getValue(),
                        LCError.MESSAGE_AUDIO_ANALUSIS_ERROR.getDesc());
                return false;

            }
            }
            String fileName = file.getName();
            ((LCFileMessageBody) message.LCMessageBody()).setFileName(fileName);
            ((LCFileMessageBody) message.LCMessageBody()).setFileLength(file.length());
        } else {
            return false;
        }
        return true;
    }

    private static boolean checkLocationBody(LCMessage message) {
        String locationFilePath = ((LCFileMessageBody) message.LCMessageBody()).getLocalPath();
        LCLocationMessageBody locationMessageBody = ((LCLocationMessageBody) message
                .LCMessageBody());
        if (TextUtils.isEmpty(locationFilePath) || locationFilePath.equals("/")) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_FILE_PATH_EMPTY.getValue(),
                    LCError.MESSAGE_FILE_PATH_EMPTY.getDesc());
            return false;
        }
        String address = locationMessageBody.getAddress();
        String desc = locationMessageBody.getLocation_desc();
        double longtitudu = locationMessageBody.getLongitude();// 经度介于-180~180
        double latitude = locationMessageBody.getLatitude();// 纬度介于-90~90，
        if (longtitudu < -180 || longtitudu > 180 || latitude < -90 || latitude > 90) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_LOCATION_WRONG.getValue(),
                    LCError.MESSAGE_LOCATION_WRONG.getDesc());
            return false;
        }
        if (address == null || address.equals("")) {
            //            ((LCLocationMessageBody) message.LCMessageBody()).setAddress("");
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_LOCATION_ADDRESS_EMPTY.getValue(),
                    LCError.MESSAGE_LOCATION_ADDRESS_EMPTY.getDesc());
            return false;
        }
        if (desc == null) {
            ((LCLocationMessageBody) message.LCMessageBody()).setLocation_desc("");
        }
        if (ChatVerifyUtils.checkFileformat(Msg.EMsgContentType.LOCATION_VALUE, locationFilePath,
                message)) {
            File file = new File(locationFilePath);
            String fileName = file.getName();
            ((LCFileMessageBody) message.LCMessageBody()).setFileName(fileName);
            ((LCFileMessageBody) message.LCMessageBody()).setFileLength(file.length());
        } else {
            //            lcMessageCallback.onError(message, LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getValue(),
            //                    LCError.MESSAGE_FILE_PIC_TYPE_WRONG.getDesc());
            return false;
        }
        return true;
    }

    private static boolean checkFileBody(LCMessage message) {
        String filepath = ((LCFileMessageBody) message.LCMessageBody()).getLocalPath();
        if (ChatVerifyUtils.checkFileformat(Msg.EMsgContentType.FILE_VALUE, filepath, message)) {
            File file = new File(filepath);
            String fileName = file.getName();
            ((LCFileMessageBody) message.LCMessageBody()).setFileName(fileName);
            ((LCFileMessageBody) message.LCMessageBody()).setFileLength(file.length());
        } else {
            return false;
        }
        return true;
    }

    private static boolean checkAtBody(LCMessage message) {
        if (LCMessage.ChatType.forNumber(
                message.LCMessageEntity().getChatType()) != LCMessage.ChatType.GroupChat) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_CHAT_TYPE_ONLY_GROUP.getValue(),
                    LCError.MESSAGE_CHAT_TYPE_ONLY_GROUP.getDesc());
            return false;
        }
        if (!GroupVerifyUtils.checkGroupId(message.LCMessageEntity().getTo())) {
            DispatchController.getInstance().onError(message, LCError.GROUP_ID_ILLEGAL.getValue(),
                    LCError.GROUP_ID_ILLEGAL.getDesc());
            return false;
        }
        if (!((LCATMessageBody) message.LCMessageBody()).getAtAll()) {
            LCATMessageBody atTextMessageBody = (LCATMessageBody) message.LCMessageBody();
            List<String> atUserList = atTextMessageBody.getAt_members();
            for (String userName : atUserList) {
                if (!checkUserName(userName)) {
                    DispatchController.getInstance().onError(message,
                            LCError.ACCOUNT_USERNAME_ILLEGAL.getValue(),
                            LCError.ACCOUNT_USERNAME_ILLEGAL.getDesc());
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkCustomBody(LCMessage message) {
        LCCustomMessageBody body = (LCCustomMessageBody) message.LCMessageBody();
        Map map = body.getMap();
        if (map == null || map.size() == 0) {
            DispatchController.getInstance().onError(message,
                    LCError.COMMON_CONTENT_NULL.getValue(), LCError.COMMON_CONTENT_NULL.getDesc());
            return false;
        }
        for (Object key : map.keySet()) {
            if (key == null || key.equals("")) {
                DispatchController.getInstance().onError(message,
                        LCError.COMMON_CONTENT_NULL.getValue(),
                        LCError.COMMON_CONTENT_NULL.getDesc());
                return false;
            }
        }
        return true;
    }

    private static boolean checkRetractAndReadReceiptBody(LCMessage message) {
        LCReadReceiptMessageBody retractBody = (LCReadReceiptMessageBody) message.LCMessageBody();
        if (TextUtils.isEmpty(retractBody.getReceipt_msgId())) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getValue(),
                    LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getDesc());
            return false;
        }
        if (!retractBody.getReceipt_msgId().startsWith("WEBIM_")) {
            if (retractBody.getReceipt_msgId().length() != 36) {
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getValue(),
                        LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getDesc());
                return false;
            }
        }
        MessageEntity retractMessageEntity = GetDataFromDB
                .queryMessageEntityByMsgId(retractBody.getReceipt_msgId());
        if (retractMessageEntity == null) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getValue(),
                    LCError.MESSAGE_CHAT_MSGID_ILLEGAL.getDesc());
            return false;
        }
        if (!retractMessageEntity.getConversationId().equals(message.getTo())) {
            DispatchController.getInstance().onError(message,
                    LCError.MESSAGE_CONVERSATION_CONFLICT.getValue(),
                    LCError.MESSAGE_CONVERSATION_CONFLICT.getDesc());
            return false;
        }
        message.LCMessageEntity().setGuid(retractMessageEntity.getGuid());
        if (message.LCMessageEntity().getContentType() == Msg.EMsgContentType.READ_RECEIPT_VALUE) {
            if (message.LCMessageEntity().getChatType() != Msg.EMsgType.CHAT_MSG_VALUE && message.LCMessageEntity().getChatType() != Msg.EMsgType.PRIVATE_MSG_VALUE) {
                DispatchController.getInstance().onError(message,
                        LCError.MESSAGE_CHAT_TYPE_WRONG.getValue(),
                        LCError.MESSAGE_CHAT_TYPE_WRONG.getDesc());
                return false;
            }
            retractMessageEntity.setRead(true);
            String conversationId = retractMessageEntity.getConversationId();
            ConversationEntity conversationEntity = DBFactory.getDBManager()
                    .getDBConversationService().load(conversationId);
            if (conversationEntity != null) {
                if (!LCChatConfig.LCChatGlobalStorage.getInstance().getIsInChat(conversationId)) {
                    conversationEntity.setUnreadCount(conversationEntity.getUnreadCount() - 1);
                    DBFactory.getDBManager().getDBConversationService().update(conversationEntity);
                }
            }
            DBFactory.getDBManager().getDBMessageService().update(retractMessageEntity);
        }
        return true;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/2 user creat
 */
