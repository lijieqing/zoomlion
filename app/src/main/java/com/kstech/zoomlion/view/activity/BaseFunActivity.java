package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.StatusBarUtil;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;

/**
 * Created by lijie on 2017/9/4.
 */

public abstract class BaseFunActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, Color.argb(255, 140, 140, 145), 32);
    }

    public abstract void camera(CheckItemParamValueVO checkItemParamValueVO, ItemOperateBodyView iobv);
}
