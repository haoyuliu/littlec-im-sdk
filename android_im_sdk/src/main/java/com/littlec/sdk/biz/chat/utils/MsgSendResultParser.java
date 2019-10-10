/* Project: android_im_sdk
 *
 * File Created at 2016/9/8
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.utils;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCReadReceiptMessageBody;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.fingo.littlec.proto.css.Chat;
import com.littlec.sdk.utils.LCLogger;

/**
 * @Type com.littlec.sdk.chat.core.parser
 * @User user
 * @Desc 发送消息结果解析
 * @Date 2016/9/8
 * @Version
 */
public class MsgSendResultParser {
    private static final String TAG = "MsgSendResultParser";
    private static LCLogger Logger = LCLogger.getLogger(TAG);

    public static void handleOnNextSucess(Chat.ChatMessageResponse chatResponse, LCMessage message) {
        if (message.contentType() == LCMessage.ContentType.RETRACT) {
            MediaEntity entity = new MediaEntity();
            Logger.d("retracted a message");
            entity.setContent("你撤回了一条消息");
            DBFactory.getDBManager().getDBMediaService().insert(entity);
            message.LCMessageEntity().setMediaEntity(entity);
            ConversationEntity conversationEntity = GetDataFromDB.insertOrUpdateConversationEntity(message, true);
            message.LCMessageEntity().setConversationId(conversationEntity.get_recipientId());
            DBFactory.getDBManager().getDBMessageService().insertOrReplace(message.LCMessageEntity());
            DBFactory.getDBManager().getDBMessageService().queryBuilder()
                    .where(MessageEntityDao.Properties.MsgId.eq(((LCReadReceiptMessageBody) message.LCMessageBody()).getReceipt_msgId()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
        }
        message.LCMessageEntity().setGuid(chatResponse.getGuid());
        message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_SUCCESS.value());
        if (message.LCMessageEntity().getContentType() != LCMessage.ContentType.READ_RECEIPT_NUM) {
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
            GetDataFromDB.insertOrUpdateConversationEntity(message, true);
        }
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/8 user creat
 */
