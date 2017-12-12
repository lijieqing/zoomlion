package com.kstech.zoomlion.model.xmlbean;

import com.kstech.zoomlion.model.ServerData;

/**
 * Created by lijie on 2017/6/13.
 */
public class QCItem extends ServerData{
    private String Id;
    private String Name;
    private String QCTimeout;
    private String QCTimes;
    private String ReadyTimeout;
    private String Require;
    private String AttachPGN;

    private Spectrum spectrum;
    private Msgs msgs;
    private QCParams qcParams;
    private ENVParams envParams;
    private RealTimeParams realTimeParams;

    public QCItem() {
    }

    public String getId() {
        return Id;
    }

    public void setId(Object id) {
        Id = (String) id;
    }

    public String getName() {
        return Name;
    }

    public void setName(Object name) {
        Name = (String) name;
    }

    public String getQCTimeout() {
        return QCTimeout;
    }

    public void setQCTimeout(Object QCTimeout) {
        this.QCTimeout = (String) QCTimeout;
    }

    public String getQCTimes() {
        return QCTimes;
    }

    public void setQCTimes(Object QCTimes) {
        this.QCTimes = (String) QCTimes;
    }

    public String getReadyTimeout() {
        return ReadyTimeout;
    }

    public void setReadyTimeout(Object readyTimeout) {
        ReadyTimeout = (String) readyTimeout;
    }

    public String getRequire() {
        return Require;
    }

    public void setRequire(Object require) {
        Require = (String) require;
    }

    public Msgs getMsgs() {
        return msgs;
    }

    public void setMsgs(Object msgs) {
        this.msgs = (Msgs) msgs;
    }

    public QCParams getQcParams() {
        return qcParams;
    }

    public void setQcParams(Object qcParams) {
        this.qcParams = (QCParams) qcParams;
    }

    public ENVParams getEnvParams() {
        return envParams;
    }

    public void setEnvParams(Object envParams) {
        this.envParams = (ENVParams) envParams;
    }

    public RealTimeParams getRealTimeParams() {
        return realTimeParams;
    }

    public void setRealTimeParams(Object realTimeParams) {
        this.realTimeParams = (RealTimeParams) realTimeParams;
    }

    public String getAttachPGN() {
        return AttachPGN;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public void setAttachPGN(String attachPGN) {
        AttachPGN = attachPGN;
    }

    @Override
    public String toString() {
        return "QCItem{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", QCTimeout='" + QCTimeout + '\'' +
                ", QCTimes='" + QCTimes + '\'' +
                ", ReadyTimeout='" + ReadyTimeout + '\'' +
                ", Require='" + Require + '\'' +
                ", AttachPGN='" + AttachPGN + '\'' +
                ", msgs=" + msgs +
                ", qcParams=" + qcParams +
                ", envParams=" + envParams +
                ", realTimeParams=" + realTimeParams +
                '}';
    }
}
