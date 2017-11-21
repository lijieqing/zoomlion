package com.kstech.zoomlion.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.databinding.ActivityUserDetailBinding;
import com.kstech.zoomlion.model.session.UserInfo;

public class UserDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserDetailBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_user_detail);
        UserInfo userInfo = new UserInfo();
        binding.setUser(userInfo);
    }
}
