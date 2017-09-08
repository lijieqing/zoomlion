package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckChartDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;

/**
 * Created by lijie on 2017/7/11.
 */
@Entity
public class CheckItemDetailData {
    @Id(autoincrement = true)
    private Long checkItemDetailId;// id

    private Long itemId; //检测项目ID

    @ToOne(joinProperty = "itemId")
    private CheckItemData itemData;

    @Property
    private Long checkerId;//检验员ID

    @Property
    private String checkerName;//调试员名称

    @Property
    private Long measureDeviceId;//测量终端ID

    @Property
    private String measureDeviceName;//测量终端名称

    @Property
    private String paramsValues;//检测参数集合

    @Property
    private Integer checkResult;//测量结果

    @Unique
    @Property
    private Date startTime;//开始时间

    @Unique
    @Property
    private Date endTime;//结束时间

    @Property
    private String DESC;//信息描述

    @Property
    private Boolean uploaded;//是否已上传

    @ToMany(referencedJoinProperty = "itemDetailId")
    private List<CheckImageData> checkImageDatas; //图片数据关联 一对多

    @ToMany(referencedJoinProperty = "itemDetailId")
    private List<CheckChartData> checkChartDatas;//谱图数据 一对多

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 823363946)
    private transient CheckItemDetailDataDao myDao;

    @Generated(hash = 1235684807)
    public CheckItemDetailData(Long checkItemDetailId, Long itemId, Long checkerId, String checkerName,
            Long measureDeviceId, String measureDeviceName, String paramsValues, Integer checkResult,
            Date startTime, Date endTime, String DESC, Boolean uploaded) {
        this.checkItemDetailId = checkItemDetailId;
        this.itemId = itemId;
        this.checkerId = checkerId;
        this.checkerName = checkerName;
        this.measureDeviceId = measureDeviceId;
        this.measureDeviceName = measureDeviceName;
        this.paramsValues = paramsValues;
        this.checkResult = checkResult;
        this.startTime = startTime;
        this.endTime = endTime;
        this.DESC = DESC;
        this.uploaded = uploaded;
    }

    @Generated(hash = 525031784)
    public CheckItemDetailData() {
    }

    public Long getCheckItemDetailId() {
        return this.checkItemDetailId;
    }

    public void setCheckItemDetailId(Long checkItemDetailId) {
        this.checkItemDetailId = checkItemDetailId;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getMeasureDeviceId() {
        return this.measureDeviceId;
    }

    public void setMeasureDeviceId(Long measureDeviceId) {
        this.measureDeviceId = measureDeviceId;
    }

    public String getMeasureDeviceName() {
        return this.measureDeviceName;
    }

    public void setMeasureDeviceName(String measureDeviceName) {
        this.measureDeviceName = measureDeviceName;
    }

    public String getParamsValues() {
        return this.paramsValues;
    }

    public void setParamsValues(String paramsValues) {
        this.paramsValues = paramsValues;
    }

    public Integer getCheckResult() {
        return this.checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getUploaded() {
        return this.uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    @Generated(hash = 1739195159)
    private transient Long itemData__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1306878728)
    public CheckItemData getItemData() {
        Long __key = this.itemId;
        if (itemData__resolvedKey == null || !itemData__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckItemDataDao targetDao = daoSession.getCheckItemDataDao();
            CheckItemData itemDataNew = targetDao.load(__key);
            synchronized (this) {
                itemData = itemDataNew;
                itemData__resolvedKey = __key;
            }
        }
        return itemData;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 521598534)
    public void setItemData(CheckItemData itemData) {
        synchronized (this) {
            this.itemData = itemData;
            itemId = itemData == null ? null : itemData.getCheckItemId();
            itemData__resolvedKey = itemId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 592604724)
    public List<CheckImageData> getCheckImageDatas() {
        if (checkImageDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckImageDataDao targetDao = daoSession.getCheckImageDataDao();
            List<CheckImageData> checkImageDatasNew = targetDao
                    ._queryCheckItemDetailData_CheckImageDatas(checkItemDetailId);
            synchronized (this) {
                if (checkImageDatas == null) {
                    checkImageDatas = checkImageDatasNew;
                }
            }
        }
        return checkImageDatas;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2069627132)
    public synchronized void resetCheckImageDatas() {
        checkImageDatas = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 929255279)
    public List<CheckChartData> getCheckChartDatas() {
        if (checkChartDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckChartDataDao targetDao = daoSession.getCheckChartDataDao();
            List<CheckChartData> checkChartDatasNew = targetDao
                    ._queryCheckItemDetailData_CheckChartDatas(checkItemDetailId);
            synchronized (this) {
                if (checkChartDatas == null) {
                    checkChartDatas = checkChartDatasNew;
                }
            }
        }
        return checkChartDatas;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1613199884)
    public synchronized void resetCheckChartDatas() {
        checkChartDatas = null;
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
    @Generated(hash = 426140751)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCheckItemDetailDataDao() : null;
    }

    public Long getCheckerId() {
        return this.checkerId;
    }

    public void setCheckerId(Long checkerId) {
        this.checkerId = checkerId;
    }

    public String getCheckerName() {
        return this.checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getDESC() {
        return this.DESC;
    }

    public void setDESC(String DESC) {
        this.DESC = DESC;
    }

}
