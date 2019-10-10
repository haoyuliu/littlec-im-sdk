///* Project: android_im_sdk
// *
// * File Created at 2016/8/4
// *
// * Copyright 2016 XXX Corporation Limited.
// * All rights reserved.
// *
// * This software is the confidential and proprietary information of
// * XXX Company. ("Confidential Information").  You shall not
// * disclose such Confidential Information and shall use it only in
// * accordance with the terms of the license.
// */
//package com.littlec.sdk.biz.user.impl;
//
//import com.littlec.sdk.biz.user.entity.LCRegisterInfo;
//import com.littlec.sdk.connect.core.ILCBuilder;
//import com.littlec.sdk.connect.core.BaseBuilder;
//import com.littlec.sdk.config.LCChatConfig;
//import com.fingo.littlec.proto.css.Enum;
//import com.fingo.littlec.proto.css.Connector;
//import com.littlec.sdk.utils.SHA256Util;
//
//import java.util.List;
//
///**
// * @Type com.littlec.sdk.chat.core.builder
// * @User zhangguoqiong
// * @Desc
// * @Date 2016/8/4
// * @Version
// */
//
//public class AccountBuilderImpl extends BaseBuilder implements ILCBuilder {
//    private static final String service_name = "littlec-user";
//    private LCRegisterInfo userInfo;
//    private String methodName;
//    private boolean isSilent;
//    private String toUserName;
//    private String nickName;
//    private int start;
//    private int range;
//    private String searchKey;
//    private String original_link;
//    private String thumbnail_link;
//    private List<String> users;
//    /***** begin *新的写法如下，数行就全部写完*/
//    /**
//     * 获取屏蔽列表
//     */
//    public static final String USER_GET_MYSHIELDING_LIST = "getMyShieldingList";
//    public static final String USER_SET_SHIELDING_STATE = "setShieldingState";
//
//    @Override
//    protected String getServiceName() {
//        return service_name;
//    }
//
//    public AccountBuilderImpl(String methodName) {
//        super(methodName);
//        switch (methodName) {
//            case USER_GET_MYSHIELDING_LIST:
//                liteBuilder = User.GetMyShieldingListRequest.newBuilder()
//                        .setAppkey(getAppkey()).setUsername(getUserName());
//                break;
//            case USER_SET_SHIELDING_STATE:
//                liteBuilder = User.SetShieldingStateRequest.newBuilder()
//                        .setMyselfUsername(getUserName())
//                        .setAppkey(getAppkey())
//                        .setFromClientType(Enum.EClientType.ANDROID);
//                break;
//            default:
//                break;
//        }
//    }
//
//    /***** end */
//
//    public AccountBuilderImpl(String original_link, String thumbnail_link, String methodName) {
//        super(methodName);
//        this.original_link = original_link;
//        this.thumbnail_link = thumbnail_link;
//        this.methodName = methodName;
//    }
//
//    public AccountBuilderImpl(LCRegisterInfo userInfo, String methodName) {
//        super(methodName);
//        this.userInfo = userInfo;
//        this.methodName = methodName;
//    }
//
//    public AccountBuilderImpl(String toUserName, String methodName, boolean isSilent) {
//        super(methodName);
//        this.toUserName = toUserName;
//        this.methodName = methodName;
//        this.isSilent = isSilent;
//    }
//
//    public AccountBuilderImpl(String toUserName, String methodName) {
//        super(methodName);
//        this.toUserName = toUserName;
//        this.methodName = methodName;
//    }
//
//    public AccountBuilderImpl(List<String> checkUsers, String methodName) {
//        super(methodName);
//        this.users = checkUsers;
//        this.methodName = methodName;
//    }
//
//    public AccountBuilderImpl(int start, int range, String searchKey, String methodName) {
//        super(methodName);
//        this.start = start;
//        this.range = range;
//        this.searchKey = searchKey;
//        this.methodName = methodName;
//    }
//
//    private User.UserRegisterRequest buildCreateAccount() {
//        User.UserRegisterRequest.Builder userRegisterRequest = User.UserRegisterRequest.newBuilder()
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setUsername(userInfo.getUserName().toLowerCase())
//                .setPassword(SHA256Util.SHA256Encrypt(userInfo.getPassWord()))
//                .setSHA256(true);
//        if (userInfo.getNickName() != null) {
//            userRegisterRequest.setNick(userInfo.getNickName());
//        }
//        if (userInfo.getPhone() != null) {
//            userRegisterRequest.setPhone(userInfo.getPhone());
//        }
//        return userRegisterRequest.build();
//    }
//
//    private User.GetUserInfoRequest buildGetUserInfo() {
//        User.GetUserInfoRequest.Builder getUserInfo = User.GetUserInfoRequest.newBuilder()
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setUsername(toUserName.toLowerCase());
//        return getUserInfo.build();
//    }
//
//    private User.SetSilentStateRequest buildSetSilent() {
//        User.SetSilentStateRequest.Builder setSilent = User.SetSilentStateRequest.newBuilder()
//                .setIsSilent(isSilent)
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setFrom(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())
//                .setTo(toUserName.toLowerCase());
//        return setSilent.build();
//    }
//
//    private User.NickUpdateRequest buildNickUpdate() {
//        User.NickUpdateRequest.Builder nickUpdate = User.NickUpdateRequest.newBuilder()
//                .setNick(userInfo.getNickName())
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
//        return nickUpdate.build();
//    }
//
//    private User.PasswordUpdateRequest buildPasswordUpdate() {
//        User.PasswordUpdateRequest.Builder passwordUpdate = User.PasswordUpdateRequest.newBuilder()
//                .setPassword(SHA256Util.SHA256Encrypt(userInfo.getPassWord()))
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName())
//                .setSHA256(true);
//        return passwordUpdate.build();
//    }
//
//    private User.PhoneUpdateRequest buildPhoneUpdate() {
//        User.PhoneUpdateRequest.Builder phoneUpdate = User.PhoneUpdateRequest.newBuilder()
//                .setUpdatePhone(userInfo.getPhone())
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
//        return phoneUpdate.build();
//    }
//
//    private User.UserSearchRequest buildUserSearch() {
//        User.UserSearchRequest.Builder userSearch = User.UserSearchRequest.newBuilder()
//                .setSearchKey(searchKey)
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setStart(start).setRange(range)
//                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
//        return userSearch.build();
//    }
//
//    private User.UploadAvatarRequest buildUploadAvatar() {
//        User.UploadAvatarRequest.Builder uploadAvatar = User.UploadAvatarRequest.newBuilder()
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .setOriginalLink(original_link).setThumbnailLink(thumbnail_link)
//                .setUsername(LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName());
//        return uploadAvatar.build();
//    }
//
//    private User.CheckUserListRequest buildCheckUserList() {
//        User.CheckUserListRequest.Builder checkUserList = User.CheckUserListRequest.newBuilder()
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .addAllUsername(users);
//        return checkUserList.build();
//    }
//
//    private User.BatchGetUserInfoRequest buildBatchGetUserInfo() {
//        User.BatchGetUserInfoRequest.Builder batchGetUserInfo = User.BatchGetUserInfoRequest.newBuilder()
//                .setAppkey(LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey())
//                .addAllUsername(users);
//        return batchGetUserInfo.build();
//    }
//
//    @Override
//    public Connector.UnaryRequest buildUnaryRequest() {
//        com.fingo.littlec.proto.css.Connector.UnaryRequest request = null;
//        switch (methodName) {
//            case "userRegister":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildCreateAccount().toByteString())
//                        .build();
//                break;
//            case "getUserInfo":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildGetUserInfo().toByteString())
//                        .build();
//                break;
//            case "setSilentState":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildSetSilent().toByteString()).build();
//                break;
//            case "nickUpdate":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildNickUpdate().toByteString())
//                        .build();
//                break;
//            case "passwordUpdate":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildPasswordUpdate().toByteString())
//                        .build();
//                break;
//            case "phoneUpdate":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildPhoneUpdate().toByteString())
//                        .build();
//                break;
//            case "userSearch":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildUserSearch().toByteString())
//                        .build();
//                break;
//            case "uploadAvatar":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildUploadAvatar().toByteString())
//                        .build();
//                break;
//            case "checkUserList":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildCheckUserList().toByteString())
//                        .build();
//                break;
//            case "batchGetUserInfo":
//                request = Connector.UnaryRequest.newBuilder().setServiceName(service_name)
//                        .setMethodName(methodName).setData(buildBatchGetUserInfo().toByteString())
//                        .build();
//                break;
//            default:
//                break;
//
//        }
//        return request;
//    }
//
//
//}
///**
// * Revision history
// * -------------------------------------------------------------------------
// * <p>
// * Date Author Note
// * -------------------------------------------------------------------------
// * 2016/8/4 zhangguoqiong creat
// */
