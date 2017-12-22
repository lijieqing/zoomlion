package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
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
 * 首先说明一个item 指的是一个检测项目记录，即db中的@{@link CheckItemDetailData}类，
 * 其中的paramsValues储存的就是该检测记录的数据，对应多个@{@link CheckItemParamValueVO}
 */


public class ItemShowViewInCheck extends RelativeLayout{
    private Context context;
    LinearLayout bodyContains;
    TextView itemTitle;

    public ItemShowViewInCheck(Context context) {
        super(context);
        this.context = context;
        this.addView(initView());
    }

    public ItemShowViewInCheck(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.addView(initView());
    }

    public ItemShowViewInCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.addView(initView());
    }

    private View initView() {
        View v = View.inflate(context, R.layout.check_item_show_in_check, null);
        bodyContains = v.findViewById(R.id.ll_body);
        itemTitle = v.findViewById(R.id.tv_title);
        return v;
    }


    /**
     * Update body.
     *
     * @param itemDBID 调试项目数据库ID
     */
    public void updateBody(long itemDBID) {
        //按时间降序获取调试项目细节数据集合
        List<CheckItemDetailData> temp = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao()
                .queryBuilder()
                .where(CheckItemDetailDataDao.Properties.ItemId.eq(itemDBID))
                .orderDesc(CheckItemDetailDataDao.Properties.StartTime)
                .build().list();

        bodyContains.removeAllViews();

        for (CheckItemDetailData paramValue : temp) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 15);
            ItemBodyShowViewInCheck ibs = new ItemBodyShowViewInCheck(context, paramValue);
            bodyContains.addView(ibs, params);
        }
    }

    public void updateHead(@NonNull CheckItemVO item) {
        itemTitle.setText(item.getName());
        Globals.paramHeadVOs.clear();
        Globals.paramHeadVOs.addAll(item.getParamNameList());
    }
}
