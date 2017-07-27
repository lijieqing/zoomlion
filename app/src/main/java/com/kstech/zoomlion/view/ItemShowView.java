package com.kstech.zoomlion.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.view.adapter.HeaderAdapter;

/**
 * Created by lijie on 2017/7/27.
 */

public class ItemShowView extends RelativeLayout {
    private Context context;
    private RecyclerView rvHeader;
    private RecyclerView rvResult;
    private HeaderAdapter headerAdapter;
    GridLayoutManager gridLayoutManager;
    public ItemShowView(Context context) {
        super(context);
        this.context = context;
        this.addView(initView());
    }

    public ItemShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.addView(initView());
    }

    public ItemShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.addView(initView());
    }

    private View initView(){
        View v = View.inflate(context, R.layout.check_item_show,null);
        rvHeader = v.findViewById(R.id.rv_head);
        rvResult = v.findViewById(R.id.rv_result);
        gridLayoutManager = new GridLayoutManager(context,1, GridLayoutManager.HORIZONTAL,false);
        headerAdapter = new HeaderAdapter(context);
        rvHeader.setLayoutManager(gridLayoutManager);
        rvHeader.setAdapter(headerAdapter);

        return v;
    }



}
