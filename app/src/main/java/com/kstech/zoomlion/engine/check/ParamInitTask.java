package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.view.activity.IndexActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2018/1/29.
 */

public class ParamInitTask extends AsyncTask<Void, Integer, Void> {
    private boolean prepared;
    private int remainSeconds;
    private Handler handler;

    public ParamInitTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        prepared = false;
        remainSeconds = 0;
        CommandSender.sendReadyToCheckCommand("253", 1);
        //创建计时器任务，延时1s后启动
        sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送准备命令 --准备阶段");
        Timer prepareTimer = new Timer();
        prepareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 500, 1000);

        String prepareResp = "";
        String content = "";
        //记录上次发送消息时间
        int lastSeconds = 0;
        while (remainSeconds < 60) {
            prepareResp = CommandResp.getReadyToCheckCommandResp("253", 1);
            if ("".equals(prepareResp)) {
                content = "等待终端回复";

            } else if ("准备就绪".equals(prepareResp)) {
                prepared = true;
                prepareTimer.cancel();
                sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "终端准备就绪");
                break;
            } else if ("传感器故障".equals(prepareResp)) {
                sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "初始化失败-准备调试未通过");
                prepareTimer.cancel();
                return null;
            }
            if (remainSeconds - lastSeconds > 2) {
                sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, content);
                lastSeconds = remainSeconds;
            }
        }

        //准备调试完成，进入项目调试流程
        if (prepared) {
            //重置数据
            remainSeconds = 0;
            content = "";
            lastSeconds = 0;
            // 发送开始调试命令
            //CommandSender.sendStartCheckCommand(Globals.currentCheckItem.getId(), 1);
            sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送初始化命令");
            //启动计时器
            Timer checkTimer = new Timer();
            checkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    remainSeconds++;
                }
            }, 500, 1000);

            String checkResp = "";
            while (remainSeconds < 60) {
                //checkResp = CommandResp.getStartCheckCommandResp(Globals.currentCheckItem.getId(), 1);
                if ("".equals(checkResp)) {
                    content = "等待终端回复";

                    if (remainSeconds == 8) {
                        checkResp = "检测完成";
                    }

                } else if ("正在检测".equals(checkResp)) {
                    content = "参数初始化中";
                    SystemClock.sleep(500);

                } else if ("检测完成".equals(checkResp)) {
                    sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "初始化完成");
                    sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, null);
                    checkTimer.cancel();
                    // TODO: 2018/1/29 初始化完成后，保存并上传记录
                    return null;
                } else if ("传感器故障".equals(checkResp) || "检测失败".equals(checkResp)) {

                    sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化失败");
                    checkTimer.cancel();
                    return null;
                }
                if (remainSeconds - lastSeconds > 2) {
                    sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, content);
                    lastSeconds = remainSeconds;
                }
            }

            checkTimer.cancel();
            return null;
        } else {

            //准备调试超时通知
            prepareTimer.cancel();
            return null;
        }
    }

    /**
     * 发送信息
     *
     * @param what    目标
     * @param content 内容
     */
    private void sendMsg(int what, String content) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = content;
        handler.sendMessage(message);
    }
}
