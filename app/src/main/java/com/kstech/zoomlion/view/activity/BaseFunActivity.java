package com.kstech.zoomlion.view.activity;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;

/**
 * Created by lijie on 2017/9/4.
 */
public abstract class BaseFunActivity extends BaseActivity {

    /**
     * 当点击“拍照采集”按钮时 调用此方法 并传入对应的参数和参数操作描述组件
     *
     * @param checkItemParamValueVO 参数vo类
     * @param iobv                  参数操作描述view
     */
    public abstract void camera(CheckItemParamValueVO checkItemParamValueVO, ItemOperateBodyView iobv);

    /**
     * 点击开始调试时 回调此方法
     */
    public abstract void startCheck();

    /**
     * 点击中止调试时 回调此方法
     */
    public abstract void stopCheck();

    /**
     * 点击保存记录时，回调此方法
     *
     * @param paramValues 已经赋值过的数据
     */
    public abstract void saveRecord(String paramValues);

    /**
     * 跳到上一调试项目
     */
    public abstract void toForward();

    /**
     * 跳到下一调试项目
     */
    public abstract void toNext();

    /**
     * 模糊块消失回调
     */
    public abstract void initDetailData();

    /**
     * 模糊块复原回调
     */
    public abstract void removeDetailData();
}
