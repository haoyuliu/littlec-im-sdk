/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.littlec.sdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Base64;

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.config.LCChatConfig.LCChatGlobalStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: CommonUtils
 * @Description: 公共工具类
 * @author: user
 * @date: 2016/8/3 17:33
 */
public class CommonUtils {
    /**
     * @Title: configurePath <br>
     * @Description: 配置所有的本地路径<br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/8/3 17:34
     */
    public static void configurePath(Context context) {
        String rootDir = CommonUtils.getDataDir(context);
        LCChatConfig.LCChatGlobalStorage.LC_DATA_PATH = rootDir
                + LCChatConfig.APPConfig.EXTRA_STORAGE_ROOT_DIR;
        LCChatConfig.LCChatGlobalStorage.LC_DATA_EMAIL_PATH = LCChatGlobalStorage.LC_DATA_PATH
                + "/email";
        LCChatGlobalStorage.LC_PROFILE_PATH = LCChatGlobalStorage.LC_DATA_PATH + "/profiles";
        LCChatGlobalStorage.LC_LOG_PATH = LCChatGlobalStorage.LC_DATA_PATH + "/log";
        LCChatGlobalStorage.LC_CRASH_LOG_PATH = LCChatGlobalStorage.LC_DATA_PATH + "/crash_log";
        LCChatGlobalStorage.LC_DOWNLOAD_PATH = LCChatGlobalStorage.LC_DATA_PATH + "/download";
        LCChatGlobalStorage.LC_DOWNLOAD_APP_UPDATE_PATH = LCChatGlobalStorage.LC_DOWNLOAD_PATH
                + "/appupdate";

        File file = new File(LCChatGlobalStorage.LC_DATA_PATH);
        file.mkdirs();
        file = new File(LCChatGlobalStorage.LC_DATA_EMAIL_PATH);
        file.mkdirs();
//        file = new File(LCChatGlobalStorage.LC_LOG_PATH);
//        file.mkdirs();
        file = new File(LCChatGlobalStorage.LC_CRASH_LOG_PATH);
        file.mkdirs();
        file = new File(LCChatGlobalStorage.LC_DOWNLOAD_PATH);
        file.mkdirs();
        file = new File(LCChatGlobalStorage.LC_DOWNLOAD_APP_UPDATE_PATH);
        file.mkdirs();

        file = new File(rootDir + "/PicPool");
        if (file != null)
            CommonUtils.delete(file);
        file = new File(LCChatGlobalStorage.LC_PROFILE_PATH);
        file.mkdirs();

        LCChatGlobalStorage.LC_DOWNLOAD_PATH = LCChatGlobalStorage.LC_DATA_PATH + "/download/"
                + "personal"; // ProfileDO.getInstance().uid;
        LCChatGlobalStorage.LC_DOWNLOAD_THUMBNAIL_SMALL_PATH = LCChatGlobalStorage.LC_DOWNLOAD_PATH
                + "/Thumbnail_Small";
        LCChatGlobalStorage.LC_DOWNLOAD_THUMBNAIL_MIDDLE_PATH = LCChatGlobalStorage.LC_DOWNLOAD_PATH
                + "/Thumbnail_Middle";
        File thumbnailFile = new File(LCChatGlobalStorage.LC_DOWNLOAD_THUMBNAIL_SMALL_PATH);
        if (!thumbnailFile.exists()) {
            thumbnailFile.mkdirs();
        }
        thumbnailFile = new File(LCChatGlobalStorage.LC_DOWNLOAD_THUMBNAIL_MIDDLE_PATH);
        if (!thumbnailFile.exists()) {
            thumbnailFile.mkdirs();
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String md5Digest32(String input) {
        String str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }

        return str;
    }

    // String in US-ASCII encoding
    public static String base64Encode(byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    public static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }

    /**
     *
     * @方法名：getDataDir
     * @描述：(存放外部数据的根目录)
     * @param context
     * @return
     * @输出：String
     * @作者：Administrator
     *
     */
    public static String getDataDir(Context context) {
        String state = Environment.getExternalStorageState();
        String dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        return dir;
    }

    /**
     *
     * @方法名：delete
     * @描述：(删除文件或目录)
     * @param file
     * @输出：void
     * @作者：Administrator
     *
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static String getAppVersionName(Context context) {
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        String version;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            version = "获取失败";
            return version;
        }
        version = packInfo.versionName;
        return version;
    }

    public static int getAppVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        int version;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
        version = packInfo.versionCode;
        return version;
    }

    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        String str = sdf.format(date);
        return str;
    }

    /**
     * @Title: getCurrentTime  <br>
     * @Description: 获取当前系统时间<br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/9/13 14:21
     */
    public static long getCurrentTime() {
        Date date = new Date();
        return date.getTime();
    }

    /**
     * @Title: getHashMap <br>
     * @Description: arrayMap效率更高 高版本需要获取ArrayMap <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/8/18 19:31
     */
    public static Map getHashMap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new ArrayMap<>();
        } else {
            return new HashMap<>();
        }

    }

    /**
     * @Title: getExcPrintStackTrace <br>
     * @Description: 获取堆栈信息<br>
     * @param:  <br>
     * @return:  <br>
     * @throws: 2016/8/19 12:46
     */
    public static String getExcPrintStackTrace(Throwable t) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

    }

    /**
     * @Title: getUUID <br>
     * @Description: 获取UUID <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/9/13 14:23
     */
    public static synchronized String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static byte[] ObjectToByte(java.lang.Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    public static Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    public static String getStringFromUserList(List<String> userList) {
        if (userList == null || userList.size() < 1) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String userName : userList) {
            stringBuilder.append(userName);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();

    }

    public static String getDateFromTimeMills(long timeMills) {
        if (timeMills <= 0) {
            return "";
        }
        Date curDate = new Date(timeMills);//获取当前时间
        long dayMills = 24 * 60 * 60 * 1000;
        long weekMills = 7 * dayMills;

        SimpleDateFormat formatter = null;
        long currentTime = System.currentTimeMillis();
        long value = currentTime - timeMills;
        if (value < dayMills) {
            formatter = new SimpleDateFormat("HH:mm");
        } else if (value < weekMills && value > dayMills) {
            formatter = new SimpleDateFormat("EEEE");
        } else if (value > weekMills) {
            formatter = new SimpleDateFormat("yy/MM/dd");
        }

        String str = formatter.format(curDate);
        return str;
    }

    public static String getFileMD5(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static boolean isAccountConflict(int code) {
//        return code == ErrorCode.USER_NOT_EXISTED_OR_PWD_ERROR_VALUE
//                || code == ErrorCode.USER_CONFLICT_LOGIN_VALUE
//                || code == ErrorCode.USER_DEVICEID_NOT_MATCH_THE_LAST_VALUE;
        return false;
    }


}
