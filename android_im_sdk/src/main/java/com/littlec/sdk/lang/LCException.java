package com.littlec.sdk.lang;

import com.littlec.sdk.lang.LCError;

/**
 * ClassName: LCException
 * Description:  sdk 通用异常
 * Creator: user
 * Date: 2016/7/25 13:46
 */
public class LCException extends Exception {
    protected String desc;
    protected int errorCode;

    public LCException() {
        this.errorCode = -1;
        this.desc = "";
    }

    public LCException(int i, String str) {
        super(str);
        this.errorCode = i;
        this.desc = str;
    }

    public LCException(String str) {
        super(str);
        this.desc = str;
        this.errorCode = -1;
    }

    public LCException(String str, Throwable th) {
        super(str);
        this.errorCode = -1;
        this.desc = str;
    }

    public LCException(LCError lcError) {
        super(lcError.getDesc());
        this.errorCode = lcError.getValue();
        this.desc = lcError.getDesc();
    }

    public void setErrorCode(int code) {
        this.errorCode = code;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

}
