package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.DateUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;
import java.util.LinkedList;


/**
 * Created by lijie on 2018/1/24.
 * 提示信息组件
 */
@ContentView(R.layout.message_show)
public class MessageShowView extends RelativeLayout {
    @ViewInject(R.id.lv_message)
    ListView lvMessage;

    LinkedList<Info> messageList;

    ArrayAdapter<Info> adapter;

    public MessageShowView(Context context) {
        super(context);
        initView(context);
    }

    public MessageShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MessageShowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化view
     *
     * @param context 上下文
     */
    private void initView(Context context) {
        View v = x.view().inject(this, LayoutInflater.from(context), null);
        messageList = new LinkedList<>();
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                android.R.id.text1, messageList);
        lvMessage.setAdapter(adapter);
        this.addView(v);
    }

    /**
     * 更新信息展示
     *
     * @param date    信息日期
     * @param content 信息内容
     */
    public void updateMessage(Date date, String content) {
        Info info = new Info(date, content);
        messageList.push(info);
        adapter.notifyDataSetChanged();
    }

    /**
     * 信息提示对象
     */
    private class Info {
        Date date;
        String content;

        Info(Date date, String content) {
            this.date = date;
            this.content = content;
        }

        @Override
        public String toString() {
            String time = DateUtil.getDateTimeFormat(date);
            return time + "：" + content;
        }
    }

}
