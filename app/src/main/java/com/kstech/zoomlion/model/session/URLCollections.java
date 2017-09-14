package com.kstech.zoomlion.model.session;

/**
 * Created by lijie on 2017/9/8.
 */

public final class URLCollections {
    private URLCollections() {
    }

    public static String REGISTER_PAD = "http://192.168.32.110:8080/portabledev/register";//调试终端注册
    public static String USER_LOGIN = "http://192.168.32.110:8080/user/login";//用户登陆
    public static String CHECKER_LOGIN = "http://192.168.32.110:8080/category/register_pad";//检验员登陆
    public static String DEVICE_LIST_GET = "http://192.168.32.110:8080/category/usablecategories";//获取机型列表
    public static String TERMINAL_LIST_GET = "http://192.168.32.110:8080/measuredev/usablemeasuredev";//获取测量终端列表
}
