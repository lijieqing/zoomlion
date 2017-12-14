package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;
import com.kstech.zoomlion.model.xmlbean.Spectrum;
import com.kstech.zoomlion.utils.Globals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import J1939.J1939_DataVar_ts;

/**
 * 项目调试任务
 */
public class ItemCheckTask extends AsyncTask<Void, String, Void> implements J1939_DataVar_ts.RealtimeChangeListener {
    /**
     * 项目调试已用时
     */
    private int remainSeconds = 0;
    /**
     * 调试项目标准描述对象
     */
    private CheckItemVO checkItemVO;
    /**
     * 调试项目参数集合
     */
    private List<CheckItemParamValueVO> headers;
    /**
     * 调试线程状态
     */
    public boolean isRunning = false;
    /**
     * 调试项目的qcID
     */
    public int qcID = -1;
    /**
     * 项目第几次调试
     */
    public int times = -1;
    /**
     * 调试项目状态回调接口
     */
    private ItemCheckCallBack callBack;
    /**
     * 计时器，用来进行调试时间计时
     */
    private Timer timer;
    /**
     * 谱图描述对象
     */
    private Spectrum spectrum;
    /**
     * 谱图序列号前缀（用来组装成dsItem的name，来获取数据）
     * 举例：谱图参数名为 臂架泵压力，谱图序列号的名称为 谱图_臂架泵压力。
     */
    private static final String preFix = "谱图_";
    /**
     * 谱图参数数据记录集合，key用谱图参数名称
     */
    private Map<String, LinkedList<Float>> specMap;
    /**
     * 谱图参数序号记录集合，key用谱图参数的名称
     */
    private Map<String, LinkedList<Float>> orderMap;

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
        //将运行状态置为TRUE
        isRunning = true;
        //开始调试启动回调
        callBack.onStart(this);
        //判断qcID和times是否有效
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
        //获取谱图描述对象
        spectrum = checkItemVO.getSpectrum();

        //如果存在谱图参数进行初始化，包括注册监听
        if (spectrum != null) {
            //初始化谱图参数相关结合
            specMap = new HashMap<>();
            orderMap = new HashMap<>();
            for (SpecParam specParam : spectrum.getSpecParams()) {
                String name = specParam.getParam();
                //谱图参数名称为key，定义空集合作为value
                specMap.put(specParam.getParam(), new LinkedList<Float>());
                orderMap.put(specParam.getParam(), new LinkedList<Float>());
                //添加谱图数据监听器
                Globals.modelFile.dataSetVO.getDSItem(name).addListener(this);
                //添加谱图序列号监听器
                Globals.modelFile.dataSetVO.getDSItem(preFix + name).addListener(this);
            }
        }
        //创建计时器任务，延时1s后启动
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 1000, 1000);

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
                //移除监听
                removeListener();
                return null;
            } else if ("传感器故障".equals(startCheckCommandResp) || "检测失败".equals(startCheckCommandResp)) {
                String content = "";
                //调试异常回调
                callBack.onResultError(headers, content);
                callBack.onTaskStop(true);
                //移除监听
                removeListener();
                return null;
            } else if ("谱图上传完成".equals(startCheckCommandResp)) {

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

    /**
     * 停止当前调试
     */
    public void stopCheck() {
        isRunning = false;
    }

    @Override
    public void onDataChanged(short dsItemPosition, float value) {
        // TODO: 2017/12/13 在此处监听谱图数据和谱图序号数据 通过dsItemPosition来区分
        //遍历谱图参数集合，将数据放入到对应的集合中
        for (SpecParam specParam : spectrum.getSpecParams()) {
            //获取谱图参数名称
            String specName = specParam.getParam();
            //组装谱图序列号
            String orderName = preFix + specName;
            //获取谱图参数在dsItem集合中的位置
            short paramPosition = Globals.modelFile.dataSetVO.getItemIndex(specName);
            //获取谱图序列号在dsItem集合中的位置
            short orderPosition = Globals.modelFile.dataSetVO.getItemIndex(orderName);
            //当谱图参数的位置相等，保存谱图数据
            if (paramPosition == dsItemPosition) {
                specMap.get(specName).add(value);
            }
            //档谱图序列号的位置相等，保存谱图序列号
            if (orderPosition == dsItemPosition) {
                orderMap.get(specName).add(value);
            }
        }
    }

    /**
     * 在谱图收集时，当任务结束需要调用此方法来取消监听
     */
    private void removeListener() {
        if (spectrum != null) {
            for (SpecParam specParam : spectrum.getSpecParams()) {
                String name = specParam.getParam();
                //添加谱图数据监听器
                Globals.modelFile.dataSetVO.getDSItem(name).removeListener(this);
                //添加谱图序列号监听器
                Globals.modelFile.dataSetVO.getDSItem(preFix + name).removeListener(this);
            }
        }
    }
}
