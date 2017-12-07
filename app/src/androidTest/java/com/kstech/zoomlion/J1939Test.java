package com.kstech.zoomlion;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.common.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        J1939_DataVar_ts data = model.getDataSetVO().getDSItem("预热时间");
        LogUtils.e(TAG,data.sName+"-"+data.dataType+"-"+data.sUnit);
    }

}
