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
}
