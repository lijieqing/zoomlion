package com.kstech.zoomlion.engine.server;

import android.os.Handler;

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
    protected String getRequestMessage() {
        return "获取用户详情";
    }

    @Override
    protected boolean needRequest() {
        return count++ < 1;
    }

    @Override
    protected String getURL() {
        return URLCollections.getGetUserDetailURL();
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        return false;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) throws JSONException {
        user = JsonUtils.fromJson(data.toString(), UserInfo.class);
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

    @Override
    protected void onRequestFinish() {
        if (user != null) {
            sendMsg(UserDetailActivity.UPDATE_USER_DETAIL, user, 0);
        } else {
            sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, "无法获取用户详细信息", 95);
        }
    }
}
