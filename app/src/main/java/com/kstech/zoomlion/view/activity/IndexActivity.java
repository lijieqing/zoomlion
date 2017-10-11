package com.kstech.zoomlion.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.manager.XMLAPI;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.Globals;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * 应用引导界面，包含了个人信息编辑入口，调试终端的信息展示，参数初始化模块
 * <p>
 * 在此界面1939通讯线程、协议线程和其他通讯线程都应启动完成
 * <p>
 * 未加载调试机型时使用软件内置的机型文件进行启动通讯线程，加载机型后重新启动线程
 *
 * @author lijie
 */
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

    @ViewInject(R.id.index_iv_user)
    private ImageView ivUser;//用户图标

    @ViewInject(R.id.index_tv_terminal_name)
    private TextView tvTerminalName;//调试终端名称

    @ViewInject(R.id.index_tv_terminal_ip)
    private TextView tvTerminalIP;//调试终端IP

    @ViewInject(R.id.index_tv_device_identity)
    private TextView tvDeviceIdentity;//待调试车辆编码

    @ViewInject(R.id.index_btn_auto_download)
    private Button btnAutoDownLoadDevice;//自动获取编码和机型按钮

    @ViewInject(R.id.index_btn_choose_from_server)
    private Button btnChooseDeviceFromServer;//自动获取编码和机型按钮

    @ViewInject(R.id.index_tv_device_born)
    private TextView tvDeviceBornDate;//设备出厂日期

    @ViewInject(R.id.index_tv_device_secondcheck)
    private TextView tvDevSecondCheck;//设备是否第二次调试

    @ViewInject(R.id.index_tv_device_itemcounts)
    private TextView tvItemCounts;//总调试项目数量

    @ViewInject(R.id.index_tv_item_checkedcounts)
    private TextView tvItemCheckedCounts;//已调试项目数量

    @ViewInject(R.id.index_tv_item_uncheckedcounts)
    private TextView tvItemUncheckedCounts;//未调试项目数量

    @ViewInject(R.id.index_tv_item_lastcheck)
    private TextView tvItemLastChecked;//上次调试项目

    @ViewInject(R.id.index_tv_record_result)
    private TextView tvRecordResult;//整机调试结论

    @ViewInject(R.id.index_btn_exit)
    private Button btnExit;//退出按钮

    @ViewInject(R.id.index_btn_goto)
    private Button btnGotoCheck;//进入调试界面

    @ViewInject(R.id.index_lv_initparams)
    private ListView lvInitParams;//初始化参数列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //String data = String.format(getString(R.string.index_welcome_user),Globals.currentUser.getName(),Globals.currentUser.getLast_login_time());
        String data = String.format(getString(R.string.index_welcome_user), "测试", new Date());
        tvUserWelcome.setText(data);
        tvTerminalName.setTextColor(Color.DKGRAY);
        tvDeviceIdentity.setTextColor(Color.RED);
        tvDeviceIdentity.setText("当前未检测到整机编号");

        // TODO: 2017/10/11 加载默认机型信息，配置并启动通讯线程
        new Thread() {
            @Override
            public void run() {
                try {
                    Globals.modelFile = DeviceModelFile.readFromFile((Device) XMLAPI.readXML(getAssets().open("new.xml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Event(value = {R.id.index_btn_goto, R.id.index_btn_exit})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_btn_goto:
                if (isAvalid()) {
                    Intent intent = new Intent(this, CheckHomeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.index_btn_exit:
                finish();
                break;
        }
    }

    private boolean isAvalid() {
        // TODO: 2017/10/11 此处机型逻辑判断
        //判断当前页面是否符合条件跳入调试界面
        return true;
    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        private final WeakReference<IndexActivity> activityReference;

        private InnerHandler(IndexActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            IndexActivity mActivity = activityReference.get();
            if (mActivity != null) {
                Toast.makeText(mActivity, "机型读取完成", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
