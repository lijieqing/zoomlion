package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2018/1/17.
 */
public class QCDataRecordDetails {
    /**
     * 调试项记录Id
     */
    private Long qcitemRecordId;
    /**
     * 调试次数
     */
    private Integer checkNo;
    /**
     * 调试项参数名称
     */
    private String name;
    /**
     * 参数数据
     */
    private Float data;
    /**
     * 单位
     */
    private String unit;
    /**
     * 图片
     */
    private Long pictureId;
    /**
     * 检测数据合格值上界，配置值或检测时插值计算值
     */
    private Float validMax;
    /**
     * 检测数据合格值下界,配置值或检测时插值计算值
     */
    private Float validMin;
    /**
     * 调试项参数状态
     */
    private Integer status;

    public Long getQcitemRecordId() {
        return qcitemRecordId;
    }

    public void setQcitemRecordId(Long qcitemRecordId) {
        this.qcitemRecordId = qcitemRecordId;
    }

    public Integer getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(Integer checkNo) {
        this.checkNo = checkNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getData() {
        return data;
    }

    public void setData(Float data) {
        this.data = data;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QCDataRecordDetails{" +
                "qcitemRecordId=" + qcitemRecordId +
                ", checkNo=" + checkNo +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", unit='" + unit + '\'' +
                ", pictureId=" + pictureId +
                ", validMax=" + validMax +
                ", validMin=" + validMin +
                ", status=" + status +
                '}';
    }
}
