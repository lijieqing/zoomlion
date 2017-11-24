package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.activity.UserDetailActivity;

/**
 * Created by lijie on 2017/11/24.
 */

public class EditPasswordView extends RelativeLayout {
    private EditText etOldPass;
    private EditText etNewPass;
    private EditText etAgainNewPass;

    private Button btnSubmit;
    private Button btnCancel;
    private CheckBox cb;
    private LinearLayout llOP;

    private Button btnReLogin;
    private LinearLayout llResult;
    private TextView tvResult;

    private LinearLayout llProgress;

    private String oldPass;
    private String newPass;
    private String newPassAgain;

    private Handler handler;

    public EditPasswordView(Context context, Handler handler) {
        super(context);
        initView(context);
        this.handler = handler;
    }

    private void initView(final Context context) {
        View view = View.inflate(context, R.layout.user_detail_edit_password, null);
        etOldPass = view.findViewById(R.id.user_detail_et_old_pass);
        etNewPass = view.findViewById(R.id.user_detail_et_new_pass);
        etAgainNewPass = view.findViewById(R.id.user_detail_et_new_again);
        btnSubmit = view.findViewById(R.id.user_detail_btn_submit);
        btnCancel = view.findViewById(R.id.user_detail_btn_cancel);
        btnReLogin = view.findViewById(R.id.user_detail_btn_relog);
        cb = view.findViewById(R.id.user_detail_cb);

        llOP = view.findViewById(R.id.user_detail_ll_op);
        llProgress = view.findViewById(R.id.user_detail_ll_progress);
        llResult = view.findViewById(R.id.user_detail_ll_result);
        tvResult = view.findViewById(R.id.user_detail_tv_result);

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = etOldPass.getText().toString().trim();
                newPass = etNewPass.getText().toString().trim();
                newPassAgain = etAgainNewPass.getText().toString().trim();

                if (isAvailable()) {
                    ThreadManager.getThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_START);
                            SystemClock.sleep(3000);
                            handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_SUCCESS);
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_CANCEL);
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seePass(isChecked);
            }
        });

        btnReLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_RELOGIN);
            }
        });

        view.setMinimumWidth(DeviceUtil.deviceWidth(context));

        this.addView(view);
    }

    private void seePass(boolean see) {
        if (see) {
            etOldPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etAgainNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            etOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etAgainNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private boolean isAvailable() {

        if (TextUtils.isEmpty(oldPass)) {
            etOldPass.setError("原始密码不能为空");
            return false;
        }

        if (TextUtils.isEmpty(newPass)) {
            etNewPass.setError("密码不能为空");
            return false;
        }

        if (TextUtils.isEmpty(newPassAgain)) {
            etAgainNewPass.setError("密码不能为空");
            return false;
        }

        if (!TextUtils.equals(newPass, newPassAgain)) {
            etNewPass.setError("密码不一致");
            etAgainNewPass.setError("密码不一致");
            return false;
        }
        return true;
    }

    public void updateView(boolean success) {
        if (success) {
            llOP.setVisibility(GONE);
            llProgress.setVisibility(GONE);
            llResult.setVisibility(VISIBLE);
        }
    }

    public void showProgress() {
        llOP.setVisibility(GONE);
        llResult.setVisibility(GONE);
        llProgress.setVisibility(VISIBLE);
    }

    public void resetView() {
        etOldPass.setError(null);
        etOldPass.clearFocus();
        etOldPass.setText("");

        etNewPass.setError(null);
        etNewPass.clearFocus();
        etNewPass.setText("");

        etAgainNewPass.setError(null);
        etAgainNewPass.clearFocus();
        etAgainNewPass.setText("");

        seePass(false);
        cb.setChecked(false);

        llOP.setVisibility(VISIBLE);
        llResult.setVisibility(GONE);
        llProgress.setVisibility(GONE);
    }
}
