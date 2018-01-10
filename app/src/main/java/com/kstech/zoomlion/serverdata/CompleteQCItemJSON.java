package com.kstech.zoomlion.serverdata;

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
     * 调试项结果
     */
    private QCItemResults qcitemResults;

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

    public QCItemResults getQcitemResults() {
        return qcitemResults;
    }

    public void setQcitemResults(QCItemResults qcitemResults) {
        this.qcitemResults = qcitemResults;
    }
}
