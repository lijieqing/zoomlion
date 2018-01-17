package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;
import com.kstech.zoomlion.model.xmlbean.Spectrum;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.activity.ItemDetailActivity;
import com.kstech.zoomlion.view.adapter.AbstractRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/27.
 */

/**
 * 首先说明一个item 指的是一个检测项目记录，即db中的@{@link CheckItemDetailData}类，
 * 其中的paramsValues储存的就是该检测记录的数据，对应多个@{@link CheckItemParamValueVO}
 */


public class ItemShowViewInCheck extends RelativeLayout {
    /**
     * 上下文对象
     */
    private Context context;
    private ItemOperateView iov;
    /**
     * recycler view
     */
    private RecyclerView rvDetailDatas;
    /**
     * 自定义adapter 处理detail data数据集合
     */
    private DetailDataAdapter detailDataAdapter;
    /**
     * 当前调试项目名称
     */
    private TextView itemTitle;
    /**
     * 当前调试机型编号
     */
    private TextView tvDeviceNum;
    /**
     * 当前调试项目结论
     */
    private TextView tvItemResult;
    /**
     * 当前项目细节说明
     */
    private TextView tvRecordInstructions;
    private TextView tvVOInstructions;
    /**
     * 支持上拉加载布局
     */
    private RefreshLayout refreshLayout;
    /**
     * recycler 线性布局管理器
     */
    private LinearLayoutManager linearLayoutManager;
    /**
     * 调试项目细节数据集合
     */
    private List<CheckItemDetailData> detailDatas = new ArrayList<>();
    /**
     * 调试项目数据ID
     */
    private long itemDBId = -1;
    /**
     * 当前调试项目细节数据每次展示的数量
     */
    private int pageItems = 7;
    /**
     * 数据库查询时的偏移量 与limit结合使用，分页效果
     */
    private int offset = 7;

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

    /**
     * 初始化布局
     *
     * @return view
     */
    private View initView() {
        View v = View.inflate(context, R.layout.check_item_show_in_check, null);
        rvDetailDatas = v.findViewById(R.id.rv_detail_datas);
        itemTitle = v.findViewById(R.id.tv_title);
        tvDeviceNum = v.findViewById(R.id.tv_device_num);
        tvItemResult = v.findViewById(R.id.tv_item_result);
        refreshLayout = v.findViewById(R.id.detail_fresh_load);
        tvRecordInstructions = v.findViewById(R.id.isv_tv_instructions_record);
        tvVOInstructions = v.findViewById(R.id.isv_tv_instructions_vo);

        detailDataAdapter = new DetailDataAdapter(detailDatas, context);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        rvDetailDatas.setLayoutManager(linearLayoutManager);
        rvDetailDatas.setAdapter(detailDataAdapter);

        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //按时间降序获取调试项目细节数据集合
                        List<CheckItemDetailData> temp = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao()
                                .queryBuilder()
                                .where(CheckItemDetailDataDao.Properties.ItemId.eq(itemDBId))
                                .orderDesc(CheckItemDetailDataDao.Properties.StartTime)
                                .offset(offset).limit(pageItems)
                                .build().list();
                        if (temp.size() > 0) {
                            detailDatas.addAll(temp);
                            detailDataAdapter.notifyDataSetChanged();
                            offset += temp.size();
                            refreshLayout.setLoading(false);
                        } else {
                            refreshLayout.setLoading(false);
                        }
                    }
                }, 1000);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SystemClock.sleep(100);
                refreshLayout.setRefreshing(false);
            }
        });
        return v;
    }


    /**
     * 更新调试项目展示组件详细信息
     *
     * @param itemDBID 调试项目数据库ID
     */
    public void updateBody(long itemDBID) {
        itemDBId = itemDBID;

        CheckItemData itemData = MyApplication.getApplication().getDaoSession().getCheckItemDataDao().load(itemDBID);
        tvDeviceNum.setText(itemData.getCheckRecord().getDeviceIdentity());
        String result = CheckItemResultEnum.getDescByCode(itemData.getCheckResult());
        tvItemResult.setText(result);

        detailDatas.clear();
        //按时间降序获取调试项目细节数据集合
        List<CheckItemDetailData> temp = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao()
                .queryBuilder()
                .where(CheckItemDetailDataDao.Properties.ItemId.eq(itemDBID))
                .orderDesc(CheckItemDetailDataDao.Properties.StartTime)
                .limit(pageItems)
                .build().list();

        detailDatas.addAll(temp);
        detailDataAdapter.notifyDataSetChanged();
        updateInstructions(itemData);
    }

    /**
     * 更新调试项目说明
     *
     * @param itemData 调试项目数据对象
     */
    private void updateInstructions(CheckItemData itemData) {
        //项目调试记录相关说明
        String itemName = itemData.getItemName();
        int sumCount = itemData.getSumCounts();
        int localSize = detailDatas.size();
        int requireTimes = Globals.currentCheckItem.getTimes();
        String result = CheckItemResultEnum.getDescByCode(itemData.getCheckResult());
        String recordModel = context.getResources().getString(R.string.item_instructions_record);
        String instruction = String.format(recordModel, itemName, sumCount, localSize, requireTimes, result);
        tvRecordInstructions.setText(instruction);
        //调试项目标准相关说明
        int timeout = Globals.currentCheckItem.getQcTimeout();
        int specInterval;
        Spectrum spec = Globals.currentCheckItem.getSpectrum();
        StringBuilder specParams = new StringBuilder();
        int paramNum;
        if (spec == null || spec.getSpecParams().size() == 0) {
            specParams.append("无谱图参数");
            specInterval = 0;
        } else {
            for (SpecParam specParam : spec.getSpecParams()) {
                specParams.append(specParam.getParam()).append(" ");
            }
            specInterval = spec.getInterval();
        }
        paramNum = Globals.currentCheckItem.getParamNameList().size();

        String voModel = context.getResources().getString(R.string.item_instructions_vo);
        instruction = String.format(voModel, timeout, specParams.toString(), specInterval, paramNum);
        tvVOInstructions.setText(instruction);

    }

    /**
     * 更新调试项目展示组件头部信息
     *
     * @param item
     */
    public void updateHead(@NonNull CheckItemVO item) {
        itemTitle.setText(item.getName());
        Globals.paramHeadVOs.clear();
        Globals.paramHeadVOs.addAll(item.getParamNameList());
    }

    /**
     * 绑定对应的iov操作组件
     *
     * @param iov ItemOperateView
     */
    public void bindIOV(ItemOperateView iov) {
        this.iov = iov;
    }

    /**
     * 继承AbstractRecyclerAdapter 实现可以添加Header和Footer功能
     */
    class DetailDataAdapter extends AbstractRecyclerAdapter<CheckItemDetailData> {
        Context mContext;

        public DetailDataAdapter(List<CheckItemDetailData> data, Context mContext) {
            super(data, mContext);
            this.mContext = mContext;
        }

        @Override
        protected MyHolder onCreateNormalViewHolder(ViewGroup parent) {
            View v = getLayout(parent, R.layout.simple_detail_data_layout);
            return new MyHolder(v);
        }

        @Override
        protected void onBindNormalViewHolder(List<CheckItemDetailData> datas, MyHolder holder, int position) {
            final CheckItemDetailData detailData = datas.get(position);
            TextView tvStartTime = holder.itemView.findViewById(R.id.tv_time_start);
            TextView tvEndTime = holder.itemView.findViewById(R.id.tv_time_end);
            TextView tvUpload = holder.itemView.findViewById(R.id.tv_item_upload);
            TextView tvResult = holder.itemView.findViewById(R.id.tv_item_result);
            TextView tvChecker = holder.itemView.findViewById(R.id.tv_item_checker);
            ImageView imgDetail = holder.itemView.findViewById(R.id.iv_detail);

            tvStartTime.setText(DateUtil.getDateTimeFormat(detailData.getStartTime()));

            tvEndTime.setText(DateUtil.getDateTimeFormat(detailData.getEndTime()));

            tvUpload.setText(detailData.getUploaded() ? "已同步" : "未同步");

            tvResult.setText(CheckItemDetailResultEnum.getDescByCode(detailData.getCheckResult()));

            tvChecker.setText(detailData.getCheckerName());

            imgDetail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iov.isInBlur()) {
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("detailID", detailData.getCheckItemDetailId());
                        context.startActivity(intent);
                        Toast.makeText(context, "展示细节", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "调试中，无法访问记录", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
