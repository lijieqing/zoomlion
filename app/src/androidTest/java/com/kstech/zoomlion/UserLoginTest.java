package com.kstech.zoomlion;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.model.session.UserInfo;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by lijie on 2018/1/3.
 */
@RunWith(AndroidJUnit4.class)
public class UserLoginTest {
    private static final String TAG = "URLTest";
    private static final String UserLoginURL = "http://192.168.32.187:8080/zoomlion/appLogin";
    private static final String CheckLineRegisterURL = "http://192.168.32.187:8080/zoomlion/checkPoint/register";
    private static final String ParseDeviceNumURL = "http://192.168.32.187:8080/zoomlion/device/register";
    private static final String LoadDeviceInfoURL = "http://192.168.32.187:8080/zoomlion/category/downloadConfigurationFile";
    private boolean loginSuccess = true;

    @Test
    public void UserLoginTest() {
        RequestParams params = new RequestParams(UserLoginURL);
        params.addBodyParameter("username", "admin");
        params.addBodyParameter("password", "123");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e(TAG, "UserLoginURL-onSuccess:\n" + result);

                if (isLogSuccess(result)) {
                    requestCheckLine();
                } else {
                    //提示登录失败，用户名或密码错误
                    loginSuccess = false;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.e(TAG, "UserLoginURL-onError:\n" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtils.e(TAG, "UserLoginURL-onCancelled:\n" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtils.e(TAG, "UserLoginURL-onFinished");
            }
        });


        SystemClock.sleep(2000);
    }

    /**
     * 是否请求成功
     *
     * @param result
     * @return
     */
    private boolean isLogSuccess(String result) {
        if (!result.contains("error")) {
            try {
                JSONObject userJO = new JSONObject(result);
                String user = userJO.getString("user");
                UserInfo userInfo = JsonUtils.fromJson(user, UserInfo.class);
                LogUtils.e(TAG, "isLogSuccess-" + userInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否请求成功
     *
     * @param result
     * @return
     */
    private boolean isSuccess(String result) {
        if (result.contains("success")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检线注册
     */
    private void requestCheckLine() {
        RequestParams params = new RequestParams(CheckLineRegisterURL);
        params.addBodyParameter("measureDevId", "1");
        params.addBodyParameter("portableDevMAC", "FF:FF:FF:FF:FF:FF");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e(TAG, "CheckLineRegisterURL-onSuccess:\n" + result);

                if (isSuccess(result)) {
                    //此处为登录成功
                    loginSuccess = true;
                } else {
                    //提示登录失败，检线故障
                    loginSuccess = false;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.e(TAG, "CheckLineRegisterURL-onError:\n" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtils.e(TAG, "CheckLineRegisterURL-onCancelled:\n" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtils.e(TAG, "CheckLineRegisterURL-onFinished");
            }
        });
    }

    @Test
    public void getDeviceInfo() {
        RequestParams params = new RequestParams(ParseDeviceNumURL);
        params.addQueryStringParameter("deviceSN", "016302A0170008");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e(TAG, "getDeviceInfo-onSuccess:\n" + result);
                if (isSuccess(result)) {
                    requestDeviceInfo("1");
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.e(TAG, "getDeviceInfo-onError:\n" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtils.e(TAG, "getDeviceInfo-onCancelled:\n" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtils.e(TAG, "getDeviceInfo-onFinished:\n");
            }
        });
    }

    private void requestDeviceInfo(String id) {
        RequestParams params = new RequestParams(LoadDeviceInfoURL);
        params.addQueryStringParameter("categoryId", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e(TAG, "getDeviceInfo-onSuccess:\n" + result.length());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.e(TAG, "getDeviceInfo-onError:\n" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtils.e(TAG, "getDeviceInfo-onCancelled:\n" + cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtils.e(TAG, "getDeviceInfo-onFinished:\n");
            }
        });
    }

}
