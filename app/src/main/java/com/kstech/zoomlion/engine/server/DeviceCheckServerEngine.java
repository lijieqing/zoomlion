package com.kstech.zoomlion.engine.server;

import com.kstech.zoomlion.model.db.CheckChartData;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lijie on 2017/12/5.
 */

public class DeviceCheckServerEngine implements IDeviceCheckEngine {
    TreeViewAdapter adapter = null;

    @Override
    public TreeViewAdapter getDeviceModelList(int userID, int terminalID) {
        MyHttpUtils myHttpUtils = new MyHttpUtils();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("userID", userID + "");
        maps.put("terminalID", terminalID + "");
        myHttpUtils.xutilsGet(null, URLCollections.DEVICE_LIST_GET, maps, new MyHttpUtils.MyHttpCallback() {
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
        return adapter;
    }

    @Override
    public void getDeviceModelFile(int userID, String deviceIdentity, String url) {

    }

    @Override
    public void getDeviceModelFile(int userID, int deviceID, String url) {

    }

    @Override
    public void uploadCheckItemDetailData(CheckItemDetailData itemDetailData, String url) {
        //重置数据
        itemDetailData.resetCheckChartDatas();
        itemDetailData.resetCheckImageDatas();
        MyHttpUtils myHttpUtils = new MyHttpUtils();

        //RequestParams params = new RequestParams(URLCollections.UPLOAD_ITEM_DETAIL_DATA);
        RequestParams params = new RequestParams(url);
        //基本ID信息
        params.addBodyParameter("device_id", itemDetailData.getItemData().getCheckRecord().getDeviceRecordServerId());
        params.addBodyParameter("device_qcitem_id", itemDetailData.getItemData().getDictId());
        params.addBodyParameter("checkline_id", "");
        //基本调试项目信息
        params.addBodyParameter("check_times", itemDetailData.getCheckTimes() + "");
        params.addBodyParameter("param_values", itemDetailData.getParamsValues());
        params.addBodyParameter("start_time", itemDetailData.getStartTime().toString());
        params.addBodyParameter("end_time", itemDetailData.getEndTime().toString());
        //谱图数据处理
        List<CheckChartData> chartDataList = itemDetailData.getCheckChartDatas();
        if (chartDataList != null) {
            List<String> chartDictList = new ArrayList<>();
            for (CheckChartData chartData : chartDataList) {
                String chartDictId = chartData.getDictId();
                chartDictList.add(chartDictId);
                params.addBodyParameter("chart_data_" + chartDictId, chartData.getChartData());
            }
            params.addBodyParameter("chart_dict_list", JsonUtils.toJson(chartDictList));
        }
        //照片数据处理
        List<CheckImageData> imgDataList = itemDetailData.getCheckImageDatas();
        if (imgDataList != null) {
            List<String> picDictList = new ArrayList<>();
            for (CheckImageData checkImageData : imgDataList) {
                String picDictId = checkImageData.getDictId();
                picDictList.add(picDictId);

                File file = new File(checkImageData.getImgPath());
                params.addBodyParameter("pic_data_" + picDictId, file);
            }
            params.addBodyParameter("pic_dict_list", JsonUtils.toJson(picDictList));
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void uploadCheckItemData(CheckItemData itemData, String url) {

    }

    @Override
    public void uploadCheckRecord(CheckRecord record, String url) {

    }

    @Override
    public void getCheckItemDetailList(String deviceIdentity, int itemId, String url) {

    }

    @Override
    public void getCheckItemDetailRecord(int itemDetailId, String url) {

    }
}
