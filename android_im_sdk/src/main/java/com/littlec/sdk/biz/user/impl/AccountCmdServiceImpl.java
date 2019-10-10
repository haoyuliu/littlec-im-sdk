/* Project: android_im_sdk
 *
 * File Created at 2016/8/29
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.user.impl;

import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.biz.user.IAccountCmdService;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.listener.LCCommonCallBack;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.utils.sp.UserInfoSP;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/8/29
 * @Version
 */
public class AccountCmdServiceImpl implements IAccountCmdService {
    private static final String TAG = "AccountCmdServiceImpl";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);

    public AccountCmdServiceImpl() {
        super();
    }

    public void doLogin(final LCCommonCallBack callback) {
        Logger.d("doLogin begain initConnection");
        LCGrpcManager.getInstance().initConnection(new LCCommonCallBack() {
            @Override
            public void onSuccess() {
                Logger.d("initConnection success,to doLogin");
                LCGrpcManager.getInstance().doLogin(callback, UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)
                        ? Connector.SessionRequest.ESessionRequestType.LOGIN_BY_AUTO : Connector.SessionRequest.ESessionRequestType.LOGIN);
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                Logger.d("initConnection onFailed,code" + code + ",errorMsg:" + errorMsg);
                callback.onFailed(code, errorMsg);
            }
        });
    }

    public void doLogout(final LCCommonCallBack callback) {
        Logger.e("dologout");
        LCGrpcManager.getInstance().doLogout(new LCCommonCallBack() {
            @Override
            public void onSuccess() {
                LCSingletonFactory.releaseCache();
                callback.onSuccess();
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                LCSingletonFactory.releaseCache();
                callback.onSuccess();
            }
        });

    }

//
//    public LCContact getUserInfo(String userName) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(userName, "getUserInfo");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        ContactEntity contactEntity = new ContactEntity();
//        LCContact contact = new LCContact(contactEntity);
//        User.GetUserInfoResponse getUserInfoResponse = null;
//        try {
//            getUserInfoResponse = User.GetUserInfoResponse.parseFrom(response.getData());
//            if (getUserInfoResponse != null) {
//                int resultCode = getUserInfoResponse.getRet().getNumber();
//                String reason = getUserInfoResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//                User.UserInfo userInfo = getUserInfoResponse.getUserInfos();
//                contact.setUserName(userInfo.getUsername());
//                contact.setNickName(userInfo.getNick());
//                contact.setPhone(userInfo.getPhone());
//                contact.setThumbnailLink(userInfo.getThumbnailLink());
//                contact.setOriginalLink(userInfo.getOriginalLink());
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//        return contact;
//    }
//
//    public List<LCContact> batchGetUserInfo(List<String> users) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(users, "batchGetUserInfo");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        List<LCContact> contacts = new ArrayList<>();
//        User.BatchGetUserInfoResponse batchGetUserInfoResponse = null;
//        try {
//            batchGetUserInfoResponse = User.BatchGetUserInfoResponse.parseFrom(response.getData());
//            if (batchGetUserInfoResponse != null) {
//                int resultCode = batchGetUserInfoResponse.getRet().getNumber();
//                String reason = batchGetUserInfoResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//                List<User.UserInfo> userInfos = batchGetUserInfoResponse.getUserInfoList();
//                for (User.UserInfo userInfo : userInfos) {
//                    ContactEntity contact = new ContactEntity();
//                    contact.setUserName(userInfo.getUsername());
//                    contact.setNickName(userInfo.getNick());
//                    contact.setPhone(userInfo.getPhone());
//                    contact.setThumbnail_link(userInfo.getThumbnailLink());
//                    contact.setOriginal_link(userInfo.getOriginalLink());
//                    LCContact lcContact = new LCContact(contact);
//                    contacts.add(lcContact);
//                }
//
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//        return contacts;
//    }
//
//    public void setSilent(String toUserName, boolean isSilent) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(toUserName, "setSilentState", isSilent);
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.SetSilentStateResponse setSilentResponse = null;
//        try {
//            setSilentResponse = User.SetSilentStateResponse.parseFrom(response.getData());
//            if (setSilentResponse != null) {
//                int resultCode = setSilentResponse.getRet().getNumber();
//                String reason = setSilentResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//            }
//
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//    }
//
//    public void updatePassWord(String passWord) throws LCException {
//        LCRegisterInfo registerInfo = LCRegisterInfo.newBuilder().setPassWord(passWord).build();
//        ILCBuilder builder = new AccountBuilderImpl(registerInfo, "passwordUpdate");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.PasswordUpdateResponse passwordUpdateResponse = null;
//        try {
//            passwordUpdateResponse = User.PasswordUpdateResponse.parseFrom(response.getData());
//            if (passwordUpdateResponse != null) {
//                int resultCode = passwordUpdateResponse.getRet().getNumber();
//                String reason = passwordUpdateResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//    }
//
//    public void updateNickName(String nickName) throws LCException {
//        LCRegisterInfo registerInfo = LCRegisterInfo.newBuilder().setNickName(nickName).build();
//        ILCBuilder builder = new AccountBuilderImpl(registerInfo, "nickUpdate");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.NickUpdateResponse nickUpdateResponse = null;
//        try {
//            nickUpdateResponse = User.NickUpdateResponse.parseFrom(response.getData());
//            if (nickUpdateResponse != null) {
//                int resultCode = nickUpdateResponse.getRet().getNumber();
//                String reason = nickUpdateResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//                UserInfoSP.putString(LCChatConfig.UserInfo.NICK, nickName);
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//    }
//
//    public void updatePhone(String phone) throws LCException {
//        LCRegisterInfo registerInfo = LCRegisterInfo.newBuilder().setPhone(phone).build();
//        ILCBuilder builder = new AccountBuilderImpl(registerInfo, "phoneUpdate");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.PhoneUpdateResponse phoneUpdateResponse = null;
//        try {
//            phoneUpdateResponse = User.PhoneUpdateResponse.parseFrom(response.getData());
//            if (phoneUpdateResponse != null) {
//                int resultCode = phoneUpdateResponse.getRet().getNumber();
//                String reason = phoneUpdateResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//                UserInfoSP.putString(LCChatConfig.UserInfo.PHONE, phone);
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//    }
//
//    public void uploadAvatar(String original_link, String thumbnail_link) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(original_link, thumbnail_link, "uploadAvatar");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.UploadAvatarResponse uploadAvatarResponse = null;
//        try {
//            uploadAvatarResponse = User.UploadAvatarResponse.parseFrom(response.getData());
//            if (uploadAvatarResponse != null) {
//                int resultCode = uploadAvatarResponse.getRet().getNumber();
//                String reason = uploadAvatarResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//
//    }
//
//    public List<LCContact> userSearch(int start, int range, String searchKey) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(start, range, searchKey, "userSearch");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance().sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.UserSearchResponse userSearchResponse = null;
//        List<LCContact> lcContacts = new ArrayList<>();
//        try {
//            userSearchResponse = User.UserSearchResponse.parseFrom(response.getData());
//            if (userSearchResponse != null) {
//                int resultCode = userSearchResponse.getRet().getNumber();
//                String reason = userSearchResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//
//                List<User.UserInfo> userInfoList = userSearchResponse.getUserInfosList();
//                for (User.UserInfo userInfo : userInfoList) {
//                    ContactEntity contactEntity = new ContactEntity();
//                    LCContact contact = new LCContact(contactEntity);
//                    contact.setUserName(userInfo.getUsername());
//                    contact.setNickName(userInfo.getNick());
//                    contact.setPhone(userInfo.getPhone());
//                    contact.setThumbnailLink(userInfo.getThumbnailLink());
//                    contact.setOriginalLink(userInfo.getOriginalLink());
//                    lcContacts.add(contact);
//                }
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//        return lcContacts;
//    }
//
//    public List<String> checkUserList(List<String> users) throws LCException {
//        ILCBuilder builder = new AccountBuilderImpl(users, "checkUserList");
//        Connector.UnaryResponse response = LCGrpcManager.getInstance()
//                .sendUnaryRequest(LCDirector.constructUnaryRequest(builder));
//        if (response.getRet() != ErrorCode.OK) {
//            throw new LCException(LCError.COMMON_SERVER_INNER_ERROR);
//        }
//        User.CheckUserListResponse checkUserListResponse = null;
//        List<String> existUsers = new ArrayList<>();
//        try {
//            checkUserListResponse = User.CheckUserListResponse.parseFrom(response.getData());
//            if (checkUserListResponse != null) {
//                int resultCode = checkUserListResponse.getRet().getNumber();
//                String reason = checkUserListResponse.getRet().name();
//                if (resultCode != 0) {
//                    throw new LCException(resultCode, reason);
//                }
//                existUsers.addAll(checkUserListResponse.getExistUsersList());
//            }
//        } catch (InvalidProtocolBufferException e) {
//            throw new LCException(e.toString());
//        }
//        return existUsers;
//    }


}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/29 user creat
 */
