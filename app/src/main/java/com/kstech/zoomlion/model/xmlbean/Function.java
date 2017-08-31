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
    private Boolean AutoQualify;
    private Boolean Alter;

    private List<DataCollectParam> dataCollectParams;
    private List<Handwrite> handwrites;
    private List<PICParam> picParams;

    private AlterDatas alterDatas;

    public Function() {
        dataCollectParams = new ArrayList<>();
        handwrites = new ArrayList<>();
        picParams = new ArrayList<>();
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

    public Boolean getAutoQualify() {
        return AutoQualify;
    }

    public void setAutoQualify(Boolean autoQualify) {
        AutoQualify = autoQualify;
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

    public List<Handwrite> getHandwrites() {
        return handwrites;
    }

    public void setHandwrites(List<Handwrite> handwrites) {
        this.handwrites.addAll(handwrites);
    }

    public List<PICParam> getPicParams() {
        return picParams;
    }

    public void setPicParams(List<PICParam> picParams) {
        this.picParams.addAll(picParams);
    }

    public Boolean getAlter() {
        return Alter;
    }

    public void setAlter(Boolean alter) {
        Alter = alter;
    }

    @Override
    public String toString() {
        return "Function{" +
                "Dialogue=" + Dialogue +
                ", Camera=" + Camera +
                ", Chart=" + Chart +
                ", Handwriting=" + Handwriting +
                ", AutoQualify=" + AutoQualify +
                ", dataCollectParams=" + dataCollectParams +
                ", alterDatas=" + alterDatas +
                '}';
    }
}
