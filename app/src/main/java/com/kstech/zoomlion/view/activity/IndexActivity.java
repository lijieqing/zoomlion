package com.kstech.zoomlion.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.check.ParamInitTask;
import com.kstech.zoomlion.engine.comm.J1939TaskService;
import com.kstech.zoomlion.engine.device.DeviceLoadTask;
import com.kstech.zoomlion.engine.server.DeviceStatusUpdateTask;
import com.kstech.zoomlion.engine.server.ServerProcessCheck;
import com.kstech.zoomlion.engine.server.UserLogoutTask;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.PG;
import com.kstech.zoomlion.model.xmlbean.SP;
import com.kstech.zoomlion.serverdata.CommissioningStatistics;
import com.kstech.zoomlion.serverdata.CommissioningStatusEnum;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.widget.MessageShowView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * 根布局
     */
    @ViewInject(R.id.index_ll)
    private LinearLayout root;

    /**
     * 服务器状态图
     */
    @ViewInject(R.id.index_iv_server_status)
    private ImageView ivServerStatus;

    /**
     * 测量终端状态图
     */
    @ViewInject(R.id.index_iv_terminal_status)
    private ImageView ivTerminalStatus;

    /**
     * 服务器状态提示信息
     */
    @ViewInject(R.id.index_tv_server_status)
    private TextView tvServerStatus;

    /**
     * 测量终端状态提示信息
     */
    @ViewInject(R.id.index_tv_terminal_status)
    private TextView tvTerminalStatus;

    /**
     * 用户欢迎语展示
     */
    @ViewInject(R.id.index_tv_welcome)
    private TextView tvUserWelcome;

    /**
     * 用户图标
     */
    @ViewInject(R.id.index_iv_user)
    private ImageView ivUser;

    /**
     * 调试终端名称
     */
    @ViewInject(R.id.index_tv_terminal_name)
    private TextView tvTerminalName;

    /**
     * 调试终端IP
     */
    @ViewInject(R.id.index_tv_terminal_ip)
    private TextView tvTerminalIP;

    /**
     * 待调试车辆编码
     */
    @ViewInject(R.id.index_tv_device_identity)
    private TextView tvDeviceIdentity;

    /**
     * 自动获取编码和机型按钮
     */
    @ViewInject(R.id.index_btn_auto_download)
    private Button btnAutoDownLoadDevice;

    /**
     * 设备出厂日期
     */
    @ViewInject(R.id.index_tv_device_born)
    private TextView tvDeviceBornDate;

    /**
     * 设备是否第二次调试
     */
    @ViewInject(R.id.index_tv_device_secondcheck)
    private TextView tvDevSecondCheck;

    /**
     * 总调试项目数量
     */
    @ViewInject(R.id.index_tv_device_itemcounts)
    private TextView tvItemCounts;

    /**
     * 已调试项目数量
     */
    @ViewInject(R.id.index_tv_item_checkedcounts)
    private TextView tvItemCheckedCounts;

    /**
     * 未调试项目数量
     */
    @ViewInject(R.id.index_tv_item_uncheckedcounts)
    private TextView tvItemUncheckedCounts;

    /**
     * 上次调试项目
     */
    @ViewInject(R.id.index_tv_item_lastcheck)
    private TextView tvItemLastChecked;

    /**
     * 整机调试结论
     */
    @ViewInject(R.id.index_tv_record_result)
    private TextView tvRecordResult;

    /**
     * 整机预热时间
     */
    @ViewInject(R.id.index_tv_preheat)
    private TextView tvPreHeat;

    /**
     * 退出按钮
     */
    @ViewInject(R.id.index_btn_exit)
    private Button btnExit;

    /**
     * 进入调试界面
     */
    @ViewInject(R.id.index_btn_goto)
    private Button btnGotoCheck;

    /**
     * 初始化参数列表
     */
    @ViewInject(R.id.index_gv_initparams)
    private GridView gvInitParams;

    /**
     * 信息提示组件
     */
    @ViewInject(R.id.index_msv)
    private MessageShowView messageShowView;

    /**
     * 参数初始化按钮
     */
    @ViewInject(R.id.index_rl_param_init)
    private RelativeLayout rlInitParam;

    @ViewInject(R.id.index_iv_param_init)
    private ImageView ivRefresh;

    /**
     * 旋转动画
     */
    private RotateAnimation animation;
    /**
     * 机型配置完成解析
     */
    public static final int DEVICE_PARSE_FINISH = 0;
    /**
     * 进入调试主界面
     */
    public static final int SKIP_TO_CHECK = 1;
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
    /**
     * 更新参数初始化信息
     */
    public static final int UPDATE_PARAM_INIT_INFO = 7;
    /**
     * 参数初始化动画结束
     */
    public static final int PARAM_INIT_ANIM_CLEAR = 8;
    /**
     * 机型加载线程
     */
    private DeviceLoadTask deviceLoadTask;
    /**
     * J1939通讯线程Service
     */
    private J1939TaskService j1939TaskService;
    /**
     * 整机状态描述对象
     */
    private CommissioningStatistics deviceStatus;
    /**
     * 是否从调试页面返回
     */
    private boolean backFromCheck = false;
    /**
     * 初始化参数集合
     */
    private List<String> initParams = new ArrayList<>();
    /**
     * 初始化参数适配器
     */
    private ArrayAdapter<String> initParamAdapter;
    /**
     * 初始化参数Key值
     */
    private static final String INIT_PARAM = "Init";
    public static final String TAG = "IndexActivity";
    /**
     * 服务连接对象
     */
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

        //初始化参数adapter
        initParamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, initParams);
        gvInitParams.setAdapter(initParamAdapter);

        //旋转动画
        animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1500);
        animation.setRepeatCount(-1);

        //加载默认机型信息，配置并启动通讯线程
        deviceLoadTask = new DeviceLoadTask(null, handler);
        deviceLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

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
        if (Globals.PROCESSID != null && backFromCheck) {
            backFromCheck = false;
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
        Snackbar.make(root, "点击退出按钮退出应用", Snackbar.LENGTH_SHORT).show();
    }

    @Event(value = {R.id.index_iv_user, R.id.index_btn_goto,
            R.id.index_btn_exit, R.id.index_btn_auto_download,
            R.id.index_btn_get_sn, R.id.index_rl_param_init})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_iv_user:
                startActivity(new Intent(this, UserDetailActivity.class));
                break;
            case R.id.index_btn_goto:
                if (Globals.PROCESSID == null) {
                    Snackbar.make(root, "请先获取调试流程", Snackbar.LENGTH_SHORT).show();
                } else {
                    ServerProcessCheck spc = new ServerProcessCheck(handler);
                    spc.setDeviceStatus(deviceStatus);
                    spc.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
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
            case R.id.index_btn_get_sn:
                Toast.makeText(this, Globals.deviceSN, Toast.LENGTH_SHORT).show();
                break;
            case R.id.index_rl_param_init:
                ivRefresh.startAnimation(animation);
                messageShowView.clearMessage();
                messageShowView.updateMessage(new Date(), "准备开始初始化");
                new ParamInitTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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

    /**
     * 获取初始化参数集合
     */
    private void getInitParams() {
        initParams.clear();
        CheckItemVO initVO = Globals.modelFile.checkItemMap.get(INIT_PARAM).get(0);
        String attachPGN = initVO.getAttachPGN();
        List<PG> pgs = Globals.modelFile.device.getJ1939().getPgs();

        for (PG pg : pgs) {
            if (attachPGN.equals(pg.getPGN())) {
                for (SP sp : pg.getSps()) {
                    String param = sp.getRef();
                    initParams.add(param);
                }
                break;
            }
        }

        initParamAdapter.notifyDataSetChanged();

        messageShowView.updateMessage(new Date(), "初始化参数加载完成");
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

                        mActivity.getInitParams();
                        //注册预热时间 数据监听器
                        Globals.modelFile.getDataSetVO().getDSItem("预热时间").addListener(mActivity);
                        break;
                    case SKIP_TO_CHECK:
                        mActivity.backFromCheck = true;
                        Intent intent = new Intent(mActivity, CheckHomeActivity.class);
                        mActivity.startActivity(intent);
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
                    case UPDATE_PARAM_INIT_INFO:
                        mActivity.messageShowView.updateMessage(new Date(), (String) msg.obj);
                        break;
                    case PARAM_INIT_ANIM_CLEAR:
                        mActivity.ivRefresh.clearAnimation();
                        if (msg.obj == null){
                            mActivity.messageShowView.updateMessage(new Date(), "参数初始化完成，可以开启调试");
                        }else {
                            mActivity.messageShowView.updateMessage(new Date(), (String) msg.obj);
                        }
                        break;

                }
            }
        }
    }

}
