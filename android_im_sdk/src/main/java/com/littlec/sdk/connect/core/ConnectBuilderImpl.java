/* Project: android_im_sdk
 *
 * File Created at 2016/8/4
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.core;

import android.content.Context;

import com.littlec.sdk.config.LCChatConfig;
import com.fingo.littlec.proto.css.Base;
import com.fingo.littlec.proto.css.Enum;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.littlec.sdk.utils.SHA256Util;

/**
 * @Type com.littlec.sdk.chat.core.builder
 * @User user
 * @Desc 登录生成器
 * @Date 2016/8/4
 * @Version
 */
public class ConnectBuilderImpl implements IConnectBuilder {
    private static final String TAG = "ConnectBuilderImpl";
    private static final LCLogger logger = LCLogger.getLogger(TAG);

    @Override
    public Base.MobileBase buildEntity() {
        logger.d(" ConnectBuilderImpl Product Model: " + android.os.Build.MODEL + "," +
                android.os.Build.VERSION.RELEASE);
        Context context = LCChatConfig.LCChatGlobalStorage.getInstance().getContext();
        String imei = LCNetworkUtil.getImei(context);
        Base.MobileBase.Builder builder = Base.MobileBase.newBuilder()
                .setClientType(Enum.EClientType.ANDROID)// 设备类型
                .setLanguageType(Enum.ELanguageType.ENGLISH) // 语言类型
                .setSdkVersion("2.0.0") // sdk 版本号
                .setOsVersion(android.os.Build.VERSION.RELEASE)  //操作系统版本，5.0
//                .setDevice(android.os.Build.MODEL)// 设备型号，SM-N9008V
                .setDevice(imei)// 设备型号，SM-N9008V
                .setImei(imei); // 设备id，imei

        LCNetworkUtil.NetState netType = LCNetworkUtil.getNetType(context);
        if (netType == null) {
            builder.setNetType(Enum.ENetworkType.UNRECOGNIZED);
        } else {
            switch (netType) {
                case NET_2G:
                case NET_3G:
                    builder.setNetType(Enum.ENetworkType.M2G);
                    break;
                case NET_4G:
                    builder.setNetType(Enum.ENetworkType.M4G);
                    break;
                case NET_WIFI:
                    builder.setNetType(Enum.ENetworkType.WIFI);
                    break;
                case NET_UNKNOWN:
                    builder.setNetType(Enum.ENetworkType.UNRECOGNIZED);
                    break;
                default:
                    break;
            }
        }
        return builder.build();
    }

    @Override
    public Connector.LoginRequest buildLoginRequest() {
        Connector.LoginRequest.Builder loginRequest = Connector.LoginRequest.newBuilder()
                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())
                .setPassword(SHA256Util.SHA256Encrypt(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginPassWord()))
                .setToken(LCChatConfig.LCChatGlobalStorage.getInstance().getToken())
                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
                .setAppkeyPassword(LCChatConfig.LCChatGlobalStorage.getInstance().getAppPassword())
                .setSHA256(true);
        return loginRequest.build();
    }

    public Connector.LogoutRequest buildLogoutRequest() {
        Connector.LogoutRequest.Builder buillder = Connector.LogoutRequest.newBuilder();
        return buillder.build();
    }

    public Connector.PingRequest buildPingRequest(String msgId) {
        Connector.PingRequest.Builder builder = Connector.PingRequest.newBuilder();
        builder.setMsgId(msgId);
        return builder.build();
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/4 user creat
 */
