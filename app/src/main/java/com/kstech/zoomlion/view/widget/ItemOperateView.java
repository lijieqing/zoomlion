package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 调试项目界面调试操作和参数相关操作提示组件
 *
 * Created by lijie on 2017/9/4.
 */
public class ItemOperateView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private LinearLayout ll_body;
    private BaseFunActivity baseFunActivity;

    private ImageView ivSave;//保存
    private ImageView ivStart;//开始
    private ImageView ivForward;//上一项目
    private ImageView ivNext;//下一项目

    private LinearLayout llCheckStatus;//当前调试项目状态，当准备就绪时变为绿色提示用户
    private Chronometer chronometer;//计时器显示

    private List<ItemOperateBodyView> bodyViews;//参数描述体 集合

    private boolean isChecking = false;//是否正在调试

    /**
     * Instantiates a new Item operate view.
     *
     * @param context the context
     */
    public ItemOperateView(Context context) {
        super(context);
        this.context = context;

        this.addView(initView());
    }

    /**
     * Instantiates a new Item operate view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ItemOperateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        this.addView(initView());
    }

    /**
     * Instantiates a new Item operate view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ItemOperateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        this.addView(initView());
    }

    /**
     * 初始化布局 注册监听事件
     *
     * @return
     */
    private View initView() {
        bodyViews = new ArrayList<>();

        View v = View.inflate(context, R.layout.check_item_operate, null);
        ll_body = v.findViewById(R.id.ll_operate_body);
        llCheckStatus = v.findViewById(R.id.ll_check_status);

        ivSave = v.findViewById(R.id.iv_save);
        ivStart = v.findViewById(R.id.iv_start);
        ivForward = v.findViewById(R.id.iv_forward);
        ivNext = v.findViewById(R.id.iv_next);
        chronometer = v.findViewById(R.id.chronometer_operate);

        ivSave.setOnClickListener(this);
        ivStart.setOnClickListener(this);
        ivForward.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        return v;
    }

    /**
     * 更新参数操作显示区
     *
     * @param itemVO 调试项目描述文件
     */
    public void update(CheckItemVO itemVO) {

        //清空参数布局集合
        bodyViews.clear();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 11);
        //参数操作布局view
        ItemOperateBodyView bodyView;
        for (CheckItemParamValueVO checkItemParamValueVO : itemVO.getParamNameList()) {
            //初始化参数操作布局
            bodyView = new ItemOperateBodyView(baseFunActivity, checkItemParamValueVO, itemVO.getId());
            ll_body.addView(bodyView, params);
            //添加到参数布局集合
            bodyViews.add(bodyView);


            if (!checkItemParamValueVO.getValueReq() || !"Auto".equals(checkItemParamValueVO.getValMode())) {
                ivStart.setEnabled(false);//无需通讯条件下开始按钮不可点击
            } else {
                ivStart.setEnabled(true);//通讯条件下开始按钮可点击
                ivSave.setEnabled(false);//通讯条件下保存按钮不可点击
            }

        }
    }

    /**
     * 更新调试状态
     *
     * @param isRunning
     */
    private void updateCheckStatus(boolean isRunning) {
        if (isRunning) {
            ivStart.setBackgroundResource(R.drawable.stop);
            ivSave.setBackgroundResource(R.drawable.save_disable);
            isChecking = true;
        } else {
            ivStart.setBackgroundResource(R.drawable.start);
            ivSave.setBackgroundResource(R.drawable.save);
            isChecking = false;
        }
    }


    /**
     * 设置功能baseFunActivity 用于回调 camera
     *
     * @param baseFunActivity the base fun activity
     */
    public void setCameraActivity(@NonNull BaseFunActivity baseFunActivity) {
        this.baseFunActivity = baseFunActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_save:
                saveRecord(isChecking);
                break;
            case R.id.iv_start:
                if (isChecking) {
                    stopCheck();
                } else {
                    startCheck();
                }
                break;
            case R.id.iv_forward:
                toForward();
                break;
            case R.id.iv_next:
                toNext();
                break;
        }
    }

    /**
     * 跳转到下一项目
     */
    private void toNext() {

    }

    /**
     * 跳转到前一个项目
     */
    private void toForward() {

    }

    /**
     * 根据是否正在调试对保存按钮事件进行处理
     *
     * @param isChecking
     */
    private void saveRecord(boolean isChecking) {
        if (isChecking) {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {

            //// TODO: 2017/9/26 保存调试记录

        }

    }

    /**
     * 开始调试
     */
    private void startCheck() {
        //更新调试状态为正在调试
        updateCheckStatus(true);
    }

    /**
     * 停止调试
     */
    private void stopCheck() {
        //更新调试状态为未在调试
        updateCheckStatus(false);
    }
}
