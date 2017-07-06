package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/6.
 */

public class Function {
    private String Camera;
    private String Chart;
    private String Handwriting;
    private String Qualify;

    public Function() {
    }

    public String getCamera() {
        return Camera;
    }

    public void setCamera(String camera) {
        Camera = camera;
    }

    public String getChart() {
        return Chart;
    }

    public void setChart(String chart) {
        Chart = chart;
    }

    public String getHandwriting() {
        return Handwriting;
    }

    public void setHandwriting(String handwriting) {
        Handwriting = handwriting;
    }

    public String getQualify() {
        return Qualify;
    }

    public void setQualify(String qualify) {
        Qualify = qualify;
    }

    @Override
    public String toString() {
        return "Function{" +
                "Camera='" + Camera + '\'' +
                ", Chart='" + Chart + '\'' +
                ", Handwriting='" + Handwriting + '\'' +
                ", Qualify='" + Qualify + '\'' +
                '}';
    }
}
