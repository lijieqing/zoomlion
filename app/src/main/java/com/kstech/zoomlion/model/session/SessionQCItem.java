package com.kstech.zoomlion.model.session;

/**
 * @author 7yrs
 * @date 2017/12/21.
 */
public class SessionQCItem {
    /**
     * 编号
     */
    private Long id;
    /**
     * 设备记录id,关联到检测设备表
     **/
    private Long deviceId;
    /**
     * 检测项目记录id，关联到tp_qcitem_dictionary.id
     **/
    private Long qcitemId;
    /**
     * 检测结果为合格的连续检测次数
     **/
    private Integer passTiems;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getQcitemId() {
        return qcitemId;
    }

    public void setQcitemId(Long qcitemId) {
        this.qcitemId = qcitemId;
    }

    public Integer getPassTiems() {
        return passTiems;
    }

    public void setPassTiems(Integer passTiems) {
        this.passTiems = passTiems;
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

    @Override
    public String toString() {
        return "SessionQCItem{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", qcitemId=" + qcitemId +
                ", passTiems=" + passTiems +
                ", doneTimes=" + doneTimes +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                '}';
    }
}
