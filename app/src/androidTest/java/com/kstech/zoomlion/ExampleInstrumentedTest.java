package com.kstech.zoomlion;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.db.MsgSetDB;
import com.kstech.zoomlion.model.db.greendao.MsgSetDBDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.model.xmlbean.Msg;
import com.kstech.zoomlion.model.xmlbean.MsgSet;
import com.kstech.zoomlion.model.xmlbean.QCItem;
import com.kstech.zoomlion.model.xmlbean.QCType;
import com.kstech.zoomlion.utils.GreenDaoUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
        Device o = (Device) XMLAPI.readXML(appContext.getAssets().open("temp.xml"));
        for (QCType qcType : o.getQcSet().getQcTypes()) {
            LogUtils.d("KSTECH",qcType.toString());
            for (QCItem qcItem : qcType.getQcItems()) {
                LogUtils.d("KSTECH",qcItem.getName());
            }
        }
    }

    @Test
    public void TestRES() throws Exception {
        MsgSetDBDao msgDao = MyApplication.getApplication().getDaoSession().getMsgSetDBDao();
        Context appContext = InstrumentationRegistry.getTargetContext();
        MsgSet m = (MsgSet) XMLAPI.readXML(appContext.getAssets().open("resource.xml"));
        List<Msg> msgs = m.getMsgs();
        LogUtils.d("KSTECH",msgs.size()+"");
        for (Msg msg : msgs) {
            MsgSetDB msd = new MsgSetDB(null,Integer.parseInt(msg.getId()),msg.getRefName(),msg.getContent());
            msgDao.insert(msd);
        }
        SystemClock.sleep(1000);
        for (MsgSetDB msgSetDB : msgDao.queryBuilder().list()) {
            LogUtils.d("KSTECH",msgSetDB.toString());
        }
    }

    @Test
    public void TestDeviceModel() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Device o = (Device) XMLAPI.readXML(appContext.getAssets().open("temp.xml"));
        DeviceModelFile result = DeviceModelFile.readFromFile(o);
        for (String s : result.checkItemMap.keySet()) {
            LogUtils.d("KSTECH",s);
            List<CheckItemVO> ls = result.checkItemMap.get(s);
            for (CheckItemVO l : ls) {
                LogUtils.d("KSTECH",l.getName());
                LogUtils.d("KSTECH","是否必须 "+l.isRequire());
            }
        }
//        for (QCType qcType : o.getQcSet().getQcTypes()) {
//            for (QCItem qcItem : qcType.getQcItems()) {
//                for (PICParam picParam : qcItem.getPicParams().getPicParams()) {
//                    LogUtils.d("KSTECH",qcItem.getName()+"--"+picParam.getParam());
//                }
//            }
//        }
    }
    @Test
    public void InitItemDB() throws IOException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Device o = (Device) XMLAPI.readXML(appContext.getAssets().open("new.xml"));
        DeviceModelFile mo = DeviceModelFile.readFromFile(o);
        GreenDaoUtils.initCheckRecord(mo);
    }

    @Test
    public void testXMLText(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(appContext.getAssets().open("temp.xml"));
            LogUtils.d("KSTECH",document.getXMLEncoding()+"");
            String str= document.asXML();
            Device o = (Device)XMLAPI.readXML(str);
            DeviceModelFile result = DeviceModelFile.readFromFile(o);
            for (String s : result.checkItemMap.keySet()) {
                LogUtils.d("KSTECH",s);
                List<CheckItemVO> ls = result.checkItemMap.get(s);
                for (CheckItemVO l : ls) {
                    LogUtils.d("KSTECH",l.getName());
                    LogUtils.d("KSTECH","是否必须 "+l.isRequire());
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
