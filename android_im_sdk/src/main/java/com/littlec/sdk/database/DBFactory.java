package com.littlec.sdk.database;

import com.littlec.sdk.utils.LCSingletonFactory;

/**
 * ClassName: DBFactory
 * Description:  db model factory
 * Creator: user
 * Date: 2016/7/20 10:51
 */
public class DBFactory {
    public static IDBManager getDBManager() {
        return LCSingletonFactory.getInstance(DBManager.class);
    }
}
