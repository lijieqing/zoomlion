package com.kstech.zoomlion.engine.server;

import android.os.Handler;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
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
    protected void beforeRequest() {
        detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
        List<CheckItemDetailData> temp = detailDataDao.queryBuilder().where(CheckItemDetailDataDao.Properties.Uploaded.eq(false)).build().list();
        if (temp != null && temp.size() > 0) {
            unUploadDetailDatas.addAll(temp);
        }
    }

    @Override
    protected boolean needRequest() {
        return unUploadDetailDatas.size() > 0;
    }

    @Override
    protected String getRequestMessage() {
        return "发现未同步数据，开始同步";
    }

    @Override
    protected String getURL() {
        return URLCollections.getUpdateCheckItemDetailDataURL();
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        uploadData = unUploadDetailDatas.pop();
        CheckItemData itemData = uploadData.getItemData();
        //更新本地数据
        int serverCount = itemData.getSumCounts();
        int localNum = uploadData.getCheckTimes() + 1;

        if (localNum >= serverCount) {
            itemData.setSumCounts(itemData.getSumCounts() + 1);
            if (uploadData.getCheckResult().equals(CheckItemDetailResultEnum.PASS.getCode())) {
                itemData.setPassCounts(itemData.getPassCounts() + 1);
            }
            itemData.update();
        }

        CompleteQCItemJSON data = packageQCItemData(uploadData, itemData);
        String result = JsonUtils.toJson(data);
        params.setBodyContent(result);
        return true;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) {
        if (data.has("success")) {
            uploadData.setUploaded(true);
            detailDataDao.update(uploadData);
        }
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

    @Override
    protected void onRequestFinish() {
        handler.sendEmptyMessage(CheckHomeActivity.ITEM_RECORD_LOADED);
    }
}
