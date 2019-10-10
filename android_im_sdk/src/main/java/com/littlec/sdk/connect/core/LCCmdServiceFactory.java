/* Project: android_im_sdk
 *
 * File Created at 2016/8/12
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.core;

import com.littlec.sdk.biz.chat.IMessageCmdService;
import com.littlec.sdk.biz.chat.impl.MessageServiceImpl;
import com.littlec.sdk.biz.history.IHmsCmdService;
import com.littlec.sdk.biz.history.impl.HmsCmdServiceImpl;
import com.littlec.sdk.biz.user.IAccountCmdService;
import com.littlec.sdk.biz.user.impl.AccountCmdServiceImpl;
import com.littlec.sdk.utils.LCSingletonFactory;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/8/12
 * @Version
 */
public class LCCmdServiceFactory {

    public static IAccountCmdService getAccountService() {
        return LCSingletonFactory.getInstance(AccountCmdServiceImpl.class);
    }


    public static IMessageCmdService getMessageService() {
        return LCSingletonFactory.getInstance(MessageServiceImpl.class);
    }


    public static IHmsCmdService getHmsService() {
        return LCSingletonFactory.getInstance(HmsCmdServiceImpl.class);
    }


}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/12 user creat
 */
