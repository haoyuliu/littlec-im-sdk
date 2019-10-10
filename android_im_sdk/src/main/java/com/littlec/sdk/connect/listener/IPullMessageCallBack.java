/* Project: android_im_sdk
 * 
 * File Created at 2016/8/19
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.connect.listener;

/**
 * @Type com.littlec.sdk.chat.core.launcher
 * @User user
 * @Desc
 * @Date 2016/8/19
 * @Version
 */
public interface IPullMessageCallBack {
    /**
     * @Title: cancelPullTimer <br>
     * @Description: 主要用于循环拉取离线消息时候，取消延时任务<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/26 14:29
     */
    void cancelPullTimer(boolean recOrSend);

    /**
     * @Title: pullCompleted <br>
     * @Description: 拉取完毕的接口，保持拉取消息的同步 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/8/26 14:29
     */
    void pullCompleted(boolean recOrSend,boolean completed);

    /**
     * @Title: pullAllMessage <br>
     * @Description: 用于登录成功后，第一次拉取的接口<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/10/10 10:54
     */
    void pullAllMessage(boolean recOrSend);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/8/19 user creat
 */