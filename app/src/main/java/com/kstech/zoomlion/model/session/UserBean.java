package com.kstech.zoomlion.model.session;

/**
 * 用户实体类
 */
public class UserBean {
    private int id; //对应数据库中的唯一索引ID
    private String name;//用户名
    private String last_time;//上次登陆时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }
}
