package com.kstech.zoomlion.engine;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;

import java.util.List;

/**
 * 项目调试过程信息回调接口
 */
interface ItemCheckCallBack {
    /**
     * 项目调试开始回调，需要在方法中赋值项目ID和调试次数
     * 以及界面显示信息操作
     *
     * @param qcID  调试项目ID
     * @param times 第几次调试
     */
    void onStart(int qcID, int times);

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
     */
    void onSuccess(List<CheckItemParamValueVO> headers, String msg);

    /**
     * 调试过程故障回调
     *
     * @param headers 参数实体集合
     * @param msg     调试结果信息
     */
    void onError(List<CheckItemParamValueVO> headers, String msg);

    /**
     * 调试时间超时回调
     *
     * @param headers 参数实体集合
     * @param msg     调试结果信息
     */
    void onTimeOut(List<CheckItemParamValueVO> headers, String msg);

    /**
     * 停止调试回调
     */
    void onStopCheck();

    /**
     * 调试暂停回调
     */
    void onPause();
}