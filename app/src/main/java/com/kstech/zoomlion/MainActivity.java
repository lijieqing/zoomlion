package com.kstech.zoomlion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
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
}
