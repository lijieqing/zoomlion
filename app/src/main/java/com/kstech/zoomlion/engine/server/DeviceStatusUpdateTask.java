package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.CommissioningStatistics;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

/**
 * Created by lijie on 2018/1/12.
 * 更新整机调试记录状态
 */
public class DeviceStatusUpdateTask extends AbstractDataTransferTask {
    private int count = 0;
    CommissioningStatistics status;

    public DeviceStatusUpdateTask(Handler handler) {
        super(handler);
    }

    @Override
    protected String getRequestMessage() {
        return "更新整机状态";
    }

    @Override
    protected boolean needRequest() {
        return count++ < 1;
    }

    @Override
    protected String getURL() {
        return URLCollections.UPDATE_DEVICE_STATUS;
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        params.addQueryStringParameter("sn", Globals.deviceSN);
        return false;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) throws JSONException {
        if (data.has("commissioningStatus")) {
            String deviceStatus = data.getString("commissioningStatus");
            status = JsonUtils.fromJson(deviceStatus, CommissioningStatistics.class);
        }
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

    @Override
    protected void onRequestFinish() {
        if (status != null) {
            message = Message.obtain();
            message.obj = status;
            message.what = IndexActivity.UPDATE_DEVICE_INFO;
            message.arg1 = 90;
            handler.sendMessage(message);
        }
    }
}
