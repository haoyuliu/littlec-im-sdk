package com.littlec.sdk.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.littlec.sdk.utils.NetworkMonitor;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_2G_NUMBER;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_3G_NUMBER;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_4G_NUMBER;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_UNKNOWN_NUMBER;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_WIFI_NUMBER;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityBroadcastReceiver";
    private static final LCLogger logger = LCLogger.getLogger(TAG);

    private static volatile LCNetworkUtil.NetState netState = LCNetworkUtil.NetState.NET_UNKNOWN;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info;
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();// 可能出现权限问题
            } catch (Exception e) {
                logger.d("onReceive net changed ,getActiveNetworkInfo error" + e);
                return;
            }

            logger.d("onReceive net changed ,last state value：" + netState);
            if (info != null && info.isAvailable()) {
                String name = info.getTypeName();
                logger.d("available ,net name:" + name);
                //处理网络切换的问题
                dealNetStateChange(context);
                //网络恢复后需要重新登录（该操作只有经过断网后才能进行）
                AtomicBoolean isNeedInitConnection = NetworkMonitor.getIsNeedInitConnection();
                logger.d("isNeedInitConnection:" + isNeedInitConnection.get());
                if (isNeedInitConnection.compareAndSet(true, false)) {
                    if (!LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable()) {
                        logger.d("startLogin");
                        //连接不可用 直接重连
                        //先判断用户是否执行过登出接口
                        if (UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
                            LCSingletonFactory.getInstance(LCConnectManager.class).startLogin();
                        }
                    }
                }
            } else {
                logger.d("no network");
                DispatchController.getInstance().onDisConnected();
                LCSingletonFactory.getInstance(LCConnectManager.class).onDestroy();
                NetworkMonitor.setIsNeedInitConnection(true);
            }
            netState = LCNetworkUtil.getNetType(context);
        }
    }

    private void dealNetStateChange(Context context) {
        logger.d("dealNetStateChange");
        LCNetworkUtil.NetState currentNetType = LCNetworkUtil.getNetType(context);
        if (netState == null || currentNetType == null) {
            logger.d("dealNetStateChange return ,netState:" + netState + ",currentNetType:" + currentNetType);
            return;
        }
        switch (netState.getNetNum()) {
            case NET_2G_NUMBER:
            case NET_3G_NUMBER:
            case NET_4G_NUMBER:
            case NET_WIFI_NUMBER:
                //当前的网络状态不等于前一个网络状态，且属于正常的网络状态
                if (!currentNetType.equals(netState) && currentNetType.getNetNum() < 5 && currentNetType.getNetNum() > 0) {
                    if (LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable()) {
                        LCSingletonFactory.getInstance(LCConnectManager.class).onDestroy();
                    }
                    //先判断用户是否执行过登出接口
                    if (UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
                        logger.d("change network,start ping false");
                        LCSingletonFactory.getInstance(LCConnectManager.class).startLogin();
                        NetworkMonitor.setIsNeedInitConnection(false);
                    }
                }
                break;
            case NET_UNKNOWN_NUMBER:
                logger.d("NET_UNKNOWN_NUMBER");
                break;
            default:
                logger.d("无法确定的网络类型");
                break;
        }
    }
}

