package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/28.
 *
 * 整机调试完成后
 *
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
     * 泵车调试结果
     */
    private CommissioningResults commissioningResults;

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

    public CommissioningResults getCommissioningResults() {
        return commissioningResults;
    }

    public void setCommissioningResults(CommissioningResults commissioningResults) {
        this.commissioningResults = commissioningResults;
    }
}
