package com.littlec.sdk;

import android.content.Context;

import com.littlec.sdk.biz.chat.ILCMessageManager;
import com.littlec.sdk.biz.history.ILCHistoryManager;
import com.littlec.sdk.biz.user.ILCAccountManager;
import com.littlec.sdk.config.LCAppConfig;
import com.littlec.sdk.connect.listener.LCConnectionListener;
import com.littlec.sdk.database.api.ILCConversationManager;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.listener.LCCommonCallBack;

/**
 * ClassName: ILCClient
 * Description:
 * Creator: user
 * Date: 2016/7/19 9:02
 */
public interface ILCClient {
    void init(Context appContext, LCAppConfig LCOptions) throws LCException;

    void doLogin(String userName, String passWd, LCCommonCallBack callBack);

    void doLoginByToken(String userName, String token, LCCommonCallBack callBack);

    void doLogOut(LCCommonCallBack callback);

    Context getContext();

    ILCMessageManager.InnerInterface messageManagerInner();

    void addConnectionListener(LCConnectionListener listener);


    ILCAccountManager accountManager();

    ILCMessageManager messageManager();


    ILCHistoryManager hmsManager();

    ILCConversationManager conversationManager();

}
