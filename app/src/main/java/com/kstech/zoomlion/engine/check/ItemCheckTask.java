package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import J1939.J1939_DataVar_ts;

/**
 * 项目调试任务
 */
public class ItemCheckTask extends AsyncTask<Void, String, Void> implements J1939_DataVar_ts.RealtimeChangeListener{

    private int remainSeconds = 0;
    private CheckItemVO checkItemVO;
    private List<CheckItemParamValueVO> headers;
    public boolean isRunning = false;
    public int qcID = -1;
    public int times = -1;
    private ItemCheckCallBack callBack;

    private Timer timer;

    /**
     * 设置项目调试回调
     *
     * @param callBack the call back
     */
    public ItemCheckTask(ItemCheckCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(Void... params) {
        isRunning = true;

        //开始调试启动回调
        callBack.onStart(this);

        if (qcID == -1 || times == -1) {
            callBack.onStartError("QCId或times未设置");
            callBack.onTaskStop(false);
            return null;
        }
        // 发送检测命令
        //CommandSender.sendStartCheckCommand(qcID + "", times);
        //获得checkitemVO
        checkItemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        //获取调试参数集合
        headers = checkItemVO.getParamNameList();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        },1000,1000);

        //规定时间内循环接受数据
        while (remainSeconds < 10 && isRunning) {
//            String startCheckCommandResp = CommandResp.getStartCheckCommandResp(qcID + "", times);
            String startCheckCommandResp = "";
            if ("".equals(startCheckCommandResp)) {
                //调试过程回调
                callBack.onProgress("等待测量终端信息");

            } else if ("正在检测".equals(startCheckCommandResp)) {
                String content = "正在检测 - - - ";
                //调试过程回调
                callBack.onProgress(content);

            } else if ("检测完成".equals(startCheckCommandResp)) {
                String content = "";
                // 检测完成回调
                callBack.onSuccess(headers, content);
                callBack.onTaskStop(true);
                return null;
            } else if ("传感器故障".equals(startCheckCommandResp) || "检测失败".equals(startCheckCommandResp)) {
                String content = "";
                //调试异常回调
                callBack.onResultError(headers, content);
                callBack.onTaskStop(true);
                return null;
            } else if ("谱图上传完成".equals(startCheckCommandResp)){

            }
        }

        timer.cancel();

        if (!isRunning) {
            callBack.onTaskStop(false);
        } else {
            // 通讯超时回调
            callBack.onTimeOut(headers, "通讯超时");
            callBack.onTaskStop(true);
        }

        return null;
    }

    public void stopCheck() {
        isRunning = false;
    }

    @Override
    public void onDataChanged(short dsItemPosition, float value) {
        // TODO: 2017/12/13 在此处监听谱图数据和谱图序号数据 通过dsItemPosition来区分
    }
}
