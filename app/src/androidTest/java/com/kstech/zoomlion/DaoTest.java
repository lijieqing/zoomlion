package com.kstech.zoomlion;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kstech.zoomlion.engine.check.BaseXmlExpression;
import com.kstech.zoomlion.engine.check.CheckResultVerify;
import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.session.SessionBlob;
import com.kstech.zoomlion.model.session.SessionQCData;
import com.kstech.zoomlion.model.session.SessionQCItem;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.DSItem;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.FileUtils;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DaoTest{
    public static final String TAG = "DaoTest";
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
        RequestParams p = new RequestParams(URLCollections.GET_DEVICE_BY_CAT_ID);
        p.addQueryStringParameter("categoryId","1");
        p.addHeader("Cookie","sid=4ee83ce8-38d8-4df5-a277-711e9337262c");
        x.http().get(p, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DbCookieStore cookie = DbCookieStore.INSTANCE;
                for (HttpCookie httpCookie : cookie.getCookies()) {
                    String name = httpCookie.getName();
                    String value = httpCookie.getValue();
                    LogUtils.e(TAG,"name: "+name);
                    LogUtils.e(TAG,"value: "+value);
                }
                Device device = JsonUtils.fromJson(result,Device.class);
                for (DSItem dsItem : device.getDataSet().getDsItems()) {
                    //LogUtils.e(TAG,dsItem.getDictID());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.e(TAG,ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtils.e(TAG,cex.toString());
            }

            @Override
            public void onFinished() {
                LogUtils.e(TAG,"onFinished");
            }
        });
        SystemClock.sleep(10000);
    }

    @Test
    public void TestXML() throws IOException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        InputStream is = appContext.getAssets().open("zoomlion.xml");
        Device device = (Device) XMLAPI.readXML(is);
        DSItem tempar = null;
        for (DSItem dsItem : device.getDataSet().getDsItems()) {
            if (dsItem.getName().equals("月温度标准")){
                tempar = dsItem;
                break;
            }
        }

        for (int mouth = 1; mouth <= 12; mouth++) {
            int minValuePosition = 2*(mouth-1);
            int maxValuePosition = minValuePosition+1;
            float minVal = Float.parseFloat(tempar.getDatas().get(minValuePosition).getValue());
            float maxVal = Float.parseFloat(tempar.getDatas().get(maxValuePosition).getValue());
            LogUtils.e(TAG,mouth+"月：最低温度-"+minVal+"|最高温度-"+maxVal);
        }
        SystemClock.sleep(1000);

    }


    @Test
    public void TestMath() throws IOException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        InputStream is = appContext.getAssets().open("zoomlion.xml");
        Device device = (Device) XMLAPI.readXML(is);
        DeviceModelFile model = DeviceModelFile.readFromFile(device);
        for (CheckItemVO checkItemVO : model.getCheckItemList()) {
            if ("液压油过滤工作时间".equals(checkItemVO.getName())){
                int qcId = Integer.parseInt(checkItemVO.getId());
                List<CheckItemData> list = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(qcId),
                        CheckItemDataDao.Properties.ItemName.eq(checkItemVO.getName()))
                        .build().list();
                CheckItemData itemData = list.get(0);
                CheckItemDetailData detailData = itemData.getCheckItemDetailDatas().get(1);
                String paramV = detailData.getParamsValues();
                List<CheckItemParamValueVO> pValues = JsonUtils.fromArrayJson(paramV, CheckItemParamValueVO.class);

                List<SessionQCData> qcDataList = new ArrayList<>();
                List<SessionBlob> blobs = new ArrayList<>();
                SessionQCItem item = new SessionQCItem();
                item.setDeviceId(1L);
                item.setId(0L);
                item.setQcitemId(2L);
                item.setDoneTimes(2);
                item.setPassTiems(1);
                item.setStatus(3);
                item.setRemark("test test");
                long blobPos = 0;
                for (int i = 0; i < pValues.size(); i++) {
                    CheckItemParamValueVO value = pValues.get(i);
                    long dictId = 15L;
                    SessionQCData qcData = new SessionQCData();
                    qcData.setDevId(1L);
                    qcData.setDeviceQcitemId(2L);
                    qcData.setDevQcitemNo(i);
                    qcData.setQcitemDataId(dictId);
                    if (value.getPicReq()){
                        SessionBlob sb = new SessionBlob();
                        sb.setType(0);
                        for (CheckImageData checkImageData : detailData.getCheckImageDatas()) {
                            if (checkImageData.getParamName().equals(value.getParamName())){
                                String s = FileUtils.getImageStr(checkImageData.getImgPath());
                                sb.setData(s);
                                blobs.add(sb);
                                qcData.setBlobId(blobPos);
                                blobPos++;
                            }
                        }
                    }
                    try {
                        float v = Float.valueOf(value.getValue());
                        qcData.setData(v);
                    }catch (NumberFormatException e){
                        String v = value.getValue();
                        if ("合格".equals(v)){
                            qcData.setStatus(3);
                        }else {
                            qcData.setStatus(4);
                        }
                    }
                    qcData.setValidMax(1000f);

                    qcData.setValidMin(0f);

                    qcData.setXdataId(123L);

                    qcData.setXdataValue(889L);

                    qcData.setDoneTime(new Date().toString());

                    qcDataList.add(qcData);
                }

                String strItem = JsonUtils.toJson(item);
                String strQCData = JsonUtils.toJson(qcDataList);
                String strBlob = JsonUtils.toJson(blobs);

                LogUtils.e(TAG,"items:"+strItem);
                LogUtils.e(TAG,"strQCData:"+strQCData);
                LogUtils.e(TAG,"strBlob:"+strBlob);

            }
        }

    }

}
