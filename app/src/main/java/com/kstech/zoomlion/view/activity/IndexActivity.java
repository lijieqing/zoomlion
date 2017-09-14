package com.kstech.zoomlion.view.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.Globals;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_index)
public class IndexActivity extends BaseActivity {

    @ViewInject(R.id.index_iv_server_status)
    private ImageView ivServerStatus;//服务器状态图

    @ViewInject(R.id.index_iv_terminal_status)
    private ImageView ivTerminalStatus;//测量终端状态图

    @ViewInject(R.id.index_tv_server_status)
    private TextView tvServerStatus;//服务器状态提示信息

    @ViewInject(R.id.index_tv_terminal_status)
    private TextView tvTerminalStatus;//测量终端状态提示信息

    @ViewInject(R.id.index_tv_welcome)
    private TextView tvUserWelcome;//用户欢迎语展示


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        String data = String.format(getString(R.string.index_welcome_user),Globals.currentUser.getName(),Globals.currentUser.getLast_login_time());
        tvUserWelcome.setText(data);
    }
}
