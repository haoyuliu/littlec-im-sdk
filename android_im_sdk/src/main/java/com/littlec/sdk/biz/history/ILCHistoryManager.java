package com.littlec.sdk.biz.history;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.lang.LCException;

import java.util.List;

/**
 * ClassName: ILCHistoryManager
 * Description:   hms manager
 * Creator: user
 * Date: 2016/7/18 17:36
 */
public interface ILCHistoryManager {

    List<LCMessage> getHistoryMessage(LCMessage.ChatType chatType,String targetName,String msgId,int count)throws LCException;

    void deleteAllSessionMessage(String targetUserName) throws LCException;

    void deleteMessage(LCMessage.ChatType type, String targetUserName,List<String> msgList) throws LCException;

    interface InnerInterface {
        void onDestory();
    }

}
