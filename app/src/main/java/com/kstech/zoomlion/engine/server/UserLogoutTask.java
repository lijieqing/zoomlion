package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

/**
 * Created by lijie on 2018/1/11.
 */
public class UserLogoutTask extends AbstractDataTransferTask {
    private int requstTimes = 0;
    private boolean logoutSuccess = false;

    public UserLogoutTask(Handler handler) {
        super(handler);
    }

    @Override
    protected String getTaskTitle() {
        return "用户注销";
    }

    @Override
    String getRequestMessage() {
        return "请求用户注销";
    }

    @Override
    boolean needRequest() {
        return requstTimes++ < 1;
    }

    @Override
    void beforeRequest() {

    }

    @Override
    String getURL() {
        return URLCollections.USER_LOGOUT;
    }

    @Override
    void initRequestParam(RequestParams params) {
    }

    @Override
    void onRequestSuccess(JSONObject data) throws JSONException {
        if (data.has("success")) {
            logoutSuccess = true;
        }
    }

    @Override
    void onRequestError() {
    }

    @Override
    boolean onReLogin(Message message) {
        return false;
    }

    @Override
    void onRequestFinish(boolean success) {
    }

    @Override
    protected void afterDialogCancel() {
        if (logoutSuccess) {
            handler.sendEmptyMessage(IndexActivity.USER_LOGOUT);
        }
    }
}
