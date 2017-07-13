package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/14.
 */
public class QCSet {
    private List<QCType> qcTypes;

    public QCSet() {
        this.qcTypes = new ArrayList<>();
    }

    public List<QCType> getQcTypes() {
        return qcTypes;
    }

    public void setQcTypes(List<QCType> qcTypes) {
        this.qcTypes.addAll(qcTypes);
    }

    @Override
    public String toString() {
        return "QCSet{" +
                "qcTypes=" + qcTypes.size() +
                '}';
    }
}
