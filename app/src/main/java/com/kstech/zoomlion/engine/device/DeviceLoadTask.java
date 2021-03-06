package com.kstech.zoomlion.engine.device;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.serverdata.CommissioningStatistics;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;

public class DeviceLoadTask extends AsyncTask<Void, String, Void> {

    /**
     * 消息处理对象
     */
    private Handler handler;
    /**
     * 整机编码
     */
    private String InExc;
    /**
     * 机型文件对象
     */
    private Device device;
    /**
     * 是否正在等待，当通讯线程启动完成后，置为false
     */
    public boolean isWaitting = true;
    /**
     * 服务器机型加载完成
     */
    private boolean deviceLoadSuccess = false;


    public DeviceLoadTask(String InExc, Handler handler) {
        this.handler = handler;
        this.InExc = InExc;
    }

    /**
     * 主要实现类 执行时机：在onPreExecute 方法执行后马上执行，该方法运行在后台线程中 作用：主要负责执行那些很耗时的后台处理工作。可以调用
     * publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。
     */

    @Override
    protected Void doInBackground(Void... params) {

        Message message = Message.obtain();
        message.what = IndexActivity.J1939_SERVICE_RESET;
        message.obj = "通讯线程重置";
        message.arg1 = 2;
        handler.sendMessage(message);

        SystemClock.sleep(1000);

        message = Message.obtain();
        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
        message.obj = "开始解析机型";
        message.arg1 = 10;
        handler.sendMessage(message);

        try {
            //整机编码不为空，向服务器请求机型数据
            if (InExc != null) {
                message = Message.obtain();
                message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "向服务器请求机型数据";
                message.arg1 = 20;
                handler.sendMessage(message);

                SystemClock.sleep(300);

                RequestParams p = new RequestParams(URLCollections.getGetDeviceBySnURL());
                p.addHeader("Cookie", Globals.SID);
                p.addQueryStringParameter("sn", InExc);

                //此处为同步请求数据操作
                String result = "";
                try {
                    result = x.http().getSync(p, String.class);

                    LogUtils.e("DeviceLoadTask", result);
                    JSONObject object = new JSONObject(result);
                    if (object.has("error")) {
                        message = Message.obtain();
                        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                        message.obj = "请求错误：" + object.getString("error") + "，正在还原配置";
                        message.arg1 = 28;
                        handler.sendMessage(message);
                        SystemClock.sleep(1000);
                    } else if (object.has("processDetails")) {
                        String deviceStatus = object.getString("commissioningStatus");
                        CommissioningStatistics status = JsonUtils.fromJson(deviceStatus, CommissioningStatistics.class);

                        JSONObject processDetail = object.getJSONObject("processDetails");
                        String deviceInfo = processDetail.getString("device");
                        Globals.PROCESSID = processDetail.getString("id");

                        device = JsonUtils.fromJson(deviceInfo, Device.class);

                        message = Message.obtain();
                        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                        message.obj = "服务器机型数据加载完成";
                        message.arg1 = 28;
                        handler.sendMessage(message);
                        SystemClock.sleep(1000);

                        message = Message.obtain();
                        message.what = IndexActivity.UPDATE_DEVICE_INFO;
                        message.obj = status;
                        message.arg1 = 30;
                        handler.sendMessage(message);
                        SystemClock.sleep(1000);
                    } else {
                        message = Message.obtain();
                        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                        message.obj = "数据格式错误，正在还原配置";
                        message.arg1 = 28;
                        handler.sendMessage(message);
                        SystemClock.sleep(1000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    message = Message.obtain();
                    if (URLCollections.isReLogin(result)) {
                        message.what = IndexActivity.USER_RELOGIN;
                        message.obj = "用户身份异常，重新登录";
                        message.arg1 = 28;
                        handler.sendEmptyMessage(IndexActivity.DIALOG_CANCEL);
                    } else {
                        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                        message.obj = "数据格式错误,正在还原配置";
                        message.arg1 = 28;
                    }
                    handler.sendMessage(message);

                    SystemClock.sleep(1000);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    message = Message.obtain();
                    message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
                    message.obj = "服务器通讯异常：" + throwable.getMessage() + "，正在还原设置";
                    message.arg1 = 28;
                    handler.sendMessage(message);
                    SystemClock.sleep(1000);
                }
            }

            message = Message.obtain();
            message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "进行机型数据解析";
            message.arg1 = 35;
            handler.sendMessage(message);

            if (device == null) {
                device = (Device) XMLAPI.readXML(MyApplication.getApplication().getAssets().open("zoomlion.xml"));
                device.getJ1939().setNodeAddr(Globals.currentTerminal.getCanNodeId());
                Globals.modelFile = DeviceModelFile.readFromFile(device);
                Globals.deviceSN = null;
            } else {
                deviceLoadSuccess = true;
                device.getJ1939().setNodeAddr(Globals.currentTerminal.getCanNodeId());
                Globals.modelFile = DeviceModelFile.readFromFile(device);
                Globals.modelFile.dataSetVO.getDSItem("整机编码_回复").setStrValue(Globals.deviceSN);
            }

            device = null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        message = Message.obtain();
        message.what = IndexActivity.DEVICE_PARSE_FINISH;
        message.obj = "机型解析完成，启动通讯线程";
        message.arg1 = 40;
        handler.sendMessage(message);

        int p = 10;
        while (isWaitting) {
            message = Message.obtain();
            message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "通讯线程正在初始化";
            message.arg1 = 40 + p;
            handler.sendMessage(message);
            publishProgress("waiting");
            SystemClock.sleep(200);
            p += 10;
        }

        message = Message.obtain();
        message.what = IndexActivity.UPDATE_PROGRESS_CONTENT;
        message.obj = "通讯线程启动完成";
        message.arg1 = 98;
        handler.sendMessage(message);
        if (deviceLoadSuccess) {
            //机型下载成功，通知线程恢复状态，并刷新初始化参数
            SystemClock.sleep(1000);
            handler.sendEmptyMessage(IndexActivity.DEVICE_LOADING_FINISH);
        } else {
            //机型下载失败，通知线程恢复状态，无需刷新初始化参数
            SystemClock.sleep(1000);
            handler.sendEmptyMessage(IndexActivity.DEVICE_LOADING_FAILED);
        }
        SystemClock.sleep(300);
        handler.sendEmptyMessage(IndexActivity.DIALOG_CANCEL);

        return null;
    }


}