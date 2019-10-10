/* Project: android_im_sdk
 * 
 * File Created at 2016/11/1
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

import com.littlec.sdk.LCClient;

/**
 * @Type com.littlec.sdk
 * @User user
 * @Desc
 * @Date 2016/11/1
 * @Version
 */
public interface LCConnectionListener {
    void onDisConnected();

    void onAccountConflict(LCClient.ClientType clientType);

    void onReConnected();

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/11/1 user creat
 */