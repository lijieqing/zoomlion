package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/13.
 */
public class QCErrMsg {
    private List<QCErr> qcErrs;
    public QCErrMsg() {
        qcErrs = new ArrayList<>();
    }

    public List<QCErr> getqcErrs() {
        return qcErrs;
    }

    public void setqcErrs(List<QCErr> qcErrs) {
        for (Object qcErr : qcErrs) {
            this.qcErrs.add((QCErr) qcErr);
        }
    }
}
