package com.kstech.zoomlion.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.ItemFunctionUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

import java.util.Date;

/**
 * Created by lijie on 2017/9/4.
 * <p>
 * 参数操作布局
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
    private CheckItemParamValueVO checkItemParamValueVO;//调试项目参数vo描述类
    private String qcID;//调试项目用于通讯的ID
    private AlertDialog handwritingDialog;//手写
    private boolean picSaved = false;
    EditText et;


    /**
     * Instantiates a new Item operate body view.
     *
     * @param context               the context
     * @param checkItemParamValueVO the check item param value vo
     * @param qcID                  the qc id
     */
    public ItemOperateBodyView(BaseFunActivity context, CheckItemParamValueVO checkItemParamValueVO, String qcID) {
        super(context);
        this.baseFunActivity = context;
        this.checkItemParamValueVO = new CheckItemParamValueVO(checkItemParamValueVO);
        this.qcID = qcID;

        this.addView(initView());
    }

    /**
     * Instantiates a new Item operate body view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ItemOperateBodyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.addView(initView());
    }

    /**
     * Instantiates a new Item operate body view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ItemOperateBodyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.addView(initView());
    }

    /**
     * 初始化布局 初始化参数功能信息
     *
     * @return
     */
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

        //初始化参数功能信息
        paramFunInit();

        return v;
    }


    /**
     * 根据测量终端状态调整布局
     */
    private void paramFunInit() {
        tvParamName.setText(checkItemParamValueVO.getParamName());

        if (checkItemParamValueVO.getValueReq()) {//判断是否需要数值
            String valMode = checkItemParamValueVO.getValMode();
            switch (valMode) {
                case "Auto":
                    tvValue.setText("测量终端获取");
                    tvOperate.setText("测量终端获取数值");
                    break;

                case "Mann":
                    tvValue.setText("手动输入");
                    ivHandWriting.setVisibility(VISIBLE);
                    tvOperate.setText("手动输入数值");
                    break;

                case "RealParam":
                    tvValue.setText("实时参数数据");
                    tvOperate.setText("自动采集");
                    break;

                case "Express":
                    tvValue.setText("表达式计算");
                    tvOperate.setText(checkItemParamValueVO.getValidAvg());
                    break;

            }

            if (ItemFunctionUtils.isSpectrumParam(checkItemParamValueVO.getParamName(), Integer.parseInt(qcID))) {
                tvChart.setText("需要谱图");
            } else {
                tvChart.setText("无需谱图");
            }
        } else {
            tvValue.setText("无需数值");
            radioGroup.setVisibility(VISIBLE);
            tvOperate.setVisibility(GONE);
            tvChart.setText("无需谱图");

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    switch (i) {
                        case R.id.rb_pass:
                            LogUtils.e("ItemOperateView", "in listener pass:" + checkItemParamValueVO.getValue() + " id:" + i);
                            // TODO: 2017/10/18 判断是否需要图片，需要图片时，在给出合格不合格时，应该判断是否已保存图片
                            if (checkItemParamValueVO.getPicReq() && !picSaved) {
                                Toast.makeText(baseFunActivity, "请先保存图片数据", Toast.LENGTH_SHORT).show();
                                radioGroup.clearCheck();
                            } else {
                                if (radioGroup.getCheckedRadioButtonId() == i) {
                                    checkItemParamValueVO.setValue("合格");
                                }
                            }
                            break;
                        case R.id.rb_unpass:
                            LogUtils.e("ItemOperateView", "in listener unpass:" + checkItemParamValueVO.getValue() + " id:" + i);
                            if (checkItemParamValueVO.getPicReq() && !picSaved) {
                                Toast.makeText(baseFunActivity, "请先保存图片数据", Toast.LENGTH_SHORT).show();
                                radioGroup.clearCheck();
                            } else {
                                if (radioGroup.getCheckedRadioButtonId() == i) {
                                    checkItemParamValueVO.setValue("不合格");
                                }
                            }
                            break;
                    }
                }
            });
        }

        if (checkItemParamValueVO.getPicReq()) {//是否需要图片
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
                                    tvValue.setText(value);
                                    checkItemParamValueVO.setValue(value);
                                }
                            }
                        })
                        .setView(et)
                        .create();
                handwritingDialog.show();
            }
        });
    }

    /**
     * 点击保存时 调用更新展示布局
     */
    public void updateCameraInfo(CheckImageData imgData) {
        if (imgData != null) {
            picSaved = true;
            String date = DateUtil.getDateTimeFormat14(new Date());
            tvCamera.setText(date);
            baseFunActivity.cameraCancel();
        }
    }

    /**
     * 更新数值展示布局，并存入checkItemParamValueVO
     *
     * @param value
     */
    public void updateValueInfo(@NonNull String value) {
        tvValue.setText(value);
        checkItemParamValueVO.setValue(value);
    }

    public boolean isValueEmpty() {
        return checkItemParamValueVO.getValue().equals("");
    }

    public CheckItemParamValueVO getInfo() {
        return checkItemParamValueVO;
    }

    public void reset() {
        //此方法每次会调用被选中按钮的OnCheckedChangeListener方法，因此会给checkItemParamValueVO设置数据
        radioGroup.clearCheck();
        paramFunInit();
        //将vo类的value值
        checkItemParamValueVO.setValue("");
        //将图片保存状态设置为未保存
        picSaved = false;
    }

}
