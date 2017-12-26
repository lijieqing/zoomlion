package com.kstech.zoomlion.model.session;

/**
 * @author 7yrs
 * @date 2017/12/25.
 */
public class SessionQCData {
    /**
     * 设备记录Id，关联到检测设备表
     */
    private Long devId;
    /**
     * 设备检测项目记录Id，关联到设备检测项目表 TP_DEVICE_QCITEM.C_ID
     */
    private Long deviceQcitemId;
    /**
     * 设备检测项目的检测序次。第1次检测时为1，第二次检测时为2…
     */
    private Integer devQcitemNo;
    /**
     * 检测项目数据项Id，关联到检测项目数据字典表TP_QCITEM_DATA_DICT.C_ID
     */
    private Long qcitemDataId;
    /**
     * 如果检测项目的数据项要求拍照留存，则为照片原图在大对象数据表中的记录Idn关联到TP_DEVICE_QCBLOB.C_IDn
     * pic : 0-499 |||| spec:500-999
     */
    private Long blobId;
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
     * 检测数据的检测状态,枚举类型（未检测0、正在检测1、中止检测2、合格3、不合格4）
     */
    private Integer status;
    /**
     * 检线Id, 关联到TP_USER_STATUS.C_ID
     */
    private Long checkLineId;
    /**
     * 检测时间
     */
    private String doneTime;

    public Long getDevId() {
        return devId;
    }

    public void setDevId(Long devId) {
        this.devId = devId;
    }

    public Long getDeviceQcitemId() {
        return deviceQcitemId;
    }

    public void setDeviceQcitemId(Long deviceQcitemId) {
        this.deviceQcitemId = deviceQcitemId;
    }

    public Integer getDevQcitemNo() {
        return devQcitemNo;
    }

    public void setDevQcitemNo(Integer devQcitemNo) {
        this.devQcitemNo = devQcitemNo;
    }

    public Long getQcitemDataId() {
        return qcitemDataId;
    }

    public void setQcitemDataId(Long qcitemDataId) {
        this.qcitemDataId = qcitemDataId;
    }

    public Long getBlobId() {
        return blobId;
    }

    public void setBlobId(Long blobId) {
        this.blobId = blobId;
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

    public Long getCheckLineId() {
        return checkLineId;
    }

    public void setCheckLineId(Long checkLineId) {
        this.checkLineId = checkLineId;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    @Override
    public String toString() {
        return "SessionQCData{" +
                "devId=" + devId +
                ", deviceQcitemId=" + deviceQcitemId +
                ", devQcitemNo=" + devQcitemNo +
                ", qcitemDataId=" + qcitemDataId +
                ", blobId=" + blobId +
                ", data=" + data +
                ", validMax=" + validMax +
                ", validMin=" + validMin +
                ", xdataId=" + xdataId +
                ", xdataValue=" + xdataValue +
                ", status=" + status +
                ", checkLineId=" + checkLineId +
                ", doneTime='" + doneTime + '\'' +
                '}';
    }
}

