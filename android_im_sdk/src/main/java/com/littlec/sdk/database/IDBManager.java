package com.littlec.sdk.database;

import android.content.Context;

import com.littlec.sdk.database.dao.ContactEntityDao;
import com.littlec.sdk.database.dao.ConversationEntityDao;
import com.littlec.sdk.database.dao.DownloadDBEntityDao;
import com.littlec.sdk.database.dao.ExcTaskDBEntityDao;
import com.littlec.sdk.database.dao.FriendReqDBEntityDao;
import com.littlec.sdk.database.dao.GroupEntityDao;
import com.littlec.sdk.database.dao.MediaEntityDao;
import com.littlec.sdk.database.dao.MemberEntityDao;
import com.littlec.sdk.database.dao.MessageEntityDao;
import com.littlec.sdk.database.dao.UploadDBEntityDao;


/** 
 * ClassName: IDBManager
 * Description:  DBManager interface
 * Creator: user
 * Date: 2016/7/20 10:52
 */
public interface IDBManager {

    void initDataBase(Context context);

    ContactEntityDao getDBContactService();

    ConversationEntityDao getDBConversationService();

    MessageEntityDao getDBMessageService();

    MediaEntityDao getDBMediaService();

    GroupEntityDao getDBGroupService();

    MemberEntityDao getDBMemberService();

    DownloadDBEntityDao getDBDownloadService();

    UploadDBEntityDao getDBUploadService();

    ExcTaskDBEntityDao getDBExcTaskService();

    FriendReqDBEntityDao getDBFriendReqService();

    void onDestory();


}
