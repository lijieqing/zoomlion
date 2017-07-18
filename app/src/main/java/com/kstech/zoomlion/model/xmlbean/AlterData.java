package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/17.
 */

public class AlterData {
    private String XValue;
    private String YValue;

    public AlterData() {
    }

    public String getXValue() {
        return XValue;
    }

    public void setXValue(String XValue) {
        this.XValue = XValue;
    }

    public String getYValue() {
        return YValue;
    }

    public void setYValue(String YValue) {
        this.YValue = YValue;
    }

    @Override
    public String toString() {
        return "AlterData{" +
                "XValue='" + XValue + '\'' +
                ", YValue='" + YValue + '\'' +
                '}';
    }
}
