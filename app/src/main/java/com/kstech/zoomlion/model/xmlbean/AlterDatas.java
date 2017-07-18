package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/17.
 */

public class AlterDatas {
    private String Name;
    private List<AlterData> alterDatas;

    public AlterDatas() {
        this.alterDatas = new ArrayList<>();
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<AlterData> getAlterDatas() {
        return alterDatas;
    }

    public void setAlterDatas(List<AlterData> alterDatas) {
        this.alterDatas.addAll(alterDatas);
    }

    @Override
    public String toString() {
        return "AlterDatas{" +
                "Name='" + Name + '\'' +
                ", alterDatas=" + alterDatas +
                '}';
    }
}
