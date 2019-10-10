/* Project: android_im_sdk
 * 
 * File Created at 2016/9/21
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.listener;

/**
 * @Type com.littlec.sdk
 * @User user
 * @Desc 公共模块回调
 * @Date 2016/9/21
 * @Version
 */
public interface LCCommonCallBack {

    void onSuccess();

    void onFailed(int code, String errorMsg);
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/9/21 user creat
 */