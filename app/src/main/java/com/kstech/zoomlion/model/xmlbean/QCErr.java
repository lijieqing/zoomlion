package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/6/13.
 */
public class QCErr {
    private String Code;
    private String Msg;
    public QCErr() {
    }

    public String getCode() {
        return Code;
    }

    public void setCode(Object code) {
        Code = (String) code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(Object msg) {
        Msg = (String) msg;
    }
}
