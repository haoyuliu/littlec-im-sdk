/* Project: android_im_sdk
 * 
 * File Created at 2016/9/18
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.history;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.lang.LCException;

import java.util.List;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/9/18
 * @Version
 */
public interface IHmsCmdService {
    List<LCMessage> getHmsMessage(LCMessage.ChatType type, String targetUserName, String beginMsgId,
                                  int limit)
            throws LCException;

    void deleteAllSession(LCMessage.ChatType type, String targetName) throws LCException;

    void deleteMessage(LCMessage.ChatType type, String targetUserName, List<String> msgList)
            throws LCException;
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/18 user creat
 */
