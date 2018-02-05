package com.kstech.zoomlion.engine.base;

import android.support.annotation.WorkerThread;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;

/**
 * Created by lijie on 2017/9/4.
 */
public interface BaseCheckFunction {

    /**
     * 当点击“拍照采集”按钮时 调用此方法 并传入对应的参数和参数操作描述组件
     *
     * @param checkItemParamValueVO 参数vo类
     * @param iobv                  参数操作描述view
     */
    void camera(CheckItemParamValueVO checkItemParamValueVO, ItemOperateBodyView iobv);

    /**
     * 图片捕获弹窗取消 回调此方法
     */
    void cameraCancel();

    /**
     * 点击开始调试时 回调此方法
     */
    void startCheck();

    /**
     * 点击中止调试时 回调此方法
     */
    void stopCheck();

    /**
     * 点击保存记录时，回调此方法
     *
     * @param paramValues 已经赋值过的数据
     */
    void saveRecord(String paramValues);

    /**
     * 跳到上一调试项目
     */
    void toForward();

    /**
     * 跳到下一调试项目
     */
    void toNext();
    /**
     * 模糊块消失前回调
     */
    void clearBlur();

    /**
     * 加载调试项目数据信息
     */
    @WorkerThread
    void loadCheckItemData();

    /**
     * 模糊状态改变回调
     *
     * @param inBlur 是否
     */
    void onBlurChange(boolean inBlur);
}
