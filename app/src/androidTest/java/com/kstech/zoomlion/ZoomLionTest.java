package com.kstech.zoomlion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.model.session.BaseSession;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.session.UserSession;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        maps.put("username","admin");
        maps.put("password","123456");
        maps.put("mac_addr",id);
        new MyHttpUtils().xutilsPost(null, URLCollections.REGISTER_PAD, maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                LogUtils.e("ServerTest","onSuccess  " +result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });
    }

    @Test
    public void LoginTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String macid = DeviceUtil.getMacid(appContext);
        HashMap<String, String> maps = new HashMap<>();
        maps.put("username","ZhangSan");
        maps.put("password","666666");
        maps.put("portable_dev_id","3");
        maps.put("measure_dev_id","2");
        new MyHttpUtils().xutilsPost(null, URLCollections.USER_LOGIN, maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest){
                LogUtils.e("ServerTest","onSuccess  " +result);
                boolean error;
                UserSession session = JsonUtils.fromJson((String) result, UserSession.class);
                error = session.isError();
                LogUtils.e("ServerTest","onSuccess  " +error);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });
    }
    @Test
    public void DeviceListTest() throws Exception {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id","1");
        maps.put("measure_dev_id","1");
        maps.put("version","8");
        new MyHttpUtils().xutilsGet(null, URLCollections.DEVICE_LIST_GET, null, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest){
                LogUtils.e("ServerTest","onSuccess  " +result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });
    }

    @Test
    public void MeasureDevListTest() throws Exception {
        new MyHttpUtils().xutilsGet(null, URLCollections.TERMINAL_LIST_GET, null, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest){
                LogUtils.e("ServerTest","onSuccess  " +result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });
    }

    @Test
    public void Cancel(){
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id","2");
        new MyHttpUtils().xutilsPost(null, URLCollections.USER_CANCEL, maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest){
                LogUtils.e("ServerTest","onSuccess  " +result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });

        maps.clear();
        maps.put("user_id","1");
        new MyHttpUtils().xutilsPost(null, URLCollections.USER_CANCEL, maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest){
                LogUtils.e("ServerTest","onSuccess  " +result);
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.e("ServerTest","onError  "+ errorMsg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtils.e("ServerTest","onLoading");
            }
        });
    }
}
