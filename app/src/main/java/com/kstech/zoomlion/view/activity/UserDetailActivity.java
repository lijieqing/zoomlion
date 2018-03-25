package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.databinding.ActivityUserDetailBinding;
import com.kstech.zoomlion.engine.server.UserDetailLoadTask;
import com.kstech.zoomlion.serverdata.UserInfo;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.view.widget.EditPasswordView;

import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 用户信息详情展示页
 */
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
     * data binding 对象
     */
    private ActivityUserDetailBinding binding;
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
    /**
     * 更新用户信息
     */
    public static final int UPDATE_USER_DETAIL = 4;
    /**
     * 密码修改失败
     */
    public static final int PASS_EDIT_ERROR = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail);

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

        //此方法是用来设置ImageView的MaxWidth和Height的，为TRUE时才有效
        binding.userDetailIvUserClear.setAdjustViewBounds(true);
        binding.userDetailIvUserClear.setMaxWidth(DeviceUtil.deviceWidth(this) / 5);
        binding.userDetailIvUserClear.setMaxHeight(DeviceUtil.deviceWidth(this) / 5);
        //用户信息加载
        new UserDetailLoadTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息对象
     */
    private void updateUser(UserInfo userInfo) {
        binding.setUser(userInfo);
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

        //组合效果 圆角和毛玻璃效果 设置背景图
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(new BlurTransformation(25),
                new RoundedCornersTransformation(5, 1));
        //默认背景图片
        RequestBuilder<Drawable> bgDefault = Glide.with(this)
                .asDrawable()
                .load(getBackgroundResid())
                .apply(RequestOptions.bitmapTransform(multi));
        //加载背景图片，联网失败加载默认图片
        Glide.with(this).load(userInfo.getPhoto())
                .error(bgDefault)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(binding.userDetailIvUser);
    }

    /**
     * 根据不同月份获取不同背景图资源ID
     *
     * @return 图片资源ID
     */
    private int getBackgroundResid() {
        int resId = R.drawable.bg_autum;
        int mouth = new Date().getMonth() + 1;
        switch (mouth) {
            case 12:
            case 1:
            case 2:
                resId = R.drawable.bg_winter;
                break;
            case 3:
            case 4:
            case 5:
                resId = R.drawable.bg_spring;
                break;
            case 6:
            case 7:
            case 8:
                resId = R.drawable.bg_summer;
                break;
            case 9:
            case 10:
            case 11:
                resId = R.drawable.bg_autum;
                break;
        }
        return resId;
    }

    final MyHandler handler = new MyHandler(this);

    private static class MyHandler extends BaseInnerHandler {
        MyHandler(UserDetailActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserDetailActivity activity = (UserDetailActivity) reference.get();
            if (activity!=null){
                switch (msg.what) {
                    case PASS_EDIT_START:
                        activity.passwordView.showProgress(true);
                        break;
                    case PASS_EDIT_SUCCESS:
                        activity.passwordView.updateView(true);
                        sendEmptyMessage(USER_RELOGIN);
                        break;
                    case PASS_EDIT_ERROR:
                        activity.passwordView.showProgress(false);
                        Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case PASS_EDIT_CANCEL:
                        activity.editPassDialog.cancel();
                        activity.passwordView.resetView();
                        break;
                    case PASS_EDIT_RELOGIN:
                        activity.editPassDialog.cancel();
                        activity.passwordView.resetView();
                        break;
                    case UPDATE_USER_DETAIL:
                        activity.updateUser((UserInfo) msg.obj);
                        break;
                }
            }
        }
    }
}
