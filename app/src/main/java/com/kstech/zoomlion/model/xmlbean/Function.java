package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/6.
 */

public class Function {
    private Boolean Dialogue;
    private Boolean AutoQualify;
    private Boolean Alter;

    private List<DataCollectParam> dataCollectParams;
    private List<Handwrite> handwrites;
    private List<PICParam> picParams;
    private List<NoValueParam> noValueParams;
    private List<DialogParam> dialogParams;

    private AlterDatas alterDatas;

    public Function() {
        dataCollectParams = new ArrayList<>();
        handwrites = new ArrayList<>();
        picParams = new ArrayList<>();
        noValueParams = new ArrayList<>();
        dialogParams = new ArrayList<>();
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

    public List<NoValueParam> getNoValueParams() {
        return noValueParams;
    }

    public void setNoValueParams(List<NoValueParam> noValueParams) {
        this.noValueParams.addAll(noValueParams);
    }

    public List<DialogParam> getDialogParams() {
        return dialogParams;
    }

    public void setDialogParams(List<DialogParam> dialogParams) {
        this.dialogParams.addAll(dialogParams);
    }

    @Override
    public String toString() {
        return "Function{" +
                "Dialogue=" + Dialogue +
                ", AutoQualify=" + AutoQualify +
                ", Alter=" + Alter +
                ", dataCollectParams=" + dataCollectParams +
                ", handwrites=" + handwrites +
                ", picParams=" + picParams +
                ", noValueParams=" + noValueParams +
                ", dialogParams=" + dialogParams +
                ", alterDatas=" + alterDatas +
                '}';
    }
}
