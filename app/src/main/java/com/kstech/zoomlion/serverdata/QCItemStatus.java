package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/21.
 */
public class QCItemStatus {
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

    public Long getQcitemDictId() {
        return qcitemDictId;
    }

    public void setQcitemDictId(Long qcitemDictId) {
        this.qcitemDictId = qcitemDictId;
    }
}
