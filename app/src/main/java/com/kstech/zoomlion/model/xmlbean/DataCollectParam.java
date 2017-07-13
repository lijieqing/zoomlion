package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/11.
 */

public class DataCollectParam {
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "DataCollectParam{" +
                "Name='" + Name + '\'' +
                '}';
    }
}
