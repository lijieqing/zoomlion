package com.kstech.zoomlion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.manager.XMLAPI;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;
import com.kstech.zoomlion.view.ItemShowView;
import com.kstech.zoomlion.view.adapter.ExpandItemAdapter;
import com.kstech.zoomlion.view.adapter.ItemAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    private TextView tvNet;
    ItemShowView itemShowView;
    ExpandableListView elvItem;
    private List<String> oneItem = new ArrayList<>();
    public DeviceModelFile modelFile;
    public List<CheckItemVO> itemList = new ArrayList<>();
    ExpandItemAdapter expandItemAdapter;
    List<String> groups = new ArrayList<>();
    Map<String,List<CheckItemVO>> checkItemMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNet = (TextView) findViewById(R.id.tv_net);
        itemShowView = (ItemShowView) findViewById(R.id.isv);
        elvItem = (ExpandableListView) findViewById(R.id.elv_item);
        expandItemAdapter = new ExpandItemAdapter(this,groups,checkItemMap);
        elvItem.setAdapter(expandItemAdapter);
        elvItem.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String key = groups.get(i);
                CheckItemVO item = checkItemMap.get(key).get(i1);
                itemShowView.updateHead(item);
                //// TODO: 2017/8/2 查询数据库更新body 调用itemShowView.updateBody();,下面使用伪数据
                List<CheckItemParamValueVO> volist = item.getParamNameList();
                for (CheckItemParamValueVO value : volist) {
                    value.setValue("45");
                }
                List<CheckItemDetailData> details = new ArrayList<>();
                CheckItemDetailData data = new CheckItemDetailData();
                data.setParamsValues(JsonUtils.toJson(volist));
                data.setStartTime(new Date());
                for (int j = 0; j < 5; j++) {
                    details.add(data);
                }

                itemShowView.updateBody(details);
                return false;
            }
        });
        new Thread(){
            @Override
            public void run() {
                try {
                    modelFile = DeviceModelFile.readFromFile((Device) XMLAPI.readXML(getAssets().open("temp.xml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void net(View view) {
        MyHttpUtils myHttpUtils = new MyHttpUtils();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("account","admin");
        maps.put("password","1");
        myHttpUtils.xutilsPost("", "http://192.168.32.102:8080/crmnew/login", maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                LogUtils.d("xUTILs",((String) result)+" ");
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.d("xUTILs",((String) errorMsg)+" ");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }

    public void get(View view) {
        MyHttpUtils myHttpUtils = new MyHttpUtils();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("username","admin");
        maps.put("password","1");
        myHttpUtils.xutilsGet("", "http://192.168.32.102:8080/crmnew/menu/userlist1", maps, new MyHttpUtils.MyHttpCallback() {
            @Override
            public void onSuccess(Object result, String whereRequest) {
                LogUtils.d("xUTILs",((String) result)+" ");
            }

            @Override
            public void onError(Object errorMsg, String whereRequest) {
                LogUtils.d("xUTILs",((String) errorMsg)+" ");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }

    public void camera(View view) {
        startActivity(new Intent(this,CameraActivity.class));
    }

    public void itemview(View view) {
        ItemShowView itemShowView = new ItemShowView(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(itemShowView)
                .create();
        dialog.show();
    }

    public void update(View view) {

    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        private final WeakReference<MainActivity> mainActivity;

        private InnerHandler(MainActivity activity) {
            this.mainActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mActivity = mainActivity.get();
            if (mActivity != null){
                mActivity.itemList.addAll(mActivity.modelFile.getCheckItemList());
                Set<String> keys = mActivity.modelFile.checkItemMap.keySet();
                mActivity.groups.addAll(keys);
                mActivity.checkItemMap.putAll(mActivity.modelFile.checkItemMap);
                mActivity.expandItemAdapter.notifyDataSetChanged();
                mActivity.itemShowView.updateHead(mActivity.modelFile.getCheckItemList().get(0));
            }
        }
    }
}
