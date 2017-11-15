package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.adapter.HeaderAdapter;

import java.util.List;

/**
 * Created by lijie on 2017/7/27.
 */

/**
 * 首先说明一个item 指的是一个检测项目记录，即db中的@{@link com.kstech.zoomlion.model.db.CheckItemDetailData}类，
 * 其中的paramsValues储存的就是该检测记录的数据，对应多个@{@link CheckItemParamValueVO}
 */
public class ItemShowView extends RelativeLayout {
    private Context context;
    /**
     * 调试项目展示view的 参数头部集合 用来展示当前项目的参数名称
     */
    private RecyclerView rvHeader;
    /**
     * 是否需要跳过调试按钮
     */
    private CheckBox cbIgnore;

    private HeaderAdapter headerAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayout bodyContains;
    TextView itemTitle;

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

    private View initView() {
        View v = View.inflate(context, R.layout.check_item_show, null);
        rvHeader = v.findViewById(R.id.rv_head);
        cbIgnore = v.findViewById(R.id.ck_ignore);
        bodyContains = v.findViewById(R.id.ll_body);
        itemTitle = v.findViewById(R.id.tv_title);
        gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
        headerAdapter = new HeaderAdapter(context);
        rvHeader.setLayoutManager(gridLayoutManager);
        rvHeader.setAdapter(headerAdapter);
        rvHeader.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));

        //对header设置滑动监听，监测到后更新记录体内的布局
        rvHeader.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Globals.onHeadScroll(dx, dy);
            }
        });

        return v;
    }


    /**
     * Update body.
     *
     * @param paramValues 从数据库里面查出的@{@link CheckItemDetailData} 将里面的paramValue的json值读取出来，
     *                    转换为@{@link CheckItemParamValueVO}集合
     */
    public void updateBody(@NonNull List<CheckItemDetailData> paramValues) {
        bodyContains.removeAllViews();
        Globals.headerListener.clear();
        for (CheckItemDetailData paramValue : paramValues) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 15);
            ItemBodyShowView ibs = new ItemBodyShowView(context, paramValue);
            bodyContains.addView(ibs, params);
            Globals.addHeadScrollListener(ibs);
        }
    }

    public void updateHead(@NonNull CheckItemVO item) {
        itemTitle.setText(item.getName());
        if (!item.isRequire()) {
            cbIgnore.setChecked(true);
        } else {
            cbIgnore.setChecked(false);
        }
        Globals.paramHeadVOs.clear();
        Globals.paramHeadVOs.addAll(item.getParamNameList());
        headerAdapter.notifyDataSetChanged();
    }
}
