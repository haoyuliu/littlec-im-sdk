/* Project: android_im_sdk
 *
 * File Created at 2016/11/1
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.littlec.sdk.utils.NetworkMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.ALARM_SERVICE;
import static com.littlec.sdk.connect.LCConnectManager.ConnectStrategy.DILIGENT_PING_NUM;
import static com.littlec.sdk.connect.LCConnectManager.ConnectStrategy.LAZY_PING_NUM;
import static com.littlec.sdk.connect.LCConnectManager.ConnectStrategy.LOGIN_NUM;
import static com.littlec.sdk.connect.LCConnectManager.ConnectStrategy.defaultPingInterval;
import static com.littlec.sdk.connect.LCConnectManager.ConnectStrategy.normalPingInterval;

/**
 * @Type com.littlec.sdk.chat.core.launcher.impl
 * @User user
 * @Desc 连接管理
 * @Date 2016/11/1
 * @Version
 */
public class LCConnectManager {
    private static final String TAG = "LCConnectManager";
    private static final String ACTION = "com.cmcc.ping";
    private Context mContext;
    private volatile List<String> msgIdList = new ArrayList<>();
    private AlarmReceiver alarmReceiver;
    private AtomicBoolean isPingRunning = new AtomicBoolean(false);
    private volatile int reLoginTimes = 0;
    private volatile ConnectStrategy connectStrategy = ConnectStrategy.LAZY_PING;
    private volatile boolean connected = false;

    enum ConnectStrategy {
        LAZY_PING(0),
        DILIGENT_PING(1),
        LOGIN(2);
        int value;

        ConnectStrategy(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        static int defaultPingInterval = 3 * 60 * 1000;
        static final int normalPingInterval = 10 * 1000;
        static final int LAZY_PING_NUM = 0;
        static final int DILIGENT_PING_NUM = 1;
        static final int LOGIN_NUM = 2;
    }

    private LCConnectManager() {

    }

    public boolean isConnectAvailable() {
        if (!connected) {
            LCLogger.getLogger(TAG).w("not connected ");
        }
        return connected;
    }

    private void setConnected(boolean connected) {
        LCLogger.getLogger(TAG).d("setConnected " + connected);
        this.connected = connected;
    }

    public synchronized void startLogin() {
        LCLogger.getLogger(TAG).d("startLogin ");
        startPing(false);
    }

    public synchronized void startPing() {
        LCLogger.getLogger(TAG).d("startPing after login sucess");
        startPing(true);
    }

    /**
     * @Title: startPing <br>
     * @Description: pingOrLogin true代表ping  false 代表Login <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/12/9 15:46
     */
    public synchronized void startPing(boolean pingOrLogin) {
        defaultPingInterval = LCChatConfig.LCChatGlobalStorage.getInstance().getPingTime() * 1000;
        LCLogger.getLogger(TAG).d("startPing,pingOrLogin:" + pingOrLogin);
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            LCLogger.getLogger(TAG).e("user not login!!! can  not enter reLogin module!!!");
            return;
        }
        if (pingOrLogin) {
            setConnected(true);
        }
        if (!LCNetworkUtil.isNetworkConnected(LCChatConfig.LCChatGlobalStorage.getInstance().getContext())) {
            LCLogger.getLogger(TAG).d("Network not Connected");
            setConnected(false);
            return;
        }
        if (isPingRunning.get()) {
            return;
        } else {
            isPingRunning.set(true);
            mContext = LCChatConfig.LCChatGlobalStorage.getInstance().getContext();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION);
            filter.setPriority(Integer.MAX_VALUE);
            alarmReceiver = new AlarmReceiver();
            mContext.registerReceiver(alarmReceiver, filter);
        }
        schedulePingTask(pingOrLogin);
    }

    /**
     * @Title: schedulePingTask <br>
     * @Description: 执行ping策略 <br>
     * @param: ping 3次就放弃<br>
     * @return: <br>
     * @throws: 2016/12/2 14:17
     */
    private void schedulePingTask(boolean pingOrLogin) {
        LCLogger.getLogger(TAG).d("schedulePingTask, pingOrLogin:" + pingOrLogin);
        /**************连接策略选择***************/
        if (pingOrLogin) {
            if (msgIdList.size() >= 3) {
                LCLogger.getLogger(TAG).e("packet size>3, so disconnect,and relogin");
                setConnected(false);
                NetworkMonitor.setIsNeedInitConnection(true);
                DispatchController.getInstance().onDisConnected();
                connectStrategy = ConnectStrategy.LOGIN;
            } else if (msgIdList.size() == 0) {
                connectStrategy = ConnectStrategy.LAZY_PING;
            } else {
                connectStrategy = ConnectStrategy.DILIGENT_PING;
            }
        } else {
            connectStrategy = ConnectStrategy.LOGIN;
        }
        switch (connectStrategy.getValue()) {
            case LAZY_PING_NUM:
            case DILIGENT_PING_NUM:
                String msgId = CommonUtils.getUUID();
                synchronized (msgIdList) {
//                    LCLogger.getLogger(TAG).d("add ping msgid:" + msgId);
                    msgIdList.add(msgId);
                }
                if (isConnectAvailable()) {
                    //ping主要用于目前是不是出于掉线状态
                    LCGrpcManager.getInstance().sendPing(msgId);
                }
                scheduleTimerTask(true);
                break;
            case LOGIN_NUM:
                /*********执行登录逻辑*******/
                LCLogger.getLogger(TAG).e("shutdownChannel and relogin");
                if (TextUtils.isEmpty(LCChatConfig.ServerConfig.getConnectAddress())) {
                    LCLogger.getLogger(TAG).e("!!! however address is null ，cannot init");
                    NetworkMonitor.setIsNeedInitConnection(true);
                    onDestroy();
                    return;
                }
                LCGrpcManager.getInstance().shutdownChannel();
                LCGrpcManager.getInstance().initConnection(new LCCommonCallBack() {
                    @Override
                    public void onSuccess() {
                        LCLogger.getLogger(TAG).d("relogin initConnection success");
                        LCGrpcManager.getInstance().doLogin(new LCCommonCallBack() {
                            @Override
                            public void onSuccess() {
                                LCLogger.getLogger(TAG).e("relogin  success");
                                setConnected(true);
                                DispatchController.getInstance().onReConnected();
                                connectStrategy = ConnectStrategy.LAZY_PING;
                                reLoginTimes = 0;
                                msgIdList.clear();
                                scheduleTimerTask(true);
                                NetworkMonitor.setIsNeedInitConnection(false);
                            }

                            @Override
                            public void onFailed(int code, String errorMsg) {
                                LCLogger.getLogger(TAG).e("reLogin failed code=" + code + " errorMsg=" + errorMsg
                                        + " reloginTimes=" + reLoginTimes);

                                //如果密码错误，通过账号冲突回调出去，提示用户已经有用户异地登录
                                if (CommonUtils.isAccountConflict(code)) {
                                    LCLogger.getLogger(TAG).e("isAccountConflict……code=" + code);
                                    DispatchController.getInstance().onAccountConflict(LCClient.ClientType.ANDROID);
                                    onDestroy();
                                    return;
                                }
                                if (reLoginTimes >= 5) {
                                    NetworkMonitor.setIsNeedInitConnection(true);
                                    onDestroy();
                                    return;
                                } else {
                                    reLoginTimes++;
                                    scheduleTimerTask(false);
                                }

                            }
                        }, Connector.SessionRequest.ESessionRequestType.LOGIN_BY_AUTO);
                    }

                    @Override
                    public void onFailed(int code, String errorMsg) {
                        LCLogger.getLogger(TAG).e("relogin initConnection failed code=" + code + " errorMsg=" + errorMsg
                                + " reloginTimes=" + reLoginTimes);
                        if (reLoginTimes >= 5) {
                            NetworkMonitor.setIsNeedInitConnection(true);
                            onDestroy();
                            return;
                        } else {
                            reLoginTimes++;
                            scheduleTimerTask(false);
                        }
                    }
                });

                break;
            default:
                LCLogger.getLogger(TAG).e("connect Strategy error");
                connectStrategy = ConnectStrategy.LAZY_PING;
                scheduleTimerTask(true);
                break;
        }

    }

    /**
     * @Title: scheduleTimerTask <br>
     * @Description: 根据不同的连接策略来设定下次执行任务的时间 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/12/9 14:44
     */
    private void scheduleTimerTask(boolean pingOrLogin) {
        long triggerAtTime;
        if (connectStrategy.equals(ConnectStrategy.LAZY_PING)) {
            triggerAtTime = SystemClock.elapsedRealtime() + defaultPingInterval;
            LCLogger.getLogger(TAG).d("LAZY_PING:" + defaultPingInterval);
        } else if (connectStrategy.equals(ConnectStrategy.DILIGENT_PING)) {
            triggerAtTime = SystemClock.elapsedRealtime() + normalPingInterval;
            LCLogger.getLogger(TAG).d("DILIGENT_PING:" + normalPingInterval);
        } else {
            triggerAtTime = reLoginTimes * 5 * 1000;
            LCLogger.getLogger(TAG).d("LOGIN_PING:" + reLoginTimes * 5 * 1000);
        }
        LCLogger.getLogger(TAG).d("triggerAtTime:" + triggerAtTime);

        if (mContext == null) {
            LCLogger.getLogger(TAG).e("mContext is null");
            return;
        }
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ACTION);
        intent.putExtra("pingOrLogin", pingOrLogin);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        LCLogger.getLogger(TAG).d("android sdk version:" + Build.VERSION.SDK_INT);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                if (connectStrategy.equals(ConnectStrategy.LAZY_PING)) {
                    alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20 * 1000, 10, pi);
                } else {
                    alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, 10, pi);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LCLogger.getLogger(TAG).e("闹钟定时器启动失败："+e.toString());
        }
    }

    public void removeMsgId(String msgId) {
        if (msgId == null) {
            return;
        }
//        LCLogger.getLogger(TAG).d("removeMsgId,msgId:" + msgId);
        setConnected(true);
        synchronized (msgIdList) {
            if (msgIdList.contains(msgId)) {
                msgIdList.clear();
            }
        }

    }

    /**
     * @ClassName: LCConnectManager
     * @Description: 闹钟接收器
     * @author: user
     * @date: 2016/12/2 11:34
     */
    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                boolean pingOrLogin = intent.getBooleanExtra("pingOrLogin", true);
                // 此处要考虑很多场景，账号冲突、退出登陆，后台等，登陆、重连本身依然存在漏洞
                try {
                    schedulePingTask(pingOrLogin);
                } catch (Exception e) {//
//                    java.util.concurrent.RejectedExecutionException
//                    Task io.grpc.internal.SerializingExecutor@aa49857 rejected from java.util.concurrent.ThreadPoolExecutor@8c98f44[Shutting down, pool size = 1, active threads = 1, queued tasks = 0, completed tasks = 21]
                    LCLogger.getLogger(TAG).e("AlarmReceiver  onReceive schedulePingTask error" + e);
                }

            }
        }
    }


    public synchronized void onDestroy() {
        if (isPingRunning.compareAndSet(true, false)) {
            if (alarmReceiver != null) {
                mContext.unregisterReceiver(alarmReceiver);
            }
            msgIdList.clear();
            reLoginTimes = 0;
            mContext = null;
        }
        LCLogger.getLogger(TAG).d("onDestroy ");
        setConnected(false);
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/11/1 user creat
 */
