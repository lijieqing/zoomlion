package com.kstech.zoomlion;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.manager.XMLAPI;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.MsgSetDB;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.db.greendao.MsgSetDBDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.AlterData;
import com.kstech.zoomlion.model.xmlbean.DataCollectParam;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.model.xmlbean.Function;
import com.kstech.zoomlion.model.xmlbean.Msg;
import com.kstech.zoomlion.model.xmlbean.MsgSet;
import com.kstech.zoomlion.model.xmlbean.PhoneStore;
import com.kstech.zoomlion.model.xmlbean.QCItem;
import com.kstech.zoomlion.model.xmlbean.QCType;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DaoTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kstech.zoomlion", appContext.getPackageName());
    }
    @Test
    public void ImgTest() throws Exception {
        CheckImageDataDao imgDao = MyApplication.getApplication().getDaoSession().getCheckImageDataDao();
        List<CheckImageData> list = imgDao.queryBuilder().where(CheckImageDataDao.Properties.ItemDetailId.eq(40)).build().list();
        for (CheckImageData checkImageData : list) {
            CheckItemDetailData data = checkImageData.getCheckItemDetailData();
            if (data == null)
                LogUtils.e("ImgTest","null");
            LogUtils.e("ImgTest",checkImageData.getParamName()+" : "+checkImageData.getImgPath());
        }
    }

    @Test
    public void CopyDB(){
        String db = MyApplication.getApplication().getDb().getPath();
        String t = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zoomlion.db";
        FileUtil.copy(db,t);
    }

    @Test
    public void TestXML() throws IOException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        InputStream is = appContext.getAssets().open("temp.xml");
        Device device = (Device) XMLAPI.readXML(is);
        DeviceModelFile modle = DeviceModelFile.readFromFile(device);
        for (CheckItemVO checkItemVO : modle.getCheckItemList()) {
            Function fun = checkItemVO.getFunction();
            LogUtils.e("TestXML",checkItemVO.getName());
            LogUtils.e("TestXML",fun.toString());
            LogUtils.e("TestXML","-------------------------");
        }
    }
}
