/* Project: android_im_sdk
 *
 * File Created at 2016/7/26
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.littlec.sdk.connect.ConnectivityBroadcastReceiver;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Type com.littlec.sdk.utils
 * @User user
 * @Desc 网络监控模块
 * @Date 2016/7/26
 * @Version
 */
public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static final LCLogger logger = LCLogger.getLogger(TAG);
    private static AtomicBoolean isNeedInitConnection = new AtomicBoolean(false);
    private static BroadcastReceiver mReceiver;

    public static void setIsNeedInitConnection(boolean needInitConnectionValue) {
        isNeedInitConnection.set(needInitConnectionValue);
    }

    public static AtomicBoolean getIsNeedInitConnection() {
        return isNeedInitConnection;
    }

    public static void registerReceiver(Context context) {
        synchronized (isNeedInitConnection) {
            if (context == null) {
                logger.e("start error，context is null!");
                return;
            }
            if (mReceiver == null) {
                IntentFilter mFilter = new IntentFilter();
                mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mReceiver = new ConnectivityBroadcastReceiver();
                context.registerReceiver(mReceiver, mFilter);
            }
        }
    }

    public static void unRegisterReceiver(Context context) {
        synchronized (isNeedInitConnection) {
            if (context == null) {
                logger.e("start error，context is null!");
                return;
            }
            if (mReceiver != null) {
                try {
                    context.unregisterReceiver(mReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mReceiver = null;
                }
            }
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/26 user creat
 */
