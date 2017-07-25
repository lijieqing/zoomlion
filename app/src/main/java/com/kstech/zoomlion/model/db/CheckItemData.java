package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.db.greendao.AuthorizeRecordDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;

/**
 * Created by lijie on 2017/7/12.
 */

@Entity
public class CheckItemData {
    @Id(autoincrement = true)
    private Long checkItemId;// id

    @Unique
    @Property
    private Integer qcId;//通讯时用到的检测项目ID

    @Property
    private String itemName;//检测项目名称

    @Property
    private Integer checkResult;//检测结果

    @Property
    private Integer sumCounts;//多有检测次数统计值

    @Property
    private Integer unpassCounts;//不合格检测次数统计值

    private Long recordId;

    @ToOne(joinProperty = "recordId")
    private CheckRecord checkRecord;

    @ToMany(referencedJoinProperty = "itemId")
    private List<AuthorizeRecord> authorizeRecords;//授权表

    @ToMany(referencedJoinProperty = "itemId")
    private List<CheckItemDetailData> checkItemDetailDatas;

    @Property
    private Boolean skipCheck;//是否跳过该检测项目

    @Property
    private Boolean uploaded;//是否已上传

    @Property
    private String itemDesc;//项目信息描述

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 224233260)
    private transient CheckItemDataDao myDao;

    @Generated(hash = 1928710603)
    public CheckItemData(Long checkItemId, Integer qcId, String itemName,
            Integer checkResult, Integer sumCounts, Integer unpassCounts,
            Long recordId, Boolean skipCheck, Boolean uploaded, String itemDesc) {
        this.checkItemId = checkItemId;
        this.qcId = qcId;
        this.itemName = itemName;
        this.checkResult = checkResult;
        this.sumCounts = sumCounts;
        this.unpassCounts = unpassCounts;
        this.recordId = recordId;
        this.skipCheck = skipCheck;
        this.uploaded = uploaded;
        this.itemDesc = itemDesc;
    }

    @Generated(hash = 1964820912)
    public CheckItemData() {
    }

    public Long getCheckItemId() {
        return this.checkItemId;
    }

    public void setCheckItemId(Long checkItemId) {
        this.checkItemId = checkItemId;
    }

    public Integer getQcId() {
        return this.qcId;
    }

    public void setQcId(Integer qcId) {
        this.qcId = qcId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getCheckResult() {
        return this.checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

    public Integer getSumCounts() {
        return this.sumCounts;
    }

    public void setSumCounts(Integer sumCounts) {
        this.sumCounts = sumCounts;
    }

    public Integer getUnpassCounts() {
        return this.unpassCounts;
    }

    public void setUnpassCounts(Integer unpassCounts) {
        this.unpassCounts = unpassCounts;
    }

    public Long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Boolean getSkipCheck() {
        return this.skipCheck;
    }

    public void setSkipCheck(Boolean skipCheck) {
        this.skipCheck = skipCheck;
    }

    public Boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getItemDesc() {
        return this.itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    @Generated(hash = 392515696)
    private transient Long checkRecord__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1532774043)
    public CheckRecord getCheckRecord() {
        Long __key = this.recordId;
        if (checkRecord__resolvedKey == null
                || !checkRecord__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckRecordDao targetDao = daoSession.getCheckRecordDao();
            CheckRecord checkRecordNew = targetDao.load(__key);
            synchronized (this) {
                checkRecord = checkRecordNew;
                checkRecord__resolvedKey = __key;
            }
        }
        return checkRecord;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 687416648)
    public void setCheckRecord(CheckRecord checkRecord) {
        synchronized (this) {
            this.checkRecord = checkRecord;
            recordId = checkRecord == null ? null : checkRecord.getCheckRecordId();
            checkRecord__resolvedKey = recordId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 667158723)
    public List<AuthorizeRecord> getAuthorizeRecords() {
        if (authorizeRecords == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AuthorizeRecordDao targetDao = daoSession.getAuthorizeRecordDao();
            List<AuthorizeRecord> authorizeRecordsNew = targetDao
                    ._queryCheckItemData_AuthorizeRecords(checkItemId);
            synchronized (this) {
                if (authorizeRecords == null) {
                    authorizeRecords = authorizeRecordsNew;
                }
            }
        }
        return authorizeRecords;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 113527127)
    public synchronized void resetAuthorizeRecords() {
        authorizeRecords = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1729872043)
    public List<CheckItemDetailData> getCheckItemDetailDatas() {
        if (checkItemDetailDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckItemDetailDataDao targetDao = daoSession
                    .getCheckItemDetailDataDao();
            List<CheckItemDetailData> checkItemDetailDatasNew = targetDao
                    ._queryCheckItemData_CheckItemDetailDatas(checkItemId);
            synchronized (this) {
                if (checkItemDetailDatas == null) {
                    checkItemDetailDatas = checkItemDetailDatasNew;
                }
            }
        }
        return checkItemDetailDatas;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1982059740)
    public synchronized void resetCheckItemDetailDatas() {
        checkItemDetailDatas = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1679887066)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCheckItemDataDao() : null;
    }

}
