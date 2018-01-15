package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.comm.J1939TaskService;
import com.kstech.zoomlion.engine.device.DeviceLoadTask;
import com.kstech.zoomlion.engine.server.DeviceStatusUpdateTask;
import com.kstech.zoomlion.engine.server.ServerProcessCheck;
import com.kstech.zoomlion.engine.server.UserLogoutTask;
import com.kstech.zoomlion.model.treelist.Element;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;
import com.kstech.zoomlion.model.treelist.TreeViewItemClickListener;
import com.kstech.zoomlion.serverdata.CommissioningStatistics;
import com.kstech.zoomlion.serverdata.CommissioningStatusEnum;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;

import J1939.J1939_DataVar_ts;

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
public class IndexActivity extends BaseActivity implements J1939_DataVar_ts.RealtimeChangeListener {

    @ViewInject(R.id.index_ll)
    private LinearLayout root;

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

    @ViewInject(R.id.index_tv_preheat)
    private TextView tvPreHeat;//整机预热时间

    @ViewInject(R.id.index_btn_exit)
    private Button btnExit;//退出按钮

    @ViewInject(R.id.index_btn_goto)
    private Button btnGotoCheck;//进入调试界面

    @ViewInject(R.id.index_lv_initparams)
    private ListView lvInitParams;//初始化参数列表

    private long checkerID;//调试员ID
    /**
     * 机型配置完成解析
     */
    public static final int DEVICE_PARSE_FINISH = 0;
    /**
     * 进入调试主界面
     */
    public static final int SKIP_TO_CHECK = 1;
    /**
     * 服务器机型列表初始化
     */
    public static final int DEV_LIST_INIT = 2;
    /**
     * 服务器机型列表初始化 完成
     */
    public static final int DEV_LIST_LOADED = 3;
    /**
     * 1939通讯线程重置
     */
    public static final int J1939_SERVICE_RESET = 4;
    /**
     * 用户登出
     */
    public static final int USER_LOGOUT = 5;
    /**
     * 更新整机状态信息
     */
    public static final int UPDATE_DEVICE_INFO = 6;

    public static final String TAG = "IndexActivity";

    //通讯线程相关变量
    private DeviceLoadTask deviceLoadTask;
    private J1939TaskService j1939TaskService;

    //机型分类列表相关变量
    private ArrayList<Element> rootElements;
    private ArrayList<Element> allElements;
    private ListView devListView;
    private AlertDialog devListDialog;
    private TreeViewAdapter adapter;
    private CommissioningStatistics deviceStatus;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(TAG, "service connect");
            J1939TaskService.MyBinder binder = (J1939TaskService.MyBinder) service;
            j1939TaskService = binder.task;
            deviceLoadTask.isWaitting = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //String data = String.format(getString(R.string.index_welcome_user),Globals.currentUser.getName(),Globals.currentUser.getLast_login_time());
        String data = String.format(getString(R.string.index_welcome_user), Globals.currentUser.getName(), Globals.currentUser.getLastLoginTime() == null ? new Date() : Globals.currentUser.getLastLoginTime());
        tvUserWelcome.setText(data);

        tvTerminalName.setTextColor(Color.DKGRAY);
        tvTerminalName.setText(Globals.currentTerminal.getName());
        tvTerminalIP.setText(Globals.currentTerminal.getIp());

        tvDeviceIdentity.setTextColor(Color.RED);
        tvDeviceIdentity.setText("当前未检测到整机编号");

        // TODO: 2017/10/11 加载默认机型信息，配置并启动通讯线程
        deviceLoadTask = new DeviceLoadTask(null, handler);
        deviceLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        //初始化机型选择列表 相关数据
        devListView = new ListView(this);
        rootElements = new ArrayList<>();
        allElements = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new TreeViewAdapter(rootElements, allElements, inflater);
        adapter.setContentSize(16);
        adapter.setDefaultHeight(DeviceUtil.deviceHeight(this) / 9);
        devListView.setAdapter(adapter);
        devListView.setOnItemClickListener(new TreeViewItemClickListener(adapter, this));
        devListDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(devListView)
                .create();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消 预热时间 数据监听器
        Globals.modelFile.getDataSetVO().getDSItem("预热时间").removeListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //恢复 预热时间 数据监听器
        Globals.modelFile.getDataSetVO().getDSItem("预热时间").addListener(this);
        if (Globals.deviceSN != null) {
            new DeviceStatusUpdateTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent j1939Intent = new Intent(J1939TaskService.ACTION);
        j1939Intent.setPackage(getPackageName());
        Globals.PROCESSID = null;
        if (j1939TaskService != null) {
            j1939TaskService.stopJ1939Service();
            unbindService(conn);
            stopService(j1939Intent);
        }
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(root, "请点击退出按钮", Snackbar.LENGTH_SHORT).show();
    }

    @Event(value = {R.id.index_iv_user, R.id.index_btn_goto,
            R.id.index_btn_exit, R.id.index_btn_auto_download,
            R.id.index_btn_view_record})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_iv_user:
                startActivity(new Intent(this, UserDetailActivity.class));
                break;
            case R.id.index_btn_goto:
                ServerProcessCheck spc = new ServerProcessCheck(handler);
                spc.setDeviceStatus(deviceStatus);
                spc.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            case R.id.index_btn_exit:
                new UserLogoutTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            case R.id.index_btn_auto_download:
                Globals.deviceSN = "016302A0170008";
                // TODO: 2018/1/5 根据整机编码获取机型信息，此处模拟已经获取到整机编码
                deviceLoadTask = new DeviceLoadTask(Globals.deviceSN, handler);
                deviceLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            case R.id.index_btn_view_record:
                Intent intent = new Intent(this, ViewRecordActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 更新整机状态信息
     */
    private void updateDeviceInfo() {
        //设置出厂日期
        String deviceDate = TextUtils.isEmpty(Globals.modelFile.device.getDevBornDate()) ? "未设置出厂日期" : Globals.modelFile.device.getDevBornDate();
        tvDeviceBornDate.setText(deviceDate);
        //设置是否二次调试
        tvDevSecondCheck.setText(deviceStatus.getCheckNo() == 0 ? "否" : "是");
        //设置总调试次数
        tvItemCounts.setText(String.valueOf(deviceStatus.getAmount()));
        //设置已调试次数
        tvItemCheckedCounts.setText(String.valueOf(deviceStatus.getCompleteNumber()));
        //设置已调未完成次数
        tvItemUncheckedCounts.setText(String.valueOf(deviceStatus.getDoingNumber()));
        //最近调试项目
        tvItemLastChecked.setText(TextUtils.isEmpty(deviceStatus.getLastQcitemName()) ? "无" : deviceStatus.getLastQcitemName());
        //整机调试结论
        tvRecordResult.setText(CommissioningStatusEnum.nameOf(deviceStatus.getStatus()).getName());
    }

    private InnerHandler handler = new InnerHandler(this);

    @Override
    public void onDataChanged(short dsItemPosition, float value) {
        // TODO: 2017/12/7 此处处理value 转换为时间格式并显示到 tvPreHeat组件
    }

    private static class InnerHandler extends BaseInnerHandler {

        private InnerHandler(IndexActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final IndexActivity mActivity = (IndexActivity) reference.get();
            if (mActivity != null) {
                switch (msg.what) {
                    case J1939_SERVICE_RESET:
                        mActivity.progressView.reset();
                        mActivity.dialog.show();
                        mActivity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        //J1939通讯线程复位
                        if (mActivity.j1939TaskService != null) {
                            mActivity.j1939TaskService.stopJ1939Service();
                            mActivity.unbindService(mActivity.conn);
                            mActivity.j1939TaskService.stopSelf();
                            mActivity.j1939TaskService = null;
                        }
                        break;
                    case DEVICE_PARSE_FINISH:
                        Intent j1939Intent = new Intent(J1939TaskService.ACTION);
                        j1939Intent.setPackage(mActivity.getPackageName());
                        j1939Intent.putExtra("reload", true);
                        mActivity.bindService(j1939Intent, mActivity.conn, BIND_AUTO_CREATE);
                        mActivity.startService(j1939Intent);
                        //注册预热时间 数据监听器
                        Globals.modelFile.getDataSetVO().getDSItem("预热时间").addListener(mActivity);
                        break;
                    case SKIP_TO_CHECK:
                        Intent intent = new Intent(mActivity, CheckHomeActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    case DEV_LIST_INIT:
                        mActivity.devListDialog.show();
                        break;
                    case DEV_LIST_LOADED:
                        mActivity.adapter.notifyDataSetChanged();
                        break;
                    case USER_LOGOUT:
                        mActivity.finish();
                        break;
                    case UPDATE_DEVICE_INFO:
                        if (msg.obj != null) {
                            mActivity.deviceStatus = (CommissioningStatistics) msg.obj;
                            mActivity.updateDeviceInfo();
                        }
                        break;

                }
            }
        }
    }

}
