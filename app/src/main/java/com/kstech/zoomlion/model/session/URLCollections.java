package com.kstech.zoomlion.model.session;

import android.text.TextUtils;

import com.kstech.zoomlion.utils.Globals;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;

/**
 * Created by lijie on 2017/9/8.
 */

public final class URLCollections {
    private URLCollections() {
    }

    private static String ADDRESS = "http://192.168.32.176:8080/zoomlion";
    /**
     * 获取测量终端列表URL
     */
    public static String TERMINAL_LIST_GET = ADDRESS + "/measure/showList";
    /**
     * 用户登录URL
     */
    public static String USER_LOGIN = ADDRESS + "/appLogin";
    /**
     * 用户登出URL
     */
    public static String USER_LOGOUT = ADDRESS + "/appLogout";
    /**
     * 根据整机编码获取机型信息URL
     */
    public static String GET_DEVICE_BY_SN = ADDRESS + "/commissioning/getDevice";
    /**
     * 向服务器请求进入调试
     */
    public static String NOTIFY_SERVER_GOTO_CHECK = ADDRESS + "/commissioning/startCommissioning";
    /**
     * 上传调试项目细节数据记录
     */
    public static String UPDATE_CHECK_ITEM_DETAIL_DATA = ADDRESS + "/commissioning/completeQCItem";
    /**
     * 在进入项目调试页面之前获取服务器相关信息
     */
    public static String GET_ITEM_CHECK_INFO = ADDRESS + "/commissioning/startQCItem";
    /**
     * 整机调试完成后，通知服务器并上传数据
     */
    public static String NOTIFY_SERVER_CHECK_COMPLETE = ADDRESS + "/commissioning/completeCommissioning";
    /**
     * 获取整机泵车调试状态数据
     */
    public static String UPDATE_DEVICE_STATUS = ADDRESS + "/commissioning/getStatistics";
    /**
     * 获取用户详细信息
     */
    public static String GET_USER_DETAIL = ADDRESS + "/user/details";
    /**
     * 修改用户密码
     */
    public static String CHANGE_PASSWORD = ADDRESS + "/user/changePassword";

    public static String REGISTER_PAD = ADDRESS + "/portabledev/register";//调试终端注册
    public static String USER_CANCEL = ADDRESS + "/user/cancel";//用户注销
    public static String CHECKER_LOGIN = ADDRESS + "/category/register_pad";//检验员登陆
    public static String DEVICE_LIST_GET = ADDRESS + "/category/usablecategories";//获取机型列表
    public static String GET_DEVICE_BY_CAT_ID = ADDRESS + "/category/downloadConfigurationFile";//根据分类ID获取机型信息

    /**
     * 判断请求是否成功
     *
     * @param object 请求完成后收到的数据
     * @return 是否成功
     */
    public static boolean isRequestSuccess(JSONObject object) {
        boolean success = true;
        try {
            String errDesc = object.getString("error");
            if (!TextUtils.isEmpty(errDesc)) {
                success = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            success = true;
        }
        return success;
    }

    /**
     * 获取通讯时的SID，并组装成如下格式
     * 字符串: sid=1asdfas24dasdf45648
     */
    public static void initSID() {
        StringBuilder sb = new StringBuilder();
        DbCookieStore cookie = DbCookieStore.INSTANCE;
        for (HttpCookie httpCookie : cookie.getCookies()) {
            String name = httpCookie.getName();
            String value = httpCookie.getValue();
            if ("sid".equals(name)) {
                sb.append(name).append("=").append(value);
            }
        }
        Globals.SID = sb.toString();
    }

    /**
     * 判断是否重新登录
     *
     * @param result 服务器获取的数据
     * @return 是否需要
     */
    public static boolean isReLogin(String result) {
        boolean relogin = false;
        if (result.contains("<form action=\"login\"")) {
            relogin = true;
        }
        return relogin;
    }
}