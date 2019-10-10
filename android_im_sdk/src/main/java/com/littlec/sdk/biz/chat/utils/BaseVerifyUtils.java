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

import android.text.TextUtils;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.LCNetworkUtil;

import java.io.File;
import java.util.List;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User user
 * @Desc
 * @Date 2016/8/2
 * @Version
 */
public class BaseVerifyUtils {
    public static boolean checkNickName(String nickName) {
        if (nickName != null) {
            if (nickName.trim().length() > 100)
                return false;
        }
        return true;
    }

    public static boolean checkTargetUserName(LCMessage.ChatType type, String targetUserName){
        if(type.equals(LCMessage.ChatType.Chat)){
           return  checkUserName(targetUserName);
        }else if(type.equals(LCMessage.ChatType.GroupChat)){
          return  GroupVerifyUtils.checkGroupId(targetUserName);
        }
       return false;
    }

    public static void checkNetworkAndLoginFlag() throws LCException{
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            throw new LCException(LCError.COMMON_NETWORK_DISCONNECTED);
        }
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
        }
    }

    public static boolean checkUserName(String userName) {
        if (userName == null) {
            return false;
        }
        return userName.matches("[0-9A-Za-z_]{1,100}$");
    }
    public static boolean checkMutiUserName(List<String> users) {
        if (users == null) {
            return false;
        }
        if(users.size()==0)
            return false;
        for(String userName:users){
            if(!userName.matches("[0-9A-Za-z_]{1,100}$"))
                return false;
        }
        return true;
    }

    public static boolean checkPhone(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        //		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1]\\d{10}";
        if (!TextUtils.isEmpty(mobiles)) {

            return mobiles.matches(telRegex);
        }
        return true;
    }

    public static boolean checkAppKey(String appkey) {
        return appkey.matches("^\\d{6}[a-zA-Z]{2}$");
    }
    public static LCException checkFileformat(String filePath){
        filePath = filePath.toLowerCase();
        File file = new File(filePath);
        if (!file.exists()) {
            return new LCException(LCError.MESSAGE_FILE_PATH_EMPTY);
        }

        if (file.length() <= 0) {
            return new LCException(LCError.MESSAGE_FILE_TOO_SMALL);
        }
        if (file.length() >= 10 * 1024 * 1024) {
            return new LCException(LCError.MESSAGE_FILE_TOO_LARGE);
        }
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")
                || filePath.endsWith(".png") || filePath.endsWith(".bmp")
                || filePath.endsWith(".gif")) {
            return null;
        }else{
            return new LCException(LCError.MESSAGE_FILE_PIC_TYPE_WRONG);
        }
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
