package com.littlec.sdk.biz.chat;

import com.fingo.littlec.proto.css.Chat;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.listener.LCMessageListener;
import com.littlec.sdk.biz.chat.listener.LCMessageSendCallBack;
import com.littlec.sdk.lang.LCException;

import java.util.List;

/**
 * ClassName: ILCMessageManager
 * Description:  Messagemanager interface
 * Creator: user
 * Date: 2016/7/18 17:11
 */
public interface ILCMessageManager {
    void sendMessage(LCMessage message);

    void setMessageStatusCallback(LCMessageSendCallBack callBack);

    LCMessageSendCallBack getCallBack();

    void addMessageListener(LCMessageListener listener);

    void forwardMessage(LCMessage.ChatType chatType, String to, String msgId) throws LCException;

//    void setToken(String regId,String pushAppSecret,String packName) throws LCException;
//
//    void clearToken() throws LCException;

    List<LCMessage> getAllMessageFromDB(String conversationId);
    List<LCMessage> getAllMessageFromDBNOId();

    void deleteAllMessageFromDB(String conversationId);

    void deleteAllMessageFromDB();

    void deleteSingleMessage(String msgId);

    void updateMessage(LCMessage message);

    void cancelSendingMessage(String msgId) throws LCException;


    void pauseSendingMessage(String msgId);

    void resumeSendingMessage(String msgId);

    Chat.FAQListResp getFAQUnit(String faqId, String input,String language);

    Chat.CustomerServiceResp getCustomerService(String code,String country);

    Chat.CheckBundleExistsResp checkBundleExists();

    interface InnerInterface {
        LCMessageSendCallBack getCallBack();

        LCMessageListener getListener();

//        void onDestroy();
    }

}
