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

import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.biz.user.entity.LCRegisterInfo;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User user
 * @Desc
 * @Date 2016/8/2
 * @Version
 */
public class AccountVerifyUtils extends BaseVerifyUtils {
    public static <T> void verifyAccountParam(T t) throws LCException{
        if (t == null) {
            throw new LCException(LCError.COMMON_CONTENT_NULL);
        }
        LCRegisterInfo userInfo = (LCRegisterInfo) t;
        String userName = userInfo.getUserName();
        String nickName = userInfo.getNickName();
        String phone = userInfo.getPhone();
        String passWord = userInfo.getPassWord();
         checkRegisterData(userName, nickName, phone, passWord);

    }

    public static LCException verifyLoginData(String userName, String passWd) {
        if (!checkUserName(userName))
            return new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
        if (!checkPassWord(passWd))
            return new LCException(LCError.ACCOUNT_PASSWORD_ILLEGAL);
        return null;
    }

    private static void checkRegisterData(String userName, String nickName, String phone,
                                                 String passWord) throws LCException{
        if (!checkUserName(userName))
            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
        if (!checkNickName(nickName))
            throw new LCException(LCError.ACCOUNT_NICKNAME_UNREQUIRED);
        if (!checkPhone(phone))
            throw new LCException(LCError.ACCOUNT_PHONE_UNREQUIRED);
        if (!checkPassWord(passWord))
            throw new LCException(LCError.ACCOUNT_PASSWORD_ILLEGAL);
    }

    public static boolean checkPassWord(String passWord) {
        if (TextUtils.isEmpty(passWord) || passWord.length() < 6 || passWord.length() > 100
                || passWord.contains(" "))
            return false;

        boolean result = true;
        try {
            char[] array = passWord.toCharArray();
            for (char c : array) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 48 && c <= 57)
                        || isValidSign(c)) {
                    continue;
                } else {
                    result = false;
                    break;

                }
            }

        } catch (Exception e) {

        }
        return result;
    }

    private static boolean checkPassWord(String passWord, boolean needCheckLength) {
        if (TextUtils.isEmpty(passWord))
            return false;
        if (needCheckLength && (passWord.length() < 6 || passWord.length() > 32)) {
            return false;
        }
        boolean result = true;
        try {
            char[] array = passWord.toCharArray();
            for (char c : array) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 48 && c <= 57)
                        || isValidSign(c)) {
                    continue;
                } else {
                    result = false;
                    break;

                }
            }

        } catch (Exception e) {

        }
        return result;
    }

    private static boolean isValidSign(char c) {
        return ("(`~!@#$%^&*()-=_+[]{}|\\;:'" + "\",./<>?)").contains(Character.toString(c));
    }

    private static boolean checkVerificationCode(String verificationCode) {
        if (TextUtils.isEmpty(verificationCode)) {
            return false;
        } else {
            return verificationCode.matches("[0-9]{6}$");
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
