package com.kstech.zoomlion.model.session;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lijie on 2017/9/8.
 */

public final class URLCollections {
    private URLCollections() {
    }

    private static String ADDRESS = "http://192.168.32.187:8080/zoomlion";

    public static String TERMINAL_LIST_GET = ADDRESS + "/measure/showList";//获取测量终端列表
    public static String USER_LOGIN = ADDRESS + "/appLogin";//用户登陆

    public static String REGISTER_PAD = ADDRESS + "/portabledev/register";//调试终端注册
    public static String USER_CANCEL = ADDRESS + "/user/cancel";//用户注销
    public static String CHECKER_LOGIN = ADDRESS + "/category/register_pad";//检验员登陆
    public static String DEVICE_LIST_GET = ADDRESS + "/category/usablecategories";//获取机型列表
    public static String UPLOAD_ITEM_DETAIL_DATA = "http://localhost:8080/updatedata";//调试项目细节数据上传
    public static String GET_DEVICE_BY_DEVICEID;
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
}
