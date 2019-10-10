package com.littlec.sdk.connect.core;


import com.google.protobuf.GeneratedMessageLite;
import com.littlec.sdk.config.LCChatConfig;
import com.fingo.littlec.proto.css.Connector;

public abstract class BaseBuilder {
    protected Connector.UnaryRequest.Builder unaryRequestBuilder;

    protected GeneratedMessageLite.Builder liteBuilder;

    public BaseBuilder(String methodname) {
        unaryRequestBuilder = Connector.UnaryRequest.newBuilder();
        unaryRequestBuilder.setServiceName(getServiceName());
        unaryRequestBuilder.setMethodName(methodname);
    }

    protected abstract String getServiceName();


    public GeneratedMessageLite.Builder getLiteBuilder() {
        return liteBuilder;
    }

    public Connector.UnaryRequest unaryRequestBuild() {
        unaryRequestBuilder.setData(getLiteBuilder().build().toByteString());
        return unaryRequestBuilder.build();
    }

    public static String getUserName() {
        return LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName();
    }

    public static String getAppkey() {
        return LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey();
    }


}