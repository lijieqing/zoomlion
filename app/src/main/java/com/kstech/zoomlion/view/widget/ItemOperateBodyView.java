package com.kstech.zoomlion.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.ItemFunctionUtils;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

/**
 * Created by lijie on 2017/9/4.
 */

public class ItemOperateBodyView extends RelativeLayout {
    private BaseFunActivity baseFunActivity;
    private TextView tvParamName;//参数名称
    private TextView tvCamera;//是否拍照描述
    private TextView tvChart;//是否获取谱图描述
    private TextView tvValue;//似乎否需要数值描述
    private TextView tvOperate;//参数操作描述
    private ImageView ivCamera;//拍照图片按钮
    private ImageView ivHandWriting;//手写图片按钮
    private RadioGroup radioGroup;//合格不合格操作按钮
    private CheckItemParamValueVO checkItemParamValueVO;
    private String qcID;
    boolean isDialog = false;//是否与测量终端通讯
    boolean isHandwriting = false;//是否手动输入参数值
    boolean isnovalue = false;//是否为无数值参数

    private AlertDialog handwritingDialog;
    EditText et;

    public ItemOperateBodyView(BaseFunActivity context, CheckItemParamValueVO checkItemParamValueVO, String qcID) {
        super(context);
        this.baseFunActivity = context;
        this.checkItemParamValueVO = checkItemParamValueVO;
        this.qcID = qcID;

        this.addView(initView());
    }

    public ItemOperateBodyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.addView(initView());
    }

    public ItemOperateBodyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.addView(initView());
    }

    private View initView() {
        View v = View.inflate(baseFunActivity, R.layout.check_item_operate_body, null);
        tvParamName = v.findViewById(R.id.tv_name);
        tvCamera = v.findViewById(R.id.tv_camera);
        tvChart = v.findViewById(R.id.tv_chart);
        tvValue = v.findViewById(R.id.tv_value);
        tvOperate = v.findViewById(R.id.tv_operate);
        ivCamera = v.findViewById(R.id.iv_camera);
        ivHandWriting = v.findViewById(R.id.iv_handwriting);
        radioGroup = v.findViewById(R.id.rg_pass);

        isDialog = ItemFunctionUtils.isDialogParam(checkItemParamValueVO.getParamName(), qcID);
        isHandwriting = ItemFunctionUtils.isHandWritingParam(checkItemParamValueVO.getParamName(), qcID);
        isnovalue = ItemFunctionUtils.isNoValueParam(checkItemParamValueVO.getParamName(), qcID);

        //初始化参数功能信息
        paramFunInit();

        return v;
    }


    private void paramFunInit() {
        tvParamName.setText(checkItemParamValueVO.getParamName());
        if (isHandwriting) {
            tvValue.setText("手动输入");
            ivHandWriting.setVisibility(VISIBLE);
            tvOperate.setText("手动输入数值");
        } else if (isnovalue) {
            tvValue.setText("无需数值");
            radioGroup.setVisibility(VISIBLE);
            tvOperate.setVisibility(GONE);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    switch (i) {
                        case R.id.rb_pass:
                            Toast.makeText(baseFunActivity, "合格", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.rb_unpass:
                            Toast.makeText(baseFunActivity, "不合格", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

        } else {
            isDialog = true;
            tvValue.setText("测量终端获取");
            tvOperate.setText("测量终端获取数值");
        }

        if (ItemFunctionUtils.isCollectParam(checkItemParamValueVO.getParamName(), Integer.parseInt(qcID))) {
            if (isDialog) {
                tvChart.setText("谱图采集");
            } else {
                tvChart.setText("非通讯参数");
            }
        } else {
            tvChart.setText("无需");
        }

        if (ItemFunctionUtils.isPICParam(checkItemParamValueVO.getParamName(), Integer.parseInt(qcID))) {
            tvCamera.setText("拍照采集");
            ivCamera.setVisibility(VISIBLE);
        } else {
            tvCamera.setText("无需");
        }

        ivCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseFunActivity.camera(checkItemParamValueVO, ItemOperateBodyView.this);
            }
        });

        ivHandWriting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                et = new EditText(baseFunActivity);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                handwritingDialog = new AlertDialog.Builder(baseFunActivity)
                        .setTitle("输入")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value = et.getText().toString();
                                if (!TextUtils.isEmpty(value) && !value.trim().equals("")) {
                                    Toast.makeText(baseFunActivity, value, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setView(et)
                        .create();
                handwritingDialog.show();
            }
        });
    }

    public void updateCameraInfo() {

    }

}
