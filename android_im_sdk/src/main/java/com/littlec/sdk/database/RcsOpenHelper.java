package com.littlec.sdk.database;

import android.content.Context;


import com.littlec.sdk.database.dao.DaoMaster;
import com.littlec.sdk.database.dao.TagEntityDao;
import com.littlec.sdk.utils.LCLogger;

import org.greenrobot.greendao.database.Database;


/**
 * Created by wanghaozhou on 2015/6/10.
 */
public class RcsOpenHelper extends DaoMaster.OpenHelper{
    private static final String TAG = "RcsOpenHelper";
    public RcsOpenHelper(Context context, String name) {
        super(context, name);
    }


    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LCLogger.getLogger(TAG).d("RcsOpenHelper on Upgrade from " + "oldVersion " + oldVersion + " to new Version " + newVersion);


    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);

    }
}
