package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/28.
 * <p>
 * 整机调试完成后
 */
public class CompleteCommissioningJSON {
    /**
     * 用户的授权码
     */
    private String authorizationCode;
    /**
     * 泵车整机编码
     */
    private String sn;
    /**
     * 设备状态：未检 、正在检测、合格、不合格、强制合格
     */
    private Integer status;
    /**
     * 设备调试结果说明
     */
    private String remark;

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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
}
