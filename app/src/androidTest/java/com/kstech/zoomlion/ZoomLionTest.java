package com.kstech.zoomlion;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ZoomLionTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kstech.zoomlion", appContext.getPackageName());
    }

    @Test
    public void RegisterTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String id = DeviceUtil.getMacid(appContext);
        HashMap<String, String> maps = new HashMap<>();
        maps.put("username", "admin");
        maps.put("password", "123456");
        maps.put("mac_addr", id);
        new MyHttpUtils().xutilsPost(null, URLCollections.REGISTER_PAD, maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                LogUtils.e("ServerTest", "onSuccess  " + result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest", "onError  " + errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest", "onLoading");
            }
        });
    }

    @Test
    public void LoginTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String macid = DeviceUtil.getMacid(appContext);
        HashMap<String, String> maps = new HashMap<>();
        maps.put("username", "admin");
        maps.put("password", "123");
//        maps.put("portable_dev_id","3");
//        maps.put("measure_dev_id","2");
        new MyHttpUtils().xutilsPost(null, URLCollections.getUserLoginURL(), maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                DbCookieStore cookie = DbCookieStore.INSTANCE;
                LogUtils.e("ServerTest", "cookie: " + cookie);
                for (HttpCookie httpCookie : cookie.getCookies()) {
                    String name = httpCookie.getName();
                    String value = httpCookie.getValue();
                    LogUtils.e("ServerTest", "name: " + name);
                    LogUtils.e("ServerTest", "value: " + value);
                }
                LogUtils.e("ServerTest", "onSuccess  " + result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest", "onError  " + errorMsg);
                DbCookieStore cookie = DbCookieStore.INSTANCE;
                for (HttpCookie httpCookie : cookie.getCookies()) {
                    String name = httpCookie.getName();
                    String value = httpCookie.getValue();
                    LogUtils.e("ServerTest", "name: " + name);
                    LogUtils.e("ServerTest", "value: " + value);
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest", "onLoading");
            }
        });
        SystemClock.sleep(5000);
    }

    @Test
    public void MeasureDevListTest() throws Exception {
        new MyHttpUtils().xutilsGet(null, URLCollections.getTerminalListURL(), null, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                LogUtils.e("ServerTest", "onSuccess  " + result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest", "onError  " + errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest", "onLoading");
            }
        });
    }

}
