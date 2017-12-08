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
public class ItemBodyShowView extends RelativeLayout implements IRecyclerScrollListener {
    /**
     * 开始时间.
     */
    TextView tvStartTime;
    /**
     * 调试结论
     */
    TextView tvResult;
    /**
     * 调试员
     */
    TextView tvChecker;
    /**
     * 查看详情按钮
     */
    ImageView imgDetail;
    /**
     * 数据上传按钮
     */
    ImageView imgUpload;
    /**
     * 参数显示view
     */
    RecyclerView rvBody;
    /**
     * The Body adapter.
     */
    BodyAdapter bodyAdapter;
    /**
     * The Grid layout manager.
     */
    GridLayoutManager gridLayoutManager;
    /**
     * 调试项目细节数据
     */
    CheckItemDetailData paramValue;

    /**
     * The Ll scroll.
     */
    LinearLayout llScroll;

    /**
     * Instantiates a new Item body show view.
     *
     * @param context    the context
     * @param paramValue the param value
     */
    public ItemBodyShowView(Context context, CheckItemDetailData paramValue) {
        super(context);
        this.paramValue = paramValue;
        this.addView(initView(context));
    }

    /**
     * Instantiates a new Item body show view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ItemBodyShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addView(initView(context));
    }

    /**
     * Instantiates a new Item body show view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ItemBodyShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addView(initView(context));
    }

    private View initView(final Context context) {
        View view = View.inflate(context, R.layout.check_item_body_show, null);
        tvStartTime = view.findViewById(R.id.tv_time_start);
        tvResult = view.findViewById(R.id.tv_item_result);
        tvChecker = view.findViewById(R.id.tv_item_checker);
        rvBody = view.findViewById(R.id.rv_body);
        llScroll = view.findViewById(R.id.ll_body_scroll);
        imgDetail = view.findViewById(R.id.iv_detail);
        imgUpload = view.findViewById(R.id.iv_upload_status);

        //设置开始时间
        tvStartTime.setText(DateUtil.getDateTimeFormat(paramValue.getStartTime()));
        //设置项目结论
        tvResult.setText(CheckItemDetailResultEnum.getDescByCode(paramValue.getCheckResult()));
        //设置调试员
        tvChecker.setText(paramValue.getCheckerName());
        //设置进入详情监听事件
        imgDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("detailID", paramValue.getCheckItemDetailId());
                context.startActivity(intent);
            }
        });
        //设置记录上传状态
        if (paramValue.getUploaded()) {
            imgUpload.setBackgroundResource(R.drawable.pic_upload_success);
        } else {
            imgUpload.setBackgroundResource(R.drawable.pic_upload_fail);
        }
        imgUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据上传状态执行不同的点击事件
            }
        });

        bodyAdapter = new BodyAdapter(context, paramValue);
        gridLayoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);

        rvBody.setLayoutManager(gridLayoutManager);
        rvBody.setAdapter(bodyAdapter);
        rvBody.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));

        //此处主要是为了让recycler view 无法正常相应监听事件
        rvBody.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onScroll(int x, int y) {
        rvBody.scrollBy(x, y);
    }
}
