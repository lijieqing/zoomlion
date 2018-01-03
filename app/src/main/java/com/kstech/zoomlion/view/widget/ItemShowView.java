package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.adapter.HeaderAdapter;
import com.kstech.zoomlion.view.adapter.ResultAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lijie on 2017/7/27.
 */

/**
 * 首先说明一个item 指的是一个检测项目记录，即db中的@{@link com.kstech.zoomlion.model.db.CheckItemDetailData}类，
 * 其中的paramsValues储存的就是该检测记录的数据，对应多个@{@link CheckItemParamValueVO}
 */
public class ItemShowView extends RelativeLayout implements IRecyclerScrollListener {
    private Context context;
    /**
     * 调试项目展示view的 参数头部集合 用来展示当前项目的参数名称
     */
    private RecyclerView rvHeader;
    /**
     * 调试项目记录统计展示
     */
    private RecyclerView rvResult;
    /**
     * 是否需要跳过调试按钮
     */
    private CheckBox cbIgnore;
    /**
     * 用来横向调整调试记录的seek bar
     */
    private SeekBar seekBar;
    /**
     * 调试项目头部RecyclerView adapter对象
     */
    private HeaderAdapter headerAdapter;
    /**
     * 调试项目结论统计RecyclerView adapter对象
     */
    private ResultAdapter resultAdapter;
    /**
     * 调试细节记录展示布局
     */
    LinearLayout bodyContains;
    /**
     * 调试项目名称
     */
    TextView itemTitle;
    /**
     * 调试项目数据统计集合
     */
    Map<String, List<Float>> avgMap;
    /**
     * 调试项目统计结果集合
     */
    List<Float> avgDatas;
    private static String TAG = "ItemShowView";

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
        avgMap = new HashMap<>();
        avgDatas = new ArrayList<>();
        View v = View.inflate(context, R.layout.check_item_show, null);
        rvHeader = v.findViewById(R.id.rv_head);
        rvResult = v.findViewById(R.id.rv_result);
        cbIgnore = v.findViewById(R.id.ck_ignore);
        bodyContains = v.findViewById(R.id.ll_body);
        itemTitle = v.findViewById(R.id.tv_title);
        seekBar = v.findViewById(R.id.sb);

        headerAdapter = new HeaderAdapter(context);
        resultAdapter = new ResultAdapter(avgDatas, context);
        rvHeader.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        rvHeader.setAdapter(headerAdapter);
        rvHeader.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
        rvResult.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        rvResult.setAdapter(resultAdapter);
        rvResult.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));

        //返回true 直接消费掉touch事件，可以实现禁止RecyclerView滑动
        rvHeader.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        rvResult.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        //对seek bar设置滑动监听，监测到后更新记录体内的布局
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int move;
                if (i > 50) {
                    move = -20;
                } else if (i < 50) {
                    move = 20;
                } else {
                    move = 0;
                }
                Globals.onSeekBarScroll(move, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(50);
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
        Globals.seekBarListener.clear();

        Globals.addSeekBarScrollListener(this);
        for (CheckItemDetailData paramValue : paramValues) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.deviceHeight(context) / 15);
            ItemBodyShowView ibs = new ItemBodyShowView(context, paramValue);
            bodyContains.addView(ibs, params);
            Globals.addSeekBarScrollListener(ibs);

            //数据处理，用于调试结果统计
            String paramDatas = paramValue.getParamsValues();
            List<CheckItemParamValueVO> paramVos = JsonUtils.fromArrayJson(paramDatas, CheckItemParamValueVO.class);
            for (CheckItemParamValueVO paramVo : paramVos) {

                if (paramVo.getValueReq()) {
                    String val = paramVo.getValue();
                    if (TextUtils.isEmpty(val)) {
                        val = "0";
                    }
                    float v = Float.valueOf(val);
                    avgMap.get(paramVo.getParamName()).add(v);
                }
            }
        }
        updateResult(avgMap);
    }

    public void updateHead(@NonNull CheckItemVO item) {
        avgMap.clear();
        avgDatas.clear();
        itemTitle.setText(item.getName());
        if (!item.isRequire()) {
            cbIgnore.setChecked(true);
        } else {
            cbIgnore.setChecked(false);
        }
        Globals.paramHeadVOs.clear();
        Globals.paramHeadVOs.addAll(item.getParamNameList());
        headerAdapter.notifyDataSetChanged();

        for (CheckItemParamValueVO checkItemParamValueVO : item.getParamNameList()) {
            avgMap.put(checkItemParamValueVO.getParamName(), new ArrayList<Float>());
        }
    }

    private void updateResult(Map<String, List<Float>> resultMap) {
        float sum = 0;
        int index = 0;
        for (Map.Entry<String, List<Float>> stringListEntry : resultMap.entrySet()) {
            String name = stringListEntry.getKey();
            List<Float> values = stringListEntry.getValue();
            for (Float value : values) {
                sum += value;
            }
            float avg = sum / values.size();
            for (int i = 0; i < Globals.paramHeadVOs.size(); i++) {
                if (Globals.paramHeadVOs.get(i).getParamName().equals(name)) {
                    index = i;
                }
            }
            if (index <= avgDatas.size()) {
                avgDatas.add(index, avg);
            } else {
                avgDatas.add(avg);
            }
            index = 0;
            sum = 0;
        }
        resultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScroll(int x, int y) {
        rvHeader.scrollBy(x, y);
        rvResult.scrollBy(x, y);
    }
}
