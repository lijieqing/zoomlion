package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/29.
 */
public class CommissioningResults {
    /*
    设备状态：未检 、正在检测、合格、不合格、强制合格
    */
    private Integer status;
    /*
    设备调试结果说明
     */
    private String remark;

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
}
