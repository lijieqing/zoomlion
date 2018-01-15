package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.UserInfo;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.BaseActivity;
import com.kstech.zoomlion.view.activity.UserDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

/**
 * Created by lijie on 2018/1/15.
 */

public class UserDetailLoadTask extends AbstractDataTransferTask {
    private int count = 0;
    private UserInfo user;

    public UserDetailLoadTask(Handler handler) {
        super(handler);
    }

    @Override
    String getRequestMessage() {
        return "获取用户详情";
    }

    @Override
    boolean needRequest() {
        return count++ < 1;
    }

    @Override
    void beforeRequest() {

    }

    @Override
    String getURL() {
        return URLCollections.GET_USER_DETAIL;
    }

    @Override
    boolean initRequestParam(RequestParams params) {
        return false;
    }

    @Override
    void onRequestSuccess(JSONObject data) throws JSONException {
        user = JsonUtils.fromJson(data.toString(), UserInfo.class);
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
        if (user != null) {
            message = Message.obtain();
            message.what = UserDetailActivity.UPDATE_USER_DETAIL;
            message.obj = user;
            handler.sendMessage(message);
        } else {
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "无法获取用户详细信息";
            message.arg1 = 95;
            handler.sendMessage(message);
        }
    }
}
