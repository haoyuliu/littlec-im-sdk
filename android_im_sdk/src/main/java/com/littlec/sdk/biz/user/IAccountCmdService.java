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
package com.littlec.sdk.biz.user;

import com.littlec.sdk.biz.user.entity.LCRegisterInfo;
import com.littlec.sdk.connect.core.ILCCmdService;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.listener.LCCommonCallBack;

import java.util.List;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/8/29
 * @Version
 */
public interface IAccountCmdService extends ILCCmdService {
    void doLogin(LCCommonCallBack callback);

    void doLogout(LCCommonCallBack callBack);

//    void createAccount(LCRegisterInfo userInfo) throws LCException;

//    void setSilent(String toUserName, boolean isSilent) throws LCException;
//
//    void updatePassWord(String passWord) throws LCException;
//
//    void updateNickName(String nickName) throws LCException;
//
//    void updatePhone(String phone) throws LCException;
//
//    void uploadAvatar(String original_link, String thumbnail_link) throws LCException;
//
//    List<String> checkUserList(List<String> users) throws LCException;
//    void onDestroy();

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/29 user creat
 */
