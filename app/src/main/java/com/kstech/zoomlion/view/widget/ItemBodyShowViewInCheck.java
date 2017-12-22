package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.view.activity.ItemDetailActivity;
import com.kstech.zoomlion.view.adapter.BodyAdapter;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;

/**
 * Created by lijie on 2017/7/28.
 */

public class ItemBodyShowViewInCheck extends RelativeLayout{
    TextView tvStartTime;
    TextView tvEndTime;
    TextView tvUpload;
    TextView tvResult;
    TextView tvChecker;
    ImageView imgDetail;
    CheckItemDetailData paramValue;

    LinearLayout llScroll;

    public ItemBodyShowViewInCheck(Context context, CheckItemDetailData paramValue) {
        super(context);
        this.paramValue = paramValue;
        this.addView(initView(context));
    }

    public ItemBodyShowViewInCheck(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addView(initView(context));
    }

    public ItemBodyShowViewInCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addView(initView(context));
    }

    private View initView(final Context context) {
        View view = View.inflate(context, R.layout.check_item_body_show_in_check, null);
        tvStartTime = view.findViewById(R.id.tv_time_start);
        tvEndTime = view.findViewById(R.id.tv_time_end);
        tvUpload = view.findViewById(R.id.tv_item_upload);
        tvResult = view.findViewById(R.id.tv_item_result);
        tvChecker = view.findViewById(R.id.tv_item_checker);
        llScroll = view.findViewById(R.id.ll_body_scroll);
        imgDetail = view.findViewById(R.id.iv_detail);

        tvStartTime.setText(DateUtil.getDateTimeFormat(paramValue.getStartTime()));

        tvEndTime.setText(DateUtil.getDateTimeFormat(paramValue.getEndTime()));

        tvUpload.setText(paramValue.getUploaded()?"已同步":"未同步");

        tvResult.setText(CheckItemDetailResultEnum.getDescByCode(paramValue.getCheckResult()));

        tvChecker.setText(paramValue.getCheckerName());

        imgDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("detailID",paramValue.getCheckItemDetailId());
                context.startActivity(intent);
                Toast.makeText(context, "展示细节", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
