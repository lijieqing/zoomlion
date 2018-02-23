package com.kstech.zoomlion.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.base.BaseCheckFunction;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;
import com.kstech.zoomlion.model.xmlbean.Spectrum;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;

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
    private BaseCheckFunction baseCheckFunction;

    private ImageView ivSave;//保存
    private ImageView ivStart;//开始
    private TextView tvStart;//
    private TextView tvForward;//上一项目
    private TextView tvNext;//下一项目

    private LinearLayout llCheckStatus;//当前调试项目状态，当准备就绪时变为绿色提示用户
    private LinearLayout llStart;
    private LinearLayout llSave;
    private LinearLayout llForward;
    private LinearLayout llNext;
    private Chronometer chronometer;//计时器显示

    private List<ItemOperateBodyView> bodyViews;//参数描述体 集合
    private List<CheckItemParamValueVO> paramValueVOList = new ArrayList<>();//调试项目参数获取数据后的集合

    private boolean isChecking = false;//是否正在调试,用于点击开始、保存按钮时的状态判断

    private boolean needCommunicate = false;//是否需要与测量终端通讯

    private LinearLayout rlBlur;//模糊布局

    private Button btnBlur;//模糊布局按钮

    private boolean inBlur = true;//当前界面是否处在模糊状态，默认进入时为模糊状态

    private AlertDialog dataAbandonDialog;//放弃数据确认弹窗

    private static final String TAG = "ItemOperateView";

    /**
     * Instantiates a new Item operate view.
     *
     * @param context the context
     */
    public ItemOperateView(Context context) {
        super(context);
        this.context = context;

        this.addView(initView(context));
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

        this.addView(initView(context));
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

        this.addView(initView(context));
    }

    /**
     * 初始化布局 注册监听事件
     *
     * @return
     */
    private View initView(Context context) {
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

        rlBlur = v.findViewById(R.id.rl_blur);
        btnBlur = v.findViewById(R.id.btn_clear_blur);

        llSave.setOnClickListener(this);
        llStart.setOnClickListener(this);
        llForward.setOnClickListener(this);
        llNext.setOnClickListener(this);
        btnBlur.setOnClickListener(this);

        dataAbandonDialog = new AlertDialog.Builder(context)
                .setTitle("确认")
                .setMessage("当前存在未保存数据，确定放弃当前数据？")
                .setNegativeButton("再看看", null)
                .setPositiveButton("放弃当前数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startCheck();
                    }
                })
                .create();

        return v;
    }

    /**
     * 重置计时器显示
     *
     * @param resid 背景色,为Null时，为占时保存状态不重置计时器
     * @param start 是否启动计时
     */
    public void chronometerReset(@Nullable Integer resid, Boolean start) {
        if (chronometer != null) {
            //背景色ID不为空时
            if (resid != null) {
                //此处重新获取颜色ID，否则会导致颜色偏差
                int colorId = getResources().getColor(resid);
                //将计时器设置 00：00
                chronometer.setBase(SystemClock.elapsedRealtime());
                //设置解释器的背景色
                chronometer.setBackgroundColor(colorId);
                //根据是否开始计时操作计时器
                if (start) {
                    chronometer.start();
                } else {
                    chronometer.stop();
                }
            } else {
                if (start) {
                    chronometer.start();
                } else {
                    chronometer.stop();
                }
            }

        }
    }

    /**
     * 更新参数操作显示区
     *
     * @param itemVO 调试项目描述文件
     */
    public void update(CheckItemVO itemVO) {

        //清空参数布局集合
        needCommunicate = false;
        bodyViews.clear();
        ll_body.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 11);
        //谱图收集参数集合
        Spectrum spec = itemVO.getSpectrum();
        if (spec != null) {
            LinearLayout.LayoutParams tvparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            StringBuffer sb = new StringBuffer();
            sb.append("谱图收集参数：");
            for (SpecParam specParam : spec.getSpecParams()) {
                sb.append(specParam.getParam()).append(" ");
            }
            sb.append(" 采集间隔：").append(spec.getInterval());

            TextView tv = new TextView(context);
            tv.setTextSize(15);
            tv.setText(sb);
            tv.setPadding(5, 5, 5, 5);

            ll_body.addView(tv, tvparams);
        }
        //参数操作布局view
        ItemOperateBodyView bodyView;
        for (CheckItemParamValueVO checkItemParamValueVO : itemVO.getParamNameList()) {
            //初始化参数操作布局
            bodyView = new ItemOperateBodyView(context, checkItemParamValueVO, itemVO.getId());
            if (checkItemParamValueVO.getPicReq()) {
                bodyView.setBaseCheckFunction(baseCheckFunction);
            }
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
            ivStart.setBackgroundResource(R.drawable.start_disable);
        }
        CheckItemVO temp = Globals.forwardCheckItem();
        if (temp == null) {
            tvForward.setText("无");
        } else {
            tvForward.setText(temp.getName());
            //还原回当前项目
            Globals.nextCheckItem();
        }
        temp = Globals.nextCheckItem();
        if (temp == null) {
            tvNext.setText("无");
        } else {
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
     * @param baseCheckFunction the base fun activity
     */
    public void setCameraActivity(@NonNull BaseCheckFunction baseCheckFunction) {
        this.baseCheckFunction = baseCheckFunction;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_check_ll_save:
                if (inBlur) {
                    Toast.makeText(context, "当前为处于调试模式，无法操作", Toast.LENGTH_SHORT).show();
                } else {
                    saveRecord(isChecking);
                }
                break;
            case R.id.item_check_ll_start:
                if (inBlur) {
                    Toast.makeText(context, "当前为处于调试模式，无法操作", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecking) {
                        stopCheck();
                    } else {
                        if (isBodyViewHasValue()) {
                            dataAbandonDialog.show();
                        } else {
                            startCheck();
                        }
                    }
                }

                break;
            case R.id.item_check_ll_forward:
                toForward();
                break;
            case R.id.item_check_ll_next:
                toNext();
                break;
            case R.id.btn_clear_blur:
                baseCheckFunction.clearBlur();
                break;

        }
    }


    /**
     * 改变blur区域
     */
    public void changeBlur() {
        if (inBlur) {
            rlBlur.setVisibility(GONE);
            inBlur = false;
        } else {
            rlBlur.setVisibility(VISIBLE);
            inBlur = true;
        }

        baseCheckFunction.onBlurChange(inBlur);
    }

    /**
     * 是否处于模糊模式
     *
     * @return inBlur
     */
    public boolean isInBlur() {
        return inBlur;
    }

    /**
     * 是否正在与测量终端 调试中
     *
     * @return boolean
     */
    public boolean isChecking() {
        return isChecking;
    }

    /**
     * 跳转到下一项目
     */
    private void toNext() {
        if (inBlur) {
            baseCheckFunction.toNext();
        } else {
            Toast.makeText(context, "当前正处于调试状态，无法进入下一项目", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到前一个项目
     */
    private void toForward() {
        if (inBlur) {
            baseCheckFunction.toForward();
        } else {
            Toast.makeText(context, "当前正处于调试状态，无法进入上一项目", Toast.LENGTH_SHORT).show();
        }
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
            //非调试状态下，点击保存，进行空值判断
            paramValueVOList.clear();
            for (ItemOperateBodyView bodyView : bodyViews) {
                if (bodyView.isValueEmpty()) {
                    //参数值获取方式 RealParam和Express 允许空值
                    //在判断合格时再赋值
                    if ("RealParam".equals(bodyView.getInfo().getValMode()) || "Express".equals(bodyView.getInfo().getValMode())) {
                        paramValueVOList.add(bodyView.getInfo());
                    } else {
                        Toast.makeText(context, bodyView.getInfo().getParamName() + "未检测到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    paramValueVOList.add(bodyView.getInfo());
                }
            }
            String paramValues = JsonUtils.toJson(paramValueVOList);

            baseCheckFunction.saveRecord(paramValues);

            //改变模糊状态
            changeBlur();

            //重置每个参数操作体ItemOperateBodyView
            resetBodyViews();

        }

    }

    /**
     * 重置参数操作体ItemOperateBodyView
     */
    public void resetBodyViews() {
        for (ItemOperateBodyView bodyView : bodyViews) {
            bodyView.reset();
        }
    }


    /**
     * 开始调试
     */
    private void startCheck() {
        //更新调试状态为正在调试
        updateCheckStatus(true, false);
        //回调baseFunActivity开始调试
        baseCheckFunction.startCheck();
    }

    /**
     * 当前调试项目是否存在数值，若存在提示是否保存
     *
     * @return
     */
    public boolean isBodyViewHasValue() {
        boolean hasValue = false;
        for (ItemOperateBodyView bodyView : bodyViews) {
            if (!bodyView.isParamEmpty()) {
                hasValue = true;
            }
        }
        return hasValue;
    }

    public void alertDataAbandon(String postiveMsg, DialogInterface.OnClickListener listener) {
        dataAbandonDialog.setButton(DialogInterface.BUTTON_POSITIVE, postiveMsg, listener);
        dataAbandonDialog.show();
    }


    /**
     * 停止调试
     */
    private void stopCheck() {
        //更新调试状态为未在调试
        updateCheckStatus(false, false);
        //回调baseFunActivity停止调试
        baseCheckFunction.stopCheck();
    }
}
