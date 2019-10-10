/* Project: android_im_sdk
 * 
 * File Created at 2016/10/10
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.net;

import com.littlec.sdk.connect.core.LCBaseTask;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;

import static com.littlec.sdk.net.HttpBaseTask.MEDIA_TYPE_MARKDOWN;

/**
 * @Type com.littlec.sdk.network
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/10/10
 * @Version
 */

public class UploadCrashTask extends LCBaseTask {
    private LCLogger logger=LCLogger.getLogger("UploadCrashTask");
    private File[] files;
    private OkHttpClient client;

    public UploadCrashTask(Object packet, int threadPoolType, String threadTaskName) {
        super(packet, threadPoolType, threadTaskName);
    }

    public UploadCrashTask(File[] files, int threadPoolType) {
        super(files, threadPoolType);
        this.files = files;
    }

    @Override
    public void run() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("platform", "ANDROID");
            jsonObject.put("device",android.os.Build.MODEL);
            jsonObject.put("osVersion",android.os.Build.VERSION.RELEASE);
            jsonObject.put("sdkVersion", CommonUtils.getAppVersionName(LCClient.getInstance().getContext()));
            jsonObject.put("appkey", LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey());
            jsonObject.put("username",LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
            jsonObject.put("netType", LCNetworkUtil.getNetType(LCClient.getInstance().getContext()));
            for(int i=0;i<files.length;i++){
                RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, files[i]);
                MultipartBuilder multipartBuilder = new MultipartBuilder();
                multipartBuilder.addFormDataPart("param", jsonObject.toString());
                multipartBuilder.addFormDataPart("file", "file", fileBody);
                RequestBody requestBody = multipartBuilder.build();
                Request request = new Request.Builder().url(LCChatConfig.ServerConfig.getCrashLogAddress())
                        .post(requestBody).build();
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    String result=response.body().string();
                    logger.d(result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    if(jsonObject1.optString("status").equals("600"))
                        files[i].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void setHttpClient(OkHttpClient client) {
        this.client = client;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/10 zhangguoqiong creat
 */
