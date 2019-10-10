package com.littlec.sdk.config;

import android.content.Context;
import android.util.Log;

import com.littlec.sdk.utils.sp.SdkInfoSp;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: LCChatConfig
 * Description:  sdk setting
 * Creator: user
 * Date: 2016/7/17 23:17
 */
public class LCChatConfig {
    //    public static Context APP_CONTEXT;
    //    public static String CURRENT_USER_NAME = "default_name";
    //    public static final String GUID = "guid";
    public static int logLevel = Log.VERBOSE;
    public static String PUSH_APP_SECRET = "vFtoafMn20/3HKj0fst7Hg==";
    public static String PACKAGE_NAME = "com.littlec.chatdemo";
    public static String PUSH_APP_ID = "2882303761517529625";
    public static String PUSH_APP_KEY = "5861752938625";
    /*************************开发环境*************************/
//    public static String APP_KEY = "001983kv";
//    public static String APPKEY_PASSWD = "9ab3efc90c8bf43772ffb30cf40055fa";
    /*  public static  String fileCommonAddress="http://218.205.115.243:9102";
    public static String postBigFileAddress="http://218.205.115.243:9102/file/rest/uploadservices/breakpointupload";
    public static String postSmallFileAddress="http://218.205.115.243:9102/file/rest/uploadservices/uploadfile";
    public static String postCrashAddress="http://218.205.115.243:9102/file/rest/uploadservices/uploadlog";*/
    /*************************测试环境*************************/
        public static String APP_KEY = "105446ry";
        public static String APPKEY_PASSWD = "6eb57f89c0945d7e4a74912a0b1001ea";
    //    public static  String fileCommonAddress="http://218.205.115.238:9102";
    //    public static String postBigFileAddress="http://218.205.115.238:9102/file/rest/uploadservices/breakpointupload";
    //    public static String postSmallFileAddress="http://218.205.115.238:9102/file/rest/uploadservices/uploadfile";
    //    public static String postCrashAddress="http://218.205.115.238:9102/file/rest/uploadservices/uploadlog";
    //public static String postCrashAddress="http://192.168.110.58:8080/file/rest/uploadservices/uploadlog";

    /**
     * ***********************和应用相关的配置****************************
     */
    public static class APPConfig {
        /**
         * 是否输出log
         */
        public static boolean LOG_OUT = true;

        private static boolean SYSTEM_OUT = false;//配合自动化测试，打印输出

        private static boolean LOG_UPLOAD = true;
        /**
         * 数据库名称
         */
        public static final String DB_ENCRYPTED_NAME = "littlec-encrypted";
        public static final String DB_UNENCRYPTED_NAME = "littlec";

        public static final String EXTRA_STORAGE_ROOT_DIR = "/grpc";

        /**
         * @return the system_out
         */
        public static boolean isSystemoutEnabled() {
            return SYSTEM_OUT;
        }

        /**
         * @param system_out the system_out to set
         */
        public static void setSystemOutEnabled(boolean system_out) {
            APPConfig.SYSTEM_OUT = system_out;
        }

        /**
         * log switch
         * @param flag
         */
        public static void setDebuggerEnabled(boolean flag) {
            LOG_OUT = flag;
        }
    }

    public static class LCChatGlobalStorage {
        private Context context;
        private String userName = "";
        private String passWord = "";
        private String token = "";
        private String appKey = "";
        private String appPassword = "";
        private int pingTime=10;
        private boolean synGuidFlag = false;
        private int X = 0;
        public static String LC_LOG_PATH;
        public static String LC_CRASH_LOG_PATH;
        public static String LC_PROFILE_PATH;
        public static String LC_DATA_PATH;
        public static String LC_DATA_EMAIL_PATH;
        public static String LC_DOWNLOAD_PATH;
        public static String LC_DOWNLOAD_APP_UPDATE_PATH;
        public static String LC_DOWNLOAD_THUMBNAIL_SMALL_PATH;
        public static String LC_DOWNLOAD_THUMBNAIL_MIDDLE_PATH;
        private Map<String, String> isInChat=new HashMap<>();
        private boolean syncMsgFlag=false;
        private EnumLoginStatus Y;
        private static volatile LCChatGlobalStorage Z = null;
        private boolean isBackGround=false;

        private LCChatGlobalStorage() {
            this.Y = EnumLoginStatus.STATE_NONE;
        }

        public static LCChatGlobalStorage getInstance() {
            if (Z == null) {
                Class var0 = LCChatGlobalStorage.class;
                synchronized (LCChatGlobalStorage.class) {
                    if (Z == null) {
                        Z = new LCChatGlobalStorage();
                    }
                }
            }

            return Z;
        }
        public Boolean getIsBackGround(){
            return isBackGround;
        }
        public void setIsBackGround(Boolean isBackGround){
            this.isBackGround=isBackGround;
        }
        public void setSyncMsgFlag(Boolean flag){
            this.syncMsgFlag=flag;
        }
        public boolean getSyncMsgFlag(){
            return syncMsgFlag;
        }

        public void setIsInChat(String conversationId, boolean isInChat) {
            if (isInChat)
                this.isInChat.put(conversationId, "1");
            else
                this.isInChat.put(conversationId, "0");
        }
        public void setPingTime(int pingTime){
            this.pingTime=pingTime;
        }
        public int getPingTime(){
            return  pingTime;
        }

        public boolean getIsInChat(String conversationId) {
            if (isInChat.get(conversationId) != null) {
                if (this.isInChat.get(conversationId).equals("1"))
                    return true;
            }
            return false;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public String getLoginUserName() {
            return this.userName;
        }

        public void setLoginUserName(String var1) {
            this.userName = var1;
        }

        public String getLoginPassWord() {
            return this.passWord;
        }

        public void setLoginPassWord(String var1) {
            this.passWord = var1;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAppKey() {
            return this.appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppPassword() {
            return appPassword;
        }

        public void setAppPassword(String appPassword) {
            this.appPassword = appPassword;
        }

        public void setLoginStatus(EnumLoginStatus var1) {
            this.Y = var1;
        }

        public void setSynGuidFlag(boolean isSyncSuccess) {
            this.synGuidFlag = isSyncSuccess;
        }

        public boolean getSynGuidFlag() {
            return synGuidFlag;
        }

        public EnumLoginStatus getLoginStatus() {
            return this.Y;
        }

        public void setVersion(int var1) {
            this.X = var1;
        }

        public int getVersion() {
            return this.X;
        }

        public void destory() {
            this.setLoginPassWord("");
            this.setLoginUserName("");
            this.setLoginStatus(EnumLoginStatus.STATE_NONE);
            Z = null;
        }
    }

    public static class SdkInfo {
        public static final String PREFS_SDK_INFO_PROFILE = "sdkinfo";
        public static final String SERVICE_CONNECT_DOMAIN = "connect_domain";
        public static final String SERVICE_FILE_DOMAIN = "file_system_domain";
        public static final String SERVICE_VOIP_DOMAIN = "voip_domain";
        public static final String SERVICE_APPKEY = "cmcc-appkey";// 应用的appkey
        public static final String APP_VERSION = "app_version";
        public static final String FRIEND_MODIFIED = "friendModified";
        public static final String FRIEND_REQ_MODIFIED = "friendReqModified";
        public static final String GROUP_LIST_MODIFIED = "groupListModified";
        public static final String GROUP_MEMBER_MODIFIED = "groupMemberModified";
    }

    public static class UserInfo {
        public static final String PREFS_USERINFO_PROFILE = "userinfo";
        public static final String APPKEY = "appKey";
        public static final String USERNAME = "username";
        public static final String NICK = "nick";
        public static final String PHONE = "phone";
        public static final String THUMBNAILLINK= "thumbnailLink";
        public static final String ORIGINALLINK= "originalLink";
        public static final String CREATE_DATE = "create_date";
        public static final String MODIFY_DATE = "modify_date";
        public static final String LOGIN_FLAG = "login_flag";
        public static final String SEND_GUID = "send_guid";
        public static final String REV_GUID = "rev_guid";
        public static final String APPPASSWD = "appPassWd";//不需要保存
        public static final String PASSWORD = "password";//不需要保存

    }

    /**
     * ***********************服务器主机地址****************************
     */
    public static class ServerConfig {

        private static  String CUSTOM_PORT_HOST = "39.100.75.226";

        private static String conAddress;

        private static String fileAddress;

        private static String voipAddress;

//        private static String appkey;
//
//        public static String getAppkey() {
//            if (appkey == null) {
//                appkey = UserInfoSP.getString(UserInfo.APPKEY, "001983vv");
//                return appkey;
//            }
//            return appkey;
//        }

        public static void setCustomPortHost(String customPortHost) {
            CUSTOM_PORT_HOST = customPortHost;
        }

        public static String getConnectAddress() {
//            if (conAddress == null) {
//                conAddress = SdkInfoSp.getString(SdkInfo.SERVICE_CONNECT_DOMAIN, "");
//            }
//            return conAddress;
            return SdkInfoSp.getString(SdkInfo.SERVICE_CONNECT_DOMAIN, "");
        }

        public static String getFileAddress() {

                fileAddress = SdkInfoSp.getString(SdkInfo.SERVICE_FILE_DOMAIN, "");

            return fileAddress;
        }

        public static String getVoipAddress() {
            if (voipAddress == null) {
                voipAddress = SdkInfoSp.getString(SdkInfo.SERVICE_VOIP_DOMAIN, "");
            }
            return voipAddress;
        }
        public static String getMd5Address(){
            StringBuilder sb = new StringBuilder();
            sb.append(getFileAddress());
            sb.append("/file/rest/uploadservices/geturlbymd5");
            return sb.toString();
        }

        public static String getBigFileAddress() {
            StringBuilder sb = new StringBuilder();
            sb.append(getFileAddress());
            sb.append("/file/rest/uploadservices/breakpointupload");
            return sb.toString();
        }

        public static String getSmallFileAddress() {
            StringBuilder sb = new StringBuilder();
            sb.append(getFileAddress());
            sb.append("/file/rest/uploadservices/uploadfile");
            return sb.toString();
        }


        public static String getAdapterConfigAddress() {
            StringBuilder sb = new StringBuilder();
            sb.append("http://" + CUSTOM_PORT_HOST + "/adapter/client/getConfig?appkey=");
            sb.append(LCChatGlobalStorage.getInstance().getAppKey());
            return sb.toString();
        }

        public static String getCrashLogAddress() {
            StringBuilder sb = new StringBuilder();
            sb.append(getFileAddress());
            sb.append("/file/rest/uploadservices/uploadlog");
            return sb.toString();
        }

    }

    enum EnumLoginStatus {
        STATE_NONE,
        STATE_STANDBY,
        STATE_LOGINING,
        STATE_LOGINED,
        STATE_LOGOUTING;
        private EnumLoginStatus() {
        }
    }

}
