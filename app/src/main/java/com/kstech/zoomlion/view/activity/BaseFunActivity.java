package com.kstech.zoomlion.view.activity;

import android.app.Activity;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;

/**
 * Created by lijie on 2017/9/4.
 */

public abstract class BaseFunActivity extends Activity {
    public abstract void camera(CheckItemParamValueVO checkItemParamValueVO,ItemOperateBodyView iobv);
    public abstract void onPicSave();
}
