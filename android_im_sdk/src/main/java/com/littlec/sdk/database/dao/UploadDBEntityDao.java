package com.littlec.sdk.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.littlec.sdk.database.entity.UploadDBEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "UPLOAD_DBENTITY".
*/
public class UploadDBEntityDao extends AbstractDao<UploadDBEntity, String> {

    public static final String TABLENAME = "UPLOAD_DBENTITY";

    /**
     * Properties of entity UploadDBEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UploadId = new Property(0, String.class, "uploadId", true, "UPLOAD_ID");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property LocalPath = new Property(2, String.class, "localPath", false, "LOCAL_PATH");
        public final static Property Count = new Property(3, Integer.class, "count", false, "COUNT");
        public final static Property CompletedSize = new Property(4, Long.class, "completedSize", false, "COMPLETED_SIZE");
        public final static Property TotalSize = new Property(5, Long.class, "totalSize", false, "TOTAL_SIZE");
        public final static Property UploadStatus = new Property(6, Integer.class, "uploadStatus", false, "UPLOAD_STATUS");
    }


    public UploadDBEntityDao(DaoConfig config) {
        super(config);
    }
    
    public UploadDBEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"UPLOAD_DBENTITY\" (" + //
                "\"UPLOAD_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: uploadId
                "\"UUID\" TEXT," + // 1: uuid
                "\"LOCAL_PATH\" TEXT," + // 2: localPath
                "\"COUNT\" INTEGER," + // 3: count
                "\"COMPLETED_SIZE\" INTEGER," + // 4: completedSize
                "\"TOTAL_SIZE\" INTEGER," + // 5: totalSize
                "\"UPLOAD_STATUS\" INTEGER);"); // 6: uploadStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"UPLOAD_DBENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UploadDBEntity entity) {
        stmt.clearBindings();
 
        String uploadId = entity.getUploadId();
        if (uploadId != null) {
            stmt.bindString(1, uploadId);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(3, localPath);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(4, count);
        }
 
        Long completedSize = entity.getCompletedSize();
        if (completedSize != null) {
            stmt.bindLong(5, completedSize);
        }
 
        Long totalSize = entity.getTotalSize();
        if (totalSize != null) {
            stmt.bindLong(6, totalSize);
        }
 
        Integer uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindLong(7, uploadStatus);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UploadDBEntity entity) {
        stmt.clearBindings();
 
        String uploadId = entity.getUploadId();
        if (uploadId != null) {
            stmt.bindString(1, uploadId);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(3, localPath);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(4, count);
        }
 
        Long completedSize = entity.getCompletedSize();
        if (completedSize != null) {
            stmt.bindLong(5, completedSize);
        }
 
        Long totalSize = entity.getTotalSize();
        if (totalSize != null) {
            stmt.bindLong(6, totalSize);
        }
 
        Integer uploadStatus = entity.getUploadStatus();
        if (uploadStatus != null) {
            stmt.bindLong(7, uploadStatus);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public UploadDBEntity readEntity(Cursor cursor, int offset) {
        UploadDBEntity entity = new UploadDBEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // uploadId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // localPath
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // count
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // completedSize
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // totalSize
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // uploadStatus
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UploadDBEntity entity, int offset) {
        entity.setUploadId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLocalPath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setCompletedSize(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setTotalSize(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setUploadStatus(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    @Override
    protected final String updateKeyAfterInsert(UploadDBEntity entity, long rowId) {
        return entity.getUploadId();
    }
    
    @Override
    public String getKey(UploadDBEntity entity) {
        if(entity != null) {
            return entity.getUploadId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UploadDBEntity entity) {
        return entity.getUploadId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
