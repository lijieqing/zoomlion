package com.kstech.zoomlion.view;

import android.content.Context;
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
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.adapter.BodyAdapter;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;

import java.util.List;

/**
 * Created by lijie on 2017/7/28.
 */

public class ItemBodyShowView extends RelativeLayout implements IRecyclerScrollListener {
    TextView tvStartTime;
    TextView tvResult;
    TextView tvChecker;
    ImageView imgDetail;
    RecyclerView rvBody;
    BodyAdapter bodyAdapter;
    GridLayoutManager gridLayoutManager;
    List<CheckItemParamValueVO> list;
    CheckItemDetailData paramValue;

    LinearLayout llScroll;
    public ItemBodyShowView(Context context, List<CheckItemParamValueVO> list, CheckItemDetailData paramValue) {
        super(context);
        this.list = list;
        this.paramValue = paramValue;
        this.addView(initView(context));
    }

    public ItemBodyShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addView(initView(context));
    }

    public ItemBodyShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addView(initView(context));
    }

    private View initView(final Context context){
        View view = View.inflate(context, R.layout.check_item_body_show,null);
        tvStartTime = view.findViewById(R.id.tv_time_start);
        tvResult = view.findViewById(R.id.tv_item_result);
        tvChecker = view.findViewById(R.id.tv_item_checker);
        rvBody = view.findViewById(R.id.rv_body);
        llScroll = view.findViewById(R.id.ll_body_scroll);
        imgDetail = view.findViewById(R.id.iv_detail);

        tvStartTime.setText(DateUtil.getDateTimeFormat(paramValue.getStartTime()));

        imgDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"展示细节",Toast.LENGTH_SHORT).show();
            }
        });

        bodyAdapter = new BodyAdapter(context,list);
        gridLayoutManager = new GridLayoutManager(context,1, LinearLayoutManager.HORIZONTAL,false);

        rvBody.setLayoutManager(gridLayoutManager);
        rvBody.setAdapter(bodyAdapter);
        rvBody.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.HORIZONTAL_LIST));
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
        rvBody.scrollBy(x,y);
    }
}
