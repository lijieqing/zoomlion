package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.CompleteCommissioningJSON;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.BaseActivity;
import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by lijie on 2018/1/11.
 */

public class CheckRecordConfirmTask extends AbstractDataTransferTask {
    private boolean need = false;
    private CheckRecord record;
    private String authorizeCode;
    private boolean pass;
    private boolean uploaded = false;

    public CheckRecordConfirmTask(Handler handler) {
        super(handler);
    }

    public void init(String authorizeCode, boolean pass) {
        this.authorizeCode = authorizeCode;
        this.pass = pass;
    }

    @Override
    protected String getRequestMessage() {
        return "整机调试记录上传";
    }

    @Override
    protected boolean needRequest() {
        return need;
    }

    @Override
    protected void beforeRequest() {
        CheckRecordDao recordDao = MyApplication.getApplication().getDaoSession().getCheckRecordDao();
        List<CheckRecord> records = recordDao.queryBuilder()
                .where(CheckRecordDao.Properties.DeviceIdentity.eq(Globals.deviceSN))
                .build().list();
        if (records != null && records.size() == 1) {
            record = records.get(0);
            int status = record.getCurrentStatus();
            if (status == CheckRecordResultEnum.FINISH.getCode()) {
                if (pass) {
                    record.setCurrentStatus(CheckRecordResultEnum.PASS.getCode());
                } else {
                    record.setCurrentStatus(CheckRecordResultEnum.UNPASS.getCode());
                }
                recordDao.update(record);
                need = true;
            } else {
                message = Message.obtain();
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "当前存在未完成调试项目，无法上传整机结论";
                message.arg1 = 100;
                handler.sendMessage(message);
            }
        }
    }

    @Override
    protected String getURL() {
        return URLCollections.NOTIFY_SERVER_CHECK_COMPLETE;
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        CompleteCommissioningJSON recordResult = new CompleteCommissioningJSON();
        recordResult.setSn(Globals.deviceSN);
        recordResult.setAuthorizationCode(authorizeCode);
        recordResult.setStatus(record.getCurrentStatus());
        recordResult.setRemark(record.getCheckRecordDesc());

        String result = JsonUtils.toJson(recordResult);
        params.setBodyContent(result);
        return true;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) throws JSONException {
        if (data.has("success")) {
            uploaded = true;
        }
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

    @Override
    protected void onRequestFinish() {
        if (record != null) {
            record.setUploaded(uploaded);
            MyApplication.getApplication().getDaoSession().getCheckRecordDao().update(record);
        }
    }

    @Override
    protected void afterDialogCancel() {
        super.afterDialogCancel();
        if (uploaded) {
            handler.sendEmptyMessage(CheckHomeActivity.CHECK_RECORD_UPDATE_SUCCESS);
        }
    }
}
