package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lijie on 2017/9/6.
 */
@Entity
public class ParamInitData {

    @Id(autoincrement = true)
    private Long _id;//ID

    @Unique
    @Property
    private String name;//参数名称;

    @Property
    private String qcID;//与测量终端命令ID

    @Property
    private String oldValue;//初始值

    @Property
    private String newValue;//最终值

    @Property
    private String paramUnit;//参数单位

    @Property
    private Boolean isUpload;//是否上传

    @Generated(hash = 1018827724)
    public ParamInitData(Long _id, String name, String qcID, String oldValue,
            String newValue, String paramUnit, Boolean isUpload) {
        this._id = _id;
        this.name = name;
        this.qcID = qcID;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.paramUnit = paramUnit;
        this.isUpload = isUpload;
    }

    @Generated(hash = 1643198185)
    public ParamInitData() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQcID() {
        return this.qcID;
    }

    public void setQcID(String qcID) {
        this.qcID = qcID;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getParamUnit() {
        return this.paramUnit;
    }

    public void setParamUnit(String paramUnit) {
        this.paramUnit = paramUnit;
    }

    public Boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(Boolean isUpload) {
        this.isUpload = isUpload;
    }
}
