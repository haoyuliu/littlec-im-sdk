/* Project: android_im_sdk
 * 
 * File Created at 2016/8/24
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

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.lang.LCError;
import com.littlec.sdk.lang.LCException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.littlec.sdk.lang.LCError.GROUP_DESC_ILLEGAL;
import static com.littlec.sdk.lang.LCError.GROUP_REFUSE_REASON_ILLEGAL;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/24
 * @Version
 */

public class GroupVerifyUtils extends BaseVerifyUtils {
    public static boolean checkGroupId(String groupId) {
        if (groupId == null || groupId.isEmpty()||groupId.replace(" ","").isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean checkGroupName(String groupName) {
        if(groupName == null || groupName.isEmpty()||groupName.replace(" ","").isEmpty() ){
            return false;
        }
        if(groupName.length() > 50) {
           return false;
        }
        return true;
    }
    /** 
     * @Title: listUnique <br>
     * @Description:  去重<br>
     * @param:  list<br>
     * @return:  list<br>
     * @throws: 2016/9/13 9:33
     */  
    public static <T> List<T> listUnique(List<T> list) {
        if(list == null)
            return list;
        else
            return new ArrayList<T>(new LinkedHashSet<T>(list));
    }
    /** 
     * @Title: uniqAndExcludeOwner <br>
     * @Description: 去重和取出群主  <br>
     * @param: list 输入list <br>
     * @return: List <br>
     * @throws: 2016/9/13 9:53
     */  
    public static <T> List<T> uniqAndExcludeOwner(List<T> list){
        String owner= LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName();
        if(list!=null){
            //去除群主
            while(list.contains(owner))
            {
                list.remove(owner);
            }

        }
        list= listUnique(list);
        return list;
    }

    public static void checkCreateGroup(String groupName, List<String> memberUserNameList,
                                           List<String> inviteeNameList, String reason,String desc) throws LCException{
        checkNetworkAndLoginFlag();
        if (!GroupVerifyUtils.checkGroupName(groupName)) {
            throw new LCException(LCError.GROUP_NEWNAME_ILLEGAL);
        }
        if (memberUserNameList != null) {
            for (String memberName : memberUserNameList) {
                if (!BaseVerifyUtils.checkUserName(memberName)) {
                    throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
                }
            }
        }
        if (inviteeNameList != null) {
            for (String memberName : inviteeNameList) {
                if (!BaseVerifyUtils.checkUserName(memberName)) {
                    throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
                }

            }
        }
        if (reason.length() > 20) {
            throw new LCException(GROUP_REFUSE_REASON_ILLEGAL);
        }
        if(desc.length()>200) {
            throw new LCException(GROUP_DESC_ILLEGAL);
        }
    }

    public static void dealInvitation(String groupId,String inviter,String declineReason,boolean acceptOrDecline) throws LCException{
        checkNetworkAndLoginFlag();
        if (!GroupVerifyUtils.checkGroupId(groupId)) {
            throw new LCException(LCError.GROUP_ID_ILLEGAL);
        }
        if (!BaseVerifyUtils.checkUserName(inviter)) {
            throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
        }
        if(!acceptOrDecline) {
            if (declineReason == null)
                declineReason = "";
            else
                declineReason = declineReason.trim();
        if (declineReason.length() > 20)
            throw new LCException(GROUP_REFUSE_REASON_ILLEGAL);
        }
    }



    public static void addUsersToGroupVerfiy(String groupId,
                                                     List<String> inviteeMemberNoAgree,
                                                     List<String> inviteeMemberNeedAgree,
                                                     String inviteReason)
            throws LCException {
        checkNetworkAndLoginFlag();
        if (inviteeMemberNoAgree == null&&inviteeMemberNeedAgree==null) {
            throw new LCException(LCError.COMMON_CONTENT_NULL);
        }
        if (!GroupVerifyUtils.checkGroupId(groupId)) {
            throw new LCException(LCError.GROUP_ID_ILLEGAL);
        }
        if(inviteeMemberNoAgree!=null){
            for (String memberName : inviteeMemberNoAgree) {
                if (!BaseVerifyUtils.checkUserName(memberName)) {
                    throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
                }
            }
        }
        if(inviteeMemberNeedAgree!=null){
            for (String memberName : inviteeMemberNeedAgree) {
                if (!BaseVerifyUtils.checkUserName(memberName)) {
                    throw new LCException(LCError.ACCOUNT_USERNAME_ILLEGAL);
                }
            }
        }
        if(inviteReason!=null){
            inviteReason = inviteReason.trim();
            if (inviteReason.length() > 20) {
                throw new LCException(GROUP_REFUSE_REASON_ILLEGAL);
            }
        }
    }
    public static void checkListRepeatUser(List<String> memberList,List<String> inviteeList) throws LCException{
        List<String> totalList=new ArrayList();
        int  memberSize=0;
        int  inviteeSize=0;
        if(memberList!=null) {
            totalList.addAll(memberList);
            memberSize=memberList.size();
        }
        if(inviteeList!=null) {
            totalList.addAll(inviteeList);
            inviteeSize=inviteeList.size();
        }
        totalList= uniqAndExcludeOwner(totalList);
        if(totalList.size()<(memberSize+inviteeSize)){
            throw new LCException(LCError.GROUP_REPEAT_MEMBER);
        }
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/24 zhangguoqiong creat
 */
