package com.kstech.zoomlion.model.db;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;

/**
 * Created by lijie on 2017/7/12.
 */

@Entity
public class CheckRecord {
    @Id(autoincrement = true)
    private Long checkRecordId;// id

    @Property
    private String deviceRecordServerId;//整机调试记录服务器ID

    @Unique
    @Property
    private String deviceIdentity;//出厂编号

    @Property
    private String checkRecordName;//检测记录名称

    @Property
    private Long checkerId;//检验员ID

    @Property
    private String checkerName;//检验员名称

    @Property
    private Integer currentStatus;//当前检测记录状态

    @Unique
    @Property
    private Date createTime;//检测记录创建时间

    @Unique
    @Property
    private Date finishTime;//检测记录完成时间

    @Property
    private Integer sumCounts;//该检测记录内所有检测次数统计

    @Property
    private Integer unpassCounts;//该检测记录内所有检测不合格次数统计

    @Property
    private String checkRecordDesc;//该检测记录的详细描述信息

    @Property
    private Integer checkTimes;//服务器上纪录的 该机型第几次检测

    @Property
    private Boolean uploaded;//该检测记录是否已上传，为true意味着关联的所有数据已上传

    @ToMany(referencedJoinProperty = "recordId")
    private List<CheckItemData> checkItemDatas;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 597129236)
    private transient CheckRecordDao myDao;

    @Generated(hash = 514164489)
    public CheckRecord(Long checkRecordId, String deviceRecordServerId, String deviceIdentity,
            String checkRecordName, Long checkerId, String checkerName, Integer currentStatus,
            Date createTime, Date finishTime, Integer sumCounts, Integer unpassCounts,
            String checkRecordDesc, Integer checkTimes, Boolean uploaded) {
        this.checkRecordId = checkRecordId;
        this.deviceRecordServerId = deviceRecordServerId;
        this.deviceIdentity = deviceIdentity;
        this.checkRecordName = checkRecordName;
        this.checkerId = checkerId;
        this.checkerName = checkerName;
        this.currentStatus = currentStatus;
        this.createTime = createTime;
        this.finishTime = finishTime;
        this.sumCounts = sumCounts;
        this.unpassCounts = unpassCounts;
        this.checkRecordDesc = checkRecordDesc;
        this.checkTimes = checkTimes;
        this.uploaded = uploaded;
    }

    @Generated(hash = 2053101341)
    public CheckRecord() {
    }

    public Long getCheckRecordId() {
        return this.checkRecordId;
    }

    public void setCheckRecordId(Long checkRecordId) {
        this.checkRecordId = checkRecordId;
    }

    public String getDeviceIdentity() {
        return this.deviceIdentity;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    public String getCheckRecordName() {
        return this.checkRecordName;
    }

    public void setCheckRecordName(String checkRecordName) {
        this.checkRecordName = checkRecordName;
    }

    public Long getCheckerId() {
        return this.checkerId;
    }

    public void setCheckerId(Long checkerId) {
        this.checkerId = checkerId;
    }

    public Integer getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
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

    public String getCheckRecordDesc() {
        return this.checkRecordDesc;
    }

    public void setCheckRecordDesc(String checkRecordDesc) {
        this.checkRecordDesc = checkRecordDesc;
    }

    public Integer getCheckTimes() {
        return this.checkTimes;
    }

    public void setCheckTimes(Integer checkTimes) {
        this.checkTimes = checkTimes;
    }

    public Boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1780867619)
    public List<CheckItemData> getCheckItemDatas() {
        if (checkItemDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckItemDataDao targetDao = daoSession.getCheckItemDataDao();
            List<CheckItemData> checkItemDatasNew = targetDao
                    ._queryCheckRecord_CheckItemDatas(checkRecordId);
            synchronized (this) {
                if (checkItemDatas == null) {
                    checkItemDatas = checkItemDatasNew;
                }
            }
        }
        return checkItemDatas;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 854204034)
    public synchronized void resetCheckItemDatas() {
        checkItemDatas = null;
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
    @Generated(hash = 1789132070)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCheckRecordDao() : null;
    }

    public String getDeviceRecordServerId() {
        return this.deviceRecordServerId;
    }

    public void setDeviceRecordServerId(String deviceRecordServerId) {
        this.deviceRecordServerId = deviceRecordServerId;
    }

    public String getCheckerName() {
        return this.checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

}
