package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/6.
 */

public class Function {
    private Boolean Camera;
    private Boolean Chart;
    private Boolean Handwriting;
    private Boolean Qualify;

    private List<DataCollectParam> dataCollectParams;
    public Function() {
        dataCollectParams = new ArrayList<>();
    }

    public Boolean getCamera() {
        return Camera;
    }

    public void setCamera(Boolean camera) {
        Camera = camera;
    }

    public Boolean getChart() {
        return Chart;
    }

    public void setChart(Boolean chart) {
        Chart = chart;
    }

    public Boolean getHandwriting() {
        return Handwriting;
    }

    public void setHandwriting(Boolean handwriting) {
        Handwriting = handwriting;
    }

    public Boolean getQualify() {
        return Qualify;
    }

    public void setQualify(Boolean qualify) {
        Qualify = qualify;
    }

    public List<DataCollectParam> getDataCollectParams() {
        return dataCollectParams;
    }

    public void setDataCollectParams(List<DataCollectParam> dataCollectParams) {
        this.dataCollectParams.addAll(dataCollectParams);
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
