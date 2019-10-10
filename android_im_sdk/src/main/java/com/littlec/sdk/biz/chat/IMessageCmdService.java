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
package com.littlec.sdk.biz.chat;

import com.fingo.littlec.proto.css.Chat;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.connect.core.ILCCmdService;
import com.littlec.sdk.lang.LCException;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/8/29
 * @Version
 */
public interface IMessageCmdService extends ILCCmdService {
    void sendPacket(LCMessage message);
    Chat.FAQListResp getFAQUnit(String id, String input,String language) throws LCException;
    Chat.CustomerServiceResp getCustomerService  (String id, String input) throws LCException;
    Chat.CheckBundleExistsResp checkBundleExists () throws LCException;
    //    void setToken(String regId,String pushAppSecret,String packName) throws LCException;
//    void clearToken()throws LCException;
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/29 user creat
 */