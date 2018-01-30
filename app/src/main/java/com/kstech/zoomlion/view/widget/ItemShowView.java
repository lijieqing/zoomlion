package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.content.Intent;
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
import com.kstech.zoomlion.view.activity.ViewRecordActivity;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.adapter.HeaderAdapter;
import com.kstech.zoomlion.view.adapter.ResultAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lijie on 2017/7/27.
 * <p>
 * 首先说明一个item 指的是一个检测项目记录，即db中的@{@link com.kstech.zoomlion.model.db.CheckItemDetailData}类，
 * 其中的paramsValues储存的就是该检测记录的数据，对应多个@{@link CheckItemParamValueVO}
 */
public class ItemShowView extends RelativeLayout implements IRecyclerScrollListener {
    private Context context;
    private TextView tvRequireTimes;
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
     * 查看服务器数据按钮
     */
    TextView tvServerData;
    /**
     * 调试项目描述信息按钮
     */
    TextView tvItemDesc;
    /**
     * 调试项目数据统计集合
     */
    Map<String, List<Float>> avgMap;
    /**
     * 调试项目统计结果集合
     */
    List<Float> avgDatas;
    /**
     * 调试项目字典ID
     */
    String dictID;
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

    /**
     * 初始化布局，设置基本监听事件
     *
     * @return 初始化好的view对象
     */
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
        tvRequireTimes = v.findViewById(R.id.tv_require_times);
        tvServerData = v.findViewById(R.id.tv_item_showserver);
        tvItemDesc = v.findViewById(R.id.tv_item_desc);

        tvServerData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewRecordActivity.class);
                intent.putExtra("dictID", dictID);
                context.startActivity(intent);
            }
        });

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
     * 更新项目调试记录展示布局
     *
     * @param paramValues 从数据库里面查出的@{@link CheckItemDetailData} 将里面的paramValue的json值读取出来，
     *                    转换为@{@link CheckItemParamValueVO}集合
     */
    public void updateBody(@NonNull List<CheckItemDetailData> paramValues) {
        bodyContains.removeAllViews();
        Globals.seekBarListener.clear();

        Globals.addSeekBarScrollListener(this);
        for (CheckItemDetailData paramValue : paramValues) {
            //更新到最新
            paramValue.refresh();

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

    /**
     * 更新头部参数数据集合
     *
     * @param item 调试项目VO对象
     */
    public void updateHead(@NonNull CheckItemVO item) {
        dictID = item.getDictId();
        //设置调试项目基本信息
        itemTitle.setText(item.getName());
        tvRequireTimes.setText(String.valueOf("判定次数：" + item.getTimes()));
        //判断是否为必调项目
        if (!item.isRequire()) {
            cbIgnore.setChecked(true);
        } else {
            cbIgnore.setChecked(false);
        }
        //添加调试参数到header集合
        Globals.paramHeadVOs.clear();
        Globals.paramHeadVOs.addAll(item.getParamNameList());
        //刷新
        headerAdapter.notifyDataSetChanged();
        //初始化参数结果集合map
        avgMap.clear();
        for (CheckItemParamValueVO checkItemParamValueVO : Globals.paramHeadVOs) {
            avgMap.put(checkItemParamValueVO.getParamName(), new ArrayList<Float>());
        }
    }

    /**
     * 计算检测结果平均值，现在是使用数组预先储存位置，然后在添加到结果集合中，避免出现参数名和数值不对应
     *
     * @param resultMap 参数名与参数值集合的map对象
     */
    private void updateResult(Map<String, List<Float>> resultMap) {
        float sum = 0;
        int index = 0;
        //临时数组，用于存放每个参数得平均值，数组position代表该参数在paramHeadVOs中的位置
        float[] temp = new float[Globals.paramHeadVOs.size()];
        //遍历resultMap
        for (Map.Entry<String, List<Float>> stringListEntry : resultMap.entrySet()) {
            String name = stringListEntry.getKey();
            List<Float> values = stringListEntry.getValue();
            //累加
            for (Float value : values) {
                sum += value;
            }
            //取平均值
            float avg = sum / values.size();

            //查找该参数在paramHeadVOs中的位置
            for (int i = 0; i < Globals.paramHeadVOs.size(); i++) {
                if (Globals.paramHeadVOs.get(i).getParamName().equals(name)) {
                    index = i;
                }
            }
            //将平均值放到临时数组的指定位置
            temp[index] = avg;
            //数据复位
            index = 0;
            sum = 0;
        }
        //按照paramHeadVOs中的参数顺序，将平均值加入到平均值集合中
        avgDatas.clear();
        for (int i = 0; i < Globals.paramHeadVOs.size(); i++) {
            avgDatas.add(temp[i]);
        }
        resultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScroll(int x, int y) {
        rvHeader.scrollBy(x, y);
        rvResult.scrollBy(x, y);
    }
}
