package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.bumptech.glide.Glide;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.databinding.ActivityUserDetailBinding;
import com.kstech.zoomlion.model.session.UserInfo;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.view.widget.EditPasswordView;

import java.lang.ref.WeakReference;

public class UserDetailActivity extends BaseActivity {

    private AlertDialog dialog;
    private EditPasswordView passwordView;
    public static final int PASS_EDIT_START = 0;
    public static final int PASS_EDIT_SUCCESS = 1;
    public static final int PASS_EDIT_CANCEL = 2;
    public static final int PASS_EDIT_RELOGIN = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("test");
        userInfo.setUsername("test_user");
        userInfo.setBirthday(DateUtil.getDateFormat(1993, 3, 1));
        userInfo.setPhoneNumber("258888");
        userInfo.setCellPhoneNumber("1585258888");
        userInfo.setSex(true);
        userInfo.setNation("汉族");
        userInfo.setMarried(false);
        userInfo.setAddress("北京");
        userInfo.setEmail("1111@example.com");
        userInfo.setEducation("本科");
        userInfo.setGraduatedFrom("野鸡大学");
        userInfo.setPhoto("http://p1.gexing.com/G1/M00/E0/34/rBACE1P0wgvxCpKsAAAgSJPMhkA965_200x200_3.jpg?recache=20131108");
        binding.setUser(userInfo);

        Glide.with(this).load(userInfo.getPhoto()).thumbnail(0.5f).into(binding.userDetailIvUser);

        passwordView = new EditPasswordView(this, handler);

        dialog = new AlertDialog.Builder(UserDetailActivity.this)
                .setView(passwordView)
                .setCancelable(false)
                .create();

        binding.userDetailLlPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    final MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<UserDetailActivity> reference;

        public MyHandler(UserDetailActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserDetailActivity activity = reference.get();
            switch (msg.what) {
                case PASS_EDIT_START:
                    activity.passwordView.showProgress();
                    break;
                case PASS_EDIT_SUCCESS:
                    activity.passwordView.updateView(true);
                    break;
                case PASS_EDIT_CANCEL:
                    activity.dialog.cancel();
                    activity.passwordView.resetView();
                    break;
                case PASS_EDIT_RELOGIN:
                    activity.dialog.cancel();
                    activity.passwordView.resetView();
                    break;
            }
        }
    }
}
