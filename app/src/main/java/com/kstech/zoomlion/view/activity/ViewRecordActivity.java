package com.kstech.zoomlion.view.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.server.ItemRecordLoadTask;
import com.kstech.zoomlion.serverdata.QCItemRecordDetails;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

@ContentView(R.layout.activity_view_record)
public class ViewRecordActivity extends BaseActivity {

    @ViewInject(R.id.vr_rv)
    private RecyclerView rvItemRecord;

    @ViewInject(R.id.vr_tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.test)
    private TextView tvTest;

    public static final int ITEM_RECORD_LOADED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        String dictID = getIntent().getStringExtra("dictID");
        tvTitle.setText("此界面用于二次调试时的记录查看");
        tvTitle.setTextSize(18);
        ItemRecordLoadTask loadTask = new ItemRecordLoadTask(handler);
        loadTask.setDictID(dictID);

        loadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends BaseInnerHandler {
        InnerHandler(ViewRecordActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ViewRecordActivity vrActivity = (ViewRecordActivity) reference.get();
            switch (msg.what) {
                case ITEM_RECORD_LOADED:
                    List<QCItemRecordDetails> datas = (List<QCItemRecordDetails>) msg.obj;
                    vrActivity.tvTest.setText(datas.size() + "");
                    break;
            }
        }
    }
}
