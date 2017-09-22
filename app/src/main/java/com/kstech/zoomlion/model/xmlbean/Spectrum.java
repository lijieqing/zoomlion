package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

public class Spectrum {

    private String StartMode;
    private Integer Interval;
    private String EndMode;
    private List<SpecParam> specParams;

    public Spectrum() {
        this.specParams = new ArrayList<>();
    }

    public String getStartMode() {
        return StartMode;
    }

    public void setStartMode(String startMode) {
        StartMode = startMode;
    }

    public Integer getInterval() {
        return Interval;
    }

    public void setInterval(Integer interval) {
        Interval = interval;
    }

    public String getEndMode() {
        return EndMode;
    }

    public void setEndMode(String endMode) {
        EndMode = endMode;
    }

    public List<SpecParam> getSpecParams() {
        return specParams;
    }

    public void setSpecParams(List<SpecParam> specParams) {
        this.specParams.addAll(specParams);
    }

    @Override
    public String toString() {
        return "Spectrum{" +
                "StartMode='" + StartMode + '\'' +
                ", Interval=" + Interval +
                ", EndMode='" + EndMode + '\'' +
                ", specParams=" + specParams +
                '}';
    }
}
