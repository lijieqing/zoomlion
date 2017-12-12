package com.kstech.zoomlion.model.xmlbean;

import com.kstech.zoomlion.model.ServerData;

public class SpecParam extends ServerData{
    private String Param;

    public String getParam() {
        return Param;
    }

    public void setParam(String param) {
        Param = param;
    }

    @Override
    public String toString() {
        return "SpecParam{" +
                "Param='" + Param + '\'' +
                '}';
    }
}
