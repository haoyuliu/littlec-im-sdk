/* Project: android_im_sdk
 * 
 * File Created at 2016/10/31
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

/**
 * @Type com.littlec.sdk.database.sp
 * @User user
 * @Desc sdk info
 * @Date 2016/10/31
 * @Version
 */
public class SdkInfoSp extends LCPreferenceUtils{

    /**
     * @Title: putStringToUserinfo <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/28 14:38
     */

    public static void putString(String prefName, String value) {
        String sdkInfoSpName="littlec_"+ LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey()+"_"+
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName()+"_"+
                LCChatConfig.SdkInfo.PREFS_SDK_INFO_PROFILE;
        putString(prefName, value, sdkInfoSpName);
    }
    /**
     * @Title: getStringFromUserinfo <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/28 14:38
     */
    public static String getString(String prefName, String defaultValue) {
        String sdkInfoSpName="littlec_"+ LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey()+"_"+
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName()+"_"+
                LCChatConfig.SdkInfo.PREFS_SDK_INFO_PROFILE;
        return  getString(prefName, defaultValue,
                sdkInfoSpName);
    }

    public static void putLong(String prefName,long value){
        String sdkInfoSpName="littlec_"+ LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey()+"_"+
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName()+"_"+
                LCChatConfig.SdkInfo.PREFS_SDK_INFO_PROFILE;
        putLong(prefName,value,sdkInfoSpName);
    }

    public static long getLong(String prefName){
        String sdkInfoSpName="littlec_"+ LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey()+"_"+
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName()+"_"+
                LCChatConfig.SdkInfo.PREFS_SDK_INFO_PROFILE;
        return getLong(prefName,sdkInfoSpName);
    }





}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/31 user creat
 */