package com.littlec.sdk.connect.core;

import com.littlec.sdk.utils.LCSingletonFactory;

/**
 * ClassName: LCPoolFactory
 * Description:  ThredPool factory
 * Creator: user
 * Date: 2016/7/19 17:24
 */
public class LCPoolFactory {
    public static ILCPoolManager getThreadPoolManager() {
        return LCSingletonFactory.getInstance(LCPoolManager.class);
    }
}
