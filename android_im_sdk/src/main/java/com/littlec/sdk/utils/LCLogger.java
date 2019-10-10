/*
 * Copyright (C) 2011 China Mobile Research Institute All Rights Reserved.
 */

/*
 * Revision History:
 * -----------------------------------------------------------------------------
 * Author | Date | Ticket NO. | Revision NO. | Description of Changes
 * -----------------------------------------------------------------------------
 * mengzhao | 2011-10-11 | N/A | N/A | Initial version.
 * -----------------------------------------------------------------------------
 */
package com.littlec.sdk.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.littlec.sdk.config.LCChatConfig;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: LCLogger
 * Description:
 * Creator: user
 * Date: 2016/7/18 9:00
 */
public class LCLogger {
    private final static String TAG = "LITTLEC-IM-SDK";
    private final static boolean LOG_OUT = LCChatConfig.APPConfig.LOG_OUT;
    // control the level of log
    private int logLevel = LCChatConfig.logLevel;

    private volatile static Hashtable<String, LCLogger> sLoggerTable = new Hashtable<>();

    private String mClassName;

    private static boolean saveLogToPath = false;
    public static boolean DEBUG=false;

    private LCLogger(String mClassName) {
        this.mClassName = mClassName;

    }
    public static void initLogger(Context context,String logPath,boolean isDebug) {
        LCCrashHandler.collectCrashInfo(context,logPath);//crash日志单独存放
        DEBUG=isDebug;
        if (TextUtils.isEmpty(logPath)) {
            saveLogToPath = false;
            return;
        }
        try {
            File file = new File(logPath);
            file.mkdirs();
            LogConfigurator debugLogConfigurator = new LogConfigurator();
            debugLogConfigurator.setUseFileAppender(true);
            debugLogConfigurator.setRootLevel(Level.DEBUG);
            debugLogConfigurator.setPriority(Priority.DEBUG);
            debugLogConfigurator.setLevel("org.apache", Level.DEBUG);
            debugLogConfigurator.setFileName(logPath+ File.separator +"log.txt");
            debugLogConfigurator.setMaxFileSize(3 * 1024 * 1024);
            debugLogConfigurator.setMaxBackupSize(4);
            debugLogConfigurator.setFilePattern("%-d{yyyy-MM-dd HH:mm:ss} [%t:%r]-[%p]-[%-20c] %m%n");
            debugLogConfigurator.configure();
            saveLogToPath = true;
        } catch (Exception e) {
            e.printStackTrace();
            saveLogToPath = false;
        }
    }

    public static void initLogger(Context context,String logPath) {
        LCCrashHandler.collectCrashInfo(context,logPath);//crash日志单独存放
        if (TextUtils.isEmpty(logPath)) {
            saveLogToPath = false;
            return;
        }
        try {
            File file = new File(logPath);
            file.mkdirs();
            LogConfigurator debugLogConfigurator = new LogConfigurator();
            debugLogConfigurator.setUseFileAppender(true);
            debugLogConfigurator.setRootLevel(Level.DEBUG);
            debugLogConfigurator.setPriority(Priority.DEBUG);
            debugLogConfigurator.setLevel("org.apache", Level.DEBUG);
            debugLogConfigurator.setFileName(logPath+ File.separator +"log.txt");
            debugLogConfigurator.setMaxFileSize(3 * 1024 * 1024);
            debugLogConfigurator.setMaxBackupSize(4);
            debugLogConfigurator.setFilePattern("%-d{yyyy-MM-dd HH:mm:ss} [%t:%r]-[%p]-[%-20c] %m%n");
            debugLogConfigurator.configure();
            saveLogToPath = true;
        } catch (Exception e) {
            e.printStackTrace();
            saveLogToPath = false;
        }
    }

    public static LCLogger getLogger(String className) {
        LCLogger classLogger;
        synchronized (sLoggerTable) {
            classLogger = sLoggerTable.get(className);
            if (classLogger == null) {
                classLogger = new LCLogger(className);
                sLoggerTable.put(className, classLogger);
            }
        }
        return classLogger;
    }

    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return CommonUtils.getTime() + "/" + mClassName + ":" + st.getLineNumber()
                    + " [" + Thread.currentThread().getName() + " " + st.getMethodName() + ""
                    + "]";
        }
        return null;
    }

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private static void runInThreadPool(Runnable task) {
        mExecutorService.submit(task);
    }

    private void write(final String log, final Throwable tr) {
        if (!saveLogToPath) {
            return;
        }
        runInThreadPool(
                new Runnable() {
                    @Override
                    public void run() {
                        if (null == tr) {
                            Logger.getLogger(mClassName).debug(DEBUG ? log : encrypt(log));
                        } else if (DEBUG) {
                            Logger.getLogger(mClassName).debug(log, tr);
                        } else {
                            Logger.getLogger(mClassName).debug(encrypt(log), tr);
                        }
                    }
                });
    }
    public static String encrypt(String str) {
        StringBuffer buf = new StringBuffer();
        char[] cs = str.toCharArray();

        for(int i = 0; i < cs.length; ++i) {
            char a = (char)(cs[i] + 5);
            buf.append(a);
        }

        return buf.toString();
    }

    public void i(Object str) {
        if (LOG_OUT) {
            if (logLevel <= Log.INFO) {
                write(str + "", null);
                String name = getFunctionName();
                if (name != null) {
                    Log.i(TAG, name + " - " + str);
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + name + " - " + str);
                    }
                } else {
                    Log.i(TAG, str.toString());
                    if (LCChatConfig.APPConfig.isSystemoutEnabled())
                        System.out.println(TAG + str.toString());
                }
            }
        }
    }

    public void d(Object str) {
        if (LOG_OUT) {
            if (logLevel < Log.DEBUG) {
                write(str + "", null);
                String name = getFunctionName();
                if (name != null) {
                    Log.d(TAG, name + " - " + str);
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + ":" + name + " - " + str);
                    }
                } else {
                    Log.d(TAG, str.toString());
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + ":" + str.toString());
                    }
                }
            }
        }
    }

    public void v(Object str) {
        if (LOG_OUT) {
            if (logLevel <= Log.VERBOSE) {
                write(str + "", null);
                String name = getFunctionName();
                if (name != null) {
                    Log.v(TAG, name + " - " + str);
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + name + " - " + str);
                    }
                } else {
                    Log.v(TAG, str.toString());
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + str.toString());
                    }
                }
            }
        }
    }

    public void w(Object str) {
        if (LOG_OUT) {
            if (logLevel <= Log.WARN) {
                write(str + "", null);
                String name = getFunctionName();
                if (name != null) {
                    Log.w(TAG, name + " - " + str);
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + name + " - " + str);
                    }
                } else {
                    Log.w(TAG, str.toString());
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + str.toString());
                    }
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(Object str) {
        if (LOG_OUT) {
            if (logLevel <= Log.ERROR) {
                write(str + "", null);
                String name = getFunctionName();
                if (name != null) {
                    Log.e(TAG, name + " - " + str);
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + name + " - " + str);
                    }
                } else {
                    Log.e(TAG, str.toString());
                    if (str instanceof Throwable) {
                        Log.e(TAG, CommonUtils.getExcPrintStackTrace((Throwable) str));
                    }
                    if (LCChatConfig.APPConfig.isSystemoutEnabled()) {
                        System.out.println(TAG + str.toString());
                    }
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param
     */
    public void e(Exception e) {
        if (LOG_OUT) {
            if (logLevel <= Log.ERROR) {
                write(e.toString() + "", null);
                Log.e(TAG, "error", e);
                Log.e(TAG, CommonUtils.getExcPrintStackTrace(e));
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (LOG_OUT) {
            String line = getFunctionName();
            Log.e(TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName + line
                    + ":] " + log + "\n", tr);
        }
    }


}
