package com.kstech.zoomlion.model.session;

import java.util.List;

/**
 * Created by lijie on 2017/12/5.
 */

public class DeviceCatSession extends BaseSession<List<DeviceCatSession>> {
    private int id;
    private int parent_id;
    private int level;
    private int sub_nums;
    private String name;
    private String remark;
    private String full_code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFull_code() {
        return full_code;
    }

    public void setFull_code(String full_code) {
        this.full_code = full_code;
    }

    public int getSub_nums() {
        return sub_nums;
    }

    public void setSub_nums(int sub_nums) {
        this.sub_nums = sub_nums;
    }

    @Override
    public String toString() {
        return "DeviceCatSession{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", level=" + level +
                ", sub_nums=" + sub_nums +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", full_code='" + full_code + '\'' +
                '}';
    }
}
