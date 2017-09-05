package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

/**
 * Created by lijie on 2017/9/4.
 */

public class ItemOperateView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private LinearLayout ll_body;
    private BaseFunActivity baseFunActivity;

    private ImageView ivSave;
    private ImageView ivStart;
    private ImageView ivStop;
    private ImageView ivNext;

    public ItemOperateView(Context context) {
        super(context);
        this.context = context;

        this.addView(initView());
    }

    public ItemOperateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        this.addView(initView());
    }

    public ItemOperateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        this.addView(initView());
    }

    private View initView() {
        View v = View.inflate(context, R.layout.check_item_operate, null);
        ll_body = v.findViewById(R.id.ll_operate_body);

        ivSave = v.findViewById(R.id.iv_save);
        ivStart = v.findViewById(R.id.iv_start);
        ivStop = v.findViewById(R.id.iv_stop);
        ivNext = v.findViewById(R.id.iv_next);

        ivSave.setOnClickListener(this);
        ivStart.setOnClickListener(this);
        ivStop.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        return v;
    }

    public void update(CheckItemVO itemVO) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 11);
        for (CheckItemParamValueVO checkItemParamValueVO : itemVO.getParamNameList()) {
            ItemOperateBodyView bodyView = new ItemOperateBodyView(baseFunActivity, checkItemParamValueVO, itemVO.getId());
            ll_body.addView(bodyView, params);
        }
    }

    public void setCameraActivity(@NonNull BaseFunActivity baseFunActivity) {
        this.baseFunActivity = baseFunActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_save:
                break;
            case R.id.iv_start:
                break;
            case R.id.iv_stop:
                break;
            case R.id.iv_next:
                break;
        }
    }
}
