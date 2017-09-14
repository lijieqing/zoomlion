package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/11.
 */

public class DataCollectParam {
    private String Name;
    private Integer Period;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getPeriod() {
        return Period;
    }

    public void setPeriod(Integer period) {
        Period = period;
    }

    @Override
    public String toString() {
        return "DataCollectParam{" +
                "Name='" + Name + '\'' +
                ", Period=" + Period +
                '}';
    }
}
