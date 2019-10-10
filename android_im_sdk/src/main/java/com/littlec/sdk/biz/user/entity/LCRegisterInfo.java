/* Project: android_im_sdk
 * 
 * File Created at 2016/8/16
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.user.entity;

/**
 * @Type com.littlec.sdk.common
 * @User user
 * @Desc
 * @Date 2016/8/16
 * @Version
 */
public class LCRegisterInfo {
    public String userName;
    public String nickName;
    public String phone;
    public String passWord;

    private LCRegisterInfo(Builder builder) {
        this.userName = builder.userName;
        this.nickName = builder.nickName;
        this.phone = builder.phone;
        this.passWord = builder.passWord;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassWord() {
        return passWord;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        public String userName;
        public String nickName;
        public String phone;
        public String passWord;

        Builder() {

        }

        public Builder setUserName(String userName) {
            if(userName!=null){
                this.userName=userName.toLowerCase();
            }
            return this;
        }

        public Builder setNickName(String NickName) {
            this.nickName = NickName;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setPassWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public LCRegisterInfo build() {
            return new LCRegisterInfo(this);
        }

    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/16 user creat
 */
