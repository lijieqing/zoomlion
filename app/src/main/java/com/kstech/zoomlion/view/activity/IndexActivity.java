package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.DeviceLoadTask;
import com.kstech.zoomlion.engine.comm.J1939TaskService;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
import com.kstech.zoomlion.model.treelist.Element;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;
import com.kstech.zoomlion.model.treelist.TreeViewItemClickListener;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.widget.TextProgressView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

    private CheckRecordDao recordDao;

    private CheckItemDataDao itemDataDao;

    private AlertDialog dialog;

    private TextProgressView progressView;

    private long checkerID;

    private long recordID = -1;

    private String deviceIdentity = "test_machine_id";

    /**
     * 机型配置开始解析
     */
    public static final int DEVICE_PARSE_START = 0;
    /**
     * 机型配置解析中
     */
    public static final int DEVICE_PARSE_ING = 1;
    /**
     * 机型配置完成解析
     */
    public static final int DEVICE_PARSE_FINISH = 2;
    /**
     * 机型调试记录数据初始化
     */
    public static final int DEVICE_RECORD_INIT = 3;
    /**
     * 机型调试记录数据初始化完成
     */
    public static final int RECORD_INIT_FINISH = 4;
    /**
     * 进入调试主界面
     */
    public static final int SKIP_TO_CHECK = 5;
    /**
     * 机型调试记录数据初始化错误
     */
    public static final int RECORD_INIT_ERROR = 6;
    /**
     * J1939通讯线程 初始化
     */
    public static final int J1939_COMM_INIT = 7;
    /**
     * J1939通讯线程 初始化完成
     */
    public static final int J1939_COMM_INITED = 8;
    /**
     * 机型加载完成
     */
    public static final int DEVICE_LOAD_FINISH= 9;
    /**
     * 服务器机型列表初始化
     */
    public static final int DEV_LIST_INIT = 10;
    /**
     * 服务器机型列表初始化 完成
     */
    public static final int DEV_LIST_LOADED = 11;
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

        //获取数据库操作对象
        recordDao = MyApplication.getApplication().getDaoSession().getCheckRecordDao();
        itemDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();

        //初始化文字显示进度窗口
        dialog = new AlertDialog.Builder(this).setCancelable(false).create();
        progressView = new TextProgressView(this);
        dialog.setView(progressView);

        // TODO: 2017/10/11 加载默认机型信息，配置并启动通讯线程
        deviceLoadTask = new DeviceLoadTask(this,"",handler);
        deviceLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        //初始化机型选择列表 相关数据
        devListView = new ListView(this);
        rootElements = new ArrayList<>();
        allElements = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new TreeViewAdapter(rootElements,allElements,inflater);
        devListView.setAdapter(adapter);
        devListView.setOnItemClickListener(new TreeViewItemClickListener(adapter,this));
        devListDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(devListView)
                .create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        j1939TaskService.stopJ1939Service();
    }

    @Event(value = {R.id.index_iv_user, R.id.index_btn_choose_from_server, R.id.index_btn_goto, R.id.index_btn_exit})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_iv_user:
                startActivity(new Intent(this,UserDetailActivity.class));
                break;
            case R.id.index_btn_goto:
                if (isAvalid()) {
                    //查询本地是否存在记录
                    CheckRecord cr = recordDao.queryBuilder().where(CheckRecordDao.Properties.DeviceIdentity
                            .eq(deviceIdentity)).build().unique();

                    //当为null时，说明此机型第一次调试，需要为其创建对应的数据
                    if (cr == null) {
                        //弹出对话框，更新数据库
                        dialog.show();
                        //数据初始化
                        initRecord();
                    } else {
                        LogUtils.e(TAG, "已存在");
                        handler.sendEmptyMessage(SKIP_TO_CHECK);
                    }

                }
                break;
            case R.id.index_btn_exit:
                finish();
                break;
            case R.id.index_btn_choose_from_server:
                handler.sendEmptyMessage(DEV_LIST_INIT);
                rootElements.clear();
                allElements.clear();
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //去服务器获取列表信息，此处模拟数据
                        //添加最外层节点
                        Element e1 = new Element("山东省", Element.TOP_LEVEL, 0, Element.NO_PARENT, true, false);
                        //添加第一层节点
                        Element e2 = new Element("青岛市", Element.TOP_LEVEL + 1, 1, e1.getId(), true, false);
                        //添加第二层节点
                        Element e3 = new Element("市南区", Element.TOP_LEVEL + 2, 2, e2.getId(), true, false);
                        //添加第三层节点
                        Element e4 = new Element("香港中路", Element.TOP_LEVEL + 3, 3, e3.getId(), false, false);
                        //添加第一层节点
                        Element e5 = new Element("烟台市", Element.TOP_LEVEL + 1, 4, e1.getId(), true, false);
                        //添加第二层节点
                        Element e6 = new Element("芝罘区", Element.TOP_LEVEL + 2, 5, e5.getId(), true, false);
                        //添加第三层节点
                        Element e7 = new Element("凤凰台街道", Element.TOP_LEVEL + 3, 6, e6.getId(), false, false);
                        //添加第一层节点
                        Element e8 = new Element("威海市", Element.TOP_LEVEL + 1, 7, e1.getId(), false, false);
                        //添加最外层节点
                        Element e9 = new Element("广东省", Element.TOP_LEVEL, 8, Element.NO_PARENT, true, false);
                        //添加第一层节点
                        Element e10 = new Element("深圳市", Element.TOP_LEVEL + 1, 9, e9.getId(), true, false);
                        //添加第二层节点
                        Element e11 = new Element("南山区", Element.TOP_LEVEL + 2, 10, e10.getId(), true, false);
                        //添加第三层节点
                        Element e12 = new Element("深南大道", Element.TOP_LEVEL + 3, 11, e11.getId(), true, false);
                        //添加第四层节点
                        Element e13 = new Element("10000号", Element.TOP_LEVEL + 4, 12, e12.getId(), false, false);
                        rootElements.add(e1);rootElements.add(e9);
                        allElements.add(e1);allElements.add(e2);allElements.add(e3);allElements.add(e4);allElements.add(e5);
                        allElements.add(e6);allElements.add(e7);allElements.add(e8);allElements.add(e9);allElements.add(e10);
                        allElements.add(e11);allElements.add(e12);allElements.add(e13);

                        handler.sendEmptyMessage(DEV_LIST_LOADED);
                    }
                });
                break;
        }
    }

    /**
     * 初始化当前机型调试记录数据
     */
    private void initRecord(){
        //点击进入调试页后，进行数据库更新，生成基本的数据库结构
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                CheckRecord record = new CheckRecord(null, deviceIdentity,
                        "test_record_name", 123l,
                        CheckRecordResultEnum.UNFINISH.getCode(), new Date(),
                        null, 0, 0
                        , "test_desc", 0, false);

                //定义调试项目数据实体类
                CheckItemData itemData;
                Message message;
                try {
                    //插入
                    recordID = recordDao.insert(record);

                    //遍历配置信息中的调试项目，并依次存入数据库
                    int p = 8;
                    for (CheckItemVO checkItemVO : Globals.modelFile.allCheckItemList) {
                        itemData = new CheckItemData(null, Integer.parseInt(checkItemVO.getId()),
                                checkItemVO.getName(), 0, 0, 0,
                                recordID, false, false, null);
                        itemDataDao.insert(itemData);
                        //handler 信息封装
                        message = Message.obtain();
                        p += 1;
                        message.what = DEVICE_RECORD_INIT;
                        message.obj = p;
                        Bundle b = new Bundle();
                        b.putCharSequence("name", checkItemVO.getName());
                        message.setData(b);
                        //发送msg 更新UI
                        handler.sendMessage(message);
                        SystemClock.sleep(200);
                    }

                    //发送成功状态 更新UI
                    handler.sendEmptyMessage(RECORD_INIT_FINISH);

                    SystemClock.sleep(2000);
                    handler.sendEmptyMessage(SKIP_TO_CHECK);
                } catch (Exception e) {
                    //异常 进行提示
                    message = Message.obtain();
                    message.what = RECORD_INIT_ERROR;
                    message.obj = e.getMessage();
                    handler.sendMessage(message);

                    LogUtils.e(TAG, e.getMessage());
                }

            }
        });
    }

    /**
     * 判断当前页面是否符合条件跳入调试界面
     *
     * @return boolean
     */
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
            final IndexActivity mActivity = activityReference.get();
            if (mActivity != null) {
                switch (msg.what) {
                    case DEVICE_PARSE_START:
                        mActivity.progressView.reset();
                        mActivity.dialog.show();
                        mActivity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        break;
                    case DEVICE_PARSE_ING:
                        mActivity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        break;
                    case DEVICE_PARSE_FINISH:
                        Intent j1939Intent = new Intent(J1939TaskService.ACTION);
                        j1939Intent.setPackage(mActivity.getPackageName());
                        j1939Intent.putExtra("reload",true);
                        mActivity.bindService(j1939Intent, new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                LogUtils.e(TAG,"service connect");
                                J1939TaskService.MyBinder binder = (J1939TaskService.MyBinder) service;
                                mActivity.j1939TaskService = binder.task;
                                mActivity.deviceLoadTask.isWaitting = false;
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        },BIND_AUTO_CREATE);
                        mActivity.startService(j1939Intent);
                        break;
                    case DEVICE_RECORD_INIT:
                        String name = (String) msg.getData().getCharSequence("name");
                        mActivity.progressView.updateProgress("初始化配置：" + name + " 数据信息", (Integer) msg.obj);
                        break;
                    case RECORD_INIT_FINISH:
                        mActivity.progressView.updateProgress("数据初始化完成", 100);
                        break;
                    case SKIP_TO_CHECK:
                        mActivity.dialog.cancel();
                        Intent intent = new Intent(mActivity, CheckHomeActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    case RECORD_INIT_ERROR:
                        mActivity.progressView.updateProgress("异常：", (Integer) msg.obj);
                        SystemClock.sleep(1000);
                        mActivity.dialog.cancel();
                        break;
                    case J1939_COMM_INIT:
                        mActivity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        break;
                    case J1939_COMM_INITED:
                        mActivity.progressView.updateProgress((String) msg.obj, msg.arg1);
                        break;
                    case DEVICE_LOAD_FINISH:
                        mActivity.dialog.cancel();
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
