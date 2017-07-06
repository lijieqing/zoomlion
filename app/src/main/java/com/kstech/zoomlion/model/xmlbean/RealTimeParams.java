package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/13.
 */
public class RealTimeParams {
    private List<RealTimeParam> realTimeParams;
    public RealTimeParams() {
        realTimeParams = new ArrayList<>();
    }

    public List<RealTimeParam> getRealTimeParams() {
        return realTimeParams;
    }

    public void setRealTimeParams(List<RealTimeParam> realTimeParams) {
        for (Object realTimeParam : realTimeParams) {
            this.realTimeParams.add((RealTimeParam) realTimeParam);
        }
    }
}
