package com.kstech.zoomlion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.model.xml.XMLAPI;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kstech.zoomlion", appContext.getPackageName());
    }
    @Test
    public void testPost() throws Exception {
        MyHttpUtils myHttpUtils = new MyHttpUtils();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("account","admin");
        maps.put("password","1");
        myHttpUtils.xutilsPost("", "http://192.168.32.102:8080/gw-factory/phone/user/login", maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {

            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.d("xUTILs");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }

    @Test
    public void testGet() throws Exception {
        MyHttpUtils myHttpUtils = new MyHttpUtils();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("username","admin");
        maps.put("password","1");
        myHttpUtils.xutilsGet("", "http://192.168.32.102:8080/crmnew/menu/userlist1", maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {

            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }

    @Test
    public void TestXml() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Object o = XMLAPI.readXML(appContext.getAssets().open("temp.xml"));
        XMLAPI.SHOWXMLINFO(o);
    }
}
