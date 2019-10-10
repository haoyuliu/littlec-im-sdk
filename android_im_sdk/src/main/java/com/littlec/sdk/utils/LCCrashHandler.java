package com.littlec.sdk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.littlec.sdk.config.LCChatConfig;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Implemented by objects that want to handle cases where a thread is being
 * terminated by an uncaught exception. Upon such termination, the handler is
 * notified of the terminating thread and causal exception. If there is no
 * explicit handler set then the thread's group is the default handler.
 * 
 * @author wei.chen
 * @version 2014-07-04
 */
public class LCCrashHandler implements UncaughtExceptionHandler {
    private LCLogger Logger = LCLogger.getLogger("LCCrashHandler");
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    private static Context mContext;
    private static String crashLogFilePath = "";

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    public static void collectCrashInfo(Context context,String filePath) {
        if(!TextUtils.isEmpty(filePath)){
            filePath=filePath+File.separator+"crashLog";
            File file=new File(filePath);
            file.mkdirs();
        }
        mContext = context;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        crashLogFilePath = TextUtils.isEmpty(filePath)?LCChatConfig.LCChatGlobalStorage.LC_CRASH_LOG_PATH:filePath + File.separator
                + "crash_" + sdf.format(date) + ".log";

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new LCCrashHandler());
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex)) {
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
        } else {
            NotificationManager manager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public boolean handleException(Throwable ex) {
        if (ex == null)
            return false;

        ex.printStackTrace();

        saveCrashInfo2File(ex);
        return true;
    }

    private String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder(
                "------------------------------Phone information-----------------------------");

//        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
//        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
//        sb.append("\nLine1Number = " + tm.getLine1Number());
//        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
//        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
//        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
//        sb.append("\nNetworkType = " + tm.getNetworkType());
//        sb.append("\nPhoneType = " + tm.getPhoneType());
//        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
//        sb.append("\nSimOperator = " + tm.getSimOperator());
//        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
//        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
//        sb.append("\nSimState = " + tm.getSimState());
//        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
//        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        sb.append("\r\n\r\n");
        return sb.toString();
    }

    /**
     * collect the information of device
     * 
     * @param context
     */
    private String getPackageInfo() {
        StringBuilder sb = new StringBuilder(
                "------------------------------Package information-----------------------------");
        Map<String, String> info = new HashMap<String, String>();
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }

        return sb.toString();
    }

    /**
     * 
     * @param fileName
     * @param content
     */
    private static boolean appendToFile(String content) {
        FileWriter writer = null;
        try {
            File file = new File(crashLogFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            // 第二个参数true表示以追加形式写文件
            writer = new FileWriter(crashLogFilePath, true);
            writer.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean saveCrashInfo2File(Throwable ex) {
        Logger.d("saveCrashInfo2File start");
        String timestamp = format.format(new Date());

        StringBuffer sb = new StringBuffer();
        sb.append(
                "---------------------------Time:" + timestamp + "--------------------------\r\n");
        sb.append("app版本号：" + CommonUtils.getAppVersionName(mContext) + "-"
                + CommonUtils.getAppVersionCode(mContext));
        try {
            File file = new File(crashLogFilePath);
            if (!file.exists()) {
                file.createNewFile();

                sb.append(getPhoneInfo());
                sb.append(getPackageInfo());
            }
            Logger.d(file.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
            Logger.d("path:" + crashLogFilePath);
        }
        sb.append(
                "----------------------------Exception statck trace:-----------------------------\r\n\r\n");
        try {
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            ex.printStackTrace(pw);
            sb.append(writer.toString());
            sb.append("\r\n\r\n\r\n");
            pw.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d("saveCrashInfo2File end");
        // save log to file
        return (appendToFile(sb.toString()));
    }

    private static String readFileToString(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str).append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
