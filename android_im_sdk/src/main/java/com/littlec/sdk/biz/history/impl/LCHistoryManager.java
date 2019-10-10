package com.littlec.sdk.biz.history.impl;

import android.text.TextUtils;

import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.history.ILCHistoryManager;
import com.littlec.sdk.connect.core.LCCmdServiceFactory;
import com.littlec.sdk.biz.chat.utils.BaseVerifyUtils;
import com.littlec.sdk.biz.chat.utils.GroupVerifyUtils;
import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.littlec.sdk.biz.LCAbstractManager;
import com.littlec.sdk.LCClient;
import com.littlec.sdk.utils.sp.UserInfoSP;
import com.littlec.sdk.utils.LCNetworkUtil;

import java.util.List;

/**
 * @ClassName: LCHistoryManager
 * @Description: 历史消息模块
 * @author: user
 * @date: 2016/9/18 15:35
 */
public class LCHistoryManager extends LCAbstractManager implements ILCHistoryManager, ILCHistoryManager.InnerInterface {
    //conversation msgId guid
    private static final String TAG = "LCHistoryManager";

    private LCHistoryManager() {
    }

    public List<LCMessage> getHistoryMessage(LCMessage.ChatType chatType, String targetName, String msgId, int count) throws LCException {
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
        }
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            throw new LCException(LCError.COMMON_NETWORK_DISCONNECTED);
        }
        if (chatType.equals(LCMessage.ChatType.Chat)) {
            if (!BaseVerifyUtils.checkUserName(targetName)) {
                throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
            }
            return getHmsMessage(LCMessage.ChatType.Chat, targetName, msgId, count);
        } else if (chatType.equals(LCMessage.ChatType.GroupChat)) {
            if (!GroupVerifyUtils.checkGroupId(targetName)) {
                throw new LCException(LCError.GROUP_ID_ILLEGAL);
            }
            return getHmsMessage(LCMessage.ChatType.GroupChat, targetName, msgId, count);
        }
        return null;
    }

    /**
     * @Title: 删除历史消息 <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/9/18 20:39
     */
    public void deleteMessage(LCMessage.ChatType type, String targetUserName, List<String> msgList) throws LCException {
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
        }
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            throw new LCException(LCError.COMMON_NETWORK_DISCONNECTED);
        }
        if (!BaseVerifyUtils.checkTargetUserName(type, targetUserName)) {
            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
        }
        for (String msgId : msgList) {
            if (TextUtils.isEmpty(msgId)) {
                throw new LCException(LCError.MESSAGE_MSG_ID_ILLEGAL);
            }
            if (msgId.length() != 36) {
                throw new LCException(LCError.MESSAGE_MSG_ID_ILLEGAL);
            }
        }

        LCCmdServiceFactory.getHmsService().deleteMessage(type, targetUserName,
                msgList);
    }

    /**
     * @Title: deleteAllSessionMessage <br>
     * @Description: 只<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/11/2 14:18
     */
    public void deleteAllSessionMessage(String targetUserName) throws LCException {
        if (!UserInfoSP.getBoolean(LCChatConfig.UserInfo.LOGIN_FLAG)) {
            throw new LCException(LCError.COMMON_NOT_LOGIN_ERROR.getValue(),
                    LCError.COMMON_NOT_LOGIN_ERROR.getDesc());
        }
        if (!LCNetworkUtil.isNetworkConnected(LCClient.getInstance().getContext())) {
            throw new LCException(LCError.COMMON_NETWORK_DISCONNECTED);
        }
        if (!BaseVerifyUtils.checkTargetUserName(LCMessage.ChatType.Chat, targetUserName)) {
            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
        }
        LCCmdServiceFactory.getHmsService().deleteAllSession(LCMessage.ChatType.Chat, targetUserName);
    }


    private List<LCMessage> getHmsMessage(LCMessage.ChatType type, String targetUserName, String beginMsgId, int limit)
            throws LCException {
        if (limit <= 0) {
            throw new LCException(LCError.HMS_NUM_ERROR);
        }
        return LCCmdServiceFactory.getHmsService().getHmsMessage(type, targetUserName, beginMsgId, limit);
    }

    /**
     * @Title: onDestory <br>
     * @Description: <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/4 18:45
     */
    public void onDestory() {

    }
}
