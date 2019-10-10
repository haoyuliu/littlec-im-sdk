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
package com.littlec.sdk.biz.history.impl;

import com.fingo.littlec.proto.css.Chat;
import com.fingo.littlec.proto.css.CssErrorCode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.littlec.sdk.biz.chat.entity.LCMessage;
import com.littlec.sdk.biz.chat.utils.MsgReceiveParser;
import com.littlec.sdk.biz.history.IHmsCmdService;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.littlec.sdk.connect.LCGrpcManager;
import com.littlec.sdk.database.api.GetDataFromDB;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;
import com.fingo.littlec.proto.css.Msg;
import com.fingo.littlec.proto.css.Connector;
import com.littlec.sdk.utils.LCLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.littlec.sdk.lang.LCError.HMS_GUID_ORDER_ERROR;

/**
 * @Type com.littlec.sdk.chat.core.launcher.impl
 * @User user
 * @Desc 历史消息服务
 * @Date 2016/9/18
 * @Version
 */
 public class HmsCmdServiceImpl implements IHmsCmdService {
    private static final String TAG = "HmsCmdServiceImpl";
    private static final LCLogger Logger = LCLogger.getLogger(TAG);
    private Map<String,HashMap<String,Long>> historyMap=new HashMap<>();

    public void addMapCache(String conversation, HashMap<String,Long> mapCache){
        historyMap.put(conversation,mapCache);
    }

    public Long queryGuidByMsgId(String conversation,String msgId){
        HashMap<String,Long> map=historyMap.get(conversation);
        if(map==null||!map.containsKey(msgId)){
            return new Long(-1);
        }else {
            return map.get(msgId);
        }
    }

    /**
     * @Title: getHmsMessage <br>
     * @Description: 按照会话同步历史消息 <br>
     * @param:  <br>   
     * @return:  <br>
     * @throws: 2016/9/18 20:31
     */
    public List<LCMessage> getHmsMessage(LCMessage.ChatType type, String targetUserName,
                                         String msgId, int limit)
            throws LCException {
        List<LCMessage> msgList = new ArrayList<>();
        long guid=-1;
        if("0".equals(msgId)){
            guid=0;
        }else{
            guid=queryGuidByMsgId(targetUserName,msgId);
            //查询数据库
            if(guid==-1){
                guid=GetDataFromDB.queryGuidByMsgId(msgId);
            }
        }
        if(guid==-1){
           throw new LCException(HMS_GUID_ORDER_ERROR);
        }
        Chat.MsgGetResponse msgGetResponse = getMessageUnit(type, targetUserName,guid,
                limit);
        List<Msg.MessageUnit> messageUnits = msgGetResponse.getDataList();
        if (messageUnits == null) {
            Logger.d("no message");
            return null;
        }
        List<LCMessage> list = MsgReceiveParser.parseMessageUnit(messageUnits,true,true);
        HashMap<String,Long> map=new HashMap<>();
        for(LCMessage message:list){
            message.LCMessageEntity().setRead(true);
            map.put(message.getMsgId(),message.LCMessageEntity().getGuid());
        }
        addMapCache(targetUserName,map);
        msgList.addAll(list);
        return msgList;
    }

    /**
     * @Title:  deleteMessage<br>
     * @Description: 删除消息 <br>
     * @param: guid 删除的特定消息id <br>
     * @return:  <br>
     * @throws: 2016/9/18 20:32
     */
    public void deleteMessage(LCMessage.ChatType type, String targetUserName, List<String> msgList)
            throws LCException {
        List<Long> guidList=new ArrayList<>();
        for(String msgId:msgList){
            long guid=queryGuidByMsgId(targetUserName,msgId);
            if(guid==-1){
                guid = GetDataFromDB.queryGuidByMsgId(msgId);
            }
            if(guid==-1){
                throw new LCException(LCError.HMS_GUID_ORDER_ERROR);
            }
            guidList.add(guid);
        }
        ILCBuilder builder=null;
        if(type== LCMessage.ChatType.Chat){
             builder = new HmsBuilderImpl("chatMessageRemove", type, false, targetUserName,
                    guidList);
        }else if(type== LCMessage.ChatType.GroupChat){
             builder = new HmsBuilderImpl("groupMessageRemove", type, false, targetUserName,
                    guidList);
        }else{
            Logger.d("不支持的聊天类型");
        }
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance().sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                Chat.MessageRemoveResponse msgRemoveResponse = Chat.MessageRemoveResponse
                        .parseFrom(unaryResponse.getData());
                if (msgRemoveResponse.getRet() != CssErrorCode.ErrorCode.OK) {
                    throw new LCException(msgRemoveResponse.getRet().getNumber(),
                            msgRemoveResponse.getRet().name());
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAllSession(LCMessage.ChatType type, String targetName) throws LCException {
        ILCBuilder builder = new HmsBuilderImpl("chatMessageRemove", type, true, targetName,null);
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance()
                .sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                Chat.MessageRemoveResponse msgRemoveResponse = Chat.MessageRemoveResponse
                        .parseFrom(unaryResponse.getData());
                if (msgRemoveResponse.getRet() != CssErrorCode.ErrorCode.OK) {
                    throw new LCException(msgRemoveResponse.getRet().getNumber(),
                            msgRemoveResponse.getRet().name());
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    private Chat.MsgGetResponse getMessageUnit(LCMessage.ChatType type, String targetUserName,
                                               long beginGuid, int limit)
            throws LCException {
        Chat.MsgGetResponse msgGetResponse = null;
        ILCBuilder builder=null;
        if(type== LCMessage.ChatType.Chat){
            builder = new HmsBuilderImpl("chatHistoryMessageGet",type, targetUserName,
                    beginGuid, limit);
        }else if(type== LCMessage.ChatType.GroupChat){
            builder = new HmsBuilderImpl("groupHistoryMessageGet", type, targetUserName,
                    beginGuid, limit);
        }else{
            Logger.d("不支持的聊天类型");
        }
        Connector.UnaryResponse unaryResponse = LCGrpcManager.getInstance()
                .sendUnaryRequest(builder.buildUnaryRequest());
        if (unaryResponse.getRet() != CssErrorCode.ErrorCode.OK) {
            throw new LCException(unaryResponse.getRet().getNumber(),
                    unaryResponse.getRet().name());
        } else {
            try {
                msgGetResponse = Chat.MsgGetResponse.parseFrom(unaryResponse.getData());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return msgGetResponse;
    }

    public void onDestory(){
        historyMap=null;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/18 user creat
 */
