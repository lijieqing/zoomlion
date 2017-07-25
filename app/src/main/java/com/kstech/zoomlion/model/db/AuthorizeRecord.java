package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.AuthorizeRecordDao;

/**
 * Created by lijie on 2017/7/12.
 */

@Entity
public class AuthorizeRecord {
    @Id(autoincrement = true)
    private Long authorizedId;//授权记录ID

    private Long itemId;//被授权项目ID

    @ToOne(joinProperty = "itemId")
    private CheckItemData checkItemData;

    @Property
    private Long checkerId;//检验员ID

    @Property
    private String authorizerName;//授权人名称

    @Property
    private Long authorizerId;//授权人ID

    @Unique
    @Property
    private Date authorizeTime;//授权时间

    @Property
    private String authorizeDesc;//细节描述信息

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1439810948)
    private transient AuthorizeRecordDao myDao;

    @Generated(hash = 941626259)
    public AuthorizeRecord(Long authorizedId, Long itemId, Long checkerId,
            String authorizerName, Long authorizerId, Date authorizeTime,
            String authorizeDesc) {
        this.authorizedId = authorizedId;
        this.itemId = itemId;
        this.checkerId = checkerId;
        this.authorizerName = authorizerName;
        this.authorizerId = authorizerId;
        this.authorizeTime = authorizeTime;
        this.authorizeDesc = authorizeDesc;
    }

    @Generated(hash = 115269139)
    public AuthorizeRecord() {
    }

    public Long getAuthorizedId() {
        return this.authorizedId;
    }

    public void setAuthorizedId(Long authorizedId) {
        this.authorizedId = authorizedId;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCheckerId() {
        return this.checkerId;
    }

    public void setCheckerId(Long checkerId) {
        this.checkerId = checkerId;
    }

    public String getAuthorizerName() {
        return this.authorizerName;
    }

    public void setAuthorizerName(String authorizerName) {
        this.authorizerName = authorizerName;
    }

    public Long getAuthorizerId() {
        return this.authorizerId;
    }

    public void setAuthorizerId(Long authorizerId) {
        this.authorizerId = authorizerId;
    }

    public Date getAuthorizeTime() {
        return this.authorizeTime;
    }

    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    public String getAuthorizeDesc() {
        return this.authorizeDesc;
    }

    public void setAuthorizeDesc(String authorizeDesc) {
        this.authorizeDesc = authorizeDesc;
    }

    @Generated(hash = 215268062)
    private transient Long checkItemData__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 467370993)
    public CheckItemData getCheckItemData() {
        Long __key = this.itemId;
        if (checkItemData__resolvedKey == null
                || !checkItemData__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CheckItemDataDao targetDao = daoSession.getCheckItemDataDao();
            CheckItemData checkItemDataNew = targetDao.load(__key);
            synchronized (this) {
                checkItemData = checkItemDataNew;
                checkItemData__resolvedKey = __key;
            }
        }
        return checkItemData;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1711190754)
    public void setCheckItemData(CheckItemData checkItemData) {
        synchronized (this) {
            this.checkItemData = checkItemData;
            itemId = checkItemData == null ? null : checkItemData.getCheckItemId();
            checkItemData__resolvedKey = itemId;
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
    @Generated(hash = 1742049263)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAuthorizeRecordDao() : null;
    }
    
}
