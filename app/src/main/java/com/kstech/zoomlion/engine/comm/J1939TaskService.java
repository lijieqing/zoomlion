package com.kstech.zoomlion.engine.comm;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.utils.Globals;

import J1939.J1939_Task;

import static java.lang.Thread.State.TERMINATED;

public class J1939TaskService extends Service {
    public static String ACTION = "com.kstech.engine.comm.J1939";

    // 1939任务
    public J1939_Task j1939ProtTask = null;

    // 1939任务
    public CommunicationWorker j1939CommTask = null;

    private Handler indexHandler;
    /**
     * @deprecated
     * 测试使用方法，正式版不会使用
     */
    public static String ipAddress;
    /**
     * @deprecated
     * 测试使用方法，正式版不会使用
     */
    public static int ipPort = 0;
    public static boolean inDebug = false;

    public J1939TaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean reload = false;
        Log.e("IndexActivity", "j1939CommTask in");
        if (intent != null) {
            reload = intent.getBooleanExtra("reload", false);
        }
        if (reload) {
            stopJ1939Service();

            j1939ProtTask = new J1939_Task();
            j1939ProtTask.Init();
            // 启动协议任务
            j1939ProtTask.start();

            // 启动通讯任务
            if (inDebug){
                j1939CommTask = new CommunicationWorker(ipAddress, ipPort, getApplicationContext(),null);
            }else {
                j1939CommTask = new CommunicationWorker(Globals.currentTerminal.getIp(), Integer.parseInt(Globals.currentTerminal.getPort()), getApplicationContext(),indexHandler);
            }

            j1939CommTask.start();
            Log.e("IndexActivity", "j1939CommTask start");
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }

    public class MyBinder extends Binder {
        public J1939TaskService task;

        MyBinder(J1939TaskService task) {
            this.task = task;
        }
    }

    /**
     * 设置 J939状态更新 Handler
     * @param handler handler
     */
    public void setIndexHandler(Handler handler){
        this.indexHandler = handler;
        j1939CommTask.setIndexHandler(handler);
    }

    /**
     * 停止 J939通讯线程
     */
    public void stopJ1939Service() {
        // 配置文件加载后需要重新初始化1939任务，任务中包括实时参数的增量初始化
        if (j1939ProtTask != null && j1939ProtTask.isRunning) {
            // 停止通讯任务
            j1939CommTask.setStop(true);
            while (TERMINATED != j1939CommTask.getState()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 停止协议任务
            j1939ProtTask.setStop(true);
            while (TERMINATED != j1939ProtTask.getState()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
