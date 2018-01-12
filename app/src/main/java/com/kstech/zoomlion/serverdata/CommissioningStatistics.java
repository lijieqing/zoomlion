package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2018/1/12.
 */
public class CommissioningStatistics {
    /**
     * 调试次数
     */
    private Integer checkNo;
    /**
     * 已完成调试项数目
     */
    private Integer completeNumber;
    /**
     * 进行中的调试项数目
     */
    private Integer doingNumber;
    /**
     * 调试项目总数
     */
    private Integer amount;
    /**
     * 上次进行中的调试项字典Id
     */
    private String lastQcitemName;
    /**
     * 泵车调试状态
     */
    private Integer status;

    public Integer getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(Integer checkNo) {
        this.checkNo = checkNo;
    }

    public Integer getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(Integer completeNumber) {
        this.completeNumber = completeNumber;
    }

    public Integer getDoingNumber() {
        return doingNumber;
    }

    public void setDoingNumber(Integer doingNumber) {
        this.doingNumber = doingNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getLastQcitemName() {
        return lastQcitemName;
    }

    public void setLastQcitemName(String lastQcitemName) {
        this.lastQcitemName = lastQcitemName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
