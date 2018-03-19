package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.activity.DebugActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2018/3/19.
 */

public class ItemReadyTask extends AsyncTask<Void,Integer,Void> {
    private int remainSeconds;
    private boolean isRunning = false;
    private String qcID;
    private int qcTimes;
    private Handler handler;

    public ItemReadyTask(String qcID, int qcTimes, Handler handler) {
        this.qcID = qcID;
        this.qcTimes = qcTimes;
        this.handler = handler;
        remainSeconds = 0;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String readyToCheckCommandResp;

        Timer countDownTimer = new Timer();
        countDownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 1000, 1000);

        // 发送准备检测命令
        CommandSender.sendReadyToCheckCommand(qcID, qcTimes);
        isRunning = true;

        while (remainSeconds<10 && isRunning){
            readyToCheckCommandResp = CommandResp.getReadyToCheckCommandResp(qcID, qcTimes);
            LogUtils.e("ItemReadyTask","readyToCheckCommandResp:"+readyToCheckCommandResp);

            if ("准备就绪".equals(readyToCheckCommandResp)) {
                String content = "准备就绪";
                Message message = Message.obtain();
                message.what = DebugActivity.UPDATE_TASK_MSG;
                message.obj = content;
                handler.sendMessage(message);
                return null;

            } else if ("传感器故障".equals(readyToCheckCommandResp)) {
                // 有响应，但是不是准备就绪，则通知UI，传感器故障。程序终止
                String content = "传感器故障";
                Message message = Message.obtain();
                message.what = DebugActivity.UPDATE_TASK_MSG;
                message.obj = content;
                handler.sendMessage(message);
                return null;
            }
        }
        isRunning = false;
        return null;
    }
}
