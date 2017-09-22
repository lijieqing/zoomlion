package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/13.
 */
public class QCParams {
    private List<QCParam> qcParams;
    public QCParams() {
        qcParams = new ArrayList<>();
    }

    public List<QCParam> getQcParams() {
        return qcParams;
    }

    public void setQcParams(List<QCParam> qcParams) {
        for (Object qcParam : qcParams) {
            this.qcParams.add((QCParam) qcParam);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (QCParam qcParam : qcParams) {
            sb.append(qcParam.toString());
            sb.append("\n");
        }

        return "QCParams{" +
                sb.toString()+
                '}';
    }
}
