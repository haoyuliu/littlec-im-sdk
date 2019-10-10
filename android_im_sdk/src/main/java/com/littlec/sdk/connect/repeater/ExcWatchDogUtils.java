/* Project: android_im_sdk
 * 
 * File Created at 2016/7/26
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

import java.util.Random;

/**
 * @Type com.littlec.sdk.chat.core
 * @User user
 * @Desc 看门狗工具类
 * @Date 2016/7/26
 * @Version
 */
class ExcWatchDogUtils {
    //重发最大次数
    private final static int MAXNUM = 16;
    //争用期
    private final static double CONTENTIONPERIOD = 20.48;
    //采用固定延时策略
    private final static DelayStrategy delayStrategy = DelayStrategy.fixed;
    //延时时间为3s
    private final static int delayTime = 3000;

    enum DelayStrategy {
        fixed,
        random;
    }

    /**
     * @Title: getRand <br>
     * @Description: 得到min和max之间的随机数 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/26 16:18
     */
    private static int getRand(int min, int max) {
        int r;
        Random random = new Random();
        r = random.nextInt(max - min + 1) + min;
        return r;
    }

    /**
     * @Title: twoPowerK<br>
     * @Description: 计算2的k次幂 <br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/26 16:25
     */
    private static int twoPowerK(int k) {
        int a = 2;
        int f = 1;
        for (int i = k; i > 0; i--) {
            f *= a;
        }
        return f;
    }

    /**
     * @Title: getBackoffTime <br>
     * @Description: 获取退避时间<br>
     * @param: <br>
     * @return: <br>
     * @throws: 2016/7/26 16:28
     */
    protected static double getBackoffTime(int times) {
        if (delayStrategy == DelayStrategy.fixed) {
            return delayTime;
        } else {
            if (times <= 0)
                return CONTENTIONPERIOD;
            int num;
            if (times > MAXNUM) {
                num = MAXNUM;
            } else {
                num = times;
            }
            return getRand(0, twoPowerK(num)) * CONTENTIONPERIOD;
        }
    }

}
/**
 * Revision history
 * -------------------------------------------------------------------------
 * <p/>
 * Date Author Note
 * -------------------------------------------------------------------------
 * 2016/7/26 user creat
 */
