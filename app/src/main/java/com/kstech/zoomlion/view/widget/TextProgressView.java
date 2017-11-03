package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.DeviceUtil;

/**
 * Created by lijie on 2017/11/2.
 */
public class TextProgressView extends RelativeLayout {
    private ProgressBar pb;
    private TextView tv;

    /**
     * Instantiates a new Text progress view.
     *
     * @param context the context
     */
    public TextProgressView(Context context) {
        super(context);
        this.addView(initView(context));
    }

    /**
     * Instantiates a new Text progress view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public TextProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addView(initView(context));
    }

    /**
     * Instantiates a new Text progress view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public TextProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addView(initView(context));
    }

    private View initView(Context context) {
        View view = View.inflate(context, R.layout.text_progress, null);

        pb = view.findViewById(R.id.pb_progress_text);
        tv = view.findViewById(R.id.tv_progress_text);
        view.setMinimumWidth(DeviceUtil.deviceWidth(context) / 2);
        view.setMinimumHeight(DeviceUtil.deviceHeight(context) / 6);
        return view;
    }

    /**
     * 更新进度条显示
     *
     * @param msg      the msg
     * @param progress the progress
     */
    public void updateProgress(String msg, int progress) {
        if (progress > 100) {
            progress = 100;
        }
        pb.setProgress(progress);

        tv.setText(msg);
    }

    /**
     * 重置进度条各个显示组件
     */
    public void reset() {
        pb.setProgress(0);
        tv.setText("");
    }
}
