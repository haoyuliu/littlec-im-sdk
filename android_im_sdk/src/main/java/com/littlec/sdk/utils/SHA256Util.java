/* Project: android_im_sdk
 * 
 * File Created at 2016/12/9
 * 
 * Copyright 2016 XXX Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * XXX Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.littlec.sdk.utils;

import java.security.MessageDigest;

/**
 * @Type com.littlec.sdk.utils
 * @User zhangguoqiong
 * @Desc
 * @Date 2016/12/9
 * @Version
 */

public class SHA256Util {
    /**
     * SHA 256 encrypt.
     *
     * @param sourceStr the source str
     * @return the string
     */
    public static String SHA256Encrypt(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/12/9 zhangguoqiong creat
 */
