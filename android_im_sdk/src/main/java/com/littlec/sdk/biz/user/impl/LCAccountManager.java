package com.littlec.sdk.biz.user.impl;

import com.littlec.sdk.biz.LCAbstractManager;
import com.littlec.sdk.biz.chat.listener.LCSyncMsgListener;
import com.littlec.sdk.biz.user.ILCAccountManager;
import com.littlec.sdk.connect.listener.LCConnectionListener;

/**
 * ClassName: LCAccountManager
 * Description:
 * Creator:user
 * Date: 2016/7/18 10:54
 */
public class LCAccountManager extends LCAbstractManager implements ILCAccountManager {
    private LCConnectionListener connectionListener;
    private LCSyncMsgListener syncMsgListener;

    private LCAccountManager() {
    }

    public void addConnectListener(LCConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void addSyncMsgListener(LCSyncMsgListener syncMsgListener) {
        this.syncMsgListener = syncMsgListener;
    }

    public LCConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public LCSyncMsgListener getSyncMsgListener() {
        return syncMsgListener;
    }



    /**
     * @Title: getUserInfo <br>
     * @Description: 获取用户信息 <br>
     * @param: <br>
     * @return: LCContact <br>
     * @throws: 2016/9/26 16:15
     */
//    public LCContact getUserInfo(String userName) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!GroupVerifyUtils.checkUserName(userName)) {
//            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
//        }
//        userName = userName.toLowerCase();
//        return LCCmdServiceFactory.getAccountService().getUserInfo(userName);
//    }
//
//    /**
//     * @Title: getUserInfo <br>
//     * @Description: 批量获取用户信息 <br>
//     * @param: <br>
//     * @return: LCContact <br>
//     * @throws: 2016/9/26 16:15
//     */
//    public List<LCContact> batchGetUserInfo(List<String> userName) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!BaseVerifyUtils.checkMutiUserName(userName)) {
//            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
//        }
//        List<String> lowerUsers = new ArrayList<>();
//        for (String user : userName) {
//            user = user.toLowerCase();
//            lowerUsers.add(user);
//        }
//        return LCCmdServiceFactory.getAccountService().batchGetUserInfo(lowerUsers);
//    }
//
//    /**
//     * @Title: setSilent <br>
//     * @Description: 设置个人静默<br>
//     * @param: toUserName要设置静默的用户名，isSilent是否静默 <br>
//     * @return: void <br>
//     * @throws: 2016/9/29 10:03
//     */
//    public void setSilent(String toUserName, boolean isSilent) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!BaseVerifyUtils.checkUserName(toUserName)) {
//            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
//        }
//        toUserName = toUserName.toLowerCase();
//        LCCmdServiceFactory.getAccountService().setSilent(toUserName, isSilent);
//    }
//
//
//    /**
//     * @Title: setShieldingState <br>
//     * @Description: 设置屏蔽，不再接收到消息<br>
//     */
//    public void setShieldingState(String toUserName, boolean isShield) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!BaseVerifyUtils.checkUserName(toUserName)) {
//            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
//        }
//        toUserName = toUserName.toLowerCase();
//        AccountBuilderImpl builder = new AccountBuilderImpl(AccountBuilderImpl.USER_SET_SHIELDING_STATE);
//        ((User.SetShieldingStateRequest.Builder) builder.getLiteBuilder()).setOtherUsername(toUserName).setIsShielding(isShield);
//        Connector.UnaryResponse response = BaseClient.sendUnaryRequest(builder.unaryRequestBuild());
//        User.SetShieldingStateResponse dataResponse = null;
//        try {
//            dataResponse = User.SetShieldingStateResponse.parseFrom(response.getData());
//            if (dataResponse.getRet() != ErrorCode.OK) {
//                Logger.e("setShieldingState error, code:" + dataResponse.getRet());
//                throw new LCException(dataResponse.getRet().getNumber(), dataResponse.getRet().name());
//            } else {
//                Logger.d("setShieldingState sucess");
//            }
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取屏蔽的用户列表
//     */
//    public List<String> getMyShieldingList() throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        AccountBuilderImpl builder = new AccountBuilderImpl(AccountBuilderImpl.USER_GET_MYSHIELDING_LIST);
//        Connector.UnaryResponse response = BaseClient.sendUnaryRequest(builder.unaryRequestBuild());
//        User.GetMyShieldingListResponse dataResponse = null;
//        try {
//            dataResponse = User.GetMyShieldingListResponse.parseFrom(response.getData());
//            if (dataResponse.getRet() != ErrorCode.OK) {
//                Logger.e("getMyShieldingList error, code:" + dataResponse.getRet());
//                throw new LCException(dataResponse.getRet().getNumber(), dataResponse.getRet().name());
//            } else {
//                Logger.d("getMyShieldingList sucess");
//            }
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//        return dataResponse == null ? null : dataResponse.getUsernamesList();
//    }
//
//    /**
//     * @Title: updatePassword <br>
//     * @Description: 更新密码<br>
//     * @param: password 密码<br>
//     * @return: void <br>
//     * @throws: 2016/10/19 17:32
//     */
//    public void updatePassword(String newPassword) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!AccountVerifyUtils.checkPassWord(newPassword)) {
//            throw new LCException(LCError.ACCOUNT_PASSWORD_ILLEGAL);
//        }
//        LCCmdServiceFactory.getAccountService().updatePassWord(newPassword);
//    }
//
//    /**
//     * MethodName: updateNickName <br>
//     * Description: 更新昵称 <br>
//     * Param:  <br>
//     * Return:  <br>
//     * Date: 2016/7/18 11:31
//     */
//    @Override
//    public void updateNickName(String nickName) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (nickName == null)
//            nickName = "";
//        if (!BaseVerifyUtils.checkNickName(nickName)) {
//            throw new LCException(LCError.ACCOUNT_NICKNAME_UNREQUIRED);
//        }
//        LCCmdServiceFactory.getAccountService().updateNickName(nickName.trim());
//    }
//
//    /**
//     * MethodName: UpdatePhoneNumber <br>
//     * Description:  更新手机号<br>
//     * Param: newPhoneNumber <br>
//     * Return: void<br>
//     * Date: 2016/7/18 11:32
//     */
//    @Override
//    public void updatePhoneNumber(String newPhoneNumber) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (newPhoneNumber == null || newPhoneNumber == "")
//            throw new LCException(LCError.ACCOUNT_PHONE_UNREQUIRED);
//        if (!BaseVerifyUtils.checkPhone(newPhoneNumber)) {
//            throw new LCException(LCError.ACCOUNT_PHONE_UNREQUIRED);
//        }
//        LCCmdServiceFactory.getAccountService().updatePhone(newPhoneNumber);
//    }
//
//    /**
//     * @Title: userSearch <br>
//     * @Description: 搜索联系人 <br>
//     * @param: <br>
//     * @return: <br>
//     * @throws: 2016/10/19 19:01
//     */
//    public List<LCContact> searchUser(int start, int range, String searchKey) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!BaseVerifyUtils.checkUserName(searchKey)) {
//            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
//        }
//        return LCCmdServiceFactory.getAccountService().userSearch(start, range, searchKey);
//    }
//
//    /**
//     * @Title: uploadAvatar <br>
//     * @Description: 上传个人头像 <br>
//     * @param: <br>
//     * @return: <br>
//     * @throws: 2016/11/17 16:27
//     */
//    public void uploadAvatar(String imagePath, final LCCommonCallBack callBack) {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            callBack.onFailed(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//            return;
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            callBack.onFailed(LCError.COMMON_NETWORK_DISCONNECTED.getValue(),
//                    LCError.COMMON_NETWORK_DISCONNECTED.getDesc());
//            return;
//        }
//        if (imagePath == null) {
//            callBack.onFailed(LCError.MESSAGE_FILE_PATH_EMPTY.getValue(),
//                    LCError.MESSAGE_FILE_PATH_EMPTY.getDesc());
//            return;
//        }
//        LCException e = BaseVerifyUtils.checkFileformat(imagePath);
//        if (e != null) {
//            callBack.onFailed(e.getErrorCode(), e.getDescription());
//            return;
//        }
//        Callback callback = new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                try {
//                    if (response.isSuccessful()) {
//                        String result = response.body().string();
//                        System.out.print(result);
//                        JSONObject jsonObject = new JSONObject(result);
//                        String small_link = jsonObject.optString("small_link");
//                        String large_link = jsonObject.optString("large_link");
//                        LCCmdServiceFactory.getAccountService().uploadAvatar(large_link,
//                                small_link);
//                        callBack.onSuccess();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (LCException e) {
//                    callBack.onFailed(e.getErrorCode(), e.getDescription());
//                }
//            }
//        };
//        HttpPostTask.newBuilder().uploadAvatar(imagePath, callback);
//    }
//
//    public List<String> checkUserList(List<String> users) throws LCException {
//        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
//            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
//                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
//        }
//        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
//            throw new LCException(COMMON_NETWORK_DISCONNECTED);
//        }
//        if (!BaseVerifyUtils.checkMutiUserName(users))
//            throw new LCException(ACCOUNT_USERNAME_ILLEGAL);
//        return LCCmdServiceFactory.getAccountService().checkUserList(users);
//    }


}
