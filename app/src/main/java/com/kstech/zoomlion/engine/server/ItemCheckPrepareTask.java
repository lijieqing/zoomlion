package com.kstech.zoomlion.engine.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.engine.base.BaseCheckFunction;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.activity.BaseActivity;
import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by lijie on 2018/1/8.
 * 项目调试准备线程
 */
public class ItemCheckPrepareTask extends AsyncTask<Void, Integer, Void> {
    /**
     * CheckHomeActivity中的handler
     */
    private Handler handler;
    /**
     * 基础功能Activity
     */
    private BaseCheckFunction funActivity;
    /**
     * 请求参数的数据集合
     */
    private RequestParams params;
    /**
     * 是否运行在ItemCheckActivity
     */
    private boolean inCheckMode = false;
    /**
     * 是跳到下一项目
     */
    private boolean next = false;
    /**
     * 当前调试项目的连续通过次数
     */
    public static int serverItemPassTimes = -1;
    /**
     * 当前调试项目的已完成次数
     */
    public static int serverItemDoneTimes = -1;
    /**
     * 当前调试项目的服务器状态
     */
    public static int serverItemStatus = -1;

    public ItemCheckPrepareTask(Handler handler) {
        this.handler = handler;
    }

    /**
     * 设置为调试模式
     *
     * @param funActivity activity工具
     * @param next        去往下一项
     */
    public void setInCheckMode(BaseCheckFunction funActivity, boolean next) {
        this.inCheckMode = true;
        this.next = next;
        this.funActivity = funActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //URLCollections.GET_ITEM_CHECK_INFO
        params = new RequestParams(URLCollections.GET_ITEM_CHECK_INFO);
        params.addHeader("Cookie", Globals.SID);

        handler.sendEmptyMessage(BaseActivity.DIALOG_SHOW);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        params.addBodyParameter("sn", Globals.deviceSN);
        params.addBodyParameter("qcitemDictId", Globals.currentCheckItem.getDictId());

        Message message;

        message = Message.obtain();
        message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
        message.obj = "向服务器验证调试项目状态";
        message.arg1 = 15;
        handler.sendMessage(message);

        String result = "";
        try {
            result = x.http().postSync(params, String.class);
            JSONObject object = new JSONObject(result);
            message = Message.obtain();
            if (URLCollections.isRequestSuccess(object)) {
                serverItemPassTimes = object.getInt("passTimes");
                serverItemDoneTimes = object.getInt("doneTimes");
                serverItemStatus = object.getInt("status");

                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "服务器请求成功，当前调试项目状态：" + CheckItemResultEnum.getDescByCode(serverItemStatus);
                message.arg1 = 30;
                handler.sendMessage(message);
                SystemClock.sleep(1000);

                handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);
                handler.sendEmptyMessage(CheckHomeActivity.ITEM_SERVER_INFO_LOADED);

                if (inCheckMode && funActivity != null) {
                    funActivity.loadCheckItemData();
                }
            } else {
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "服务器请求失败，无法进入项目调试";
                message.arg1 = 100;
                handler.sendMessage(message);
                SystemClock.sleep(1000);

                handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);

                if (inCheckMode && funActivity != null) {
                    if (next) {
                        Globals.forwardCheckItem();
                    } else {
                        Globals.nextCheckItem();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            message = Message.obtain();
            if (URLCollections.isReLogin(result)) {
                message.what = BaseActivity.USER_RELOGIN;
                message.obj = "用户身份异常，重新登录";
                handler.sendMessage(message);
            } else {
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "数据格式错误";
                message.arg1 = 100;
                handler.sendMessage(message);
                SystemClock.sleep(1000);
            }
            handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);

            if (inCheckMode && funActivity != null) {
                if (next) {
                    Globals.forwardCheckItem();
                } else {
                    Globals.nextCheckItem();
                }
            }
        } catch (Throwable throwable) {
            //此异常为无法
            throwable.printStackTrace();
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = throwable.getMessage();
            message.arg1 = 100;
            handler.sendMessage(message);

            SystemClock.sleep(1000);
            handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);

            if (inCheckMode && funActivity != null) {
                if (next) {
                    Globals.forwardCheckItem();
                } else {
                    Globals.nextCheckItem();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);
    }
}
