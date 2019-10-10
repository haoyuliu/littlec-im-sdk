/* Project: android_im_sdk
 *
 * File Created at 2016/8/8
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.util;

import com.littlec.sdk.BuildConfig;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.RpcChannelManager;
import com.littlec.sdk.connect.core.LCCmdServiceFactory;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.net.HttpGetTask;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.NetworkMonitor;
import com.littlec.sdk.utils.sp.SdkInfoSp;
import com.squareup.okhttp.Callback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Type com.littlec.sdk.chat.business
 * @User user
 * @Desc 登录处理———获取adapter配置
 * @Date 2016/8/8
 * @Version
 */
public class LCLoginUtil {
    private static final String TAG = "LCLoginUtil";
    private static LCLogger Logger = LCLogger.getLogger(TAG);
    public static final String ACTIVATE_NODE = "activate";

//    public static void doLogin(final LCCommonCallBack callBack) {
//        Logger.d("login begain");
//        final LCCommonCallBack loginCallBack = new LCCommonCallBack() {
//            @Override
//            public void onSuccess() {
//                Logger.d("login end sucess .setIsNeedInitConnection false");
//                NetworkMonitor.setIsNeedInitConnection(false);
//                if (callBack != null) {
//                    callBack.onSuccess();
//                }
//            }
//
//            @Override
//            public void onFailed(int code, String errorMsg) {
//                Logger.d("login end onFailed setIsNeedInitConnection true,code:" + code);
//                NetworkMonitor.setIsNeedInitConnection(true);
//                RpcChannelManager.getInstance().shutdownChannel();
//                if (callBack != null) {
//                    callBack.onFailed(code, errorMsg);
//                }
//            }
//        };
//        getServerConfig(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Logger.d("getServerConfig onFailure: " + e);
//                if (!TextUtils.isEmpty(LCChatConfig.ServerConfig.getConnectAddress())) {
//                    LCCmdServiceFactory.getAccountService().doLogin(loginCallBack);
//                    return;
//                }
//                loginCallBack.onFailed(COMMON_SERVER_ERROR.getValue(), e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (response == null || response.code() != 200) {
//                    Logger.d("getServerConfig onResponse not 200. code:" + response.code());
//                    loginCallBack.onFailed(response.code(), response.message());
//                    return;
//                }
//                try {
//                    String body = response.body().string();
//                    Logger.d(body);
//                    String conn = parseAdapter(body);
//                    if (!TextUtils.isEmpty(conn)) {//bugly上出现多次闪退，http请求成功但是初始化时从sp获取数据依然是空
//                        RpcChannelManager.getInstance().initChannel(conn, true);
//                    }
//                } catch (LCException e) {
//                    e.printStackTrace();
//                    loginCallBack.onFailed(e.getErrorCode(), e.getDescription());
//                    return;
//                }
//                LCCmdServiceFactory.getAccountService().doLogin(loginCallBack);
//            }
//        });
//    }

    public static void doLoginNoAuth(final LCCommonCallBack callBack) {
        Logger.d("login begain");
        final LCCommonCallBack loginCallBack = new LCCommonCallBack() {
            @Override
            public void onSuccess() {
                Logger.d("login end sucess .setIsNeedInitConnection false");
                NetworkMonitor.setIsNeedInitConnection(false);
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                Logger.d("login end onFailed setIsNeedInitConnection true,code:" + code);
                NetworkMonitor.setIsNeedInitConnection(true);
                RpcChannelManager.getInstance().shutdownChannel();
                if (callBack != null) {
                    callBack.onFailed(code, errorMsg);
                }
            }
        };
        JSONObject obj = new JSONObject();
        String conn = BuildConfig.Service_Url;
        String file = BuildConfig.File_Url;
        try {
            obj.put("connDomain", conn);
            obj.put("pafsDomain", file);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveConfiguration(obj);

        RpcChannelManager.getInstance().initChannel(conn, true);
        LCCmdServiceFactory.getAccountService().doLogin(loginCallBack);

    }


    private static void getServerConfig(Callback callback) {
        Logger.d("getServerConfig begain");
        HttpGetTask task = new HttpGetTask(LCChatConfig.ServerConfig.getAdapterConfigAddress());
        task.setCallback(callback);
        task.doAynsExcute();
    }


    private static String parseAdapter(String response) throws LCException {
        if (response == null) {
            throw new NullPointerException();
        }
        try {
            JSONObject obj = new JSONObject(response);
            boolean _activate_node = obj.optBoolean(ACTIVATE_NODE);
            if (!_activate_node) {
                throw new LCException(LCError.COMMON_APPKEY_NOT_ACTIVE);
            }
            return saveConfiguration(obj);

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            throw new LCException(LCError.COMMON_ADAPTER_PARSER_FAILED);
        }
    }

    private static String saveConfiguration(JSONObject obj) {
        String connDomain = obj.optString("connDomain");
        SdkInfoSp.putString(LCChatConfig.SdkInfo.SERVICE_CONNECT_DOMAIN, connDomain);

        String voipDomain = obj.optString("voipDomain");
        SdkInfoSp.putString(LCChatConfig.SdkInfo.SERVICE_VOIP_DOMAIN, voipDomain);

        String pafsDomain = obj.optString("pafsDomain");
        SdkInfoSp.putString(LCChatConfig.SdkInfo.SERVICE_FILE_DOMAIN, pafsDomain);

        String version = obj.optString("version");
        SdkInfoSp.putString(LCChatConfig.SdkInfo.APP_VERSION, version);
        return connDomain;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/8 user creat
 */
