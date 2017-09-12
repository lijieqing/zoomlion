package com.kstech.zoomlion.model.session;

/**
 * Created by lijie on 2017/9/12.
 */

public class UserSession extends BaseSession<UserSession> {
    private int user_id; //对应数据库中的唯一索引ID
    private String name;//用户名
    private String last_login_time;//上次登陆时间
    private String Cversion;//版本号

    @Override
    public String toString() {
        return "UserSession{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", last_login_time='" + last_login_time + '\'' +
                ", version='" + Cversion + '\'' +
                super.toString() +
                '}';
    }
}
