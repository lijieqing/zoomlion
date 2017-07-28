package com.kstech.zoomlion.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.adapter.HeaderAdapter;

/**
 * Created by lijie on 2017/7/27.
 */

public class ItemShowView extends RelativeLayout implements IRecyclerFlingListener{
    private Context context;
    private RecyclerView rvHeader;
    private RecyclerView rvResult;
    private HeaderAdapter headerAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayout bodyContains;
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
        bodyContains = v.findViewById(R.id.ll_body);
        gridLayoutManager = new GridLayoutManager(context,1, GridLayoutManager.HORIZONTAL,false);
        headerAdapter = new HeaderAdapter(context);
        rvHeader.setLayoutManager(gridLayoutManager);
        rvHeader.setAdapter(headerAdapter);

        for (int i = 0; i < 5; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DeviceUtil.deviceHeight(context)/15);
            ItemBodyShowView ibs = new ItemBodyShowView(context);
            bodyContains.addView(ibs,params);
            Globals.addFlingListener(ibs);
        }

        rvHeader.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Globals.onScroll(ItemShowView.this,dx,dy);
            }
        });

        return v;
    }

    @Override
    public void onFling(int x, int y) {
        rvHeader.scrollBy(x,y);
    }
}
