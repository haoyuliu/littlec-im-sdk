package com.littlec.sdk.utils.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.utils.AESEncryptor;
import com.littlec.sdk.utils.LCLogger;

import java.io.File;

/**
 * ClassName: LCPreferenceUtils
 * Description:  sharedpreference util
 * Creator: user
 * Date: 2016/7/17 23:23
 */
public class LCPreferenceUtils {
    protected final static LCLogger Logger = LCLogger.getLogger(LCPreferenceUtils.class.getName());
    protected static boolean isEncryped = false;
    protected final static String ENCRYPTPASSWD = "LITTLEC";
    private static SharedPreferences prefs;


    public static void modifiedFileName(String phone, String prefName) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        String sdkInfoSpName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                phone + "_" + prefName;
        String filePath = "/data/data/" + LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getPackageName().toString() + "/shared_prefs";
        File file = new File(filePath, sdkInfoSpName + ".xml");
        if (!file.exists()) {
            return;
        }
        String newSdkInfoName = "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_" +
                LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_" + prefName;
        file.renameTo(new File(filePath, newSdkInfoName + ".xml"));
    }

    /**
     * set String Preference Value
     *
     * @param prefName Preference name
     * @param value    Preference value
     */
    public static void putString(String prefName, String value, String PREFS_FILE_NAME) {
        if (isEncryped) {
            try {
                value = AESEncryptor.encrypt(ENCRYPTPASSWD, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.commit();
    }

    /**
     * get String Preference Value
     *
     * @param context
     * @param prefName
     * @return
     */
    public static String getString(String prefName, String defaultValue, String PREFS_FILE_NAME) {
        try {
            prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                    Context.MODE_PRIVATE);
            String result = prefs.getString(prefName, defaultValue);
            if (isEncryped) {
                try {
                    result = AESEncryptor.decrypt(ENCRYPTPASSWD, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    // -----------------------------------------------
    public static void removeString(String prefName, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.remove(prefName);
        editor.commit();
    }


    /**
     * set Integer Preference Value
     *
     * @param context
     * @param prefName
     * @param Value
     */
    public static void putInt(String prefName, int Value, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(prefName, Value);
        editor.commit();
    }

    // -----------------------------------------------

    /**
     * get Integer Preference Value
     *
     * @param context
     * @param prefName
     * @return
     */
    public static int getInt(String prefName, int defaultValue, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getInt(prefName, defaultValue);
    }

    // -----------------------------------------------

    /**
     * set Boolean Preference Value
     *
     * @param context
     * @param prefName
     * @param Value
     */
    public static void setBoolean(String prefName, Boolean Value,
                                  String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(prefName, Value);
        editor.commit();
    }

    // -----------------------------------------------

    /**
     * get Boolean Preference Value
     *
     * @param context
     * @param prefName
     * @return
     */
    public static boolean getBoolean(String prefName, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(prefName, false);
    }

    // -----------------------------------------------

    /**
     * set Float Preference Value
     *
     * @param context
     * @param prefName
     * @param Value
     */
    public static void setFloat(Context context, String prefName, Float Value,
                                String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putFloat(prefName, Value);
        editor.commit();
    }

    // -----------------------------------------------
    // static File SDCardRoot = Environment.getExternalStorageDirectory();

    /**
     * get Float Preference Value
     *
     * @param context
     * @param prefName
     * @return
     */
    public static float getFloat(Context context, String prefName, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getFloat(prefName, 0);
    }

    // ------------------------------------------------

    /**
     * set Long Preference Value
     *
     * @param context
     * @param prefName
     * @param Value
     */
    public static void putLong(String prefName, Long Value, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(prefName, Value);
        editor.commit();
    }

    // -----------------------------------------------

    /**
     * get Long Preference Value
     *
     * @param prefName
     * @return
     */
    public static long getLong(String prefName, String PREFS_FILE_NAME) {
        prefs = LCChatConfig.LCChatGlobalStorage.getInstance().getContext().getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getLong(prefName, 0);
    }
}
