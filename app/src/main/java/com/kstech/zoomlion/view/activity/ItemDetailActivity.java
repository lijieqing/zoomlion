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
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.DeviceUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_item_detail)
public class ItemDetailActivity extends BaseActivity {

    @ViewInject(R.id.detail_lv_param_img)
    private ListView imgListView;

    @ViewInject(R.id.detail_lv_param_result)
    private ListView resultListView;

    @ViewInject(R.id.detail_tv_name_value)
    private TextView tvItemName;

    @ViewInject(R.id.detail_tv_date_value)
    private TextView tvItemCreateTime;

    @ViewInject(R.id.detail_chart_line)
    private LineChart lineChart;

    private CheckItemDetailDataDao detailDataDao;
    private AlertDialog picShowDialog;
    private ImgDataListAdapter imgDataListAdapter;
    private List<CheckImageData> imgList = new ArrayList<>();
    private CheckItemDetailData detailData;

    Bitmap bp;
    long detailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        detailID = getIntent().getLongExtra("detailID", -1);
        detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        //先查询已存在的记录项目集合
        if (detailID != -1) {
            detailData = detailDataDao.load(detailID);
            imgList.addAll(detailData.getCheckImageDatas());
        }

        tvItemName.setText(detailData.getItemData().getItemName());

        tvItemCreateTime.setText(DateUtil.getDateTimeFormat(detailData.getStartTime()));


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
}
