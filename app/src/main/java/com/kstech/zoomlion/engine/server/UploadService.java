package com.kstech.zoomlion.engine.server;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import java.util.Timer;
import java.util.TimerTask;

public class UploadService extends Service {
    private Timer timer;
    private UploadTask task;
    private Handler handler;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        task = new UploadTask();
        Log.d("UploadService", "---------onCreate---------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("UploadService", "---------scheduledExecutionTime---" + task.scheduledExecutionTime());
        if (task.scheduledExecutionTime() <= 0) {
            timer.schedule(task, 0, 1000 * 60 * 10);
        } else {
            new QCItemDataReLoadTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UploadService", "---------onDestroy---------");
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UploadBinder(this);
    }

    public class UploadBinder extends Binder {
        UploadService service;

        public UploadBinder(UploadService service) {
            this.service = service;
        }

        public void setHandler(Handler handler) {
            service.handler = handler;
        }
    }

    class UploadTask extends TimerTask {

        @Override
        public void run() {
            while (handler == null) {
                Log.d("UploadService", "---------upload---等待连接---" + this);
            }
            handler.sendEmptyMessage(CheckHomeActivity.RECORD_UPDATE_START);
            Log.d("UploadService", "---------upload---start---" + this);
            SystemClock.sleep(3000);
            Log.d("UploadService", "---------upload---end---" + this);
            handler.sendEmptyMessage(CheckHomeActivity.RECORD_UPDATE_FINISH);
        }
    }
}
