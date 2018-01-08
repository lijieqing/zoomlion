package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.databinding.ActivityUserDetailBinding;
import com.kstech.zoomlion.model.session.UserInfo;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.view.widget.EditPasswordView;

import java.lang.ref.WeakReference;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UserDetailActivity extends BaseActivity {
    /**
     * 编辑密码时弹出的对话框
     */
    private AlertDialog editPassDialog;
    /**
     * 密码编辑view
     */
    private EditPasswordView passwordView;
    /**
     * 密码修改提交开始
     */
    public static final int PASS_EDIT_START = 0;
    /**
     * 密码修改提交成功
     */
    public static final int PASS_EDIT_SUCCESS = 1;
    /**
     * 密码修改提交开取消
     */
    public static final int PASS_EDIT_CANCEL = 2;
    /**
     * 重新登录 跳转到登录界面
     */
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
        userInfo.setSex("男");
        userInfo.setNation("汉族");
        userInfo.setMarried(false);
        userInfo.setAddress("北京");
        userInfo.setEmail("1111@example.com");
        userInfo.setEducation("本科");
        userInfo.setGraduatedFrom("某某大学");
        userInfo.setPhoto("http://p1.gexing.com/G1/M00/E0/34/rBACE1P0wgvxCpKsAAAgSJPMhkA965_200x200_3.jpg?recache=20131108");
        binding.setUser(userInfo);

        //组合效果 圆角和毛玻璃效果 设置背景图
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(new BlurTransformation(25),
                new RoundedCornersTransformation(5, 1));
        //默认背景图片
        RequestBuilder<Drawable> bgDefault = Glide.with(this)
                .asDrawable()
                .load(R.drawable.pic_header_default)
                .apply(RequestOptions.bitmapTransform(multi));
        //加载背景图片，联网失败加载默认图片
        Glide.with(this).load(userInfo.getPhoto())
                .error(bgDefault)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(binding.userDetailIvUser);
        //此方法是用来设置ImageView的MaxWidth和Height的，为TRUE时才有效
        binding.userDetailIvUserClear.setAdjustViewBounds(true);
        binding.userDetailIvUserClear.setMaxWidth(DeviceUtil.deviceWidth(this) / 5);
        binding.userDetailIvUserClear.setMaxHeight(DeviceUtil.deviceWidth(this) / 5);
        //默认头像图片
        RequestBuilder<Drawable> headerDefault = Glide.with(this)
                .asDrawable()
                .load(R.drawable.pic_header_default)
                .apply(RequestOptions.circleCropTransform());
        //加载头像图片，联网失败加载默认头像
        Glide.with(this).load(userInfo.getPhoto())
                .error(headerDefault)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userDetailIvUserClear);

        passwordView = new EditPasswordView(this, handler);

        editPassDialog = new AlertDialog.Builder(UserDetailActivity.this)
                .setView(passwordView)
                .setCancelable(false)
                .create();

        binding.userDetailLlPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassDialog.show();
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
                    activity.editPassDialog.cancel();
                    activity.passwordView.resetView();
                    break;
                case PASS_EDIT_RELOGIN:
                    activity.editPassDialog.cancel();
                    activity.passwordView.resetView();
                    break;
            }
        }
    }
}
