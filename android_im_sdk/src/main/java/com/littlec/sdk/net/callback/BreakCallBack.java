/* Project: android_im_sdk
 * 
 * File Created at 2016/8/12
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.net.callback;

import com.littlec.sdk.biz.chat.entity.LCMessage;

/**
 * @Type com.littlec.sdk.utils.http.callback
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/8/12
 * @Version
 */

public interface BreakCallBack {
    void success(String result, LCMessage message);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/12 zhangguoqiong creat
 */
