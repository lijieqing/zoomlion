package com.kstech.zoomlion.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.base.ItemCheckCallBack;
import com.kstech.zoomlion.engine.check.ItemCheckTask;
import com.kstech.zoomlion.engine.comm.J1939TaskService;
import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.engine.device.XMLAPI;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.fragment.RealTimeViewsFragment;
import com.kstech.zoomlion.view.widget.MessageShowView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_debug)
public class DebugActivity extends BaseActivity {

    @ViewInject(R.id.debug_msv)
    private MessageShowView messageShowView;

    @ViewInject(R.id.debug_qc_id)
    private EditText qcId;

    @ViewInject(R.id.debug_qc_times)
    private EditText qcTimes;

    @ViewInject(R.id.debug_terminal_ip)
    private EditText terminalIP;
    private static final int DEVICE_LOAD_SUCCESS = 0;
    private static final int DEVICE_LOAD_ERROR = 1;
    private static final int COMM_SERVICE_START = 2;
    private static final int UPDATE_TASK_MSG = 3;
    private static final int J1939_COMM_STOPED = 4;

    J1939TaskService j1939TaskService;

    ItemCheckTask checkTask;

    RealTimeViewsFragment realTimeFragment;
    /**
     * 服务连接对象
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messageShowView.updateMessage(new Date(), "通讯线程已启动");
            J1939TaskService.MyBinder binder = (J1939TaskService.MyBinder) service;
            j1939TaskService = binder.task;
            handler.sendEmptyMessage(COMM_SERVICE_START);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messageShowView.updateMessage(new Date(), "通讯线程已断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        realTimeFragment = new RealTimeViewsFragment();
    }

    @Event(type = View.OnClickListener.class,
            value = {
                    R.id.debug_read_xml,
                    R.id.debug_qc_start,
                    R.id.debug_comm_stop,
                    R.id.debug_dev_num
            }
    )
    private void event(View view) {
        switch (view.getId()) {
            case R.id.debug_read_xml:
                messageShowView.updateMessage(new Date(), "机型加载中，请稍候...");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Device device = (Device) XMLAPI.readXML(getAssets().open("zoomlion.xml"));
                            Globals.modelFile = DeviceModelFile.readFromFile(device);
                            handler.sendEmptyMessage(DEVICE_LOAD_SUCCESS);
                            Globals.modelFile.dataSetVO.getDSItem("整机编码_回复").setStrValue("AAAAAS");
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(DEVICE_LOAD_ERROR);
                        }
                    }
                }.start();
                break;
            case R.id.debug_comm_stop:
                //J1939通讯线程复位
                if (j1939TaskService != null) {
                    new Thread(){
                        @Override
                        public void run() {
                            j1939TaskService.stopJ1939Service();
                            handler.sendEmptyMessage(J1939_COMM_STOPED);
                        }
                    }.start();
                }
                break;
            case R.id.debug_qc_start:
                if (j1939TaskService == null){
                    messageShowView.updateMessage(new Date(),"通讯线程未启动");
                }else {
                    checkTask = new ItemCheckTask(new DebugCheckTask());
                    checkTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
                break;
            case R.id.debug_dev_num:
                String str = Globals.modelFile.getDataSetVO().getDSItem("整机编码").getStrValue();
                messageShowView.updateMessage(new Date(),"整机编码："+str);
                break;
        }
    }

    private final DebugHandler handler = new DebugHandler(this);

    private static class DebugHandler extends Handler {
        WeakReference<DebugActivity> reference;

        public DebugHandler(DebugActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DebugActivity activity = reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case DEVICE_LOAD_SUCCESS:
                    activity.messageShowView.updateMessage(new Date(), "机型加载完成，开始启动通讯线程");
                    Intent j1939Intent = new Intent(J1939TaskService.ACTION);
                    j1939Intent.setPackage(activity.getPackageName());
                    j1939Intent.putExtra("reload", true);
                    activity.bindService(j1939Intent, activity.conn, BIND_AUTO_CREATE);
                    activity.startService(j1939Intent);

                    activity.realTimeFragment.init(activity);
                    activity.showRealTimeView();
                    break;
                case DEVICE_LOAD_ERROR:
                    activity.messageShowView.updateMessage(new Date(), "加载失败");
                    break;
                case UPDATE_TASK_MSG:
                    activity.messageShowView.updateMessage(new Date(), (String) msg.obj);
                    break;
                case J1939_COMM_STOPED:
                    activity.unbindService(activity.conn);
                    activity.j1939TaskService.stopSelf();
                    activity.j1939TaskService = null;
                    activity.showRealTimeView();
                    break;
            }
        }
    }

    private void updateTaskInfo(String msg){
        Message message = Message.obtain();
        message.what = UPDATE_TASK_MSG;
        message.obj = msg;
        handler.sendMessage(message);
    }

    private void showRealTimeView(){
        if (realTimeFragment.isAdded()){
            getFragmentManager().beginTransaction().remove(realTimeFragment).commit();
        }else {
            getFragmentManager().beginTransaction().add(R.id.debug_ll_realtime,realTimeFragment).commit();
        }
    }

    class DebugCheckTask implements ItemCheckCallBack {

        @Override
        public void onStart(ItemCheckTask task) {
            String id = qcId.getText().toString();
            String times = qcTimes.getText().toString();
            task.qcID = Integer.parseInt(TextUtils.isEmpty(id)?"-1":id);
            task.times = Integer.parseInt(TextUtils.isEmpty(times)?"-1":times);
        }

        @Override
        public void onStartError(String msg) {
            updateTaskInfo(msg);
        }

        @Override
        public void onProgress(String progress) {
            updateTaskInfo(progress);
        }

        @Override
        public void onSuccess(List<CheckItemParamValueVO> headers, Map<String, LinkedList<Float>> specMap, String msg) {
            updateTaskInfo(msg);
        }

        @Override
        public void onResultError(List<CheckItemParamValueVO> headers, String msg) {
            updateTaskInfo(msg);
        }

        @Override
        public void onTimeOut(List<CheckItemParamValueVO> headers, String msg, Map<String, LinkedList<Float>> specMap) {
            updateTaskInfo(msg);
        }

        @Override
        public void onTaskStop(boolean canSave) {

        }
    }

}
