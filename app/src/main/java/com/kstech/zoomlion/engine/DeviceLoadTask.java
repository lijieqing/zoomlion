package com.kstech.zoomlion.engine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.manager.XMLAPI;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.activity.IndexActivity;

import java.io.IOException;

public class DeviceLoadTask extends AsyncTask<Void, String, Void> {

    private Handler handler;
    private String InExc;
    private Context context;
    public boolean isWaitting = true;


    public DeviceLoadTask(Context context, String InExc, Handler handler) {
        this.handler = handler;
        this.context = context;
        this.InExc = InExc;
    }

    /**
     * 主要实现类 执行时机：在onPreExecute 方法执行后马上执行，该方法运行在后台线程中 作用：主要负责执行那些很耗时的后台处理工作。可以调用
     * publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。
     */

    @Override
    protected Void doInBackground(Void... params) {

        Message message = Message.obtain();
        message.what = IndexActivity.DEVICE_PARSE_START;
        message.obj = "开始解析机型";
        message.arg1 = 10;
        handler.sendMessage(message);

        SystemClock.sleep(1000);

        message = Message.obtain();
        message.what = IndexActivity.DEVICE_PARSE_ING;
        message.obj = "机型解析中";
        message.arg1 = 20;

        try {
            handler.sendMessage(message);
            Globals.modelFile = DeviceModelFile.readFromFile((Device) XMLAPI
                    .readXML(context.getAssets().open("new.xml")));
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
            message.what = IndexActivity.J1939_COMM_INIT;
            message.obj = "通讯线程正在初始化";
            message.arg1 = 40 + p;
            handler.sendMessage(message);
            publishProgress("waiting");
            SystemClock.sleep(1000);
            p += 10;
        }

        message = Message.obtain();
        message.what = IndexActivity.J1939_COMM_INITED;
        message.obj = "通讯线程启动完成";
        message.arg1 = 98;
        handler.sendMessage(message);

        SystemClock.sleep(1000);
        handler.sendEmptyMessage(IndexActivity.DEVICE_LOAD_FINISH);

        return null;
    }


}