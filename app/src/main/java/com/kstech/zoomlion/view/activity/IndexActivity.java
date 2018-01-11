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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.comm.J1939TaskService;
import com.kstech.zoomlion.engine.device.DeviceLoadTask;
import com.kstech.zoomlion.engine.server.ServerProcessCheck;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.session.DeviceCatSession;
import com.kstech.zoomlion.model.treelist.Element;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;
import com.kstech.zoomlion.model.treelist.TreeViewItemClickListener;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.ThreadManager;

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

    @ViewInject(R.id.index_tv_preheat)
    private TextView tvPreHeat;//整机预热时间

    @ViewInject(R.id.index_btn_exit)
    private Button btnExit;//退出按钮

    @ViewInject(R.id.index_btn_goto)
    private Button btnGotoCheck;//进入调试界面

    @ViewInject(R.id.index_lv_initparams)
    private ListView lvInitParams;//初始化参数列表

    private CheckRecordDao recordDao;

    private CheckItemDataDao itemDataDao;

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

    private List<CheckItemVO> newItemList;
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
        String data = String.format(getString(R.string.index_welcome_user), Globals.currentUser.getUsername(), Globals.currentUser.getLastLoginTime() == null ? new Date() : Globals.currentUser.getLastLoginTime());
        tvUserWelcome.setText(data);

        tvTerminalName.setTextColor(Color.DKGRAY);
        tvTerminalName.setText(Globals.currentTerminal.getName());
        tvTerminalIP.setText(Globals.currentTerminal.getIp());

        tvDeviceIdentity.setTextColor(Color.RED);
        tvDeviceIdentity.setText("当前未检测到整机编号");

        //获取数据库操作对象
        recordDao = MyApplication.getApplication().getDaoSession().getCheckRecordDao();
        itemDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();

        // TODO: 2017/10/11 加载默认机型信息，配置并启动通讯线程
        deviceLoadTask = new DeviceLoadTask(this, null, handler);
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

    @Event(value = {R.id.index_iv_user, R.id.index_btn_choose_from_server,
            R.id.index_btn_goto, R.id.index_btn_exit, R.id.index_btn_auto_download})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_iv_user:
                startActivity(new Intent(this, UserDetailActivity.class));
                break;
            case R.id.index_btn_goto:
                new ServerProcessCheck(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            case R.id.index_btn_exit:
                finish();
                break;
            case R.id.index_btn_auto_download:
                // TODO: 2018/1/5 根据整机编码获取机型信息，此处模拟已经获取到整机编码
                deviceLoadTask = new DeviceLoadTask(this, Globals.deviceSN, handler);
                deviceLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            case R.id.index_btn_choose_from_server:
                handler.sendEmptyMessage(DEV_LIST_INIT);
                rootElements.clear();
                allElements.clear();
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        String s = "{\"data\":[{\"id\":1,\"parent_id\":0,\"level\":1,\"sub_nums\":1,\"name\":\"root1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}," +
                                "{\"id\":2,\"parent_id\":0,\"level\":1,\"sub_nums\":1,\"name\":\"root2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                                "{\"id\":3,\"parent_id\":1,\"level\":2,\"sub_nums\":1,\"name\":\"second1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                                "{\"id\":4,\"parent_id\":2,\"level\":2,\"sub_nums\":1,\"name\":\"second2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                                "{\"id\":5,\"parent_id\":3,\"level\":3,\"sub_nums\":1,\"name\":\"third1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                                "{\"id\":6,\"parent_id\":4,\"level\":3,\"sub_nums\":1,\"name\":\"third2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                                "{\"id\":7,\"parent_id\":6,\"level\":4,\"sub_nums\":0,\"name\":\"fourth1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}]}";
                        //去服务器获取列表信息，此处模拟数据
                        DeviceCatSession devCat = JsonUtils.fromJson(s, DeviceCatSession.class);
                        List<DeviceCatSession> devs = devCat.getData();
                        int parentID;
                        int id;
                        int level;
                        String content;
                        String fullCode;
                        String desc;
                        boolean hasChildren = true;

                        Element element;
                        for (DeviceCatSession dev : devs) {
                            parentID = dev.getParent_id();
                            id = dev.getId();
                            level = dev.getLevel();
                            content = dev.getName();
                            fullCode = dev.getFull_code();
                            desc = dev.getRemark();

                            if (dev.getSub_nums() == Element.NO_PARENT) {
                                hasChildren = false;
                            }

                            element = new Element(content, level, id, parentID, hasChildren, false);
                            element.setFull_code(fullCode);
                            element.setDesc(desc);

                            if (level == Element.TOP_LEVEL) {
                                rootElements.add(element);
                            }
                            allElements.add(element);
                        }

                        handler.sendEmptyMessage(DEV_LIST_LOADED);
                    }
                });
                break;
        }
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
                        mActivity.dialog.cancel();
                        Intent intent = new Intent(mActivity, CheckHomeActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    case DEV_LIST_INIT:
                        mActivity.devListDialog.show();
                        break;
                    case DEV_LIST_LOADED:
                        mActivity.adapter.notifyDataSetChanged();
                        break;

                }
            }
        }
    }

}
