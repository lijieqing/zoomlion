package com.kstech.zoomlion.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lijie on 2017/7/6.
 */
@Entity(nameInDb = "tb_msg")
public class MsgSetDB {
    @Id(autoincrement = true)
    private Long id;//ID

    @Unique
    private Integer msgID;//msg信息标识ID

    @Unique
    private String refName;//msg信息标识fmi

    @Property
    private String Content;//msg内容

    @Generated(hash = 1566671909)
    public MsgSetDB(Long id, Integer msgID, String refName, String Content) {
        this.id = id;
        this.msgID = msgID;
        this.refName = refName;
        this.Content = Content;
    }

    @Generated(hash = 1334171358)
    public MsgSetDB() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMsgID() {
        return this.msgID;
    }

    public void setMsgID(Integer msgID) {
        this.msgID = msgID;
    }

    public String getRefName() {
        return this.refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    @Override
    public String toString() {
        return "MsgSetDB{" +
                "id=" + id +
                ", msgID=" + msgID +
                ", refName='" + refName + '\'' +
                ", Content='" + Content + '\'' +
                '}';
    }
}
