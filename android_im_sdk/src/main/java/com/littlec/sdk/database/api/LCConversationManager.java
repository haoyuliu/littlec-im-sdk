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

import com.littlec.sdk.LCClient;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.ConversationEntityDao;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.config.LCChatConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @Type com.littlec.sdk.manager.managerimpl
 * @User user
 * @Desc 会话管理类
 * @Date 2016/10/17
 * @Version
 */
public class LCConversationManager implements ILCConversationManager {

    private LCConversationManager() {
    }

    /**
     * @Title: getConversationList <br>
     * @Description: 获取会话列表 <br>
     * @param: void <br>
     * @return: void <br>
     * @throws: 2016/10/17 15:05
     */
    public List<LCConversation> getConversationList() {
        List<ConversationEntity> conversationEntities = DBFactory.getDBManager()
                .getDBConversationService().queryBuilder().orderDesc(ConversationEntityDao.Properties.Date)
                .list();
        if (conversationEntities == null) {
            return null;
        }
        List<LCConversation> conversationList = new ArrayList<>();
        for (ConversationEntity entity : conversationEntities) {
            conversationList.add(new LCConversation(entity));
        }
        return conversationList;
    }

    //    public void updateConversation(LCConversation lcConversation){
//        if(lcConversation!=null)
//        DBFactory.getDBManager().getDBConversationService().update(lcConversation.getConversationEntity());
//    }
    public void deleteConversation(String conversationId) {
        if (conversationId == null)
            return;
        LCClient.getInstance().messageManager().deleteAllMessageFromDB(conversationId);
        DBFactory.getDBManager().getDBConversationService().deleteByKey(conversationId);
    }

    public LCConversation getConversation(String conversationId) {
        if (conversationId == null)
            return null;
        ConversationEntity conversationEntity = DBFactory.getDBManager().getDBConversationService()
                .load(conversationId);
        if (conversationEntity != null) {
            return new LCConversation(conversationEntity);
        } else
            return null;
    }

    //    public void clearConversationUnread(String conversationId){
//        if(conversationId!=null){
//            ConversationEntity conversationEntity=DBFactory.getDBManager().getDBConversationService().load(conversationId);
//            if(conversationEntity!=null){
//                conversationEntity.setUnreadCount(0);
//                DBFactory.getDBManager().getDBConversationService().update(conversationEntity);
//            }
//        }
//    }
    public void setIsInChat(String conversationId, boolean isInChat) {
        if (conversationId == null)
            return;
        if (isInChat) {
            if (conversationId != null) {
                ConversationEntity conversationEntity = DBFactory.getDBManager()
                        .getDBConversationService().load(conversationId);
                if (conversationEntity != null) {
                    conversationEntity.setUnreadCount(0);
                    DBFactory.getDBManager().getDBConversationService().update(conversationEntity);
                }
                LCChatConfig.LCChatGlobalStorage.getInstance().setIsInChat(conversationId, true);
            }
        } else {
            LCChatConfig.LCChatGlobalStorage.getInstance().setIsInChat(conversationId, false);
        }

    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/10/17 user creat
 */
