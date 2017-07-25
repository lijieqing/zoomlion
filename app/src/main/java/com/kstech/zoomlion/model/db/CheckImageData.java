package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;

/**
 * Created by lijie on 2017/7/11.
 */
@Entity
public class CheckImageData {
    @Id(autoincrement = true)
    private Long imgId;// id

    private Long itemDetailId; //关联的检测项目详情ID

    @ToOne(joinProperty = "itemDetailId")
    private CheckItemDetailData checkItemDetailData;

    @Property
    private String paramName;

    @Property
    private String imgPath; //图片路径

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1944620709)
    private transient CheckImageDataDao myDao;

    @Generated(hash = 852817714)
    public CheckImageData(Long imgId, Long itemDetailId, String paramName,
            String imgPath) {
        this.imgId = imgId;
        this.itemDetailId = itemDetailId;
        this.paramName = paramName;
        this.imgPath = imgPath;
    }

    @Generated(hash = 678704347)
    public CheckImageData() {
    }

    public Long getImgId() {
        return this.imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
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

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
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
    @Generated(hash = 2141123792)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCheckImageDataDao() : null;
    }

}
