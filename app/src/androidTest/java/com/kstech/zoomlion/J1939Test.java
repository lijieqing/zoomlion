package com.kstech.zoomlion;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import J1939.J1939_DataVar_ts;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class J1939Test {
    private static final String TAG = "J1939LOG";
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kstech.zoomlion", appContext.getPackageName());
    }
    @Test
    public void ImgTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        InputStream is = appContext.getAssets().open("zoomlion.xml");
        Device device = (Device) XMLAPI.readXML(is);
        DeviceModelFile model = DeviceModelFile.readFromFile(device);
        for (J1939_DataVar_ts j1939_dataVar_ts : model.getDataSetVO().getJ1939_DataVarCfg()) {
            LogUtils.e(TAG,j1939_dataVar_ts.sName);
        }
        String specParam = model.getCheckItemList().get(1).getSpectrum().getSpecParams().get(0).getParam();
        short d = model.getDataSetVO().getItemIndex("谱图_" + specParam);
        short x = model.getDataSetVO().getItemIndex("谱图_顺序号");
        LogUtils.e(TAG,x+"-->x");
        LogUtils.e(TAG,d+"-->y");
    }

}
