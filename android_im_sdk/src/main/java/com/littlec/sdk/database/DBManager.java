package com.littlec.sdk.database;

import android.content.Context;

import com.littlec.sdk.config.LCChatConfig;
import com.littlec.sdk.database.dao.ContactEntityDao;
import com.littlec.sdk.database.dao.ConversationEntityDao;
import com.littlec.sdk.database.dao.DaoMaster;
import com.littlec.sdk.database.dao.DaoSession;
import com.littlec.sdk.database.dao.DownloadDBEntityDao;
import com.littlec.sdk.database.dao.ExcTaskDBEntityDao;
import com.littlec.sdk.database.dao.FriendReqDBEntityDao;
import com.littlec.sdk.database.dao.GroupEntityDao;
import com.littlec.sdk.database.dao.MediaEntityDao;
import com.littlec.sdk.database.dao.MemberEntityDao;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.dao.UploadDBEntityDao;
import com.littlec.sdk.utils.LCLogger;

import org.greenrobot.greendao.database.Database;


/**
 * ClassName: DBManager
 * Description:  所有表结构对象的持有者，负责数据库的初始化和销毁
 * Creator: user
 * Date: 2016/7/15 17:36
 */
class DBManager implements IDBManager {
    private static final String TAG = "DBManager";
    private LCLogger Logger = LCLogger.getLogger(TAG);
    private DaoSession daoSession;
    private boolean ENCRYPTED = false;//是否加密
    private Database db;
    private RcsOpenHelper openHelper;
    private String passWd = "123456";
    private volatile MessageEntityDao messagedbService;
    private volatile ContactEntityDao dbContactService;
    private volatile ConversationEntityDao dbConversationService;
    private volatile MediaEntityDao dbMediaService;
    private volatile GroupEntityDao dbGroupService;
    private volatile MemberEntityDao dbMemberService;

    private volatile UploadDBEntityDao dbUploadService;
    private volatile DownloadDBEntityDao downloadSService;

    private volatile ExcTaskDBEntityDao dbExcTaskService;
    private volatile FriendReqDBEntityDao dbFriendReqService;

    private DBManager() {
    }

    public void initDataBase(Context context) {
        Logger.d("DBName:" + getDBName());
        if (openHelper != null) {
            if (getDBName().equals(openHelper.getDatabaseName())) {
                Logger.d("db is the same ，return");
                return;
            } else {
                //关闭
                Logger.d("onDestory");
                onDestory();
            }
        }
        try {
            openHelper = new RcsOpenHelper(context, getDBName());
            db = (ENCRYPTED ? openHelper.getEncryptedWritableDb(passWd) : openHelper.getWritableDb());
            daoSession = new DaoMaster(db).newSession();
        } catch (NoClassDefFoundError e) {
            Logger.e("no greendao complied");
            e.printStackTrace();
        }

    }

    private String getDBName() {
        return "littlec_" + LCChatConfig.LCChatGlobalStorage.getInstance().getAppKey() + "_"
                + LCChatConfig.LCChatGlobalStorage.getInstance().getLoginUserName() + "_encrypted";
    }

    public ContactEntityDao getDBContactService() {
        if (dbContactService == null) {
            synchronized (DBManager.class) {
                if (dbContactService == null) {
                    dbContactService = daoSession.getContactEntityDao();
                }
            }
        }
        return dbContactService;
    }

    public ConversationEntityDao getDBConversationService() {
        if (dbConversationService == null) {
            synchronized (DBManager.class) {
                if (dbConversationService == null) {
                    dbConversationService = daoSession.getConversationEntityDao();
                }
            }
        }
        return dbConversationService;
    }

    public MessageEntityDao getDBMessageService() {
        if (messagedbService == null) {
            synchronized (DBManager.class) {
                if (messagedbService == null) {
                    messagedbService = daoSession.getMessageEntityDao();
                }
            }
        }
        return messagedbService;
    }

    public MediaEntityDao getDBMediaService() {
        if (dbMediaService == null) {
            synchronized (DBManager.class) {
                if (dbMediaService == null) {
                    dbMediaService = daoSession.getMediaEntityDao();
                }
            }
        }
        return dbMediaService;
    }

    public GroupEntityDao getDBGroupService() {
        if (dbGroupService == null) {
            synchronized (DBManager.class) {
                if (dbGroupService == null) {
                    dbGroupService = daoSession.getGroupEntityDao();
                }
            }
        }
        return dbGroupService;
    }

    public MemberEntityDao getDBMemberService() {
        if (dbMemberService == null) {
            synchronized (DBManager.class) {
                if (dbMemberService == null) {
                    dbMemberService = daoSession.getMemberEntityDao();
                }
            }
        }
        return dbMemberService;
    }

    public DownloadDBEntityDao getDBDownloadService() {
        if (downloadSService == null) {
            synchronized (DBManager.class) {
                if (downloadSService == null) {
                    downloadSService = daoSession.getDownloadDBEntityDao();
                }
            }
        }
        return downloadSService;
    }

    public UploadDBEntityDao getDBUploadService() {
        if (dbUploadService == null) {
            synchronized (DBManager.class) {
                if (dbUploadService == null) {
                    dbUploadService = daoSession.getUploadDBEntityDao();
                }
            }
        }
        return dbUploadService;
    }

    public ExcTaskDBEntityDao getDBExcTaskService() {
        if (dbExcTaskService == null) {
            synchronized (DBManager.class) {
                if (dbExcTaskService == null) {
                    dbExcTaskService = daoSession.getExcTaskDBEntityDao();
                }
            }
        }
        return dbExcTaskService;
    }

    public FriendReqDBEntityDao getDBFriendReqService() {
        if (dbFriendReqService == null) {
            synchronized (DBManager.class) {
                if (dbFriendReqService == null) {
                    dbFriendReqService = daoSession.getFriendReqDBEntityDao();
                }
            }
        }
        return dbFriendReqService;
    }

    @Override
    public void onDestory() {
        try {
            if (db != null) {
                db.close();
            }
            if (openHelper != null) {
                openHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db = null;
            openHelper = null;
        }

        messagedbService = null;
        dbContactService = null;
        dbConversationService = null;
        dbMediaService = null;
        dbGroupService = null;
        dbMemberService = null;
        dbUploadService = null;
        downloadSService = null;
        dbExcTaskService = null;
        dbFriendReqService = null;

    }

}
