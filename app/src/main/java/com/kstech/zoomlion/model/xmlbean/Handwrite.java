package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/8/3.
 */

public class Handwrite {
    private String Name;
    private String Unit;

    public Handwrite() {
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public String toString() {
        return "Handwrite{" +
                "Name='" + Name + '\'' +
                ", Unit='" + Unit + '\'' +
                '}';
    }
}
