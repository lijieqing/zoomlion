package com.kstech.zoomlion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import com.kstech.zoomlion.view.adapter.ItemAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private TextView tvNet;
    ItemShowView itemShowView;
    ListView lvItem;
    private List<String> oneItem = new ArrayList<>();
    public DeviceModelFile modelFile;
    public List<CheckItemVO> itemList = new ArrayList<>();
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNet = (TextView) findViewById(R.id.tv_net);
        itemShowView = (ItemShowView) findViewById(R.id.isv);
        lvItem = (ListView) findViewById(R.id.lv_item);
        itemAdapter = new ItemAdapter(itemList,this);
        lvItem.setAdapter(itemAdapter);
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckItemVO item = modelFile.getCheckItemList().get(i);
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

    private void initData(){
        Globals.paramHeadVOs.clear();
        String source0 = "{\"itemName\":\"压力检测\",\"paramName\":\"底盘\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source1 = "{\"itemName\":\"压力检测\",\"paramName\":\"底盘\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source2 = "{\"itemName\":\"压力检测\",\"paramName\":\"底盘\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source3 = "{\"itemName\":\"压力检测\",\"paramName\":\"灯管\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source4 = "{\"itemName\":\"压力检测\",\"paramName\":\"油缸沉降量(洞壁深处)\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source5 = "{\"itemName\":\"压力检测\",\"paramName\":\"左回转压力\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source6 = "{\"itemName\":\"压力检测\",\"paramName\":\"左回转压力\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source7 = "{\"itemName\":\"压力检测\",\"paramName\":\"左回转压力\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";

        CheckItemParamValueVO checkItemParamValueVO;
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source0, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source1, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source2, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source3, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source4, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source5, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source6, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source7, CheckItemParamValueVO.class);
        Globals.paramHeadVOs.add(checkItemParamValueVO);

        String s = JsonUtils.toJson(Globals.paramHeadVOs);
        oneItem.add(s);
        LogUtils.e("ItemShowView",s);
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
                mActivity.itemAdapter.notifyDataSetChanged();
                mActivity.itemShowView.updateHead(mActivity.modelFile.getCheckItemList().get(0));
            }
        }
    }
}
