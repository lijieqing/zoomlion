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
import android.support.annotation.NonNull;
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
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
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
import com.kstech.zoomlion.view.widget.TextProgressView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
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

    private AlertDialog dialog;//任务进度条弹窗

    private TextProgressView progressView;//带信息提示的进度条

    private long checkerID;//调试员ID

    private String deviceIdentity = "test_machine_id";//调试机型编码

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
    public static final int DEVICE_LOAD_FINISH = 9;
    /**
     * 服务器机型列表初始化
     */
    public static final int DEV_LIST_INIT = 10;
    /**
     * 服务器机型列表初始化 完成
     */
    public static final int DEV_LIST_LOADED = 11;
    /**
     * 存在新的调试项目 需要初始化记录
     */
    public static final int NEW_ITEM_RECORD_INIT = 12;
    /**
     * 数据校验开始
     */
    public static final int VERIFY_RECORD_START = 13;
    /**
     * 数据校验进行中
     */
    public static final int VERIFY_RECORD_ING = 14;
    /**
     * 数据校验结束
     */
    public static final int VERIFY_RECORD_END = 15;

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
        deviceLoadTask = new DeviceLoadTask(this, "", handler);
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

        if (j1939TaskService != null){
            j1939TaskService.stopJ1939Service();
            unbindService(conn);
            stopService(j1939Intent);
        }
    }

    @Event(value = {R.id.index_iv_user, R.id.index_btn_choose_from_server, R.id.index_btn_goto, R.id.index_btn_exit})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.index_iv_user:
                startActivity(new Intent(this, UserDetailActivity.class));
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
                        //弹出对话框
                        dialog.show();
                        //如果不为空，进行机型文件和数据库的校验
                        verifyCheckRecord(cr);
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

    /**
     * 校验机型文件中的调试项目与数据库中的调试项目是否一致
     *
     * @param cr 整机调试记录实体类
     */
    private void verifyCheckRecord(@NonNull final CheckRecord cr) {
        Globals.recordID = cr.getCheckRecordId();
        if (newItemList == null) {
            newItemList = new ArrayList<>();
        } else {
            newItemList.clear();
        }
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(VERIFY_RECORD_START);
                Message message;
                int p = 20;

                //对当前的调试项目遍历，与数据库比对
                for (CheckItemVO checkItemVO : Globals.modelFile.allCheckItemList) {
                    //在数据库已存在，默认false
                    boolean inDB = false;
                    //获取机型文件中的调试项目ID
                    String qcID = checkItemVO.getId();
                    //遍历数据库中的调试项目记录
                    for (CheckItemData checkItemData : cr.getCheckItemDatas()) {
                        //比较数据库记录中的调试项目ID和机型文件中的调试项目ID
                        String qcIDInDB = String.valueOf(checkItemData.getQcId());
                        //ID相同，则更改inDB状态为true
                        if (qcID.equals(qcIDInDB)) {
                            inDB = true;
                        }
                    }
                    //如果当前的CheckItemVO找不到数据库记录，将其加入新调试项目集合
                    if (!inDB) {
                        newItemList.add(checkItemVO);
                    }
                    message = Message.obtain();
                    message.what = VERIFY_RECORD_ING;
                    message.arg1 = p;
                    Bundle b = new Bundle();
                    b.putCharSequence("name", checkItemVO.getName());
                    message.setData(b);
                    handler.sendMessage(message);
                    p += 1;
                    SystemClock.sleep(50);
                }
                //发送校验完成状态
                message = Message.obtain();
                message.what = VERIFY_RECORD_END;
                message.obj = p;
                handler.sendMessage(message);

                //最后判断 新调试项目集合是否为空
                if (newItemList.size() == 0) {
                    //数据库中的数据是完整的,直接进入主调试界面
                    LogUtils.e(TAG, "已存在");
                    handler.sendEmptyMessage(SKIP_TO_CHECK);
                } else {
                    //发送 更新数据 状态
                    message = Message.obtain();
                    message.what = NEW_ITEM_RECORD_INIT;
                    message.obj = p + 10;
                    handler.sendMessage(message);
                    //初始化新的调试项目记录
                    CheckItemData itemData;
                    try {
                        for (CheckItemVO checkItemVO : newItemList) {
                            itemData = new CheckItemData(null, Integer.parseInt(checkItemVO.getId()),
                                    checkItemVO.getDictId(), checkItemVO.getName(), 0, 0, 0,
                                    Globals.recordID, false, false, null);
                            itemDataDao.insert(itemData);
                        }
                    } catch (Exception e) {
                        //异常 进行提示
                        message = Message.obtain();
                        message.what = RECORD_INIT_ERROR;
                        message.obj = e.getMessage();
                        handler.sendMessage(message);
                    }

                    handler.sendEmptyMessage(SKIP_TO_CHECK);
                }
            }
        });
    }

    /**
     * 初始化当前机型调试记录数据
     */
    private void initRecord() {
        //点击进入调试页后，进行数据库更新，生成基本的数据库结构
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                CheckRecord record = new CheckRecord(null, "", deviceIdentity,
                        "test_record_name", 123l,
                        CheckRecordResultEnum.UNFINISH.getCode(), new Date(),
                        null, 0, 0
                        , null, 0, false);

                //定义调试项目数据实体类
                CheckItemData itemData;
                Message message;
                try {
                    //插入
                    Globals.recordID = recordDao.insert(record);

                    //遍历配置信息中的调试项目，并依次存入数据库
                    int p = 8;
                    for (CheckItemVO checkItemVO : Globals.modelFile.allCheckItemList) {
                        itemData = new CheckItemData(null, Integer.parseInt(checkItemVO.getId()),
                                checkItemVO.getDictId(), checkItemVO.getName(), 0, 0, 0,
                                Globals.recordID, false, false, null);
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

    @Override
    public void onDataChanged(short dsItemPosition, float value) {
        // TODO: 2017/12/7 此处处理value 转换为时间格式并显示到 tvPreHeat组件
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<IndexActivity> activityReference;

        private InnerHandler(IndexActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final IndexActivity mActivity = activityReference.get();
            String name;
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
                        j1939Intent.putExtra("reload", true);
                        mActivity.bindService(j1939Intent, mActivity.conn, BIND_AUTO_CREATE);
                        mActivity.startService(j1939Intent);
                        //注册预热时间 数据监听器
                        Globals.modelFile.getDataSetVO().getDSItem("预热时间").addListener(mActivity);
                        break;
                    case DEVICE_RECORD_INIT:
                        name = (String) msg.getData().getCharSequence("name");
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
                    case VERIFY_RECORD_START:
                        mActivity.progressView.updateProgress("数据校验中", 15);
                        break;
                    case VERIFY_RECORD_ING:
                        name = (String) msg.getData().getCharSequence("name");
                        mActivity.progressView.updateProgress("数据校验：" + name, msg.arg1);
                        break;
                    case VERIFY_RECORD_END:
                        mActivity.progressView.updateProgress("数据校验完成", msg.arg1);
                        break;
                    case NEW_ITEM_RECORD_INIT:
                        mActivity.progressView.updateProgress("数据库更新中", msg.arg1);
                        break;
                }
            }
        }
    }

}
