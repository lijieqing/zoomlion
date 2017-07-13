package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/13.
 */

public class QCType {
    private String Name;
    private List<QCItem> qcItems;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public QCType() {
        qcItems = new ArrayList<>();
    }

    public List<QCItem> getQcItems() {
        return qcItems;
    }

    public void setQcItems(List<QCItem> qcItems) {
        this.qcItems.addAll(qcItems);
    }

    @Override
    public String toString() {
        return "QCType{" +
                "Name='" + Name + '\'' +
                ", qcItems=" + qcItems +
                '}';
    }
}
