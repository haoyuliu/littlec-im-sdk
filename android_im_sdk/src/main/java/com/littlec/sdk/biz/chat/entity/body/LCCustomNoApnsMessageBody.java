/* Project: android_im_sdk
 * 
 * File Created at 2016/9/27
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.biz.chat.entity.body;

import java.util.Map;

/**
 * @Type com.littlec.sdk.chat.bean
 * @User user
 * @Desc 自定义消息体
 * @Date 2016/9/27
 * @Version
 */
public class LCCustomNoApnsMessageBody extends LCCustomMessageBody {

    public LCCustomNoApnsMessageBody(Map<String, String> map, String notification) {
        super(map, notification);
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/27 user creat
 */
