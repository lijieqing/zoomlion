package com.kstech.zoomlion.serverdata;

import java.util.List;

/**
 * @author 7yrs
 * @date 2017/12/28.
 */
public class QCItemResults {
    /**
     * 调试项进度信息
     */
    private QCItemStatus qcitemStatus;
    /**
     * 调试项参数信息
     */
    private List<QCDataRecordForm> qcdataRecordForms;
    /**
     * 调试项附件信息
     */
    private List<AttachedFile> attachedFiles;

    public QCItemResults(QCItemStatus qcitemStatus, List<QCDataRecordForm> QCDataRecordFormList,
                         List<AttachedFile> attachedFiles) {
        this.qcitemStatus = qcitemStatus;
        this.qcdataRecordForms = QCDataRecordFormList;
        this.attachedFiles = attachedFiles;
    }

    public QCItemStatus getQcitemStatus() {
        return qcitemStatus;
    }

    public void setQcitemStatus(QCItemStatus qcitemStatus) {
        this.qcitemStatus = qcitemStatus;
    }

    public List<QCDataRecordForm> getQcdataRecordForms() {
        return qcdataRecordForms;
    }

    public void setQcdataRecordForms(List<QCDataRecordForm> qcdataRecordForms) {
        this.qcdataRecordForms = qcdataRecordForms;
    }

    public List<AttachedFile> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<AttachedFile> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }
}
