/* Project: android_im_sdk
 *
 * File Created at 2016/7/28
 *
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.repeater;

import com.fingo.littlec.proto.css.CssErrorCode;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.listener.AynMsgResponseListener;
import com.littlec.sdk.connect.core.LCBaseTask;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.connect.util.LCDirector;
import com.littlec.sdk.biz.chat.impl.MessageBuilderImpl;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.database.DBFactory;
import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc 异常消息处理类
 * @Date 2016/7/28
 * @Version
 */
class ExcMsgTask extends LCBaseTask {
    private static final String TAG = "ExcMsgTask";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);

    public ExcMsgTask(Object packet) {
        super(packet);
    }

    @Override
    public void run() {
        /***************执行操作***************/
        if (packet == null) {
            Logger.e("packet is null");
            return;
        }

        final LCMessage message = (LCMessage) packet;
        ILCBuilder builder = new MessageBuilderImpl(message);
        Connector.UnaryRequest request = LCDirector.constructUnaryRequest(builder);
        Logger.d("ExcMsgTask start run the exc msgid:" + message.LCMessageEntity().getMsgId());
        LCGrpcManager.getInstance().sendAynRequest(request,
                new AynMsgResponseListener() {
                    @Override
                    public void onNext(Chat.ChatMessageResponse response) {
                        Logger.d("onNext,ret:" + response.getRet());
                        if (response.getRet() == CssErrorCode.ErrorCode.OK) {
                            //加入异常处理模块，应该只针对
                            ExcTaskManager.getInstance().removeExceptionTask(getTaskID());
                            //删除重发表里面的数据
                            Logger.d("send success,taskid:" + getTaskID());
//                            Logger.d(((LCTextMessageBody) message.LCMessageBody())
//                                    .getMessageContent());
//                            if(message.contentType()== LCMessage.ContentType.RETRACT){
//                                Logger.d("撤回消息成功");
//                                MediaEntity entity = new MediaEntity();
//                                entity.setContent("你撤回了一条消息");
//                                DBFactory.getDBManager().getDBMediaService().insert(entity);
//                                message.LCMessageEntity().setMediaEntity(entity);
//                                ConversationEntity conversationEntity = GetDataFromDB
//                                        .insertOrUpdateConversationEntity(message);
//                                message.LCMessageEntity().setConversationId(conversationEntity.get_recipientId());
//                                DBFactory.getDBManager().getDBMessageService().insertOrReplace(message.LCMessageEntity());
//                                DBFactory.getDBManager().getDBMessageService().queryBuilder()
//                                        .where(MessageDao.Properties.MsgId.eq(
//                                                ((LCReadReceiptMessageBody) message.LCMessageBody()).getReceipt_msgId()))
//                                        .buildDelete().executeDeleteWithoutDetachingEntities();
//                            }
                            ExcTaskManager.getInstance().removeDBExceptionTask(getTaskID());
                            if (response.getGuid() == 0) {
                                Logger.e("receive msg guid==0," + response.toString());
                            }
                            //更新发送消息
                            message.LCMessageEntity().setGuid(response.getGuid());
                            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_SUCCESS.value());
                            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
                            DispatchController.getInstance().onSuccess(message);
                            Logger.d("success,msgId:" + message.getMsgId());
                        } else if (response.getRet() == CssErrorCode.ErrorCode.CHAT_RECEIVER_NOT_EXIST
                             /*   || response.getRet() == ErrorCode.CHAT_SENDER_IS_SHIELDED
                                || response.getRet() == ErrorCode.GROUP_NOT_EXIST
                                || response.getRet() == ErrorCode.GROUP_REQUESTER_NOT_IN_GROUP*/) {
                            Logger.e("sendAynRequest onNext chatResponse ,code name:" + response.getRet().toString());
                            ExcTaskManager.handleSendError(getTaskID(), message, response.getRet());
                        } else {
                            Logger.e("send error ,not ok，msgid:" + message.LCMessageEntity().getMsgId());
                            resetTask();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Logger.e("onError, msgid:" + message.LCMessageEntity().getMsgId() + "," + t);
                        resetTask();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.d("onCompleted");
                    }
                });
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/28 user creat
 */
