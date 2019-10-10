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
package com.littlec.sdk.connect.core;

import com.fingo.littlec.proto.css.Connector;

/**
 * @Type com.littlec.sdk.chat.core.builder
 * @User user
 * @Desc
 * @Date 2016/8/3
 * @Version
 */
public interface ILCBuilder {
    Connector.UnaryRequest buildUnaryRequest();
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/3 user creat
 */
