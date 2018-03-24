package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.activity.BaseActivity;
import com.kstech.zoomlion.view.activity.UserDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 密码修改组件
 * Created by lijie on 2017/11/24.
 */
public class EditPasswordView extends RelativeLayout {
    /**
     * 原始密码
     */
    private EditText etOldPass;
    /**
     * 新密码
     */
    private EditText etNewPass;
    /**
     * 再次确认新密码
     */
    private EditText etAgainNewPass;

    /**
     * 提交修改
     */
    private Button btnSubmit;
    /**
     * 取消修改
     */
    private Button btnCancel;
    /**
     * 是否显示密码
     */
    private CheckBox cb;
    /**
     * 密码修改操作布局
     */
    private LinearLayout llOP;
    /**
     * 重新登录按钮
     */
    private Button btnReLogin;
    /**
     * 密码修改结果展示布局
     */
    private LinearLayout llResult;
    /**
     * 密码修改结果提示信息
     */
    private TextView tvResult;
    /**
     * 密码修改过程进度条
     */
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

    /**
     * 初始化布局
     *
     * @param context 上下文对象
     */
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
        //提交修改
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = etOldPass.getText().toString().trim();
                newPass = etNewPass.getText().toString().trim();
                newPassAgain = etAgainNewPass.getText().toString().trim();
                //判断是否满足基本条件，提交修改
                if (isAvailable()) {
                    ThreadManager.getThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_START);
                            RequestParams params = new RequestParams(URLCollections.CHANGE_PASSWORD);
                            params.addHeader("Cookie", Globals.SID);
                            params.addBodyParameter("originPassword", oldPass);
                            params.addBodyParameter("newPassword", newPass);
                            String result = "";
                            try {
                                result = x.http().postSync(params, String.class);
                                JSONObject object = new JSONObject(result);
                                if (object.has("success")) {
                                    //用户退出登录
                                    params = new RequestParams(URLCollections.USER_LOGOUT);
                                    params.addHeader("Cookie", Globals.SID);
                                    String re = x.http().getSync(params, String.class);
                                    if (re != null && re.contains("success")) {
                                        handler.sendEmptyMessage(UserDetailActivity.PASS_EDIT_SUCCESS);
                                    }
                                }
                                if (object.has("error")) {
                                    Message message = Message.obtain();
                                    message.what = UserDetailActivity.PASS_EDIT_ERROR;
                                    message.obj = object.getString("error");
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                if (URLCollections.isReLogin(result)) {
                                    handler.sendEmptyMessage(BaseActivity.USER_RELOGIN);
                                } else {
                                    Message message = Message.obtain();
                                    message.what = UserDetailActivity.PASS_EDIT_ERROR;
                                    message.obj = "服务器异常";
                                    handler.sendMessage(message);
                                }
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                Message message = Message.obtain();
                                message.what = UserDetailActivity.PASS_EDIT_ERROR;
                                message.obj = throwable.getMessage();
                                handler.sendMessage(message);
                            }
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

    /**
     * 设置密码是否为明文
     *
     * @param see 是否明文
     */
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

    /**
     * 判断是否满足标准，不能为空，新密码必须一致
     *
     * @return 是否可以
     */
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

    /**
     * 根据不同结论展示不同布局
     *
     * @param success 是否成功
     */
    public void updateView(boolean success) {
        if (success) {
            llOP.setVisibility(GONE);
            llProgress.setVisibility(GONE);
            llResult.setVisibility(VISIBLE);
        }
    }

    /**
     * 更新进度条展示
     *
     * @param show 是否展示进度条
     */
    public void showProgress(boolean show) {
        if (show) {
            llOP.setVisibility(GONE);
            llResult.setVisibility(GONE);
            llProgress.setVisibility(VISIBLE);
        } else {
            llOP.setVisibility(VISIBLE);
            llResult.setVisibility(GONE);
            llProgress.setVisibility(GONE);
        }
    }

    /**
     * 布局刷新
     */
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
