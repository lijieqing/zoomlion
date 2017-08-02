package com.kstech.zoomlion.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.view.adapter.BodyAdapter;

import java.util.List;

/**
 * Created by lijie on 2017/7/28.
 */

public class ItemBodyShowView extends RelativeLayout implements IRecyclerFlingListener{
    TextView tvStartTime;
    TextView tvResult;
    TextView tvChecker;
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

    private View initView(Context context){
        View view = View.inflate(context, R.layout.check_item_body_show,null);
        tvStartTime = view.findViewById(R.id.tv_time_start);
        tvResult = view.findViewById(R.id.tv_item_result);
        tvChecker = view.findViewById(R.id.tv_item_checker);
        rvBody = view.findViewById(R.id.rv_body);
        llScroll = view.findViewById(R.id.ll_body_scroll);

        tvStartTime.setText(DateUtil.getDateTimeFormat(paramValue.getStartTime()));

        bodyAdapter = new BodyAdapter(context,list);
        gridLayoutManager = new GridLayoutManager(context,1, LinearLayoutManager.HORIZONTAL,false);

        rvBody.setLayoutManager(gridLayoutManager);
        rvBody.setAdapter(bodyAdapter);

        return view;
    }

    @Override
    public void onFling(int x, int y) {
        rvBody.scrollBy(x,y);
    }
}
