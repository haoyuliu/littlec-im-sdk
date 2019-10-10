package com.littlec.sdk.config;

import android.util.Log;

/**
 * ClassName: LCAppConfig
 * Description:  init entity SDK config options
 * Creator: user
 * Date: 2016/7/20 17:03
 */
public class LCAppConfig {
    private String appkey;

    private int logLevel;

    private String appKeyPassWd;
    private boolean push;
    private int pingTime;

    private String logPath;

    private LCAppConfig(Builder builder) {
        this.appkey = builder.appkey;
        this.appKeyPassWd = builder.appKeyPassWd;
        this.logLevel = builder.logLevel;
        this.push=builder.push;
        this.pingTime=builder.pingTime;
        this.logPath=builder.logPath;
    }

    public int getLogLevel() {
        return logLevel;
    }
    public String getLogPath(){
        return logPath;
    }

    public String getAppkey() {
        return appkey;
    }

    public String getAppKeyPassWd() {
        return appKeyPassWd;
    }

    public boolean getPush(){
        return push;
    }
    public int getPingTime(){
        return pingTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String appkey;

        private String appKeyPassWd;

        private int logLevel;
        private boolean push;
        private int pingTime;
        private String logPath;
        public Builder() {
            appkey = "";
            appKeyPassWd = "";
            logLevel = Log.VERBOSE;
            push=true;
            pingTime=10;
            logPath="";
        }

        public Builder setAppkey(String appkey) {
            this.appkey = appkey;
            return this;
        }

        public Builder setAppKeyPassWd(String appKeyPassWd) {
            this.appKeyPassWd = appKeyPassWd;
            return this;
        }

        public Builder setLogLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }
        public Builder setPingTime(int pingTime) {
            this.pingTime=pingTime;
            return this;
        }

        public Builder setPush(boolean push){
            this.push=push;
            return this;
        }
        public Builder setLogPath(String logPath){
            this.logPath=logPath;
            return this;
        }

        public LCAppConfig build() {
            return new LCAppConfig(this);
        }
    }

}
