package com.kstech.zoomlion.serverdata;

import java.util.List;

/**
 * @author 7yrs
 * @date 2017/12/28.
 */
public class CompleteQCItemJSON {

    /**
     * 泵车整机编号
     */
    private String sn;
    /**
     * 调试项Id
     */
    private Long qcitemDictId;
    /**
     * 检测结果为合格的连续检测次数
     **/
    private Integer passTimes;
    /**
     * 本检测项目已检测的总次数（包括合格和检测和不合格的检测）
     **/
    private Integer doneTimes;
    /**
     * 检测状态(枚举类型：未调试、未完成、合格、不合格）
     **/
    private Integer status;
    /**
     * 检测项检测结果说明
     **/
    private String remark;
    /**
     * 调试项参数信息
     */
    private List<QCDataRecordCreateForm> qcdataRecordCreateForms;
    /**
     * 调试项附件信息
     */
    private List<AttachedFile> attachedFiles;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getQcitemDictId() {
        return qcitemDictId;
    }

    public void setQcitemDictId(Long qcitemDictId) {
        this.qcitemDictId = qcitemDictId;
    }

    public Integer getPassTimes() {
        return passTimes;
    }

    public void setPassTimes(Integer passTimes) {
        this.passTimes = passTimes;
    }

    public Integer getDoneTimes() {
        return doneTimes;
    }

    public void setDoneTimes(Integer doneTimes) {
        this.doneTimes = doneTimes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<QCDataRecordCreateForm> getQcdataRecordCreateForms() {
        return qcdataRecordCreateForms;
    }

    public void setQcdataRecordCreateForms(List<QCDataRecordCreateForm> qcdataRecordCreateForms) {
        this.qcdataRecordCreateForms = qcdataRecordCreateForms;
    }

    public List<AttachedFile> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<AttachedFile> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }
}
