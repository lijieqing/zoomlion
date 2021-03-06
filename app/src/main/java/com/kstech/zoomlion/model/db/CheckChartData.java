package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckChartDataDao;

import java.util.Date;

/**
 * Created by lijie on 2017/7/11.
 */

@Entity
public class CheckChartData {
    @Id(autoincrement = true)
    private Long chartId;

    private String dictId;//该谱图参数对应的服务器字典ID

    @Unique
    private Date createTime;//谱图创建时间

    private Long itemDetailId;//调试项目细节表ID

    @ToOne(joinProperty = "itemDetailId")
    private CheckItemDetailData checkItemDetailData;

    @Property
    private String paramName;//参数名称

    @Property
    private String unit;//谱图的Y轴单位

    @Property
    private String chartData;//谱图数据JSON串

    @Property
    private String imgPath;//缩略图路径

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1959493665)
    private transient CheckChartDataDao myDao;

    @Generated(hash = 1280726563)
    public CheckChartData(Long chartId, String dictId, Date createTime, Long itemDetailId,
            String paramName, String unit, String chartData, String imgPath) {
        this.chartId = chartId;
        this.dictId = dictId;
        this.createTime = createTime;
        this.itemDetailId = itemDetailId;
        this.paramName = paramName;
        this.unit = unit;
        this.chartData = chartData;
        this.imgPath = imgPath;
    }

    @Generated(hash = 1002407687)
    public CheckChartData() {
    }

    public Long getChartId() {
        return this.chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public Long getItemDetailId() {
        return this.itemDetailId;
    }

    public void setItemDetailId(Long itemDetailId) {
        this.itemDetailId = itemDetailId;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getChartData() {
        return this.chartData;
    }

    public void setChartData(String chartData) {
        this.chartData = chartData;
    }

    @Generated(hash = 1841648991)
    private transient Long checkItemDetailData__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1085242501)
    public CheckItemDetailData getCheckItemDetailData() {
        Long __key = this.itemDetailId;
        if (checkItemDetailData__resolvedKey == null
                || !checkItemDetailData__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckItemDetailDataDao targetDao = daoSession
                    .getCheckItemDetailDataDao();
            CheckItemDetailData checkItemDetailDataNew = targetDao.load(__key);
            synchronized (this) {
                checkItemDetailData = checkItemDetailDataNew;
                checkItemDetailData__resolvedKey = __key;
            }
        }
        return checkItemDetailData;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1234636611)
    public void setCheckItemDetailData(CheckItemDetailData checkItemDetailData) {
        synchronized (this) {
            this.checkItemDetailData = checkItemDetailData;
            itemDetailId = checkItemDetailData == null ? null
                    : checkItemDetailData.getCheckItemDetailId();
            checkItemDetailData__resolvedKey = itemDetailId;
        }
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
    @Generated(hash = 378641548)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCheckChartDataDao() : null;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDictId() {
        return this.dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

}
