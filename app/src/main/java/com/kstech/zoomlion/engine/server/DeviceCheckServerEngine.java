package com.kstech.zoomlion.engine.server;

import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;
import com.kstech.zoomlion.utils.MyHttpUtils;

import java.util.HashMap;

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
