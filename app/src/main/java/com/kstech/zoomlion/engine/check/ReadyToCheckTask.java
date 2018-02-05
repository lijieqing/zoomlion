package com.kstech.zoomlion.engine.check;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.activity.ItemCheckActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2017/5/24.
 */

public class ReadyToCheckTask extends AsyncTask<Void, String, Void> {
    /**
     * 准备调试展示窗
     */
    private AlertDialog dialog;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 消息发送handler
     */
    private Handler handler;
    /**
     * 准备调试时间计时
     */
    private int remainSeconds = 0;
    /**
     * 信息显示view
     */
    private TextView tvMsg;
    /**
     * 计时器
     */
    private Chronometer chronometer;
    /**
     * 进入调试操作按钮
     */
    private Button btnIn;
    /**
     * 取消调试操作按钮
     */
    private Button btnCancel;
    /**
     * 准备调试结果IMG view
     */
    private ImageView imageView;
    /**
     * 准备调试任务正在运行
     */
    private boolean isRunning;
    /**
     * 计时器，用来倒计时进入调试时间
     */
    private Timer timer;
    /**
     * 默认准备完成5秒后进入调试
     */
    private int countdown = 5;
    /**
     * 倒计时进入调试 task
     */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            countdown--;
            if (countdown < 0) {
                publishProgress("countFinish", "准备检测完成", countdown + "");
            } else {
                publishProgress("countDown", "准备检测完成", countdown + "");
            }
        }
    };

    public ReadyToCheckTask(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_view,null,false);

        tvMsg = view.findViewById(R.id.tv_msg_ready_check);
        chronometer = view.findViewById(R.id.chronom);
        btnIn = view.findViewById(R.id.btn_in);
        btnCancel = view.findViewById(R.id.btn_cancel);
        imageView = view.findViewById(R.id.iv_progress);

        //btnCancel.setVisibility(View.INVISIBLE);
        btnIn.setVisibility(View.INVISIBLE);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                dialog.cancel();
                isRunning = false;
            }
        });
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                dialog.cancel();
                isRunning = false;
                handler.sendEmptyMessage(ItemCheckActivity.TERMINAL_READY_CHECK);
            }
        });

        chronometer.setFormat("耗时：%s");
        dialog = new AlertDialog.Builder(context)
                .setView(view).setCancelable(false)
                .create();
        dialog.show();
        chronometer.start();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String readyToCheckCommandResp = "";

        Timer countDownTimer = new Timer();
        countDownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 1000, 1000);
        // 发送准备检测命令
        CommandSender.sendReadyToCheckCommand(Globals.currentCheckItem.getId(), 1);
        isRunning = true;

        publishProgress("progress", "与终端通讯进行准备检测--最大耗时--", Globals.currentCheckItem.getReadyTimeout()+"秒");
        while (remainSeconds < Globals.currentCheckItem.getReadyTimeout() && isRunning) {
            //readyToCheckCommandResp = CommandResp.getReadyToCheckCommandResp(Globals.currentCheckItem.getId(), 1);

            if (remainSeconds == 7){
                readyToCheckCommandResp = "准备就绪";
            }
            if ("准备就绪".equals(readyToCheckCommandResp)) {
                String readyMsg = Globals.currentCheckItem.getReadyMsg();
                String content = "准备就绪";
                // if (readyMsg != null && !readyMsg.equals("")) {
                //     content = Globals.getResConfig().getResourceVO().getMsg(readyMsg).getContent();
                // }
                // 通知UI线程准备就绪，退出循环程序继续执行
                publishProgress("ok", "与终端进行准备检测通讯--准备就绪--", content);
                SystemClock.sleep(1000);
                return null;

            } else if ("传感器故障".equals(readyToCheckCommandResp)) {
                // 有响应，但是不是准备就绪，则通知UI，传感器故障。程序终止
                String notReadyMsg = Globals.currentCheckItem.getNotReadyMsg();
                Log.e("ReadyToCheckTask", notReadyMsg);
                String content = "传感器故障";
                // if (notReadyMsg != null && !notReadyMsg.equals("")) {
                //     content = Globals.getResConfig().getResourceVO().getMsg(notReadyMsg).getContent();
                // }
                publishProgress("error", "--无法进入检测--", content);
                SystemClock.sleep(1000);
                return null;
            }
        }
        publishProgress("timeout", "--与终端进行准备检测通讯--超时--", "无法开始检测");
        SystemClock.sleep(1000);
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if ("progress".equals(values[0])) {
            tvMsg.setText(values[1] + values[2]);
            btnCancel.setVisibility(View.VISIBLE);
        }
        if ("error".equals(values[0])) {
            tvMsg.setText(values[1] + values[2]);
            chronometer.stop();
            isRunning = false;
            imageView.setBackgroundResource(R.drawable.progress_error);
            btnCancel.setVisibility(View.VISIBLE);
        }
        if ("ok".equals(values[0])) {
            tvMsg.setText(values[1] + values[2]);
            chronometer.stop();
            isRunning = false;
            imageView.setBackgroundResource(R.drawable.progress_ok);
            btnIn.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);

            timer = new Timer();
            timer.schedule(timerTask, 0, 1000);
        }
        if ("timeout".equals(values[0])) {
            tvMsg.setText(values[1] + values[2]);
            chronometer.stop();
            isRunning = false;
            imageView.setBackgroundResource(R.drawable.progress_error);
            btnCancel.setVisibility(View.VISIBLE);
        }
        if ("countDown".equals(values[0])) {
            tvMsg.setText(values[1] + values[2] + "s 后进入");
        }
        if ("countFinish".equals(values[0])) {
            timer.cancel();
            dialog.cancel();
            isRunning = false;
            handler.sendEmptyMessage(ItemCheckActivity.TERMINAL_READY_CHECK);
        }
    }
}
