/* Project: android_im_sdk
 * 
 * File Created at 2016/8/2
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.lang;

/**
 * @Type com.littlec.sdk.chat.utils
 * @User user
 * @Desc SDK所有错误码
 * @Date 2016/8/2
 * @Version
 */
public enum LCError {
    /*****************************Common***********************************/
    COMMON_CONTENT_NULL(50001, "参数无效"),
    COMMON_NETWORK_DISCONNECTED(50002, "网络连接失败"),
    COMMON_APPKEY_ILLEGAL(50003, "登录失败，APPKEY无效"),
    COMMON_ADAPTER_PARSER_FAILED(50004, "adapter解析失败"),
    COMMON_LOGIN_RESPONSE_NULL(50005, "登录响应为空"),
    COMMON_LOGIN_PARSE_FAILED(50006, "登录响应解析失败"),
    COMMON_APPKEYPASSWORD_ILLEGAL(50007, "APPKEYPASSWORD无效"),
    COMMON_LOGOUT_RESPONSE_NULL(50008,"登出响应为空"),
    COMMON_LOGOUT_PARSE_FAILED(50009, "登出响应解析失败"),
    COMMON_LOGOUT_NOT_LOGIN(50010,"用户未登录"),
    COMMON_SERVER_ERROR(50011,"服务器异常"),
    COMMON_SERVER_INNER_ERROR(50012,"服务器内部异常"),
    COMMON_NOT_LOGIN_ERROR(50013,"用户没有登录"),
    COMMON_AUTO_LOGIN_ERROR(50013,"用户自动登录失败"),
    COMMON_APPKEY_NOT_ACTIVE(50014,"appKey没有激活"),
    COMMON_INIT_FAIL(50015,"grpc初始化失败"),
    /**********************message*****************************************/
    MESSAGE_TXT_CONTENT_NULL(50201, "文本消息内容为空"),
    MESSAGE_TXT_OVER_LENGTH(50202, "文本消息内容超过1000字节"),
    MESSAGE_TO_NULL(50203, "消息发送对象为空"),
    MESSAGE_FILE_PATH_EMPTY(50204, "文件路径为空"),
    MESSAGE_FILE_NOT_EXIST(50205, "文件不存在"),
    MESSAGE_FILE_TOO_SMALL(50206, "文件太小"),
    MESSAGE_FILE_TOO_LARGE(50207, "文件太大"),
    MESSAGE_FILE_PIC_ILLEGAL(50208, "不是图片文件"),
    MESSAGE_FILE_PIC_TYPE_WRONG(50209, "传入图片文件格式错误，不支持的图片格式"),
    MESSAGE_FILE_AUDIO_TYPE_ILLEGAL(50210, "传入音频文件格式错误，不支持的音频格式"),
    MESSAGE_FILE_VIDEO_TYPE_ILLEGAL(50211, "传入视频文件格式错误，不支持的视频格式"),
    MESSAGE_LOCATION_WRONG(50212, "经纬度输入错误，纬度介于-90~90，经度介于-180~180"),
    MESSAGE_LOCATION_ADDRESS_EMPTY(50213, "位置地址描述不能为空"),
    MESSAGE_CONTENT_TYPE_NULL(50214, "消息类型为空"),
    MESSAGE_POST_FILE_FAIL(50215, "上传文件失败"),
    MESSAGE_PARSE_ERROR(50216, "发送消息响应解析失败"),
    MESSAGE_SEND_ERROR(50217, "发送消息失败"),
    MESSAGE_CHAT_TYPE_ONLY_GROUP(50218, "传入会话类型参数错误，仅能为群聊"),
    MESSAGE_CHAT_MSGID_ILLEGAL(50219, "msgId非法"),
    MESSAGE_CHAT_TYPE_WRONG(50220,"传入会话类型参数错误，不支持的会话类型"),
    MESSAGE_CHAT_GROUP_ID_ILLEGAL(50221,"群id非法"),
    MESSAGE_REGID_ILLEGAL(50222,"regId非法"),
    MESSAGE_FILE_TYPE_ILLEGAL(50223,"文件格式错误，不支持的文件格式"),
    MESSAGE_MSG_ID_ILLEGAL(50224,"消息id非法"),
    MESSAGE_AUDIO_DURATION_ILLEGAL(50225,"音频文件时长超过180秒"),
    MESSAGE_MULTI_TARGET_USERNAME_NULL(50226,"群发用户名列表不合法"),
    MESSAGE_MAX_NUM_LIMIT(50227,"群发人数最高为200人"),
    MESSAGE_FILE_OVER_DATE(50228,"文件过期"),
    MESSAGE_FILE_INFO_ERROR(50229,"文件信息错误"),
    MESSAGE_EXIST_ERROR(50230,"消息不存在"),
    MESSAGE_CONVERSATION_CONFLICT(50231,"消息不属于该会话"),
    MESSAGE_VIDEO_ANALUSIS_ERROR(50232,"视频文件解析错误"),
    MESSAGE_AUDIO_ANALUSIS_ERROR(50233,"音频文件解析错误"),
    /*************************Account**************************************/
    ACCOUNT_USERNAME_ILLEGAL(50401, "用户名或群Id不合法，请重新输入"),
    ACCOUNT_NICKNAME_UNREQUIRED(50402, "昵称不合法，请重新输入"),
    ACCOUNT_PHONE_UNREQUIRED(50403, "手机号输入不合法, 请重新输入"),
    ACCOUNT_PASSWORD_ILLEGAL(50404, "密码不合法，请重新输入"),
    ACCOUNT_PASSWORD_NOTSAME(50405, "两次输入的密码不一致，请重新输入"),
    ACCOUNT_VERIFICATION_CODE_ILLEGAL(50406, "验证码为4位的数字，请重新输入"),
    ACCOUNT_ALREADY_CONNECTED(50407, "已经连接成功了，无需登陆"),
    /*************************Group**************************************/
    GROUP_ID_ILLEGAL(50601, "群id不合法"),
    GROUP_NEWNAME_ILLEGAL(50602, "新的群名称不合法"),
    GROUP_NICKNAME_ILLEGAL(50603, "群昵称不合法"),
    GROUP_USERNAME_ILLEGAL(50604, "用户名不合法"),
    GROUP_JOINREASON_ILLEGAL(50605, "申请入群理由不合法"),
    GROUP_PARSE_ERROR(50606,"响应解析错误"),
    GROUP_REFUSE_REASON_ILLEGAL(50607,"理由限制为20个字"),
    GROUP_DESC_ILLEGAL(50608,"群描述限制为200个字"),
    GROUP_REPEAT_MEMBER(50609,"需要同意的列表和不需要同意的列表人员重复"),
    GROUP_ONLY_OWNER(50610,"邀请成员列表里面需包括非本人的成员"),
    GROUP_NOT_OWNER(50611,"群主不能转移给自己"),
    /*************************Contact**************************************/
    CONTACT_USERNAME_ILLRGAL(50801,"好友用户名不合法"),
    CONTACT_REMARK_ILLEGAL(50802,"理由限制为20个字"),
    CONTACT_REGID_NULL(50803,"regId为空"),
    CONTACT_DISPLAY_ILLEGAL(50804,"好友备注名不合法"),
    CONTACT_NOT_USERSELF(50805,"不能删除自己"),
    CONTACT_REGID_NOT_EXIST(50806,"好友请求id不存在"),
    /******************************Hms****************************************/
    HMS_OPTIONS_ILLEGAL(60001,"拉取历史消息参数不合法"),
    HMS_NUM_ERROR(60002,"拉取消息条数为0"),
    HMS_GUID_ILLEGAL(60003,"guid不合法"),
    HMS_GUID_ORDER_ERROR(60004,"传入msgId不合法"),
    /*******************************ui library****************************************/
    LIBRARY_FILE_NOT_FOUND(60201,"文件找不到"),
    LIBRARY_FILE_INVALID(60202,"文件无效"),
    LIBRARY_FILE_UPLOAD_FAILED (60203,"文件上传失败"),
    LIBRARY_FILE_DOWNLOAD_FAILED(60204,"文件下载失败"),


    ;
    private int value;
    private String desc;

    LCError(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toString() {
        return "[" + this.value + "]" + this.desc;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/2 user creat
 */
