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
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.DSItem;
import com.kstech.zoomlion.model.xmlbean.Device;
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
    public void TestMath(){
        String s= "getRealTime('ok')";

    }

}
