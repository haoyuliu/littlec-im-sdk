package com.littlec.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_2G;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_3G;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_4G;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_UNKNOWN;
import static com.littlec.sdk.utils.LCNetworkUtil.NetState.NET_WIFI;


public class LCNetworkUtil {
    private static final String TAG = "LCNetworkUtil";
    public static final String CMWAP_PROXY = "10.0.0.172";
    public static final int CMWAP_PROXY_PORT = 80;

    public static final int ERROR_NETWORK_PROTOCOL_EXCEPTION = 1;
    public static final int ERROR_NETWORK_IO_EXCEPTION = 2;
    public static final int ERROR_NETWORK_PARSE_EXCEPTION = 3;
    public static final int ERROR_NETWORK_CONNECTION_TIMEOUT = 4;
    public static final int ERROR_NETWORK_SOCKET_TIMEOUT = 5;
    public static final int ERROR_SERVER_RESPONSE_PARSE_EXCEPTION = 6;
    public static final int ERROR_NETWORK_NOT_CONNECTED = 7;
    public static final String NETWORK_TYPE_MOBILE_2G = "2G";
    public static final String NETWORK_TYPE_MOBILE_3G = "3G";
    public static final String NETWORK_TYPE_MOBILE_4G = "4G";
    public static final String NETWORK_TYPE_MOBILE_LTE = "LTE";
    public static final String NETWORK_TYPE_WIFI = "WIFI";
    /**
     * Wifi is not enable
     */
    public static final int WIFI_STATUS_DISABLE = -1;

    /**
     * Wifi is enable, but not connect to any network
     */
    public static final int WIFI_STATUS_ENABLE = 0;

    /**
     * Wifi is enable and connect to some network except CMCC wifi
     */
    public static final int WIFI_STATUS_CONNECT = 1;

    /**
     * Wifi is enable and connect with CMCC wifi, but may not verified
     */
    public static final int WIFI_STATUS_CONNECT_CMCC = 2;

    public static final String SSID_CMCC = "CMCC";
    public static final String SSID_CMCC_EDU = "CMCC-EDU";
    public static final String PREFS_FILE = "device_id.xml";
    public static final String PREFS_DEVICE_ID = "device_id";

    public static boolean isCmwap(Context context) {
        int wifiStatus = getWifiStatus(context);
        if (wifiStatus == WIFI_STATUS_DISABLE || wifiStatus == WIFI_STATUS_ENABLE) {
            ConnectivityManager conManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = conManager.getActiveNetworkInfo();
            if (ni != null) {
                String apn = ni.getExtraInfo();
                if (apn != null && apn.equalsIgnoreCase("cmwap")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getWifiStatus(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            ConnectivityManager connect = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State state = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState();
            if (state == NetworkInfo.State.CONNECTED) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null && info.getSSID() != null && (info.getSSID().equals(SSID_CMCC)
                        || info.getSSID().equals(SSID_CMCC_EDU))) {
                    return WIFI_STATUS_CONNECT_CMCC; // connect to CMCC, but may
                    // be not verified
                } else {
                    return WIFI_STATUS_CONNECT; // connect to wifi
                }
            } else {
                return WIFI_STATUS_ENABLE; // wifi is enable, but no wifi
                // connect
            }
        } else {
            return WIFI_STATUS_DISABLE; // wifi is not enable
        }
    }

    /**
     * @param context
     * @return
     * @方法名：isNetworkConnected
     * @描述：(网络是否连接)
     * @输出：boolean
     * @作者：Administrator
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {// 主要是防止出现空指针问题
            return true;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return ni.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    // error code
    public static final int ERROR_CODE_NO_CONN = 1;
    public static final int ERROR_CODE_WRONG_JSON = 2;

    /**
     * 获取imei
     */
    public static String getImei(Context context) {
        UUID uuid = null;
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        final String id = prefs.getString(PREFS_DEVICE_ID, null);
        if (id != null) {
            uuid = UUID.fromString(id);
        } else {
            final String androidId = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } else {
                    String imei = null;
                    try {
                        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                        imei = mTelephonyMgr.getDeviceId();//需权限
                    } catch (Exception e) {
                        LCLogger.getLogger(TAG).d("getDeviceId error ，permission error");
                    }
                    uuid = imei != null ? UUID.nameUUIDFromBytes(imei.getBytes("utf8"))
                            : UUID.randomUUID();
                }
                prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //		if(imei == null) {
        //			imei = "000000000000000";
        //		}
        LCLogger.getLogger(TAG).d("imei=" + (uuid != null ? uuid.toString() : ""));
        return uuid != null ? uuid.toString() : "";
    }

    public enum NetState {
        NET_2G(1),
        NET_3G(2),
        NET_4G(3),
        NET_WIFI(4),
        NET_UNKNOWN(5);
        private int num;

        NetState(int num) {
            this.num = num;
        }

        public int getNetNum() {
            return num;
        }

        public static final int NET_2G_NUMBER = 1;
        public static final int NET_3G_NUMBER = 2;
        public static final int NET_4G_NUMBER = 3;
        public static final int NET_WIFI_NUMBER = 4;
        public static final int NET_UNKNOWN_NUMBER = 5;
    }

    /**
     * @return
     * @方法名：getNetType
     * @描述：获取终端网络接入方式（2G/3G/4G/LTE/WiFi）
     * @输出：String
     */
    public static NetState getNetType(Context context) {
        NetState strNetworkType = null;
        // 获取网络连接管理者
        ConnectivityManager connectionManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取网络的状态信息，有下面三种方式
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = NET_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String strSubTypeName = networkInfo.getSubtypeName();
                int networkSubType = networkInfo.getSubtype();
                Log.e("LCNetworkUtil",
                        "strSubTypeName=" + strSubTypeName + "  networkSubType=" + networkSubType);
                switch (networkSubType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = NET_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: //api<13 : replace by 15
                        strNetworkType = NET_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: //api<11 : replace by 13
                        strNetworkType = NET_4G;
                        break;
                    default:
                        //TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if ("TD-SCDMA".equalsIgnoreCase(strSubTypeName)
                                || "WCDMA".equalsIgnoreCase(strSubTypeName)
                                || "CDMA2000".equalsIgnoreCase(strSubTypeName)) {
                            strNetworkType = NET_3G;
                        } else {
                            strNetworkType = NET_UNKNOWN;
                        }
                        break;
                }
            }
        }
        Log.e("LCNetworkUtil", "getNetType:" + strNetworkType);
        return strNetworkType;
    }
}
