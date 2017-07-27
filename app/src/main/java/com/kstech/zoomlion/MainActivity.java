package com.kstech.zoomlion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;
import com.kstech.zoomlion.view.ItemShowView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
    private TextView tvNet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNet = (TextView) findViewById(R.id.tv_net);
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
        String source2 = "{\"itemName\":\"压力检测\",\"paramName\":\"底盘\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source3 = "{\"itemName\":\"压力检测\",\"paramName\":\"灯管\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source4 = "{\"itemName\":\"压力检测\",\"paramName\":\"油缸沉降量(洞壁深处)\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source5 = "{\"itemName\":\"压力检测\",\"paramName\":\"左回转压力\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";

        CheckItemParamValueVO checkItemParamValueVO;
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source2, CheckItemParamValueVO.class);
        Globals.paramValueVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source3, CheckItemParamValueVO.class);
        Globals.paramValueVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source4, CheckItemParamValueVO.class);
        Globals.paramValueVOs.add(checkItemParamValueVO);
        checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source5, CheckItemParamValueVO.class);
        Globals.paramValueVOs.add(checkItemParamValueVO);
        ItemShowView itemShowView = new ItemShowView(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(itemShowView)
                .create();
        dialog.show();
    }
}
