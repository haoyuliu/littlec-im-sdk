package com.littlec.sdk.biz.user;

import com.littlec.sdk.biz.chat.listener.LCSyncMsgListener;
import com.littlec.sdk.connect.listener.LCConnectionListener;

/**
 * ClassName: ILCAccountManager
 * Description:account manager interface
 * Creator: user
 * Date: 2016/7/18 16:57
 */
public interface ILCAccountManager {

//    void createAccount(LCRegisterInfo registerInfo, LCCommonCallBack callback);

//    void updateNickName(String nickName) throws LCException;
//
//    void updatePhoneNumber(String newPhoneNumber) throws LCException;

//    LCContact getUserInfo(String userName) throws LCException;

    void addConnectListener(LCConnectionListener connectionListener);

    LCConnectionListener getConnectionListener();

    void addSyncMsgListener(LCSyncMsgListener syncMsgListener);

    LCSyncMsgListener getSyncMsgListener();

//    void setSilent(String toUserName, boolean isSilent) throws LCException;
//
//    void setShieldingState(String toUserName, boolean isSilent) throws LCException;
//
//    List<String> getMyShieldingList() throws LCException;
//
//    void updatePassword(String password) throws LCException;

//    List<LCContact> searchUser(int start, int range, String searchKey) throws LCException;

//    void uploadAvatar(String imagePath, final LCCommonCallBack callBack);
//
//    List<String> checkUserList(List<String> users) throws LCException;

//    List<LCContact> batchGetUserInfo(List<String> userName) throws LCException;
//
//    interface InnerInterface {
//        void onDestroy();
//    }
}
