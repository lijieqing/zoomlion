package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.ItemFunctionUtils;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.adapter.LineChartAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调试项目记录细节展示界面
 */
@ContentView(R.layout.activity_item_detail)
public class ItemDetailActivity extends BaseActivity {

    //图片数据列表
    @ViewInject(R.id.detail_lv_param_img)
    private ListView imgListView;

    //无图片提示view
    @ViewInject(R.id.detail_tv_nopic)
    private TextView tvNoPIC;

    //值数据列表
    @ViewInject(R.id.detail_lv_param_result)
    private ListView resultListView;

    //无值数据提示view
    @ViewInject(R.id.detail_tv_novalue)
    private TextView tvNoValue;

    //当前项目名称
    @ViewInject(R.id.detail_tv_name_value)
    private TextView tvItemName;

    //当前记录创建时间
    @ViewInject(R.id.detail_tv_date_value)
    private TextView tvItemCreateTime;

    //谱图展示组件
    @ViewInject(R.id.detail_chart_line)
    private LineChart lineChart;

    //调试记录细节表 数据库操作类
    private CheckItemDetailDataDao detailDataDao;
    //图片展示弹窗组件
    private AlertDialog picShowDialog;
    //图片数据列表适配器
    private ImgDataListAdapter imgDataListAdapter;
    //图片数据集合
    private List<CheckImageData> imgList = new ArrayList<>();
    //值数据集合
    private List<CheckItemParamValueVO> params;
    //当前调试记录细节 实体类
    private CheckItemDetailData detailData;

    //用来展示的位图
    Bitmap bp;
    //当前调试记录细节 数据库ID
    long detailID;
    //图表布局适配器
    LineChartAdapter lineChartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        detailID = getIntent().getLongExtra("detailID", -1);
        detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        //先查询已存在的记录项目集合
        if (detailID != -1) {
            detailData = detailDataDao.load(detailID);
        }
        //更新项目名称和时间
        tvItemName.setText(detailData.getItemData().getItemName());
        tvItemCreateTime.setText(DateUtil.getDateTimeFormat(detailData.getStartTime()));

        int qcID = detailData.getItemData().getQcId();
        //判断是否是需要数值的项目，并做出相应布局调整
        if (ItemFunctionUtils.isNoValueItem(qcID)) {
            resultListView.setVisibility(View.GONE);
            tvNoValue.setVisibility(View.VISIBLE);
        } else {
            resultListView.setVisibility(View.VISIBLE);
            tvNoValue.setVisibility(View.GONE);
            String values = detailData.getParamsValues();
            params = ItemFunctionUtils.getValueReqParam(values);

            ResultAdapter resultAdapter = new ResultAdapter();
            resultListView.setAdapter(resultAdapter);

        }
        //判断是否是需要图片的项目，并做出相应布局调整
        if (ItemFunctionUtils.isNoPICItem(qcID)) {
            imgListView.setVisibility(View.GONE);
            tvNoPIC.setVisibility(View.VISIBLE);
        } else {
            imgListView.setVisibility(View.VISIBLE);
            tvNoPIC.setVisibility(View.GONE);

            //初始化图片列表相关参数
            imgList.addAll(detailData.getCheckImageDatas());
            picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            imgDataListAdapter = new ImgDataListAdapter();
            imgListView.setAdapter(imgDataListAdapter);
            //实现item点击事件
            imgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    new Thread() {
                        @Override
                        public void run() {
                            if (bp != null)
                                bp.recycle();
                            bp = BitmapFactory.decodeFile(imgList.get(i).getImgPath());
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });

        }

        if (ItemFunctionUtils.isSpectrumItem(qcID)) {
            chartDataInit();
        } else {
            lineChart.setNoDataText("项目:" + detailData.getItemData().getItemName() + ", 无参数采集");
        }
    }

    /**
     * 谱图数据初始化，目前并未查询数据库，随机生成了一些数据
     */
    private void chartDataInit() {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<Integer> list1 = new ArrayList<>();
                List<Integer> list2 = new ArrayList<>();
                List<Integer> list3 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    list1.add((int) (Math.random() * 50) + 10);
                    list2.add((int) (Math.random() * 80) + 10);
                    list3.add((int) (Math.random() * 100));
                }
                Map<String, List<Integer>> listMap = new HashMap<>();
                listMap.put("canshu1", list1);
                listMap.put("canshu2", list2);
                listMap.put("canshu3", list3);

                lineChartAdapter = new LineChartAdapter(lineChart, listMap);
                lineChartAdapter.setYAxis(300, 0, 15);
            }
        });
    }

    //更新图片展示界面
    public void updateDialog() {
        ImageView iv = new ImageView(this);

        iv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setImageBitmap(bp);
        if (picShowDialog.isShowing()) {
            picShowDialog.cancel();
            picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        } else {
            picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        }
    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        private final WeakReference<ItemDetailActivity> reference;

        private InnerHandler(ItemDetailActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ItemDetailActivity mActivity = reference.get();
            if (mActivity != null) {
                mActivity.updateDialog();
            }
        }
    }

    /**
     * 图片列表适配器，实现图片数据的详细展示
     */
    class ImgDataListAdapter extends BaseAdapter {

        public ImgDataListAdapter() {
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public CheckImageData getItem(int i) {
            return imgList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(ItemDetailActivity.this, R.layout.list_camera_data_item, null);
                holder.tvParamName = view.findViewById(R.id.tv_param_name);
                holder.tvDesc = view.findViewById(R.id.tv_desc);
                holder.imageView = view.findViewById(R.id.iv_param_img);
                view.setTag(holder);
            }
            holder = (ViewHolder) view.getTag();
            holder.tvParamName.setText(getItem(i).getParamName());
            holder.tvDesc.setText(getItem(i).getImgPath());
            Glide.with(ItemDetailActivity.this).load(getItem(i).getImgPath()).thumbnail(0.1f).into(holder.imageView);

            view.setMinimumHeight(DeviceUtil.deviceHeight(ItemDetailActivity.this) / 7);

            return view;
        }


        class ViewHolder {
            TextView tvParamName;
            TextView tvDesc;
            ImageView imageView;
        }
    }

    /**
     * 值列表适配器，实现项目细节记录中含值参数的展示
     */
    class ResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return params.size();
        }

        @Override
        public CheckItemParamValueVO getItem(int position) {
            return params.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(ItemDetailActivity.this, R.layout.list_result_data_item, null);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.detail_item_tv_param_name);
                holder.tvValue = convertView.findViewById(R.id.detail_item_tv_param_value);
                holder.tvMax = convertView.findViewById(R.id.detail_item_tv_param_max);
                holder.tvMin = convertView.findViewById(R.id.detail_item_tv_param_min);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String name = getItem(position).getParamName();
            holder.tvName.setText(name);
            holder.tvValue.setText(getItem(position).getValue() + " " + getItem(position).getUnit());
            holder.tvMax.setText("最大值：" + getItem(position).getValidMax());
            holder.tvMin.setText("最小值：" + getItem(position).getValidMin());

            return convertView;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvValue;
            TextView tvMax;
            TextView tvMin;
        }
    }
}
