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
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 调试项目界面调试操作和参数相关操作提示组件
 * <p>
 * Created by lijie on 2017/9/4.
 */
public class ItemOperateView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private LinearLayout ll_body;//调试项目参数操作显示区域
    private BaseFunActivity baseFunActivity;

    private ImageView ivSave;//保存
    private ImageView ivStart;//开始
    private TextView tvStart;//
    private TextView tvForward;//上一项目
    private TextView tvNext;//下一项目

    public LinearLayout llCheckStatus;//当前调试项目状态，当准备就绪时变为绿色提示用户
    private LinearLayout llStart;
    private LinearLayout llSave;
    private LinearLayout llForward;
    private LinearLayout llNext;
    public Chronometer chronometer;//计时器显示

    private List<ItemOperateBodyView> bodyViews;//参数描述体 集合
    private List<CheckItemParamValueVO> paramValueVOList = new ArrayList<>();//调试项目参数获取数据后的集合

    private boolean isChecking = false;//是否正在调试

    private boolean needCommunicate = false;//是否需要与测量终端通讯

    private static final String TAG = "ItemOperateView";

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
        llStart = v.findViewById(R.id.item_check_ll_start);
        llSave = v.findViewById(R.id.item_check_ll_save);
        llForward = v.findViewById(R.id.item_check_ll_forward);
        llNext = v.findViewById(R.id.item_check_ll_next);

        ivSave = v.findViewById(R.id.iv_save);
        ivStart = v.findViewById(R.id.iv_start);
        tvStart = v.findViewById(R.id.tv_start);
        tvForward = v.findViewById(R.id.tv_forward);
        tvNext = v.findViewById(R.id.tv_next);
        chronometer = v.findViewById(R.id.chronometer_operate);

        llSave.setOnClickListener(this);
        llStart.setOnClickListener(this);
        llForward.setOnClickListener(this);
        llNext.setOnClickListener(this);

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
        ll_body.removeAllViews();

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
                //无需数值，或者不是自动获取数值的参数 不做处理
            } else {
                //改变参数状态
                needCommunicate = true;
            }

        }

        //根据是否与测量终端通讯调整组件状态
        if (needCommunicate) {
            llStart.setEnabled(true);
            ivStart.setEnabled(true);
            updateCheckStatus(false, false);
        } else {
            //无需通讯条件下开始按钮不可点击
            llStart.setEnabled(false);
            ivStart.setEnabled(false);
        }
        CheckItemVO temp = Globals.forwardCheckItem();
        if (temp == null){
            tvForward.setText("无");
        }else {
            tvForward.setText(temp.getName());
            //还原回当前项目
            Globals.nextCheckItem();
        }
        temp = Globals.nextCheckItem();
        if (temp == null){
            tvNext.setText("无");
        }else {
            tvNext.setText(temp.getName());
            //还原回当前项目
            Globals.forwardCheckItem();
        }
    }


    /**
     * Update check status.
     *
     * @param isRunning 是否正在运行
     * @param canSave   是否是强制行为
     */
    public void updateCheckStatus(boolean isRunning, boolean canSave) {
        if (isRunning) {
            ivStart.setBackgroundResource(R.drawable.stop);
            tvStart.setText("停止");
            ivSave.setBackgroundResource(R.drawable.save_disable);
            isChecking = true;
        } else {
            ivStart.setBackgroundResource(R.drawable.start);
            tvStart.setText("开始");
            if (canSave) {
                ivSave.setBackgroundResource(R.drawable.save);
            }
            isChecking = false;
        }
    }

    /**
     * 更新各个参数操作组件
     *
     * @param values
     */
    public void updateBodyAutoView(List<CheckItemParamValueVO> values) {
        for (ItemOperateBodyView bodyView : bodyViews) {
            for (CheckItemParamValueVO value : values) {
                if (bodyView.getInfo().getParamName().equals(value.getParamName())) {
                    bodyView.updateValueInfo(value.getValue());
                }
            }
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
            case R.id.item_check_ll_save:
                saveRecord(isChecking);
                break;
            case R.id.item_check_ll_start:
                if (isChecking) {
                    stopCheck();
                } else {
                    startCheck();
                }
                break;
            case R.id.item_check_ll_forward:
                toForward();
                break;
            case R.id.item_check_ll_next:
                toNext();
                break;
        }
    }

    /**
     * 跳转到下一项目
     */
    private void toNext() {
        baseFunActivity.toNext();
    }

    /**
     * 跳转到前一个项目
     */
    private void toForward() {
        baseFunActivity.toForward();
    }

    /**
     * 根据是否正在调试对保存按钮事件进行处理
     *
     * @param isChecking
     */
    private void saveRecord(boolean isChecking) {
        if (isChecking) {
            Toast.makeText(context, "调试未完成，无法保存！", Toast.LENGTH_SHORT).show();
        } else {
            //// TODO: 2017/9/26 保存调试记录
            paramValueVOList.clear();
            for (ItemOperateBodyView bodyView : bodyViews) {
                if (bodyView.isValueEmpty()) {
                    Toast.makeText(baseFunActivity, bodyView.getInfo().getParamName() + "未检测到数据", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    paramValueVOList.add(bodyView.getInfo());
                }
            }
            String paramValues = JsonUtils.toJson(paramValueVOList);

            baseFunActivity.saveRecord(paramValues);

            for (ItemOperateBodyView bodyView : bodyViews) {
                bodyView.reset();
            }
        }

    }

    /**
     * 开始调试
     */
    private void startCheck() {
        //更新调试状态为正在调试
        updateCheckStatus(true, false);
        //回调baseFunActivity开始调试
        baseFunActivity.startCheck();
    }

    /**
     * 停止调试
     */
    private void stopCheck() {
        //更新调试状态为未在调试
        updateCheckStatus(false, false);
        //回调baseFunActivity停止调试
        baseFunActivity.stopCheck();
    }
}
