package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kstech.zoomlion.utils.PermissionUtils;
import com.kstech.zoomlion.view.widget.TextProgressView;

import java.lang.ref.WeakReference;

/**
 * Created by lijie on 2017/9/7.
 * 基础activity，统一实现沉浸式状态栏
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * 开始调试弹窗
     */
    protected AlertDialog dialog;
    /**
     * 进度展示组件，与dialog结合使用
     */
    protected TextProgressView progressView;
    /**
     * 弹出弹窗
     */
    public static final int DIALOG_SHOW = 999;
    /**
     * 关闭弹窗
     */
    public static final int DIALOG_CANCEL = 998;
    /**
     * 更新进度条内容
     */
    public static final int UPDATE_PROGRESS_CONTENT = 997;
    /**
     * 重新登录
     */
    public static final int USER_RELOGIN = 996;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏 sdk 21以上
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //设置始终横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) actionbar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //初始化文字显示进度窗口
        dialog = new AlertDialog.Builder(this).setCancelable(false).create();
        progressView = new TextProgressView(this);
        dialog.setView(progressView);
    }

    /**
     * edit text 监听事件设置 点击完成后回收键盘并且取消焦点
     *
     * @param et
     * @param activity
     */
    protected void editTextInit(@NonNull final EditText et, @NonNull final Activity activity) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }
                    et.clearFocus();
                }
                return false;
            }
        });
    }

    protected void initExternalPermission() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
    }

    /**
     * 权限授权成功监听事件
     */
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    PermissionUtils.requestPermission(BaseActivity.this, PermissionUtils.CODE_CAMERA, this);
                    //Toast.makeText(BaseActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    /**
     * 重新登录
     */
    protected void relogDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("重新登录")
                .setCancelable(false)
                .setMessage("用户凭证已过时，请重新登录")
                .setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        finish();
                    }
                }).create();
        dialog.show();
    }

    /**
     * handler Base类，通过弱引用来避免内存泄露,并实现一些基本信息的处理
     */
    protected static class BaseInnerHandler extends Handler {
        WeakReference<BaseActivity> reference;

        BaseInnerHandler(BaseActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case DIALOG_SHOW:
                        activity.progressView.reset();
                        activity.dialog.show();
                        break;
                    case DIALOG_CANCEL:
                        activity.dialog.cancel();
                        break;
                    case UPDATE_PROGRESS_CONTENT:
                        if (msg.obj != null && msg.arg1 > 0) {
                            activity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        }
                        break;
                    case USER_RELOGIN:
                        activity.relogDialog();
                        break;
                }
            }

        }
    }

}
