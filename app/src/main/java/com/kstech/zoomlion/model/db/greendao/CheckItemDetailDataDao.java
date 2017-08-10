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

import com.kstech.zoomlion.model.db.CheckItemData;

import com.kstech.zoomlion.model.db.CheckItemDetailData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHECK_ITEM_DETAIL_DATA".
*/
public class CheckItemDetailDataDao extends AbstractDao<CheckItemDetailData, Long> {

    public static final String TABLENAME = "CHECK_ITEM_DETAIL_DATA";

    /**
     * Properties of entity CheckItemDetailData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property CheckItemDetailId = new Property(0, Long.class, "checkItemDetailId", true, "_id");
        public final static Property ItemId = new Property(1, Long.class, "itemId", false, "ITEM_ID");
        public final static Property CheckerId = new Property(2, Long.class, "checkerId", false, "CHECKER_ID");
        public final static Property CheckerName = new Property(3, String.class, "checkerName", false, "CHECKER_NAME");
        public final static Property MeasureDeviceId = new Property(4, Long.class, "measureDeviceId", false, "MEASURE_DEVICE_ID");
        public final static Property MeasureDeviceName = new Property(5, String.class, "measureDeviceName", false, "MEASURE_DEVICE_NAME");
        public final static Property ParamsValues = new Property(6, String.class, "paramsValues", false, "PARAMS_VALUES");
        public final static Property CheckResult = new Property(7, Integer.class, "checkResult", false, "CHECK_RESULT");
        public final static Property StartTime = new Property(8, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(9, java.util.Date.class, "endTime", false, "END_TIME");
        public final static Property Uploaded = new Property(10, Boolean.class, "uploaded", false, "UPLOADED");
    }

    private DaoSession daoSession;

    private Query<CheckItemDetailData> checkItemData_CheckItemDetailDatasQuery;

    public CheckItemDetailDataDao(DaoConfig config) {
        super(config);
    }
    
    public CheckItemDetailDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHECK_ITEM_DETAIL_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: checkItemDetailId
                "\"ITEM_ID\" INTEGER," + // 1: itemId
                "\"CHECKER_ID\" INTEGER," + // 2: checkerId
                "\"CHECKER_NAME\" TEXT," + // 3: checkerName
                "\"MEASURE_DEVICE_ID\" INTEGER," + // 4: measureDeviceId
                "\"MEASURE_DEVICE_NAME\" TEXT," + // 5: measureDeviceName
                "\"PARAMS_VALUES\" TEXT," + // 6: paramsValues
                "\"CHECK_RESULT\" INTEGER," + // 7: checkResult
                "\"START_TIME\" INTEGER UNIQUE ," + // 8: startTime
                "\"END_TIME\" INTEGER UNIQUE ," + // 9: endTime
                "\"UPLOADED\" INTEGER);"); // 10: uploaded
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHECK_ITEM_DETAIL_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CheckItemDetailData entity) {
        stmt.clearBindings();
 
        Long checkItemDetailId = entity.getCheckItemDetailId();
        if (checkItemDetailId != null) {
            stmt.bindLong(1, checkItemDetailId);
        }
 
        Long itemId = entity.getItemId();
        if (itemId != null) {
            stmt.bindLong(2, itemId);
        }
 
        Long checkerId = entity.getCheckerId();
        if (checkerId != null) {
            stmt.bindLong(3, checkerId);
        }
 
        String checkerName = entity.getCheckerName();
        if (checkerName != null) {
            stmt.bindString(4, checkerName);
        }
 
        Long measureDeviceId = entity.getMeasureDeviceId();
        if (measureDeviceId != null) {
            stmt.bindLong(5, measureDeviceId);
        }
 
        String measureDeviceName = entity.getMeasureDeviceName();
        if (measureDeviceName != null) {
            stmt.bindString(6, measureDeviceName);
        }
 
        String paramsValues = entity.getParamsValues();
        if (paramsValues != null) {
            stmt.bindString(7, paramsValues);
        }
 
        Integer checkResult = entity.getCheckResult();
        if (checkResult != null) {
            stmt.bindLong(8, checkResult);
        }
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(9, startTime.getTime());
        }
 
        java.util.Date endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(10, endTime.getTime());
        }
 
        Boolean uploaded = entity.getUploaded();
        if (uploaded != null) {
            stmt.bindLong(11, uploaded ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CheckItemDetailData entity) {
        stmt.clearBindings();
 
        Long checkItemDetailId = entity.getCheckItemDetailId();
        if (checkItemDetailId != null) {
            stmt.bindLong(1, checkItemDetailId);
        }
 
        Long itemId = entity.getItemId();
        if (itemId != null) {
            stmt.bindLong(2, itemId);
        }
 
        Long checkerId = entity.getCheckerId();
        if (checkerId != null) {
            stmt.bindLong(3, checkerId);
        }
 
        String checkerName = entity.getCheckerName();
        if (checkerName != null) {
            stmt.bindString(4, checkerName);
        }
 
        Long measureDeviceId = entity.getMeasureDeviceId();
        if (measureDeviceId != null) {
            stmt.bindLong(5, measureDeviceId);
        }
 
        String measureDeviceName = entity.getMeasureDeviceName();
        if (measureDeviceName != null) {
            stmt.bindString(6, measureDeviceName);
        }
 
        String paramsValues = entity.getParamsValues();
        if (paramsValues != null) {
            stmt.bindString(7, paramsValues);
        }
 
        Integer checkResult = entity.getCheckResult();
        if (checkResult != null) {
            stmt.bindLong(8, checkResult);
        }
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(9, startTime.getTime());
        }
 
        java.util.Date endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(10, endTime.getTime());
        }
 
        Boolean uploaded = entity.getUploaded();
        if (uploaded != null) {
            stmt.bindLong(11, uploaded ? 1L: 0L);
        }
    }

    @Override
    protected final void attachEntity(CheckItemDetailData entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CheckItemDetailData readEntity(Cursor cursor, int offset) {
        CheckItemDetailData entity = new CheckItemDetailData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // checkItemDetailId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // itemId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // checkerId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // checkerName
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // measureDeviceId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // measureDeviceName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // paramsValues
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // checkResult
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // startTime
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // endTime
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0 // uploaded
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CheckItemDetailData entity, int offset) {
        entity.setCheckItemDetailId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setItemId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setCheckerId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setCheckerName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMeasureDeviceId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setMeasureDeviceName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setParamsValues(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCheckResult(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setStartTime(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setEndTime(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setUploaded(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CheckItemDetailData entity, long rowId) {
        entity.setCheckItemDetailId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CheckItemDetailData entity) {
        if(entity != null) {
            return entity.getCheckItemDetailId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CheckItemDetailData entity) {
        return entity.getCheckItemDetailId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "checkItemDetailDatas" to-many relationship of CheckItemData. */
    public List<CheckItemDetailData> _queryCheckItemData_CheckItemDetailDatas(Long itemId) {
        synchronized (this) {
            if (checkItemData_CheckItemDetailDatasQuery == null) {
                QueryBuilder<CheckItemDetailData> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ItemId.eq(null));
                checkItemData_CheckItemDetailDatasQuery = queryBuilder.build();
            }
        }
        Query<CheckItemDetailData> query = checkItemData_CheckItemDetailDatasQuery.forCurrentThread();
        query.setParameter(0, itemId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getCheckItemDataDao().getAllColumns());
            builder.append(" FROM CHECK_ITEM_DETAIL_DATA T");
            builder.append(" LEFT JOIN CHECK_ITEM_DATA T0 ON T.\"ITEM_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected CheckItemDetailData loadCurrentDeep(Cursor cursor, boolean lock) {
        CheckItemDetailData entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        CheckItemData itemData = loadCurrentOther(daoSession.getCheckItemDataDao(), cursor, offset);
        entity.setItemData(itemData);

        return entity;    
    }

    public CheckItemDetailData loadDeep(Long key) {
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
    public List<CheckItemDetailData> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<CheckItemDetailData> list = new ArrayList<CheckItemDetailData>(count);
        
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
    
    protected List<CheckItemDetailData> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<CheckItemDetailData> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
