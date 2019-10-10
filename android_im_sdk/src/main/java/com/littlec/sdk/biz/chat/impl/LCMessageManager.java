package com.littlec.sdk.biz.chat.impl;

import com.fingo.littlec.proto.css.Chat;
import com.littlec.sdk.biz.DispatchController;
import com.littlec.sdk.biz.LCAbstractManager;
import com.littlec.sdk.biz.chat.ILCMessageManager;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.entity.body.LCATMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCAudioMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCCustomNoApnsMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFamilyCustomMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCFileMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCImageMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCLocationMessageBody;
import com.littlec.sdk.biz.chat.entity.body.LCTextMessageBody;
import com.littlec.sdk.biz.chat.listener.LCMessageListener;
import com.littlec.sdk.biz.chat.listener.LCMessageSendCallBack;
import com.littlec.sdk.biz.chat.utils.BaseVerifyUtils;
import com.littlec.sdk.biz.chat.utils.ChatVerifyUtils;
import com.littlec.sdk.biz.chat.utils.GroupVerifyUtils;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.connect.LCConnectManager;
import com.littlec.sdk.connect.core.LCCmdServiceFactory;
import com.littlec.sdk.database.DBFactory;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.entity.ConversationEntity;
import com.littlec.sdk.database.entity.MediaEntity;
import com.littlec.sdk.database.entity.MessageEntity;
import com.fingo.littlec.proto.css.Msg;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.net.HttpPostTask;
import com.littlec.sdk.net.UploadFactory;
import com.littlec.sdk.utils.LCLogger;
import com.littlec.sdk.utils.LCSingletonFactory;
import com.littlec.sdk.utils.sp.UserInfoSP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.littlec.sdk.lang.LCError.MESSAGE_EXIST_ERROR;

/**
 * ClassName: LCMessageManager
 * Description: message model manager
 * Creator: user
 * Date: 2016/7/18 12:17
 */
public class LCMessageManager extends LCAbstractManager implements ILCMessageManager, ILCMessageManager.InnerInterface {
    private static final String TAG = "LCMessageManager";
    private LCLogger Logger = LCLogger.getLogger(TAG);
    private volatile LCMessageSendCallBack callBack;
    private volatile LCMessageListener listener;

    private LCMessageManager() {
    }

    public void setMessageStatusCallback(LCMessageSendCallBack callBack) {
        this.callBack = callBack;
    }

    public void addMessageListener(LCMessageListener listener) {
        /*  DispatchController.getInstance().register(DispatchController.CallBackType.LCMessageListener,
                listener);*/
        this.listener = listener;
    }

    public LCMessageSendCallBack getCallBack() {
        if (callBack == null) {
            Logger.e("LCMessageSendCallBack is null");
            callBack = new LCMessageSendCallBack() {
                @Override
                public void onSuccess(LCMessage message) {
                    Logger.d("send sucess callback." + message);
                }

                @Override
                public void onError(LCMessage message, int code, String errorString) {
                    Logger.d("message=" + message + "code=" + code + " errorString" + errorString);
                }

                @Override
                public void onProgress(LCMessage message, int progress) {
                    Logger.d("message=" + message + "progress=" + progress);
                }
            };
        }
        return callBack;
    }

    public LCMessageListener getListener() {
        if (listener == null) {
            Logger.e("LCMessageListener is null");
            listener = new LCMessageListener() {
                @Override
                public void onReceivedChatMessage(List<LCMessage> message) {
                    Logger.e("message=" + message);
                }
            };
        }
        return listener;
    }

    /**
     * MethodName: sendMessage <br>
     * Description:  发送普通消息<br>
     * Description:  发送自定义扩展消息<br>
     * Creator: user<br>
     * Param:  <br>
     * Return:  <br>
     * Date: 2016/7/18 12:22
     */
    public void sendMessage(LCMessage message) {
        if (!ChatVerifyUtils.verifyChatParam(message))
            return;
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            throw new RuntimeException("the user not login");
        }
        if (!LCSingletonFactory.getInstance(LCConnectManager.class).isConnectAvailable()) {
            //连接不可用 直接重连
            LCSingletonFactory.getInstance(LCConnectManager.class).startLogin();
        }
        sendNMessageAfterVerify(message);
    }

    private void sendNMessageAfterVerify(LCMessage message) {
        Logger.d("message sending");
        //已读回执消息不插入数据库
        if (message.LCMessageEntity().getContentType() != LCMessage.ContentType.READ_RECEIPT_NUM
                && message.LCMessageEntity().getContentType() != LCMessage.ContentType.RETRACT_NUM) {
            ConversationEntity conversationEntity = GetDataFromDB.insertOrUpdateConversationEntity(message, true);
            message.LCMessageEntity().setConversationId(conversationEntity.get_recipientId());
            DBFactory.getDBManager().getDBMessageService().insertOrReplace(message.LCMessageEntity());
        }
        //后面执行后续流程
        switch (message.LCMessageEntity().getContentType()) {
            case Msg.EMsgContentType.TEXT_VALUE:
                sendMessageText(message);
                break;
            case Msg.EMsgContentType.IMAGE_VALUE:
                sendMessagePic(message);
                break;
            case Msg.EMsgContentType.AUDIO_VALUE:
                sendMessageAudio(message);
                break;
            case Msg.EMsgContentType.VIDEO_VALUE:
                sendMessageVideo(message);
                break;
            case Msg.EMsgContentType.LOCATION_VALUE:
                sendMessageLocation(message);
                break;
            case Msg.EMsgContentType.FILE_VALUE:
                sendMessageFile(message);
                break;
            case Msg.EMsgContentType.AT_VALUE:
                sendMessageAtText(message);
                break;
            case Msg.EMsgContentType.READ_RECEIPT_VALUE:
                sendReadReceiptMessage(message);
                break;
            case Msg.EMsgContentType.RETRACT_VALUE:
                sendRetractMessage(message);
                break;
            case Msg.EMsgContentType.CUSTOM_VALUE:
                sendCustomMessage(message);
                break;
            case Msg.EMsgContentType.CUSTOM_NO_APNS_VALUE:
                sendCustomNoApnsMessage(message);
                break;
            case Msg.EMsgContentType.CUSTOM_FAMILY_VALUE:
                sendFamilyCustomMessage(message);
                break;
            default:
                DispatchController.getInstance().onError(message, LCError.MESSAGE_CONTENT_TYPE_NULL.getValue(),
                        LCError.MESSAGE_CONTENT_TYPE_NULL.getDesc());
        }
    }

    private void sendMessageText(LCMessage message) {
        //会话列表更新
        MediaEntity entity = new MediaEntity();
        entity.setContent(((LCTextMessageBody) message.LCMessageBody()).getMessageContent());
        DBFactory.getDBManager().getDBMediaService().insert(entity);
        message.LCMessageEntity().setMediaEntity(entity);
        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * @MethodName: sendMessagePic<br>
     * @Description: 发送图片消息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: LCMessage<br>
     * @return: null <br>
     * @throws: 2016/8/11 9:44
     */

    private void sendMessagePic(LCMessage message) {
        //会话列表更新
        MediaEntity fileMessageExtention = getMediaEntity(message);
        fileMessageExtention.setIsOrigin(((LCImageMessageBody) message.LCMessageBody()).getisOrigin());
        try {
            DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
            message.LCMessageEntity().setMediaEntity(fileMessageExtention);
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
            HttpPostTask.newBuilder().uploadFile(message);
        } catch (Exception e) {
            DispatchController.getInstance().onError(message, LCError.MESSAGE_POST_FILE_FAIL.getValue(), e.toString());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }
    }

    private MediaEntity getMediaEntity(LCMessage message) {
        MediaEntity fileMessageExtention = new MediaEntity();
        fileMessageExtention
                .setFileName(((LCFileMessageBody) message.LCMessageBody()).getFileName());
        fileMessageExtention
                .setContent(((LCFileMessageBody) message.LCMessageBody()).getLocalPath());
        fileMessageExtention
                .setFileLength(((LCFileMessageBody) message.LCMessageBody()).getFileLength());
        DBFactory.getDBManager().getDBMediaService().insert(fileMessageExtention);
        return fileMessageExtention;
    }

    /**
     * @MethodName: sendMessageAudio<br>
     * @Description: 发送语音消息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: LCMessage<br>
     * @return: null <br>
     * @throws: 2016/8/11 9:46
     */
    private void sendMessageAudio(LCMessage message) {
        MediaEntity fileMessageExtention = getMediaEntity(message);
        fileMessageExtention
                .setDuration(((LCAudioMessageBody) message.LCMessageBody()).getDuration());

        try {
            DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
            message.LCMessageEntity().setMediaEntity(fileMessageExtention);
            HttpPostTask.newBuilder().uploadFile(message);
        } catch (Exception e) {
            DispatchController.getInstance().onError(message, LCError.MESSAGE_POST_FILE_FAIL.getValue(), e.toString());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }

    }

    /**
     * @MethodName: sendMessageAudio<br>
     * @Description: 发送视频消息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: LCMessage<br>
     * @return: null <br>
     * @throws: 2016/8/11 9:46
     */
    private void sendMessageVideo(LCMessage message) {
        MediaEntity fileMessageExtention = getMediaEntity(message);
        try {
            message.LCMessageEntity().setMediaEntity(fileMessageExtention);
            HttpPostTask.newBuilder().uploadFile(message);
        } catch (Exception e) {
            DispatchController.getInstance().onError(message, LCError.MESSAGE_POST_FILE_FAIL.getValue(), e.toString());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }
    }

    /**
     * @Title: sendMessageLocation <br>
     * @Description: 发送位置消息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: LCMessage <br>
     * @return: null <br>
     * @throws: 2016/8/11 14:52
     */
    public void sendMessageLocation(LCMessage message) {
        MediaEntity fileMessageExtention = getMediaEntity(message);
        fileMessageExtention
                .setLatitude(((LCLocationMessageBody) message.LCMessageBody()).getLatitude());
        fileMessageExtention
                .setLongitude(((LCLocationMessageBody) message.LCMessageBody()).getLongitude());
        fileMessageExtention.setLocationDes(
                ((LCLocationMessageBody) message.LCMessageBody()).getLocation_desc());
        fileMessageExtention
                .setAddress(((LCLocationMessageBody) message.LCMessageBody()).getAddress());
        try {
            DBFactory.getDBManager().getDBMediaService().update(fileMessageExtention);
            message.LCMessageEntity().setMediaEntity(fileMessageExtention);
            HttpPostTask.newBuilder().uploadFile(message);
        } catch (Exception e) {
            DispatchController.getInstance().onError(message, LCError.MESSAGE_POST_FILE_FAIL.getValue(), e.toString());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }
    }

    /**
     * @Title: sendFileMessage <br>
     * @Description: 发送文件消息 <br>
     * @param: LCMessage<br>
     * @return: void <br>
     * @throws: 2016/9/23 9:57
     */
    public void sendMessageFile(LCMessage message) {
        MediaEntity fileMessageExtention = getMediaEntity(message);
        try {
            message.LCMessageEntity().setMediaEntity(fileMessageExtention);
            HttpPostTask.newBuilder().uploadFile(message);
        } catch (Exception e) {
            DispatchController.getInstance().onError(message, LCError.MESSAGE_POST_FILE_FAIL.getValue(), e.toString());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_FAIL.value());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        }
    }

    /**
     * @Title: sendMessageAtText <br>
     * @Description: @消息 考虑@all的情况<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/26 16:51
     */
    private void sendMessageAtText(LCMessage message) {
        try {
            LCATMessageBody lcatMessageBody = (LCATMessageBody) message.LCMessageBody();
            StringBuilder sb = new StringBuilder();
            if (lcatMessageBody.getAtAll()) {
                sb.append("@All");
            } else {
                List<String> atUserList = GroupVerifyUtils
                        .uniqAndExcludeOwner(lcatMessageBody.getAt_members());
                lcatMessageBody.setAt_members(atUserList);
                for (String userName : atUserList) {
                    sb.append(userName).append(",");
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", lcatMessageBody.getText());
            jsonObject.put("members", sb);
            message.LCMessageEntity().setExtra(jsonObject.toString());
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
            //        callBack.onProgress(message, LCMessage.PREPARED);
            LCCmdServiceFactory.getMessageService().sendPacket(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: sendReceiptMessage <br>
     * @Description: 发送已读回执消息<br>
     * @param: message 消息 <br>
     * @return: void <br>
     * @throws: 2016/9/26 12:37
     */
    private void sendReadReceiptMessage(LCMessage message) {
        //将对应消息更新为已读
        /*  message.LCMessageEntity().setRead(true);
        DBFactory.getDBManager().getLCMessageDao().update(message.LCMessageEntity());*/
        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * @Title: sendRetractMessage <br>
     * @Description: 发送撤回消息 <br>
     * @param: message消息 <br>
     * @return: void <br>
     * @throws: 2016/9/26 12:37
     */
    private void sendRetractMessage(LCMessage message) {

        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * @Title: sendCustomMessage <br>
     * @Description: 发送自定义消息<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/27 10:31
     */
    private void sendCustomMessage(LCMessage message) {
        message.LCMessageEntity()
                .setExtra(((LCCustomMessageBody) message.LCMessageBody()).getContent());
        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * @Title: sendCustomNoApnsMessage <br>
     * @Description: 发送自定义消息, 对方没有apns推送<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/27 10:31
     */
    private void sendCustomNoApnsMessage(LCMessage message) {
        message.LCMessageEntity()
                .setExtra(((LCCustomNoApnsMessageBody) message.LCMessageBody()).getContent());
        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * 发送和家亲自定义消息
     *
     * @param message
     */
    private void sendFamilyCustomMessage(LCMessage message) {
        message.LCMessageEntity()
                .setExtra(((LCFamilyCustomMessageBody) message.LCMessageBody()).getContent());
        DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
        LCCmdServiceFactory.getMessageService().sendPacket(message);
    }

    /**
     * @Title: cancelSendingMessage <br>
     * @Description: 取消发送信息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/28 10:20
     */
    public void cancelSendingMessage(String msgId) throws LCException {
        //        DBFactory.getDBManager().getLCMessageDao().deleteByKey(id);
        MessageEntity messageEntity = DBFactory.getDBManager().getDBMessageService().queryBuilder()
                .where(MessageEntityDao.Properties.MsgId.eq(msgId)).unique();
        if (messageEntity == null)
            throw new LCException(MESSAGE_EXIST_ERROR);
        UploadFactory.getUploadManager().cancel(msgId);
        DBFactory.getDBManager().getDBMessageService().delete(messageEntity);
    }

    /**
     * @Title: pauseSendingMessage <br>
     * @Description: 暂停发送信息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/29 9:22
     */
    public void pauseSendingMessage(String msgId) {
        UploadFactory.getUploadManager().pause(msgId);
    }

    /**
     * @Title: resumeSendingMessage <br>
     * @Description: 恢复发送信息 <br>
     * @Creator: zhangguoqiong<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/29 9:26
     */
    public void resumeSendingMessage(String msgId) {
        UploadFactory.getUploadManager().resume(msgId);
    }

    /**
     * @Title: forwardMessage<br>
     * @Description: 转发消息 <br>
     * @param: chatType 聊天类型<br>
     * @param: toChatUserName 转发对象<br>
     * @param: msgId 待转发消息id <br>
     * @return: <br>
     * @throws: 2016/9/24 16:41
     */
    @Override
    public void forwardMessage(LCMessage.ChatType chatType, String toChatUserName, String msgId)
            throws LCException {
        if (LCMessage.ChatType.MultiChat.equals(chatType)) {
            throw new LCException(LCError.MESSAGE_CHAT_TYPE_WRONG);
        }
        if ((chatType.equals(LCMessage.ChatType.Chat) || chatType.equals(LCMessage.ChatType.PrivateChat))
                && !BaseVerifyUtils.checkUserName(toChatUserName)) {
            throw new LCException(LCError.CONTACT_USERNAME_ILLRGAL);
        } else if (chatType.equals(LCMessage.ChatType.GroupChat)
                && !GroupVerifyUtils.checkGroupId(toChatUserName)) {
            throw new LCException(LCError.GROUP_ID_ILLEGAL);
        }
        LCMessage message = GetDataFromDB.createNewMessageByMsgId(chatType, toChatUserName, msgId);
        //更新会话表
        message.LCMessageEntity().setSendOrRecv(LCMessage.Direct.SEND.value());
        ConversationEntity conversationEntity = GetDataFromDB
                .insertOrUpdateConversationEntity(message, true);
        //发送成功的情况
        if (message.LCMessageEntity().getStatus() == LCMessage.Status.MSG_RECEIVED.value() || message
                .LCMessageEntity().getStatus() == LCMessage.Status.MSG_SEND_SUCCESS.value()) {
//            message.LCMessageEntity().setSendOrRecv(LCMessage.Direct.SEND.value());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_PROGRESS.value());
            message.LCMessageEntity().setConversationId(conversationEntity.get_recipientId());
            DBFactory.getDBManager().getDBMessageService()
                    .insertOrReplace(message.LCMessageEntity());
            LCCmdServiceFactory.getMessageService().sendPacket(message);
        } else {
//            message.LCMessageEntity().setSendOrRecv(LCMessage.Direct.SEND.value());
            message.LCMessageEntity().setStatus(LCMessage.Status.MSG_SEND_PROGRESS.value());
            sendNMessageAfterVerify(message);
        }
    }

    /**
     * @Title: getAllMessageFromDB <br>
     * @Description: 从数据库获取对应会话所有消息 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/10/25 18:40
     */
    public List<LCMessage> getAllMessageFromDB(String conversationId) {
        return GetDataFromDB.getAllMessage(conversationId);
    }

    @Override
    public List<LCMessage> getAllMessageFromDBNOId() {
        return GetDataFromDB.getAllMessage();
    }

    /**
     * @Title: deleteAllMessageFromDB <br>
     * @Description: 从数据库删除对应会话所有消息 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/10/31 10:52
     */
    public void deleteAllMessageFromDB(String conversationId) {
        if (conversationId != null) {
            List<Long> mediaIdList = new ArrayList<>();
            List<MessageEntity> messageEntities = DBFactory.getDBManager().getDBMessageService()
                    .queryBuilder().where(MessageEntityDao.Properties.ConversationId.eq(conversationId))
                    .list();
            for (MessageEntity messageEntity : messageEntities) {
                if (messageEntity.getMediaEntity() != null) {
                    mediaIdList.add(messageEntity.getMediaId());
                }
            }
            DBFactory.getDBManager().getDBMediaService().deleteByKeyInTx(mediaIdList);
            DBFactory.getDBManager().getDBMessageService().deleteInTx(messageEntities);
            DBFactory.getDBManager().getDBMessageService().queryBuilder()
                    .where(MessageEntityDao.Properties.ConversationId.eq(conversationId)).buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            ConversationEntity conversationEntity = DBFactory.getDBManager().getDBConversationService().load(conversationId);
            if (conversationEntity != null) {
                conversationEntity.setMsgContent("");
                conversationEntity.setMsgContentType(LCMessage.ContentType.TXT_NUM);
                DBFactory.getDBManager().getDBConversationService().update(conversationEntity);
            }
        }
    }

    /**
     * @Title: deleteAllMessageFromDB <br>
     * @Description: 从数据库删除对应会话所有消息 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/10/31 10:52
     */
    public void deleteAllMessageFromDB() {
        List<Long> mediaIdList = new ArrayList<>();
        List<MessageEntity> messageEntities = DBFactory.getDBManager().getDBMessageService()
                .queryBuilder()
                .list();
        for (MessageEntity messageEntity : messageEntities) {
            if (messageEntity.getMediaEntity() != null) {
                mediaIdList.add(messageEntity.getMediaId());
            }
        }
        DBFactory.getDBManager().getDBMediaService().deleteByKeyInTx(mediaIdList);
        DBFactory.getDBManager().getDBMessageService().deleteInTx(messageEntities);
        DBFactory.getDBManager().getDBMessageService().queryBuilder()
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
    /**
     * @Title: deleteSingleMessage <br>
     * @Description: 删除单条消息 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/2 14:47
     */
    public void deleteSingleMessage(String msgId) {
        if (msgId != null)
            DBFactory.getDBManager().getDBMessageService().queryBuilder()
                    .where(MessageEntityDao.Properties.MsgId.eq(msgId)).buildDelete()
                    .executeDeleteWithoutDetachingEntities();
    }

    public void updateMessage(LCMessage message) {
        if (message != null)
            DBFactory.getDBManager().getDBMessageService().update(message.LCMessageEntity());
    }

    public void insertMessageToDB(LCMessage message) {

    }


    @Override
    public Chat.FAQListResp getFAQUnit(String id, String input,String language){
        try {
            return LCCmdServiceFactory.getMessageService().getFAQUnit(id,input,language);
        } catch (LCException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Chat.CustomerServiceResp getCustomerService(String code, String country){
        try {
            return LCCmdServiceFactory.getMessageService().getCustomerService(code,country);
        } catch (LCException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Chat.CheckBundleExistsResp checkBundleExists() {
        try {
            return LCCmdServiceFactory.getMessageService().checkBundleExists();
        } catch (LCException e) {
            e.printStackTrace();
            return null;
        }
    }
}
