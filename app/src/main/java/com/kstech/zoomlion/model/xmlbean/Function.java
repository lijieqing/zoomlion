package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/6.
 */

public class Function {
    private Boolean Dialogue;
    private Boolean Camera;
    private Boolean Chart;
    private Boolean Handwriting;
    private Boolean Qualify;

    private List<DataCollectParam> dataCollectParams;

    private AlterDatas alterDatas;

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

    public Boolean getDialogue() {
        return Dialogue;
    }

    public void setDialogue(Boolean dialogue) {
        Dialogue = dialogue;
    }

    public AlterDatas getAlterDatas() {
        return alterDatas;
    }

    public void setAlterDatas(AlterDatas alterDatas) {
        this.alterDatas = alterDatas;
    }

    @Override
    public String toString() {
        return "Function{" +
                "Dialogue=" + Dialogue +
                ", Camera=" + Camera +
                ", Chart=" + Chart +
                ", Handwriting=" + Handwriting +
                ", Qualify=" + Qualify +
                ", dataCollectParams=" + dataCollectParams +
                ", alterDatas=" + alterDatas +
                '}';
    }
}
