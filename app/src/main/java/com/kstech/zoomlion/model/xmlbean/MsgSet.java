package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/6.
 */

public class MsgSet {
    private List<Msg> msgs;

    public MsgSet() {
        this.msgs = new ArrayList<>();
    }

    public List<Msg> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<Msg> msgList) {
        for (Msg msg : msgList) {
            this.msgs.add(msg);
        }
    }

    @Override
    public String toString() {
        return "MsgSet{" +
                "msgs=" + msgs +
                '}';
    }
}
