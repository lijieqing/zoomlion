package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/8/3.
 */

public class PICParams {
    private List<PICParam> picParams;

    public PICParams() {
        this.picParams = new ArrayList<>();
    }

    public List<PICParam> getPicParams() {
        return picParams;
    }

    public void setPicParams(List<PICParam> picParams) {
        this.picParams.addAll(picParams);
    }
}
