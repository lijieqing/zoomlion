package com.kstech.zoomlion.view.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.kstech.zoomlion.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_view_record)
public class ViewRecordActivity extends BaseActivity {

    @ViewInject(R.id.vr_rv)
    private RecyclerView rvItemRecord;

    @ViewInject(R.id.vr_tv_title)
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        tvTitle.setText("此界面用于二次调试时的记录查看");
        tvTitle.setTextSize(18);
    }
}
