/* Project: android_im_sdk
 * 
 * File Created at 2016/10/17
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.database.api;

import com.littlec.sdk.database.api.LCConversation;

import java.util.List;

/**
 * @Type com.littlec.sdk.manager.imanager
 * @User user
 * @Desc
 * @Date 2016/10/17
 * @Version
 */
public interface ILCConversationManager {

    List<LCConversation> getConversationList();
//    void updateConversation(LCConversation lcConversation);
    LCConversation getConversation(String conversationId);
//    void clearConversationUnread(String conversationId);
    void setIsInChat(String conversationId,boolean isInChat);
    void deleteConversation(String conversationId);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/17 user creat
 */