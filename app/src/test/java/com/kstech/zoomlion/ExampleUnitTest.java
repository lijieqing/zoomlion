package com.kstech.zoomlion;

import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.session.DeviceCatSession;
import com.kstech.zoomlion.model.xmlbean.DSItem;
import com.kstech.zoomlion.model.xmlbean.Data;
import com.kstech.zoomlion.model.xmlbean.DataSet;
import com.kstech.zoomlion.model.xmlbean.J1939;
import com.kstech.zoomlion.model.xmlbean.PG;
import com.kstech.zoomlion.model.xmlbean.QCItem;
import com.kstech.zoomlion.model.xmlbean.QCParam;
import com.kstech.zoomlion.model.xmlbean.QCParams;
import com.kstech.zoomlion.model.xmlbean.QCType;
import com.kstech.zoomlion.model.xmlbean.SP;
import com.kstech.zoomlion.utils.JsonUtils;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void FileCopy() {
        FileChannel outChannel = null;
        FileChannel inChannel = null;
        try {
            outChannel = new FileOutputStream("/Users/lijie/Desktop/out.jpeg").getChannel();
            inChannel = new FileInputStream("/Users/lijie/Desktop/WechatIMG178.jpeg").getChannel();
            outChannel.transferFrom(inChannel, 0, inChannel.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outChannel != null)
                    outChannel.close();
                if (inChannel != null)
                    inChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void JsonCreate() {
//        BaseSession<UserBean> b = new BaseSession<>();
//        String s = "{\"data\":{\"last_login_time\":\"yyyy-MM-dd hh:mm:ss\",\"user_id\":\"1\",\"name\":\"ZS\",\"Cversion\":\"11\"}}";
//        UserSession session = JsonUtils.fromJson(s, UserSession.class);
//        UserSession d = session.getData();
//        DeviceCatSession deviceCatSession = new DeviceCatSession();
//        deviceCatSession.setId(1);
//        deviceCatSession.setLevel(3);
//        deviceCatSession.setName("top");
//        deviceCatSession.setParent_id(0);
//        deviceCatSession.setFull_code("GSFUTYOPP");
//        deviceCatSession.setRemark("remark_content");
        String s = "{\"data\":[{\"id\":1,\"parent_id\":0,\"level\":1,\"name\":\"root1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}," +
                "{\"id\":2,\"parent_id\":0,\"level\":1,\"name\":\"root2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":3,\"parent_id\":1,\"level\":2,\"name\":\"second1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":4,\"parent_id\":2,\"level\":2,\"name\":\"second2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":5,\"parent_id\":3,\"level\":3,\"name\":\"third1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":6,\"parent_id\":4,\"level\":3,\"name\":\"third2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":7,\"parent_id\":6,\"level\":4,\"name\":\"fourth1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}]}";
        DeviceCatSession baseSession = JsonUtils.fromJson(s, DeviceCatSession.class);
        List<DeviceCatSession> list = baseSession.getData();
        for (DeviceCatSession deviceCatSession : list) {
            System.out.println(deviceCatSession.toString());
        }
    }

    @Test
    public void strTest() throws IOException, IllegalAccessException, InvocationTargetException {
        String v = "右前支腿水平伸出时间\n" +
                "右前支腿水平回收时间\n" +
                "右前支腿垂直伸出时间\n" +
                "右前支腿垂直回收时间\n" +
                "左后支腿水平伸出时间\n" +
                "左后支腿水平回收时间\n" +
                "左后支腿垂直伸出时间\n" +
                "左后支腿垂直回收时间\n" +
                "右后支腿水平伸出时间\n" +
                "右后支腿水平回收时间\n" +
                "右后支腿垂直伸出时间\n" +
                "右后支腿垂直回收时间";
        String[] vs = v.split("\n");
        J1939 type = (J1939) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/zoo.xml"));
        int id = 6;
        int pgn = 0xff5d;
        for (String s : vs) {
            PG p = new PG();
            p.setPGN("0x" + Integer.toHexString(pgn).toUpperCase());
            p.setDir("Rx");
            p.setLen("8");
            p.setPrio("3");
            p.setRate("100");
            p.setReq("0");
            p.setReqCyc("0");
            p.setSA("0x01");
            p.setType("2");
            // p.getSps().add(pg.getSps().get(0));
            SP sp = new SP();
            sp.setBits("0");
            sp.setBytes("2");
            sp.setOff("0");
            //sp.setRef(s.replace("时间","")+"不检测时间");
            sp.setRef(s);
            sp.setRes("0.001");
            sp.setSBit("1");
            sp.setSByte("1");
            sp.setSPN("0");
            sp.setType("1");
            p.getSps().add(sp);
            // sp = new SP();
            // sp.setBits("0");
            // sp.setBytes("2");
            // sp.setOff("0");
            // sp.setRef(s.replace("时间","")+"终止斜率阀值");
            // sp.setRes("1");
            // sp.setSBit("1");
            // sp.setSByte("5");
            // sp.setSPN("0");
            // sp.setType("1");
            // p.getSps().add(sp);

            pgn++;
            type.getPgs().add(p);
        }

        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/zoo.xml");
    }

    @Test
    public void testDS() throws IOException, IllegalAccessException, InvocationTargetException {
        String v = "右前支腿水平伸出时间\n" +
                "右前支腿水平回收时间\n" +
                "右前支腿垂直伸出时间\n" +
                "右前支腿垂直回收时间\n" +
                "左后支腿水平伸出时间\n" +
                "左后支腿水平回收时间\n" +
                "左后支腿垂直伸出时间\n" +
                "左后支腿垂直回收时间\n" +
                "右后支腿水平伸出时间\n" +
                "右后支腿水平回收时间\n" +
                "右后支腿垂直伸出时间\n" +
                "右后支腿垂直回收时间";
        String[] vs = v.split("\n");
        DataSet type = (DataSet) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/zoo.xml"));
        type.getDsItems().clear();
        for (String s : vs) {
            DSItem ds = new DSItem();
            ds.setDataType("FLOAT");
            ds.setDecLen("0");
            //ds.setName(s.replace("时间","")+"不检测时间");
            ds.setName(s);
            ds.setUnit("s");
            ds.setValue("0");
            type.getDsItems().add(ds);
            // ds = new DSItem();
            // ds.setDataType("FLOAT");
            // ds.setDecLen("0");
            // ds.setName(s.replace("时间","")+"终止斜率阀值");
            // ds.setUnit("UN");
            // ds.setValue("0");
            // type.getDsItems().add(ds);
        }

        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/zoo.xml");
    }

    @Test
    public void testQC() throws IOException, IllegalAccessException, InvocationTargetException {
        String v = "右前支腿水平伸出时间\n" +
                "右前支腿水平回收时间\n" +
                "右前支腿垂直伸出时间\n" +
                "右前支腿垂直回收时间\n" +
                "左后支腿水平伸出时间\n" +
                "左后支腿水平回收时间\n" +
                "左后支腿垂直伸出时间\n" +
                "左后支腿垂直回收时间\n" +
                "右后支腿水平伸出时间\n" +
                "右后支腿水平回收时间\n" +
                "右后支腿垂直伸出时间\n" +
                "右后支腿垂直回收时间";
        String[] vs = v.split("\n");
        QCParams type = (QCParams) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/zoo.xml"));
        type.getQcParams().clear();
        for (String s : vs) {
            QCParam param = new QCParam();
            param.setParam(s);
            param.setValueReq(true);
            param.setPicReq(true);
            param.setValMode("Auto");
            param.setQCMode("Auto");
            param.setValidMax("");
            param.setValidAvg("");
            param.setValidMin("");

            type.getQcParams().add(param);
        }
        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/zoo.xml");
    }

    @Test
    public void TestAttachPgn() throws IOException, IllegalAccessException, InvocationTargetException {
        PG pg = (PG) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/zoo.xml"));

        List<SP> sps = pg.getSps();

        System.out.println("总 sp 个数：" + sps.size());
        int total = 0;

        for (int i = 0; i < sps.size(); i++) {
            String bytes = sps.get(i).getBytes();
            sps.get(i).setSByte(String.valueOf(total + 1));
            total += Integer.parseInt(bytes);

        }

        System.out.println("总长度：" + total);

        XMLAPI.writeXML2File(pg, "/Users/lijie/Desktop/spec.xml");
    }

    @Test
    public void testFloat() throws IOException, IllegalAccessException, InvocationTargetException {
        QCType type = (QCType) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/spec.xml"));

        QCItem item = type.getQcItems().get(0);
        for (int i = 0; i < type.getQcItems().size(); i++) {
            if (i != 0) {
                type.getQcItems().get(i).setSpectrum(item.getSpectrum());
            }
        }

        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/spec.xml");
    }

    @Test
    public void testDataSet() throws IOException, IllegalAccessException, InvocationTargetException {
        DataSet type = (DataSet) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/zoo.xml"));
        List<DSItem> dsItem = type.getDsItems();
        List<Data> pressList = dsItem.get(0).getDatas();
        List<Data> minList = dsItem.get(1).getDatas();
        List<Data> maxList = dsItem.get(2).getDatas();

        DSItem ds = new DSItem();

        List<Data> datas = ds.getDatas();
        Data d = new Data();
        d.setValue(pressList.size() + "");
        datas.add(d);
        for (int i = 0; i < pressList.size(); i++) {
            d = new Data();
            String press = pressList.get(i).getValue();
            d.setValue(press);
            datas.add(d);

            d = new Data();
            String min = minList.get(i).getValue();
            d.setValue(min);
            datas.add(d);

            d = new Data();
            String max = maxList.get(i).getValue();
            d.setValue(max);
            datas.add(d);
        }

        ds.setDatas(datas);
        dsItem.add(ds);

        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/spec.xml");
    }
    @Test
    public void testInit() throws IOException, IllegalAccessException, InvocationTargetException {
        DataSet type = (DataSet) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/init.xml"));
        List<DSItem> dsItems = type.getDsItems();
        for (DSItem dsItem : dsItems) {
            dsItem.setValue("0");
        }
        XMLAPI.writeXML2File(type, "/Users/lijie/Desktop/spec.xml");
    }
}