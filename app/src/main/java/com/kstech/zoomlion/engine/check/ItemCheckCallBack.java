package com.kstech.zoomlion.engine.check;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 项目调试过程信息回调接口
 */
public interface ItemCheckCallBack {
    /**
     * 项目调试开始回调，需要在方法中赋值项目ID和调试次数
     * 以及界面显示信息操作
     *
     * @param task 调试项目任务
     */
    void onStart(ItemCheckTask task);

    /**
     * 发送调试命令前如果有异常回调此方法
     *
     * @param msg the msg
     */
    void onStartError(String msg);

    /**
     * 项目调试过程回调，会传入过程信息
     *
     * @param progress 过程信息
     */
    void onProgress(String progress);

    /**
     * 项目调试成功回调，返回参数实体列表和 msg描述信息
     *
     * @param headers 参数实体集合
     * @param msg     调试结果信息
     * @param specMap 谱图参数数据集合
     */
    void onSuccess(List<CheckItemParamValueVO> headers, Map<String, LinkedList<Float>> specMap, String msg);

    /**
     * 调试过程故障回调
     *
     * @param headers 参数实体集合
     * @param msg     调试结果信息
     */
    void onResultError(List<CheckItemParamValueVO> headers, String msg);

    /**
     * 调试时间超时回调
     *
     * @param headers 参数实体集合
     * @param msg     调试结果信息
     */
    void onTimeOut(List<CheckItemParamValueVO> headers, String msg, Map<String, LinkedList<Float>> specMap);

    void onTaskStop(boolean canSave);
}