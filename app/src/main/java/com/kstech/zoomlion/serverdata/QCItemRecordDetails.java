package com.kstech.zoomlion.serverdata;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author 7yrs
 * @date 2018/1/19.
 */
public class QCItemRecordDetails implements Comparable<QCItemRecordDetails> {
    /**
     * 调试项记录Id
     */
    private Long qcitemRecordId;
    /**
     * 调试项字典Id
     */
    private Long qcitemDictId;
    /**
     * 调试员名称
     */
    private String operatorName;
    /**
     * 测量终端名称
     */
    private String measureDeviceName;
    /**
     * 调试次数
     */
    private Integer checkNo;
    /**
     * 谱图Id
     */
    private Long spectrogramId;
    /**
     * 调试项参数详情列表
     */
    private List<QCDataRecordDetails> qcDataRecordDetailsList;
    /**
     * 单次调试项结果
     */
    private Integer status;
    /**
     * 完成时间
     */
    private String doneDate;

    public Long getQcitemRecordId() {
        return qcitemRecordId;
    }

    public void setQcitemRecordId(Long qcitemRecordId) {
        this.qcitemRecordId = qcitemRecordId;
    }

    public Long getQcitemDictId() {
        return qcitemDictId;
    }

    public void setQcitemDictId(Long qcitemDictId) {
        this.qcitemDictId = qcitemDictId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getMeasureDeviceName() {
        return measureDeviceName;
    }

    public void setMeasureDeviceName(String measureDeviceName) {
        this.measureDeviceName = measureDeviceName;
    }

    public Integer getCheckNO() {
        return checkNo;
    }

    public void setCheckNO(Integer checkNO) {
        this.checkNo = checkNO;
    }

    public List<QCDataRecordDetails> getQcDaraRecordDetailList() {
        return qcDataRecordDetailsList;
    }

    public void setQcdataRecordVOList(List<QCDataRecordDetails> qcdataRecordVOList) {
        this.qcDataRecordDetailsList = qcdataRecordVOList;
    }

    public Long getSpectrogramId() {
        return spectrogramId;
    }

    public void setSpectrogramId(Long spectrogramId) {
        this.spectrogramId = spectrogramId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    @Override
    public String toString() {
        return "QCItemRecordDetails{" +
                "qcitemRecordId=" + qcitemRecordId +
                ", qcitemDictId=" + qcitemDictId +
                ", operatorName='" + operatorName + '\'' +
                ", measureDeviceName='" + measureDeviceName + '\'' +
                ", checkNo=" + checkNo +
                ", spectrogramId=" + spectrogramId +
                ", qcdataRecordVOList=" + qcDataRecordDetailsList +
                ", status=" + status +
                ", doneDate='" + doneDate + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull QCItemRecordDetails o) {
        return o.getCheckNO() - checkNo;
    }
}
