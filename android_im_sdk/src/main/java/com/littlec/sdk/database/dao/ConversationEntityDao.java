package com.littlec.sdk.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.littlec.sdk.database.entity.ConversationEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONVERSATION_ENTITY".
*/
public class ConversationEntityDao extends AbstractDao<ConversationEntity, String> {

    public static final String TABLENAME = "CONVERSATION_ENTITY";

    /**
     * Properties of entity ConversationEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _recipientId = new Property(0, String.class, "_recipientId", true, "_RECIPIENT_ID");
        public final static Property Top = new Property(1, Boolean.class, "top", false, "TOP");
        public final static Property Hide = new Property(2, Boolean.class, "hide", false, "HIDE");
        public final static Property Chattype = new Property(3, int.class, "chattype", false, "CHATTYPE");
        public final static Property MsgContent = new Property(4, String.class, "msgContent", false, "MSG_CONTENT");
        public final static Property MsgContentType = new Property(5, int.class, "msgContentType", false, "MSG_CONTENT_TYPE");
        public final static Property MsgStatus = new Property(6, int.class, "msgStatus", false, "MSG_STATUS");
        public final static Property Date = new Property(7, long.class, "date", false, "DATE");
        public final static Property TotalCount = new Property(8, int.class, "totalCount", false, "TOTAL_COUNT");
        public final static Property UnreadCount = new Property(9, int.class, "unreadCount", false, "UNREAD_COUNT");
        public final static Property Data1 = new Property(10, String.class, "data1", false, "DATA1");
    }

    private DaoSession daoSession;


    public ConversationEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ConversationEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONVERSATION_ENTITY\" (" + //
                "\"_RECIPIENT_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: _recipientId
                "\"TOP\" INTEGER," + // 1: top
                "\"HIDE\" INTEGER," + // 2: hide
                "\"CHATTYPE\" INTEGER NOT NULL ," + // 3: chattype
                "\"MSG_CONTENT\" TEXT," + // 4: msgContent
                "\"MSG_CONTENT_TYPE\" INTEGER NOT NULL ," + // 5: msgContentType
                "\"MSG_STATUS\" INTEGER NOT NULL ," + // 6: msgStatus
                "\"DATE\" INTEGER NOT NULL ," + // 7: date
                "\"TOTAL_COUNT\" INTEGER NOT NULL ," + // 8: totalCount
                "\"UNREAD_COUNT\" INTEGER NOT NULL ," + // 9: unreadCount
                "\"DATA1\" TEXT);"); // 10: data1
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONVERSATION_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ConversationEntity entity) {
        stmt.clearBindings();
 
        String _recipientId = entity.get_recipientId();
        if (_recipientId != null) {
            stmt.bindString(1, _recipientId);
        }
 
        Boolean top = entity.getTop();
        if (top != null) {
            stmt.bindLong(2, top ? 1L: 0L);
        }
 
        Boolean hide = entity.getHide();
        if (hide != null) {
            stmt.bindLong(3, hide ? 1L: 0L);
        }
        stmt.bindLong(4, entity.getChattype());
 
        String msgContent = entity.getMsgContent();
        if (msgContent != null) {
            stmt.bindString(5, msgContent);
        }
        stmt.bindLong(6, entity.getMsgContentType());
        stmt.bindLong(7, entity.getMsgStatus());
        stmt.bindLong(8, entity.getDate());
        stmt.bindLong(9, entity.getTotalCount());
        stmt.bindLong(10, entity.getUnreadCount());
 
        String data1 = entity.getData1();
        if (data1 != null) {
            stmt.bindString(11, data1);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ConversationEntity entity) {
        stmt.clearBindings();
 
        String _recipientId = entity.get_recipientId();
        if (_recipientId != null) {
            stmt.bindString(1, _recipientId);
        }
 
        Boolean top = entity.getTop();
        if (top != null) {
            stmt.bindLong(2, top ? 1L: 0L);
        }
 
        Boolean hide = entity.getHide();
        if (hide != null) {
            stmt.bindLong(3, hide ? 1L: 0L);
        }
        stmt.bindLong(4, entity.getChattype());
 
        String msgContent = entity.getMsgContent();
        if (msgContent != null) {
            stmt.bindString(5, msgContent);
        }
        stmt.bindLong(6, entity.getMsgContentType());
        stmt.bindLong(7, entity.getMsgStatus());
        stmt.bindLong(8, entity.getDate());
        stmt.bindLong(9, entity.getTotalCount());
        stmt.bindLong(10, entity.getUnreadCount());
 
        String data1 = entity.getData1();
        if (data1 != null) {
            stmt.bindString(11, data1);
        }
    }

    @Override
    protected final void attachEntity(ConversationEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public ConversationEntity readEntity(Cursor cursor, int offset) {
        ConversationEntity entity = new ConversationEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // _recipientId
            cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0, // top
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // hide
            cursor.getInt(offset + 3), // chattype
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // msgContent
            cursor.getInt(offset + 5), // msgContentType
            cursor.getInt(offset + 6), // msgStatus
            cursor.getLong(offset + 7), // date
            cursor.getInt(offset + 8), // totalCount
            cursor.getInt(offset + 9), // unreadCount
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // data1
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ConversationEntity entity, int offset) {
        entity.set_recipientId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTop(cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0);
        entity.setHide(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setChattype(cursor.getInt(offset + 3));
        entity.setMsgContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMsgContentType(cursor.getInt(offset + 5));
        entity.setMsgStatus(cursor.getInt(offset + 6));
        entity.setDate(cursor.getLong(offset + 7));
        entity.setTotalCount(cursor.getInt(offset + 8));
        entity.setUnreadCount(cursor.getInt(offset + 9));
        entity.setData1(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final String updateKeyAfterInsert(ConversationEntity entity, long rowId) {
        return entity.get_recipientId();
    }
    
    @Override
    public String getKey(ConversationEntity entity) {
        if(entity != null) {
            return entity.get_recipientId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ConversationEntity entity) {
        return entity.get_recipientId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
