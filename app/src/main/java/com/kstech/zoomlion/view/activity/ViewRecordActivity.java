package com.kstech.zoomlion.view.activity;

import android.os.Bundle;

import com.kstech.zoomlion.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_view_record)
public class ViewRecordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
