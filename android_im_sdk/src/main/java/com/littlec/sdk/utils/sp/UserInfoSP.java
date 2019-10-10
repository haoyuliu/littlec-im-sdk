/* Project: android_im_sdk
 *
 * File Created at 2016/8/28
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.utils.sp;

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.utils.LCLogger;

/**
 * @Type com.littlec.sdk.database.sp
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/28
 * @Version
 */

public class UserInfoSP extends LCPreferenceUtils {
    private static final String TAG = "UserInfoSP";
    private final static LCLogger logger = LCLogger.getLogger(TAG);

    /**
     * @Title: putStringToUserinfo <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/28 14:38
     */

    public static void putString(String prefName, String value) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        putString(prefName, value, userInfoSpName);

    }

    public static void putInt(String prefName, int value) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        putInt(prefName, value, userInfoSpName);
    }

    public static void setBoolean(String prefName, boolean value) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        setBoolean(prefName, value, userInfoSpName);
    }

    public static boolean getBoolean(String prefName) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        return getBoolean(prefName, userInfoSpName);
    }

    /**
     * @Title: getStringFromUserinfo <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/28 14:38
     */
    public static String getString(String prefName, String defaultValue) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        return getString(prefName, defaultValue,
                userInfoSpName);
    }

    public static void removeString(String prefName) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        removeString(prefName, userInfoSpName);
    }

    public static void putGuid(String prefName, long value) {
        long guid = getLong(prefName);
        if (value > guid) {
            putLong(prefName, value);
        } else {
            logger.e("put guid:" + value + ",but local is bigger or equal,local:" + guid);
        }
    }
    public static void clearGuid(String prefName) {
        putLong(prefName, 0);

    }


    public  static void putLong(String prefName, long value) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        putLong(prefName, value, userInfoSpName);
    }

    public static long getLong(String prefName) {
        String userInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" +
                LCChatConfig.UserInfo.PREFS_USERINFO_PROFILE;
        return getLong(prefName, userInfoSpName);
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/28 zhangguoqiong creat
 */
