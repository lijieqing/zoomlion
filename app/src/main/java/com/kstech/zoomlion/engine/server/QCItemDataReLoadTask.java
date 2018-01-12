package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import org.json.JSONObject;
import org.xutils.http.RequestParams;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lijie on 2018/1/10.
 * 未同步调试项目细节数据再同步
 */
public class QCItemDataReLoadTask extends AbstractDataTransferTask {
    private LinkedList<CheckItemDetailData> unUploadDetailDatas;
    CheckItemDetailData uploadData;
    CheckItemDetailDataDao detailDataDao;

    public QCItemDataReLoadTask(Handler handler) {
        super(handler);
        unUploadDetailDatas = new LinkedList<>();
    }

    @Override
    protected String getTaskTitle() {
        return "本地数据同步校验";
    }

    @Override
    void beforeRequest() {
        detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
        List<CheckItemDetailData> temp = detailDataDao.queryBuilder().where(CheckItemDetailDataDao.Properties.Uploaded.eq(false)).build().list();
        if (temp != null && temp.size() > 0) {
            unUploadDetailDatas.addAll(temp);
        }
    }

    @Override
    boolean needRequest() {
        return unUploadDetailDatas.size() > 0;
    }

    @Override
    String getRequestMessage() {
        return "发现未同步数据，开始同步";
    }

    @Override
    String getURL() {
        return URLCollections.UPDATE_CHECK_ITEM_DETAIL_DATA;
    }

    @Override
    void initRequestParam(RequestParams params) {
        uploadData = unUploadDetailDatas.pop();
        CompleteQCItemJSON data = packageQCItemData(uploadData,uploadData.getItemData());
        String result = JsonUtils.toJson(data);
        params.setBodyContent(result);
    }

    @Override
    void onRequestSuccess(JSONObject data) {
        if (data.has("success")) {
            uploadData.setUploaded(true);
            detailDataDao.update(uploadData);
        }
    }

    @Override
    void onRequestError() {

    }

    @Override
    boolean onReLogin(Message message) {
        return true;
    }

    @Override
    void onRequestFinish(boolean success) {
        handler.sendEmptyMessage(CheckHomeActivity.ITEM_RECORD_LOADED);
    }
}
