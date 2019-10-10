package com.littlec.sdk;

import android.content.Context;

import com.littlec.sdk.biz.chat.ILCMessageManager;
import com.littlec.sdk.biz.chat.impl.LCMessageManager;
import com.littlec.sdk.biz.chat.utils.AccountVerifyUtils;
import com.littlec.sdk.biz.history.ILCHistoryManager;
import com.littlec.sdk.biz.history.impl.LCHistoryManager;
import com.littlec.sdk.biz.user.ILCAccountManager;
import com.littlec.sdk.biz.user.impl.LCAccountManager;
import com.littlec.sdk.config.LCAppConfig;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.LCConnectManager;
import com.littlec.sdk.connect.core.LCCmdServiceFactory;
import com.littlec.sdk.connect.repeater.ExcTaskManager;
import com.littlec.sdk.connect.listener.LCConnectionListener;
import com.littlec.sdk.connect.util.LCLoginUtil;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.api.ILCConversationManager;
import com.littlec.sdk.database.api.LCConversationManager;
import com.fingo.littlec.proto.css.Enum;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.utils.CommonUtils;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCNetworkUtil;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.utils.sp.UserInfoSP;

/**
 * ClassName: LCClient
 * Description:  sdk entry
 * Creator: user
 * Date: 2016/7/18 10:56
 */
public class LCClient implements ILCClient {
    private static final String TAG = "LCClient";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);

    public enum ClientType {
        ANDROID(Enum.EClientType.ANDROID_VALUE),
        IOS(Enum.EClientType.IOS_VALUE),
        WEB(Enum.EClientType.WEB_VALUE);

        public static ClientType forNumber(int value) {
            switch (value) {
                case Enum.EClientType.ANDROID_VALUE:
                    return ANDROID;
                case Enum.EClientType.IOS_VALUE:
                    return IOS;
                case Enum.EClientType.WEB_VALUE:
                    return WEB;
                default:
                    return null;
            }
        }

        private final int value;

        ClientType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }


    private Context context;

    public Context getContext() {
        return context;
    }

    public static ILCClient getInstance() {
        return LCSingletonFactory.getInstance(LCClient.class);
    }

    /**
     * MethodName: init <br>
     * Description: 初始化接口 <br>
     * Param: options sdk配置参数 <br>
     * Return:  <br>
     * Date: 2016/7/18 11:31
     */
    @Override
    public void init(Context appContext, LCAppConfig options) throws LCException {
        LCLogger.getLogger(TAG).d("init");
        if (appContext == null || options.getAppkey() == null || options.getAppkey().length() == 0
                || options.getAppKeyPassWd() == null || options.getAppKeyPassWd().length() == 0) {
            throw new LCException(LCError.COMMON_CONTENT_NULL);
        }


//        if(options.getAppkey().length() !=8||options.getAppkey().length() !=32){
//            throw new LCException(LCError.COMMON_APPKEY_ILLEGAL);
//        }
        if (options.getAppKeyPassWd() == null || options.getAppKeyPassWd().length() == 0) {
            throw new LCException(LCError.COMMON_APPKEYPASSWORD_ILLEGAL);
        }

        this.context = appContext.getApplicationContext();
        LCChatConfig.LCChatGlobalStorage.getInstance().setContext(context);
        LCChatConfig.LCChatGlobalStorage.getInstance().setAppKey(options.getAppkey());
        LCChatConfig.LCChatGlobalStorage.getInstance().setAppPassword(options.getAppKeyPassWd());
        LCChatConfig.LCChatGlobalStorage.getInstance().setPingTime(options.getPingTime());
        LCLogger.initLogger(appContext.getApplicationContext(), options.getLogPath());
        if (options.getLogLevel() != 0) {
            LCChatConfig.logLevel = options.getLogLevel();
        }
        CommonUtils.configurePath(context);
        // LCCrashHandler.collectCrashInfo(context,"");

    }

    /**
     * MethodName: doLogin <br>
     * Description: 登录接口 <br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/18 11:31
     */
    @Override
    //和家亲废弃密码登陆，全部用统一认证token登陆
    public void doLogin(String userName, String passWd, LCCommonCallBack callBack) {
        doLoginByPasswd(userName, passWd, callBack);
    }

    public void doLoginByToken(String userName, String token, LCCommonCallBack callBack) {
        if (callBack == null) {
            throw new NullPointerException();
        }
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            callBack.onFailed(LCError.COMMON_NETWORK_DISCONNECTED.getValue(),
                    LCError.COMMON_NETWORK_DISCONNECTED.getDesc());
            return;
        }
        LCException exception = AccountVerifyUtils.verifyLoginData(userName, token);
        if (exception != null) {
            callBack.onFailed(exception.getErrorCode(), exception.getDescription());
            return;
        }
        if (LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable()) {
            //如果已登录成功着，
            callBack.onFailed(LCError.ACCOUNT_ALREADY_CONNECTED.getValue(),
                    LCError.ACCOUNT_ALREADY_CONNECTED.getDesc());
            return;
        }
        LCChatConfig.LCChatGlobalStorage.getInstance().setLoginUserName(userName.toLowerCase());
        LCChatConfig.LCChatGlobalStorage.getInstance().setToken(token);
        LCLoginUtil.doLoginNoAuth(callBack);
    }

    private void doLoginByPasswd(String userName, String passWd, LCCommonCallBack callBack) {
        if (callBack == null) {
            throw new NullPointerException();
        }
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            callBack.onFailed(LCError.COMMON_NETWORK_DISCONNECTED.getValue(),
                    LCError.COMMON_NETWORK_DISCONNECTED.getDesc());
            return;
        }
        LCException exception = AccountVerifyUtils.verifyLoginData(userName, passWd);
        if (exception != null) {
            callBack.onFailed(exception.getErrorCode(), exception.getDescription());
            return;
        }
        if (LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable()) {
            //如果已登录成功着，
            callBack.onFailed(LCError.ACCOUNT_ALREADY_CONNECTED.getValue(),
                    LCError.ACCOUNT_ALREADY_CONNECTED.getDesc());
            return;
        }
        LCChatConfig.LCChatGlobalStorage.getInstance().setLoginUserName(userName.toLowerCase());
        LCChatConfig.LCChatGlobalStorage.getInstance().setLoginPassWord(passWd);
        LCLoginUtil.doLoginNoAuth(callBack);
    }

    /**
     * MethodName:  doLogOut<br>
     * Description: 登出接口 <br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/18 11:31
     */
    @Override
    public void doLogOut(LCCommonCallBack callback) {
        Logger.e("doLogOut");//stopRepeater ,destroy connect,db,sp,singleCache,receivers
        if (callback == null) {
            throw new NullPointerException();
        }
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            Logger.e("doLogOut the user not login");
            callback.onSuccess();
        }
        //停止异常任务
        ExcTaskManager.getInstance().stopRepeater();
        //ping相关
        LCSingletonFactory.getInstance(LCConnectManager.class).onDestroy();
        //grpc连接相关，停止消息拉取

//        DispatchController.getInstance().onDestroy();
        DBFactory.getDBManager().onDestory();
        UserInfoSP.setBoolean(LCChatConfig.UserInfo.LOGIN_FLAG, false);
        UserInfoSP.removeString(LCChatConfig.UserInfo.PASSWORD);
        UserInfoSP.removeString(LCChatConfig.UserInfo.USERNAME);

        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            LCSingletonFactory.releaseCache();
            callback.onSuccess();
            return;
        }

        LCCmdServiceFactory.getAccountService().doLogout(callback);
    }


    /**
     * MethodName: addConnectionListener <br>
     * Description: 添加连接监听 <br>
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/25 14:12
     */
    @Override
    public void addConnectionListener(LCConnectionListener listener) {

    }

    public ILCAccountManager accountManager() {
        return LCSingletonFactory.getInstance(LCAccountManager.class);
    }

    public ILCMessageManager.InnerInterface messageManagerInner() {
        return LCSingletonFactory.getInstance(LCMessageManager.class);
    }

    public ILCMessageManager messageManager() {
        return LCSingletonFactory.getInstance(LCMessageManager.class);
    }


    public ILCHistoryManager hmsManager() {
        return LCSingletonFactory.getInstance(LCHistoryManager.class);
    }

    public ILCConversationManager conversationManager() {
        return LCSingletonFactory.getInstance(LCConversationManager.class);
    }


}
