/* Project: android_im_sdk
 *
 * File Created at 2016/8/5
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.listener;

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.LCConnectManager;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.connect.util.NotificationParser;
import com.fingo.littlec.proto.css.Ntf;
import com.fingo.littlec.proto.css.Connector.SessionNotify;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.utils.sp.UserInfoSP;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grpc.stub.StreamObserver;

/**
 * @Type com.littlec.sdk.chat.core.parser
 * @User user
 * @Desc 通知监听——包括登录 新消息
 * @Date 2016/8/5
 * @Version
 */
public class PacketListener<T> implements StreamObserver<T>, IPullMessageCallBack {
    private static final String TAG = "PacketListener";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);
    private static final long INTERVAL = 1000;
    private long lastTime = 0;

    private Timer pullTimer;

    private volatile PullMsgDelayTask pullRecMsgTask;//接收消息延时任务
    private volatile PullMsgDelayTask pullSendMsgTask;//接收消息延时任务

    private AtomicBoolean pullRevDelayTaskExist = new AtomicBoolean(false);//标识拉取接收延时任务是否存在
    private AtomicBoolean pullRevMsgTaskRunning = new AtomicBoolean(false);//标识拉取接收是否正在进行

    private AtomicBoolean pullSendDelayTaskExist = new AtomicBoolean(false);//标识拉取延时任务是否存在
    private AtomicBoolean pullSendMsgTaskRunning = new AtomicBoolean(false);//标识拉取是否正在进行

    private LCCommonCallBack loginCallback;
    private LCCommonCallBack logoutCallback;

    public PacketListener() {
        pullTimer = new Timer();
    }

    @Override
    public void onNext(T value) {
        if (value instanceof SessionNotify) {
            SessionNotify message = (SessionNotify) value;
            switch (Ntf.ENtfType.forNumber(message.getType())) {
                case STREAM_INIT_RESPONSE:
                    if (loginCallback != null) {
                        Logger.d("STREAM_INIT_RESPONSE");
                        loginCallback.onSuccess();
                    }
                    break;
                case LOGIN_RESPONSE:
                    //解析登录是否正常
                    Logger.w("Login response, loginNtfParser");
                    NotificationParser.loginNtfParser(message.getData(), loginCallback);
                    loginCallback = null;//登录解析成功后把回调置空，防止内存泄漏
                    break;
                case PING_RESPONSE:
                    //                    Logger.d("Ping_Response");
                    NotificationParser.pingNtfParser(message.getData());
                    break;
                case NEW_MSG:
                    Logger.w("new message notify!");
                    pullTaskHandle(true);
                    break;
                case LOGOUT_RESPONSE:
                    NotificationParser.logoutNtfParser(message.getData(), logoutCallback);
                    break;
                //被踢出
                case KICK_NOTIFICATION:
                    Logger.w("new KICK_NOTIFICATION!");
                    NotificationParser.kickNotificationParser(message.getData());
                    break;

                case RETRACT_NOTIFICATION:
                    NotificationParser.retractNotificationParser(message.getData());
                    break;
                /**********伙伴终端消息通知*********/
                case CARBON_MSG:
                    pullTaskHandle(false);
                    break;
                case GROUP_LIST_UPDATE_NOTIFICATION:

                    break;
                default:
                    Logger.e("ILLEGAL NOTIFICATION TYPE!");
                    break;
            }

        }
    }

    private void pullTaskHandle(boolean recOrSend) {
        AtomicBoolean delayTaskExist;
        AtomicBoolean delayTaskRunning;
        if (recOrSend) {
            delayTaskExist = pullRevDelayTaskExist;
            delayTaskRunning = pullRevMsgTaskRunning;
        } else {
            delayTaskExist = pullSendDelayTaskExist;
            delayTaskRunning = pullSendMsgTaskRunning;
        }
        if ((System.currentTimeMillis() - lastTime > INTERVAL)
                && delayTaskRunning.compareAndSet(false, true)) {
            Logger.d("pullTaskHandle,to synmessage");
            if (delayTaskExist.compareAndSet(true, false)) {
                Logger.d("pullTaskHandle, exist delay task ,to cancel it");
                if (recOrSend) {
                    pullRecMsgTask.cancel();
                } else {
                    pullSendMsgTask.cancel();
                }
            }
            lastTime = System.currentTimeMillis();
            synMessage(recOrSend);
        } else {
            if (delayTaskExist.compareAndSet(false, true)) {
                Logger.d("pullTaskHandle, not exist delay task ,to new delay task");
                try {
                    if (pullTimer != null) {
                        if (recOrSend) {
                            pullRecMsgTask = new PullMsgDelayTask(recOrSend);
                            pullTimer.schedule(pullRecMsgTask, INTERVAL);
                        } else {
                            pullSendMsgTask = new PullMsgDelayTask(recOrSend);
                            pullTimer.schedule(pullSendMsgTask, INTERVAL);
                        }
                    } else {
                        Logger.e("pullTaskHandle, not exist delay task ,to new delay task,however pullTimer is null!!");//todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("pullTaskHandle, new PullMsgDelayTask error" + e);
                }
            } else {
                Logger.d("pullTaskHandle, already exist delay task ");
            }
        }
    }

    class PullMsgDelayTask extends TimerTask {
        private boolean recOrSend = true;

        public PullMsgDelayTask(boolean revOrSend) {
            this.recOrSend = revOrSend;
        }

        @Override
        public void run() {
            Logger.d("PullMsgDelayTask run");
            if (pullTimer != null) {
                Logger.d("PullMsgDelayTask inner class access outer member,timer not null");
            } else {
                Logger.e("PullMsgDelayTask inner class access outer member,timer is null");
            }
            lastTime = System.currentTimeMillis();
            AtomicBoolean pullRunningFlag = recOrSend ? pullRevMsgTaskRunning : pullSendMsgTaskRunning;
            if (!pullRunningFlag.get()) {
                Logger.d("PullMsgDelayTask ,not pulling ,to synmessage");
                synMessage(recOrSend);
                pullRunningFlag.compareAndSet(false, true);
                if (recOrSend) {
                    pullRevDelayTaskExist.compareAndSet(true, false);
                } else {
                    pullSendDelayTaskExist.compareAndSet(true, false);
                }
            } else {
                Logger.e("PullMsgDelayTask, is already pulling ,so new PullMsgDelayTask ");
                try {
                    if (pullTimer != null) {
                        if (recOrSend) {
                            pullRecMsgTask = new PullMsgDelayTask(recOrSend);
                            pullTimer.schedule(pullRecMsgTask, INTERVAL);
                        } else {
                            pullSendMsgTask = new PullMsgDelayTask(recOrSend);
                            pullTimer.schedule(pullSendMsgTask, INTERVAL);
                        }
                    } else {
                        Logger.e("PullMsgDelayTask, is already pulling ,to new delay task,however pullTimer is null!!");//todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("PullMsgDelayTask, new PullMsgDelayTask error" + e);
                }
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        Logger.e(toString() + "，PacketListener onError，t==null?" + (t == null));
        if (t != null) {
            t.printStackTrace();
            if (t.getCause() != null) {
                Logger.e("throwable:" + t.getCause().toString());
            }
        }
        boolean connected = LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable();
        boolean logined = UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG);
        Logger.e("onError，connected:" + connected + ",logined:" + logined);
        if (loginCallback != null)
            loginCallback.onFailed(LCError.COMMON_INIT_FAIL.getValue(), LCError.COMMON_INIT_FAIL.getDesc());
    }

    @Override
    public void onCompleted() {
        Logger.d("PacketListener onCompleted");
    }

    /**
     * @Title: cancelPullTimer <br>
     * @Description: 取消定时任务，在循环拉取等情况下<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/24 8:33
     */
    @Override
    public void cancelPullTimer(boolean recOrSend) {
        Logger.d("cancelPullTimer");
        AtomicBoolean cancelAtomicFlag = recOrSend ? pullRevDelayTaskExist : pullSendDelayTaskExist;
        if (cancelAtomicFlag.compareAndSet(true, false)) {
            if (pullTimer != null) {
                pullTimer.cancel();
            }
        }
    }

    @Override
    public synchronized void pullAllMessage(boolean recOrSend) {
        Logger.d("pullAllMessage,recOrSend:" + recOrSend);
        AtomicBoolean pullAllMessageFlag;
        if (recOrSend) {
            pullAllMessageFlag = pullRevMsgTaskRunning;
        } else {
            pullAllMessageFlag = pullSendMsgTaskRunning;
        }
        Logger.d("pullAllMessage,pullAllMessageFlag:" + pullAllMessageFlag.get());
        if (pullAllMessageFlag.compareAndSet(false, true)) {
            synMessage(recOrSend);
        }
    }

    /**
     * @Title: pullCompleted <br>
     * @Description: 拉取消息完毕 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/26 14:32
     */
    @Override
    public void pullCompleted(boolean recOrSend, boolean completed) {
        Logger.d(" pullCompleted recOrSend= " + recOrSend + " completed =" + completed);
        AtomicBoolean pullRunningFlag = recOrSend ? pullRevMsgTaskRunning : pullSendMsgTaskRunning;
        AtomicBoolean pullDelayExistFlag = recOrSend ? pullRevDelayTaskExist
                : pullSendDelayTaskExist;
        //set flag false
        if (completed) {
            Logger.d("pullCompleted end");
            pullRunningFlag.compareAndSet(true, false);
        } else {
            if (pullRunningFlag.compareAndSet(false, true)) {
                Logger.d("pullCompleted, not pulling ,to synmessage");
                synMessage(recOrSend);
            } else {
                if (pullDelayExistFlag.compareAndSet(false, true)) {
                    Logger.d("pullCompleted, not exist delay task ,to new delay task");
                    try {
                        if (pullTimer != null) {
                            if (recOrSend) {
                                pullRecMsgTask = new PullMsgDelayTask(recOrSend);
                                pullTimer.schedule(pullRecMsgTask, INTERVAL);
                            } else {
                                pullSendMsgTask = new PullMsgDelayTask(recOrSend);
                                pullTimer.schedule(pullSendMsgTask, INTERVAL);
                            }
                        } else {
                            Logger.e("pullCompleted, not exist delay task ,to new delay task,however pullTimer is null!!");//todo
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("pullCompleted, new PullMsgDelayTask error" + e);
                    }
                } else {
                    Logger.d("pullCompleted, alreadly exist delay task");
                }
            }
        }

    }

    public void setLogoutCallback(LCCommonCallBack callback) {
        logoutCallback = callback;
    }

    public void setLoginCallback(LCCommonCallBack callback) {
        loginCallback = callback;
    }

    private static void synMessage(boolean recOrSend) {
        long guid = recOrSend ? UserInfoSP.getLong(LCChatConfig.UserInfo.REV_GUID)
                : UserInfoSP.getLong(LCChatConfig.UserInfo.SEND_GUID);
        if (recOrSend)
            Logger.d("synMessage recv Guid:" + guid);
        else
            Logger.d("synMessage send Guid:" + guid);
        LCGrpcManager.getInstance().synMessage(guid, recOrSend);
       /* if (LCChatConfig.LCChatGlobalStorage.getInstance().getSynGuidFlag()) {
            long guid = recOrSend ? UserInfoSP.getLong(LCChatConfig.UserInfo.REV_GUID)
                    : UserInfoSP.getLong(LCChatConfig.UserInfo.SEND_GUID);
            Logger.e("receiveGuid"+guid);
            LCGrpcManager.getInstance().synMessage(guid, recOrSend);
        } else {
            if (synMsgGuid()) {
                long guid = recOrSend ? UserInfoSP.getLong(LCChatConfig.UserInfo.REV_GUID)
                        : UserInfoSP.getLong(LCChatConfig.UserInfo.SEND_GUID);
                Logger.e("sendGuid"+guid);
                LCGrpcManager.getInstance().synMessage(guid, recOrSend);
            } else {
                Logger.e("syn guid failed");
            }
        }*/
    }

  /*  private boolean synMsgGuid() {
        if (UserInfoSP.getLong(LCChatConfig.UserInfo.SEND_GUID) == 0) {
            Connector.UnaryResponse synGuidResponse = LCGrpcManager.getInstance().synGuid();
            if (parseSyncServerGUIDResponse(synGuidResponse)) {
                LCChatConfig.LCChatGlobalStorage.getInstance().setSynGuidFlag(true);
                return true;
            } else {
                return false;
            }
        } else
            return true;
    }*/

    /**
     * @Title: onDestory<br>
     * @Description: 释放资源 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/19 13:14
     */
    public void onDestroy() {
        pullRecMsgTask = null;
        pullSendMsgTask = null;
        if (pullTimer != null) {
            pullTimer.cancel();
            pullTimer = null;
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/5 user creat
 */
