/* Project: android_im_sdk
 *
 * File Created at 2016/8/19
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */

package com.littlec.sdk.biz;

import android.os.Handler;
import android.os.Message;

import com.littlec.sdk.LCClient;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.listener.LCMessageListener;
import com.littlec.sdk.biz.chat.listener.LCMessageSendCallBack;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.listener.IPullMessageCallBack;
import com.littlec.sdk.connect.listener.LCConnectionListener;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCSingletonFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.littlec.sdk.biz.DispatchController.CallBackType.LCCONNECTION_LISTENER_NUM;
import static com.littlec.sdk.biz.DispatchController.CallBackType.LCMESSAGE_LISTENER_NUM;
import static com.littlec.sdk.biz.DispatchController.CallBackType.LCMESSAGE_SENDCALLBACK_NUM;

/**
 * @Type com.littlec.sdk.common
 * @User user
 * @Desc 事件调度中心
 * @Date 2016/8/19
 * @Version
 */

public class DispatchController implements LCMessageListener, LCConnectionListener, LCMessageSendCallBack {
    private LCLogger Logger = LCLogger.getLogger("DispatchController");
    private LCMessageListener messageListener = null;

    private LCConnectionListener connectionListener = null;

    private LCMessageSendCallBack sendCallBack = null;


    private static Map<Integer, ArrayList<Object>> map = new ConcurrentHashMap<>();//需要使用线程安全


    public static DispatchController getInstance() {
        return LCSingletonFactory.getInstance(DispatchController.class);
    }

  /*  private DispatchController() {
        messageListener = LCClient.getInstance().messageManagerInner()
                .getListener();
        groupChangeListener = LCClient.getInstance().groupManager().getGroupChangeListener();
        connectionListener = LCClient.getInstance().accountManager().getConnectionListener();
        contactListener = LCClient.getInstance().contactManager().getContactListener();
        sendCallBack=LCClient.getInstance().messageManager().getCallBack();
    }*/

    /**
     * @Title: register <br>
     * @Description: 注册事件监听 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/19 11:23
     */

    public void register(CallBackType type, Object callback) {
        ArrayList<Object> list = map.get(type.getValue());
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.contains(callback)) {
            return;
        }
        list.add(callback);
        map.put(type.getValue(), list);
    }

    /**
     * @Title: unregister <br>
     * @Description: 取消事件监听<br>
     * @throws: 2016/8/19 11:23
     */

    public void unregister(CallBackType type, Object callback) {
        ArrayList<Object> list = map.get(type.getValue());
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.contains(callback)) {
            list.remove(callback);
        }
        map.put(type.getValue(), list);
    }

    private void postCallbackMessage(CallBackType callBackType, Runnable runnable) {
        switch (callBackType.getValue()) {
            case LCMESSAGE_LISTENER_NUM:
                messageListener = LCClient.getInstance().messageManagerInner()
                        .getListener();
                if (messageListener == null) {
                    return;
                }
                break;
            case LCCONNECTION_LISTENER_NUM:
                connectionListener = LCClient.getInstance().accountManager().getConnectionListener();
                if (connectionListener == null) {
                    return;
                }
                break;
            case LCMESSAGE_SENDCALLBACK_NUM:
                sendCallBack = LCClient.getInstance().messageManager().getCallBack();
                if (sendCallBack == null) {
                    return;
                }
                break;
            default:
                break;
        }
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }


    /**
     * @ClassName: DispatchController
     * @Description: 回调时间类型
     * @author: user
     * @date: 2016/8/19 11:24
     */

    public enum CallBackType {
        PULLMessageCallBack(0),

        LCMessageListener(1),

        LCGroupChangeListener(2),

        LCConnectionListener(3),

        LCContactListener(4),

        LCMessageSendCallBack(5),

        PacketParserManager(6);

        final static int PULL_MESSAGE_CALLBACK_VALUE = 0;

        final static int LCMESSAGE_LISTENER_NUM = 1;

        final static int LCGROUP_CHANGE_LISTENER_NUM = 2;

        final static int LCCONNECTION_LISTENER_NUM = 3;

        final static int LCCONTACT_LISTENER_NUM = 4;

        final static int LCMESSAGE_SENDCALLBACK_NUM = 5;

        final static int PACKETPARSERMANAGER_NUM = 6;

        private int type;

        CallBackType(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }

    }

    public enum PullMessageMethodType {
        cancelPullTimer(0),
        pullCompleted(1),
        pullAllMessage(2);

        private int type;

        PullMessageMethodType(int type) {
            this.type = type;
        }

        public int getValue() {
            return type;
        }

    }

    @Override
    public void onReceivedChatMessage(final List<LCMessage> messageList) {
        postCallbackMessage(CallBackType.LCMessageListener, new Runnable() {
            @Override
            public void run() {
                messageListener.onReceivedChatMessage(messageList);
            }
        });
    }

    @Override
    public void onDisConnected() {
        postCallbackMessage(CallBackType.LCConnectionListener, new Runnable() {
            @Override
            public void run() {
                if (connectionListener != null)
                    connectionListener.onDisConnected();
            }
        });
    }

    @Override
    public void onAccountConflict(final LCClient.ClientType clientType) {
        postCallbackMessage(CallBackType.LCConnectionListener, new Runnable() {
            @Override
            public void run() {
                if (connectionListener != null)
                    connectionListener.onAccountConflict(clientType);
            }
        });
    }

    @Override
    public void onReConnected() {
        postCallbackMessage(CallBackType.LCConnectionListener, new Runnable() {
            @Override
            public void run() {
                if (connectionListener != null)
                    connectionListener.onReConnected();
            }
        });
    }

    @Override
    public void onSuccess(final LCMessage message) {
        postCallbackMessage(CallBackType.LCMessageSendCallBack, new Runnable() {
            @Override
            public void run() {
                sendCallBack.onSuccess(message);
            }
        });
    }

    @Override
    public void onError(final LCMessage message, final int code, final String errorString) {
        postCallbackMessage(CallBackType.LCMessageSendCallBack, new Runnable() {
            @Override
            public void run() {
                sendCallBack.onError(message, code, errorString);
            }
        });
    }

    @Override
    public void onProgress(final LCMessage message, final int progress) {
        postCallbackMessage(CallBackType.LCMessageSendCallBack, new Runnable() {
            @Override
            public void run() {
                sendCallBack.onProgress(message, progress);
            }
        });
    }


    private Handler mHandler = new Handler(LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg == null)
                return;
            try {
                Logger.e("msg: " + msg.arg1);
                dispatchMsg(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * @Title: dispatch <br>
     * @Description: 指派任务 <br>
     * @param: arg1 代表哪种回调<br>
     * @return: <br>
     * @throws: 2016/8/19 11:24
     */

    private void dispatchMsg(Message msg) {
        ArrayList<Object> list = map.get(msg.arg1);
        if (list == null || list.isEmpty()) {
            Logger.e("dispatchMsg error");
            return;
        }
        for (Object callback : list) {
            switch (msg.arg1) {
                case CallBackType.PULL_MESSAGE_CALLBACK_VALUE:
                    IPullMessageCallBack iPullMessageCallBack = (IPullMessageCallBack) callback;
                    int methodType = msg.arg2;
                    if (methodType == PullMessageMethodType.cancelPullTimer.getValue()) {
                        iPullMessageCallBack.cancelPullTimer((boolean) msg.obj);
                    } else if (methodType == PullMessageMethodType.pullCompleted.getValue()) {
                        iPullMessageCallBack.pullCompleted((boolean) msg.obj, msg.what == 1 ? true : false);
                    } else if (methodType == PullMessageMethodType.pullAllMessage.getValue()) {
                        iPullMessageCallBack.pullAllMessage((boolean) msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void sendPullMessage(PullMessageMethodType methodType, boolean isCompleted, boolean recOrSend) {
        if (methodType.getValue() == PullMessageMethodType.pullAllMessage.getValue()) {
            sendMessage(CallBackType.PULLMessageCallBack.getValue(), methodType.getValue(), isCompleted ? 1 : 0, recOrSend);
        } else if (methodType.getValue() == PullMessageMethodType.pullCompleted.getValue()) {
            sendMessage(CallBackType.PULLMessageCallBack.getValue(), methodType.getValue(), isCompleted ? 1 : 0, recOrSend);
        } else if (methodType.getValue() == PullMessageMethodType.cancelPullTimer.getValue()) {
            sendMessage(CallBackType.PULLMessageCallBack.getValue(), methodType.getValue(), isCompleted ? 1 : 0, recOrSend);
        }
    }


    public void sendMessage(int callBackType, int methodType) {
        sendMessage(callBackType, methodType, 0, null);
    }

    public void sendMessage(int callBackType, int methodType, int what) {
        sendMessage(callBackType, methodType, what, null);
    }

    /**
     * @Title: sendMessage <br>
     * @Description: 发送消息<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/19 15:15
     */

    public void sendMessage(int arg1, int arg2, int what, Object object) {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.what = what;
        msg.obj = object;
        msg.sendToTarget();
    }

    public void onDestroy() {
        if (map != null) {
            map.clear();
        }
    }

}

/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/19 user creat
 */

