/* Project: android_im_sdk
 * 
 * File Created at 2016/7/28
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.repeater;

import com.littlec.sdk.connect.core.LCBaseTask;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc 新的异常任务的监听
 * @Date 2016/7/28
 * @Version
 */
interface ExcTaskListener {
    void notify(String taskId, LCBaseTask task);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/28 user creat
 */
