package com.kstech.zoomlion.base;

/**
 * 用户实体类
 */
public class UserBean {
    private int id; //对应数据库中的唯一索引ID
    private String name;//用户名
    private String password;//密码

    public UserBean(String name, String password) {
        this.name = name;
        this.password = password;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
