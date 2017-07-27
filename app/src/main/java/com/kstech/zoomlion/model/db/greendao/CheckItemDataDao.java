package com.kstech.zoomlion.model.db.greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.kstech.zoomlion.model.db.CheckRecord;

import com.kstech.zoomlion.model.db.CheckItemData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHECK_ITEM_DATA".
*/
public class CheckItemDataDao extends AbstractDao<CheckItemData, Long> {

    public static final String TABLENAME = "CHECK_ITEM_DATA";

    /**
     * Properties of entity CheckItemData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property CheckItemId = new Property(0, Long.class, "checkItemId", true, "_id");
        public final static Property QcId = new Property(1, Integer.class, "qcId", false, "QC_ID");
        public final static Property ItemName = new Property(2, String.class, "itemName", false, "ITEM_NAME");
        public final static Property CheckResult = new Property(3, Integer.class, "checkResult", false, "CHECK_RESULT");
        public final static Property SumCounts = new Property(4, Integer.class, "sumCounts", false, "SUM_COUNTS");
        public final static Property UnpassCounts = new Property(5, Integer.class, "unpassCounts", false, "UNPASS_COUNTS");
        public final static Property RecordId = new Property(6, Long.class, "recordId", false, "RECORD_ID");
        public final static Property SkipCheck = new Property(7, Boolean.class, "skipCheck", false, "SKIP_CHECK");
        public final static Property Uploaded = new Property(8, Boolean.class, "uploaded", false, "UPLOADED");
        public final static Property ItemDesc = new Property(9, String.class, "itemDesc", false, "ITEM_DESC");
    }

    private DaoSession daoSession;

    private Query<CheckItemData> checkRecord_CheckItemDatasQuery;

    public CheckItemDataDao(DaoConfig config) {
        super(config);
    }
    
    public CheckItemDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHECK_ITEM_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: checkItemId
                "\"QC_ID\" INTEGER UNIQUE ," + // 1: qcId
                "\"ITEM_NAME\" TEXT," + // 2: itemName
                "\"CHECK_RESULT\" INTEGER," + // 3: checkResult
                "\"SUM_COUNTS\" INTEGER," + // 4: sumCounts
                "\"UNPASS_COUNTS\" INTEGER," + // 5: unpassCounts
                "\"RECORD_ID\" INTEGER," + // 6: recordId
                "\"SKIP_CHECK\" INTEGER," + // 7: skipCheck
                "\"UPLOADED\" INTEGER," + // 8: uploaded
                "\"ITEM_DESC\" TEXT);"); // 9: itemDesc
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHECK_ITEM_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CheckItemData entity) {
        stmt.clearBindings();
 
        Long checkItemId = entity.getCheckItemId();
        if (checkItemId != null) {
            stmt.bindLong(1, checkItemId);
        }
 
        Integer qcId = entity.getQcId();
        if (qcId != null) {
            stmt.bindLong(2, qcId);
        }
 
        String itemName = entity.getItemName();
        if (itemName != null) {
            stmt.bindString(3, itemName);
        }
 
        Integer checkResult = entity.getCheckResult();
        if (checkResult != null) {
            stmt.bindLong(4, checkResult);
        }
 
        Integer sumCounts = entity.getSumCounts();
        if (sumCounts != null) {
            stmt.bindLong(5, sumCounts);
        }
 
        Integer unpassCounts = entity.getUnpassCounts();
        if (unpassCounts != null) {
            stmt.bindLong(6, unpassCounts);
        }
 
        Long recordId = entity.getRecordId();
        if (recordId != null) {
            stmt.bindLong(7, recordId);
        }
 
        Boolean skipCheck = entity.getSkipCheck();
        if (skipCheck != null) {
            stmt.bindLong(8, skipCheck ? 1L: 0L);
        }
 
        Boolean uploaded = entity.getUploaded();
        if (uploaded != null) {
            stmt.bindLong(9, uploaded ? 1L: 0L);
        }
 
        String itemDesc = entity.getItemDesc();
        if (itemDesc != null) {
            stmt.bindString(10, itemDesc);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CheckItemData entity) {
        stmt.clearBindings();
 
        Long checkItemId = entity.getCheckItemId();
        if (checkItemId != null) {
            stmt.bindLong(1, checkItemId);
        }
 
        Integer qcId = entity.getQcId();
        if (qcId != null) {
            stmt.bindLong(2, qcId);
        }
 
        String itemName = entity.getItemName();
        if (itemName != null) {
            stmt.bindString(3, itemName);
        }
 
        Integer checkResult = entity.getCheckResult();
        if (checkResult != null) {
            stmt.bindLong(4, checkResult);
        }
 
        Integer sumCounts = entity.getSumCounts();
        if (sumCounts != null) {
            stmt.bindLong(5, sumCounts);
        }
 
        Integer unpassCounts = entity.getUnpassCounts();
        if (unpassCounts != null) {
            stmt.bindLong(6, unpassCounts);
        }
 
        Long recordId = entity.getRecordId();
        if (recordId != null) {
            stmt.bindLong(7, recordId);
        }
 
        Boolean skipCheck = entity.getSkipCheck();
        if (skipCheck != null) {
            stmt.bindLong(8, skipCheck ? 1L: 0L);
        }
 
        Boolean uploaded = entity.getUploaded();
        if (uploaded != null) {
            stmt.bindLong(9, uploaded ? 1L: 0L);
        }
 
        String itemDesc = entity.getItemDesc();
        if (itemDesc != null) {
            stmt.bindString(10, itemDesc);
        }
    }

    @Override
    protected final void attachEntity(CheckItemData entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CheckItemData readEntity(Cursor cursor, int offset) {
        CheckItemData entity = new CheckItemData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // checkItemId
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // qcId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // itemName
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // checkResult
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // sumCounts
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // unpassCounts
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // recordId
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // skipCheck
            cursor.isNull(offset + 8) ? null : cursor.getShort(offset + 8) != 0, // uploaded
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // itemDesc
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CheckItemData entity, int offset) {
        entity.setCheckItemId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQcId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setItemName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCheckResult(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setSumCounts(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setUnpassCounts(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setRecordId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setSkipCheck(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setUploaded(cursor.isNull(offset + 8) ? null : cursor.getShort(offset + 8) != 0);
        entity.setItemDesc(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CheckItemData entity, long rowId) {
        entity.setCheckItemId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CheckItemData entity) {
        if(entity != null) {
            return entity.getCheckItemId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CheckItemData entity) {
        return entity.getCheckItemId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "checkItemDatas" to-many relationship of CheckRecord. */
    public List<CheckItemData> _queryCheckRecord_CheckItemDatas(Long recordId) {
        synchronized (this) {
            if (checkRecord_CheckItemDatasQuery == null) {
                QueryBuilder<CheckItemData> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.RecordId.eq(null));
                checkRecord_CheckItemDatasQuery = queryBuilder.build();
            }
        }
        Query<CheckItemData> query = checkRecord_CheckItemDatasQuery.forCurrentThread();
        query.setParameter(0, recordId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getCheckRecordDao().getAllColumns());
            builder.append(" FROM CHECK_ITEM_DATA T");
            builder.append(" LEFT JOIN CHECK_RECORD T0 ON T.\"RECORD_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected CheckItemData loadCurrentDeep(Cursor cursor, boolean lock) {
        CheckItemData entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        CheckRecord checkRecord = loadCurrentOther(daoSession.getCheckRecordDao(), cursor, offset);
        entity.setCheckRecord(checkRecord);

        return entity;    
    }

    public CheckItemData loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<CheckItemData> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<CheckItemData> list = new ArrayList<CheckItemData>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<CheckItemData> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<CheckItemData> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
