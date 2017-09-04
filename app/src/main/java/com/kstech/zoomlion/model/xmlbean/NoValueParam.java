package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/8/3.
 */

public class NoValueParam {
    private String Name;

    public NoValueParam() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return "NoValueParam{" +
                "Name='" + Name + '\'' +
                '}';
    }
}
