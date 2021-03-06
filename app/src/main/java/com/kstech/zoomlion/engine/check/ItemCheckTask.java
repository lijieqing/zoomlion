package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.kstech.zoomlion.engine.base.ItemCheckCallBack;
import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;
import com.kstech.zoomlion.model.xmlbean.Spectrum;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;

import org.greenrobot.greendao.annotation.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
     * 项目调试已用时单位：秒
     */
    private int remainSeconds = 0;
    /**
     * 调试线程状态
     */
    public static boolean isRunning = false;
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
     * 谱图描述对象
     */
    private Spectrum spectrum;
    /**
     * 谱图数据参数名前缀（用来组装成dsItem的name，来获取数据）
     * 举例：谱图参数名为 臂架泵压力，谱图数据参数名为 谱图_臂架泵压力。
     */
    private static final String preFix = "谱图_";
    /**
     * 谱图数据的顺序号，测量终端发送的第几个参数数据
     */
    private static final String specOrder = "谱图_顺序号";
    /**
     * 谱图参数数据记录集合，key用谱图参数名称
     */
    private Map<String, List<Float>> specMap;
    /**
     * 谱图顺序号集合
     */
    private List<Float> specOrderList;
    /**
     * 待补传的谱图顺序号集合
     */
    private LinkedList<Float> lostSpecList;
    /**
     * 是否处于谱图补传阶段
     */
    private boolean inSpecRepairMode = false;

    private boolean hasLost = false;

    private boolean repaired = false;

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
        CommandSender.sendStartCheckCommand(qcID + "", times);
        //获得checkitemVO
        CheckItemVO checkItemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        //获取调试参数集合
        List<CheckItemParamValueVO> headers = checkItemVO.getParamNameList();
        //获取谱图描述对象
        spectrum = checkItemVO.getSpectrum();

        //如果存在谱图参数进行初始化，包括注册监听
        if (spectrum != null) {
            //初始化谱图参数相关结合
            specMap = new HashMap<>();
            specOrderList = new LinkedList<>();
            for (SpecParam specParam : spectrum.getSpecParams()) {
                String name = specParam.getParam();
                //谱图参数名称为key，定义空集合作为value
                specMap.put(name, new LinkedList<Float>());
            }

            registerTaskListener();
        }
        //创建计时器任务，延时1s后启动
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 1000, 1000);

        //规定时间内循环接受数据
        while (remainSeconds < 12 * 60 && isRunning) {
            String startCheckCommandResp = CommandResp.getStartCheckCommandResp(qcID + "", times);
            //String startCheckCommandResp = "";
            if ("".equals(startCheckCommandResp)) {
                //调试过程回调
                callBack.onProgress("等待测量终端信息");

            } else if ("正在检测".equals(startCheckCommandResp)) {
                String content = "正在检测 - - - ";
                //调试过程回调
                callBack.onProgress(content);

            } else if ("检测完成".equals(startCheckCommandResp)) {
                String content = "";
                //处理参数数据
                List<CheckItemParamValueVO> resluts = paramValueFormat(headers, true);
                if (specMap != null) {
                    //过滤重复序列号数据
                    filterList(specOrderList, specMap);
                }
                // 检测完成回调，返回paramResults
                callBack.onSuccess(resluts, specMap, content);
                callBack.onTaskStop(true);
                ItemCheckTask.isRunning = false;
                //移除监听,取消计时
                timer.cancel();
                removeTaskListener();
                return null;
            } else if ("传感器故障".equals(startCheckCommandResp) || "检测失败".equals(startCheckCommandResp)) {
                //处理参数数据
                List<CheckItemParamValueVO> resluts = paramValueFormat(headers, true);
                //调试异常回调
                callBack.onResultError(resluts, startCheckCommandResp);
                callBack.onTaskStop(true);
                ItemCheckTask.isRunning = false;
                //移除监听,取消计时
                timer.cancel();
                removeTaskListener();
                return null;
            } else if ("谱图上传完成".equals(startCheckCommandResp) && !repaired) {
                //初始化补传顺序号集合
                lostSpecList = new LinkedList<>();
                //获取谱图数据总数量
                int totalSize = (int) Globals.modelFile.getDataSetVO().getDSItem("附加码").getValue();
                //判断是否存在遗漏顺序号
                hasLost = verifySpecOrder(totalSize, specOrderList, lostSpecList);
                if (hasLost) {
                    removeTaskListener();

                    //存在漏传，开启谱图补传模式
                    inSpecRepairMode = true;
                    SpecRepairThread specRepairThread = new SpecRepairThread();
                    try {
                        specRepairThread.start();
                        specRepairThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //发送无需补传命令
                    CommandSender.sendSpecRepairCommand(0xffff, null);
                }

            }
        }

        timer.cancel();

        if (!isRunning) {
            callBack.onTaskStop(false);
        } else {
            // 通讯超时回调
            callBack.onTimeOut(headers, "通讯超时", specMap);
            callBack.onTaskStop(true);

            isRunning = false;
        }
        return null;
    }

    /**
     * 停止当前调试
     */
    public void stopCheck() {
        CommandSender.sendStopCheckCommand(qcID + "", times);
        isRunning = false;
        removeTaskListener();
    }

    @Override
    public void onDataChanged(short dsItemPosition, Object value) {
        if (!inSpecRepairMode) {
            //谱图序列号数据回调
            if (dsItemPosition == Globals.modelFile.dataSetVO.getItemIndex(specOrder)) {
                //非谱图补传状态，正常接收处理数据
                LogUtils.e("ItemCheckTask", " 正常接收序列号: " + value);
                //将谱图顺序号存入集合
                specOrderList.add((Float) value);
            } else {
                //非谱图序列号回调
                //遍历谱图参数集合，获取对应的谱图参数数据
                for (SpecParam specParam : spectrum.getSpecParams()) {
                    //获取谱图参数名称
                    String specName = specParam.getParam();
                    //组装谱图数据参数名
                    String specValueName = preFix + specName;
                    if (dsItemPosition == Globals.modelFile.dataSetVO.getItemIndex(specValueName)) {
                        //谱图数据参数对象
                        J1939_DataVar_ts dataVar = Globals.modelFile.getDataSetVO().getDSItem(specValueName);
                        //获取谱图参数数据，并转换成标准精度
                        String data = getDataVarValue(dataVar);
                        //添加到指定的谱图参数 数据集合中
                        specMap.get(specName).add(Float.valueOf(data));
                        //非谱图补传状态，正常接收处理数据
                        LogUtils.e("ItemCheckTask", " 正常接收数值: " + data);
                    }
                }
            }

        }
    }

    /**
     * 过滤重复序列号，以及对应的数据
     *
     * @param posList 序列号集合
     * @param specMap 数据集合
     */
    private void filterList(List<Float> posList, Map<String, List<Float>> specMap) {
        if (posList == null || specMap == null) return;
        float temp = 0;
        for (int i = 0; i < posList.size(); i++) {
            if (i == 0) {
                temp = posList.get(i);
            } else {
                float currentF = posList.get(i);
                if (temp == currentF) {
                    posList.remove(i);
                    for (List<Float> floats : specMap.values()) {
                        floats.remove(i);
                    }
                }
                temp = posList.get(i);
            }
        }
    }

    /**
     * 判断集合中的数据是否为连续，并将丢失部分计算出来
     *
     * @param total             谱图参数数据总数
     * @param specOrderlist     被判断的集合
     * @param lostSpecOrderList 丢失数据集合
     * @return 是否连续
     */
    private boolean verifySpecOrder(int total, @NotNull List<Float> specOrderlist, @NotNull List<Float> lostSpecOrderList) {
        //是否存在漏传数据，TRUE为存在漏传
        boolean result = false;
        if (specOrderlist == null || lostSpecOrderList == null) {
            return false;
        }
        //对集合先排序
        Collections.sort(specOrderlist);
        total += 1;
        //比较最后一个参数顺序号与参数数据数量
        float endLost = total - specOrderlist.get(specOrderlist.size()-1);
        if (endLost > 1) {
            //出现偏差，加入补发集合
            result = true;
            for (int i = 1; i < endLost; i++) {
                float repair = specOrderlist.get(specOrderlist.size()-1) + i;
                lostSpecOrderList.add(repair);
            }
        }
        //定义变量记录 上一条序号数据
        float last = 1;
        //按顺序遍历谱图顺序号集合
        for (int i = 0; i < specOrderlist.size(); i++) {
            if (i == 0) {
                //i=0 时第一个数据，不需要判断
                float temp = specOrderlist.get(i);
                for (float f = 1; f < temp; f++) {
                    lostSpecOrderList.add(f);
                }
                last = temp;
            } else {
                //设置临时变量，计算上一次的顺序号和本次序列号是否连续
                float temp = specOrderlist.get(i) - last;
                if (temp > 1f) {
                    //不连续，将所丢失的序列号计算出来，加入集合中
                    for (float f = 1f; f < temp; f++) {
                        lostSpecOrderList.add(last + f);
                    }
                    result = true;
                }
                //计算结束后重新赋值
                last = specOrderlist.get(i);
            }
        }
        Collections.sort(lostSpecOrderList);
        return result;
    }

    /**
     * 获取J939DataVar中的数据
     *
     * @param dataVar J939DataVar对象
     * @return 数据值
     */
    public static String getDataVarValue(J1939_DataVar_ts dataVar) {
        String checkvalue;
        //小数点后位数
        byte bDataDec = dataVar.bDataDec;
        StringBuffer sb = new StringBuffer("0");
        if (bDataDec != 0) {
            sb.append(".");
            for (int i = 0; i < bDataDec; i++) {
                sb.append("0");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
        if (dataVar.isFloatType()) {
            Float v = dataVar.getFloatValue();
            String formatValue = decimalFormat.format(v);
            //如果第一个字符是"."，在前面加0
            if (".".equals(formatValue.substring(0, 1))) {
                formatValue = "0" + formatValue;
            }
            //如果结果是0，无需添加精度，直接为0
            if ("0".equals(formatValue)) {
                formatValue = "0";
            }
            checkvalue = formatValue;
        } else {
            checkvalue = String.valueOf(dataVar.getValue());
        }

        return checkvalue;
    }

    /**
     * 参数数据处理
     *
     * @param sample  当前调试项目的参数集合
     * @param success 调试是否成功
     * @return 处理后的参数数据
     */
    private List<CheckItemParamValueVO> paramValueFormat(List<CheckItemParamValueVO> sample, boolean success) {
        //创建参数数据变量集合
        List<CheckItemParamValueVO> paramResults = new ArrayList<>();
        //遍历所有的参数
        for (CheckItemParamValueVO header : sample) {
            //判断是否自动取值
            if (header.getValueReq() && "Auto".equals(header.getValMode())) {
                //是自动取值，新建参数数据对象并赋值
                CheckItemParamValueVO paramResult = new CheckItemParamValueVO(header);
                //调试成功还是失败
                if (success) {
                    //成功，查找对应的数据进行赋值
                    J1939_DataVar_ts dataVar = Globals.modelFile.dataSetVO.getDSItem(header.getParamName());
                    String data = getDataVarValue(dataVar);
                    paramResult.setValue(data);
                } else {
                    //失败，默认保存数值0
                    paramResult.setValue("0");
                }
                //添加集合
                paramResults.add(paramResult);
            }
        }
        return paramResults;
    }

    /**
     * 在谱图收集时，当任务结束需要调用此方法来取消监听
     */
    private void removeTaskListener() {
        if (spectrum != null) {
            //移除谱图序列号监听器
            Globals.modelFile.dataSetVO.getDSItem(specOrder).removeListener(this);
            for (String specName : specMap.keySet()) {
                //组装谱图数据参数名
                String specValueName = preFix + specName;
                //注册监听
                Globals.modelFile.dataSetVO.getDSItem(specValueName).removeListener(this);
            }
        }
    }

    /**
     * 注册监听
     */
    private void registerTaskListener() {
        if (spectrum != null) {
            //添加谱图顺序号监听器
            Globals.modelFile.dataSetVO.getDSItem(specOrder).addListener(this);
            for (String specName : specMap.keySet()) {
                //组装谱图数据参数名
                String specValueName = preFix + specName;
                //注册监听
                Globals.modelFile.dataSetVO.getDSItem(specValueName).addListener(this);
            }
        }
    }

    /**
     * 谱图补传线程
     */
    class SpecRepairThread extends Thread implements J1939_DataVar_ts.RealtimeChangeListener {
        boolean received;
        float index;

        @Override
        public void run() {
            //添加谱图顺序号监听器
            registerListener();
            while (lostSpecList.size() > 0) {
                //取出顶部元素，发送补传命令
                index = lostSpecList.getFirst();
                LogUtils.e("ItemCheckTask", "SpecRepairThread Spec Float: " + index);

                CommandSender.sendSpecRepairCommand((long) index, spectrum.getSpecParams());
                received = false;
                while (!received) {
                    //等待接收到数值后，结束此循环
                }
            }
            repaired = true;
            //清楚谱图顺序号监听器
            removeListener();

            //退出谱图补传模式
            inSpecRepairMode = false;
            //设置为无需补传
            hasLost = false;
            //发送无需补传命令
            CommandSender.sendSpecRepairCommand(0xffff, null);
            lostSpecList.clear();
            SystemClock.sleep(1000);
        }

        @Override
        public void onDataChanged(short dsItemPosition, Object value) {
            if (inSpecRepairMode) {
                //谱图补传状态
                //序列号即为每个集合的下标
                //遍历谱图参数集合，获取对应的谱图参数数据
                for (SpecParam specParam : spectrum.getSpecParams()) {
                    //获取谱图参数名称
                    String specName = specParam.getParam();
                    //组装谱图数据参数名
                    String specValueName = preFix + specName;

                    short specPos = Globals.modelFile.dataSetVO.getItemIndex(specValueName);

                    if (dsItemPosition == specPos) {
                        //获取谱图参数数据，并转换成标准精度
                        LogUtils.e("ItemCheckTask", "index:" + index + " specValueName:" + specValueName + " value:" + value);
                        //添加到指定的谱图参数 数据集合中
                        insertValueInPosition(index, (Float) value, specOrderList, specMap.get(specName));
                        //更新谱图参数集合
                        insertValueInPosition(index, index, specOrderList, specOrderList);
                        lostSpecList.pop();
                        received = true;
                    }
                }
            }
        }

        /**
         * 注册谱图数据监听
         */
        private void registerListener() {
            for (String specName : specMap.keySet()) {
                //组装谱图数据参数名
                String specValueName = preFix + specName;
                //注册监听
                Globals.modelFile.dataSetVO.getDSItem(specValueName).addListener(this);
            }
        }

        /**
         * 移除谱图数据监听
         */
        private void removeListener() {
            for (String specName : specMap.keySet()) {
                //组装谱图数据参数名
                String specValueName = preFix + specName;
                //注册监听
                Globals.modelFile.dataSetVO.getDSItem(specValueName).removeListener(this);
            }
        }

        /**
         * 将数值插入到valueList指定位置，
         * 切起，对于posList 我们默认再添加数据时默认是按顺序添加的
         * 否则此方法无法达到预期效果
         *
         * @param currentPos 当前插入的位置
         * @param value      当前插入的数值
         * @param posList    位置集合列表
         * @param valueList  待插入数值集合列表
         */
        private void insertValueInPosition(float currentPos, float value, List<Float> posList, List<Float> valueList) {
            int index = 0;
            //当前位置最大，添加到集合的尾部
            if (currentPos > posList.get(posList.size()-1)) {
                valueList.add(value);
                return;
            }
            //当前位置最小，添加到集合的头部
            if (currentPos < posList.get(0)) {
                valueList.add(0,value);
                return;
            }
            //在位置集合内部
            for (int i = 0; i < posList.size(); i++) {
                if (currentPos > posList.get(i)) {
                    index++;
                } else {
                    index = i;
                    break;
                }
            }
            valueList.add(index, value);
        }
    }
}
