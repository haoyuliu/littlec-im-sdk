/* Project: android_im_sdk
 * 
 * File Created at 2016/8/3
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.util;

import com.littlec.sdk.connect.core.IConnectBuilder;
import com.littlec.sdk.connect.core.ILCBuilder;
import com.fingo.littlec.proto.css.Connector;

/**
 * @Type com.littlec.sdk.chat.core.builder
 * @User user
 * @Desc 生产者控制类
 * @Date 2016/8/3
 * @Version
 */
public class LCDirector {
    /**
     * @Title: constructUnaryRequest <br>
     * @Description: 包含所有一对一请求的构造 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/25 9:53
     */
    public static Connector.UnaryRequest constructUnaryRequest(ILCBuilder builder) {
        if (builder != null) {
            return builder.buildUnaryRequest();
        } else
            return null;
    }

    public static Connector.SessionRequest constructLoginRequest(IConnectBuilder loginBuilder, Connector.SessionRequest.ESessionRequestType type) {
        if (loginBuilder != null) {
            Connector.SessionRequest request = Connector.SessionRequest.newBuilder()
                    .setType(type)
                    .setBaseInfo(loginBuilder.buildEntity())
                    .setData(loginBuilder.buildLoginRequest().toByteString()).build();
            return request;
        }
        return null;
    }

    public static Connector.SessionRequest constructLogoutRequest(IConnectBuilder builder) {
        if (builder != null) {
            Connector.SessionRequest request = Connector.SessionRequest.newBuilder()
                    .setType(Connector.SessionRequest.ESessionRequestType.LOGOUT)
//                    .setBaseInfo(builder.buildEntity())
                    .setData(builder.buildLogoutRequest().toByteString()).build();
            return request;
        }
        return null;
    }

    public static Connector.SessionRequest constructPingRequest(IConnectBuilder builder, String msgId) {
        if (builder != null) {
            Connector.SessionRequest request = Connector.SessionRequest.newBuilder()
                    .setType(Connector.SessionRequest.ESessionRequestType.PING)
//                    .setBaseInfo(builder.buildEntity())
                    .setData(builder.buildPingRequest(msgId).toByteString()).build();
            return request;
        }
        return null;
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/3 user creat
 */
