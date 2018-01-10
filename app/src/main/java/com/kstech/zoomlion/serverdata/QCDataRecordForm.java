package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/28.
 */
public class QCDataRecordForm {
    /**
     * 设备检测项目的检测序次。第1次检测时为1，第二次检测时为2…
     */
    private Integer checkNo;
    /**
     * 检测项目数据项Id，关联到检测项目数据字典表TP_QCITEM_DATA_DICT.C_ID
     */
    private Long qcdataDictId;
    /**
     * 如果检测项目的数据项要求拍照留存，则为照片原图在大对象数据表中的记录Idn关联到TP_DEVICE_QCBLOB.C_IDn
     */
    private Integer attachedFileIndex;
    /**
     * 检测数据值。显示时根据关联数据项类型确定显示格式
     */
    private Float data;
    /**
     * 检测数据合格值上界，配置值或检测时插值计算值
     */
    private Float validMax;
    /**
     * 检测数据合格值下界,配置值或检测时插值计算值
     */
    private Float validMin;
    /**
     * 检测数据的约束数据Id，关联到数据字典表TP_DATA_DICT.C_ID
     */
    private Long xdataId;
    /**
     * 检测数据的约束数据的检测值
     */
    private Long xdataValue;
    /**
     * 检测数据的检测状态,枚举类型（未检测、正在检测、中止检测、合格、不合格）
     */
    private Integer status;

    public Integer getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(Integer checkNo) {
        this.checkNo = checkNo;
    }

    public Long getQcdataDictId() {
        return qcdataDictId;
    }

    public void setQcdataDictId(Long qcdataDictId) {
        this.qcdataDictId = qcdataDictId;
    }

    public Integer getAttachedFileIndex() {
        return attachedFileIndex;
    }

    public void setAttachedFileIndex(Integer attachedFileIndex) {
        this.attachedFileIndex = attachedFileIndex;
    }

    public Float getData() {
        return data;
    }

    public void setData(Float data) {
        this.data = data;
    }

    public Float getValidMax() {
        return validMax;
    }

    public void setValidMax(Float validMax) {
        this.validMax = validMax;
    }

    public Float getValidMin() {
        return validMin;
    }

    public void setValidMin(Float validMin) {
        this.validMin = validMin;
    }

    public Long getXdataId() {
        return xdataId;
    }

    public void setXdataId(Long xdataId) {
        this.xdataId = xdataId;
    }

    public Long getXdataValue() {
        return xdataValue;
    }

    public void setXdataValue(Long xdataValue) {
        this.xdataValue = xdataValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
