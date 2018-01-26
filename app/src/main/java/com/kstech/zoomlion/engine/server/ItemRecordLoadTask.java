package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.QCItemRecordDetails;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.ViewRecordActivity;

import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by lijie on 2018/1/23.
 */

public class ItemRecordLoadTask extends AbstractDataTransferTask {
    private int count = 0;
    private String dictID;

    public ItemRecordLoadTask(Handler handler) {
        super(handler);
    }

    public void setDictID(String dictID) {
        this.dictID = dictID;
    }

    @Override
    protected String getRequestMessage() {
        return "调试项目数据获取中";
    }

    @Override
    protected boolean needRequest() {
        return count++ < 1;
    }

    @Override
    protected String getURL() {
        return URLCollections.GET_ITEM_RECORD_BY_DICT;
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        params.addQueryStringParameter("sn", Globals.deviceSN);
        params.addQueryStringParameter("qcitemDictId", dictID);
        return false;
    }

    @Override
    protected boolean onResponse(String response) {

        List<QCItemRecordDetails> datas = JsonUtils.fromArrayJson(response, QCItemRecordDetails.class);
        message = Message.obtain();
        message.obj = datas;
        message.what = ViewRecordActivity.ITEM_RECORD_LOADED;
        handler.sendMessage(message);
        return true;
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

}
