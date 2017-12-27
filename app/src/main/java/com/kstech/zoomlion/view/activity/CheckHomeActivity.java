package com.kstech.zoomlion.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.vo.RealTimeParamVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.widget.ClearFocusByDownEditView;
import com.kstech.zoomlion.view.widget.ItemShowView;
import com.kstech.zoomlion.view.widget.RealTimeView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调试项目引导界面
 *
 * @author lijie
 */
@ContentView(R.layout.activity_check_home)
public class CheckHomeActivity extends BaseActivity {

    //所有调试项目集合
    @ViewInject(R.id.ch_elv_item)
    private ExpandableListView itemsList;

    //调试项目展示组件
    @ViewInject(R.id.ch_isv)
    private ItemShowView itemShowView;

    //实时显示参数集合
    @ViewInject(R.id.ch_rv_realtimes)
    private RecyclerView realTimes;

    //开始调试按钮
    @ViewInject(R.id.ch_tv_start_check)
    private TextView tvStartCheck;

    //调试设备类型
    @ViewInject(R.id.check_home_tv_device_type)
    private TextView tvDeviceType;

    //调试设备编号
    @ViewInject(R.id.check_home_tv_device_identity)
    private TextView tvDeviceIdentity;

    //根布局
    @ViewInject(R.id.ll_check_home)
    private LinearLayout llRoot;

    //整机调试记录描述
    @ViewInject(R.id.check_home_et_admin_desc)
    private ClearFocusByDownEditView etDesc;

    private List<RealTimeView> inHomeRealTimeViews = new ArrayList<>();//实时参数集合

    private GridLayoutManager gridLayoutManager;//recycler view layout管理器

    private ExpandItemAdapter expandItemAdapter;//expand list view 适配器

    //当前调试项目所包含的 调试细节记录表集合
    private List<CheckItemDetailData> ls = new ArrayList<>();

    private RealTimeAdapter rvAdapter;

    int lastGroup = 0;
    private static final int ITEM_RECORD_LOADED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        editTextInit(etDesc, this);

        expandItemAdapter = new ExpandItemAdapter(this, Globals.modelFile.checkItemMap);

        itemsList.setAdapter(expandItemAdapter);
        //设置调试项目列表的点击事件
        itemsList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                //赋值坐标
                Globals.groupPosition = i;
                Globals.childPosition = i1;

                //获取当前调试项目的 group 和 value
                String key = Globals.groups.get(i);
                CheckItemVO item = Globals.modelFile.checkItemMap.get(key).get(i1);
                Globals.currentCheckItem = item;
                //更新 调试项目展示组件信息
                updateView(item);
                //刷新调试项目列表
                expandItemAdapter.notifyDataSetChanged();

                if (lastGroup != i) {
                    Globals.setSelectedItem(expandableListView);
                    lastGroup = i;
                }
                return false;
            }
        });

        //实例化实时展示参数组件
        for (RealTimeParamVO realTimeParamVO : Globals.modelFile.getRealTimeParamList()) {
            inHomeRealTimeViews.add(new RealTimeView(this, realTimeParamVO));
        }
        rvAdapter = new RealTimeAdapter();

        if (inHomeRealTimeViews.size() > 9) {
            gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false);
        }
        realTimes.setAdapter(rvAdapter);
        realTimes.setLayoutManager(gridLayoutManager);
        realTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        realTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void updateView(CheckItemVO item) {
        itemShowView.updateHead(item);
        //清空调试细节记录表
        ls.clear();
        //查询数据库，获取调试项目细节记录 数据
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
                //获取当前的调试项目记录数据
                CheckItemData itemdb = itemDao.queryBuilder()
                        .where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(Globals.currentCheckItem.getId())))
                        .build().unique();
                if (itemdb != null) {
                    CheckItemDetailDataDao detail = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
                    //根据数据库ID 获取调试项目细节数据
                    List<CheckItemDetailData> temp = detail.queryBuilder()
                            .where(CheckItemDetailDataDao.Properties.ItemId.eq(itemdb.getCheckItemId()))
                            .orderDesc(CheckItemDetailDataDao.Properties.StartTime)
                            .build().list();
                    ls.addAll(temp);
                }
                handler.sendEmptyMessage(ITEM_RECORD_LOADED);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String key = Globals.groups.get(Globals.groupPosition);
        CheckItemVO item = Globals.modelFile.checkItemMap.get(key).get(Globals.childPosition);
        Globals.currentCheckItem = item;

        updateView(item);
        //重新进入页面时刷新列表
        expandItemAdapter.notifyDataSetChanged();

        Globals.setSelectedItem(itemsList);
    }

    /**
     * 点击事件 处理
     *
     * @param view
     */
    @Event(value = {R.id.ch_tv_start_check})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ch_tv_start_check:
                if (Globals.currentCheckItem == null) {
                    Toast.makeText(CheckHomeActivity.this, "未选择调试项目", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CheckHomeActivity.this, ItemCheckActivity.class);
                    intent.putExtra("itemID", Globals.currentCheckItem.getId());
                    startActivity(intent);
                }
                break;
        }
    }


    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        /**
         * The Reference.
         */
        final WeakReference<CheckHomeActivity> reference;

        /**
         * Instantiates a new Inner handler.
         *
         * @param activity the activity
         */
        InnerHandler(CheckHomeActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CheckHomeActivity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case ITEM_RECORD_LOADED:
                        activity.itemShowView.updateBody(activity.ls);
                        break;
                }
            }

        }
    }

    /**
     * Created by lijie on 2017/8/2.
     */

    private class ExpandItemAdapter extends BaseExpandableListAdapter {
        /**
         * The Check item map.
         */
        Map<String, List<CheckItemVO>> checkItemMap;
        /**
         * The Context.
         */
        Context context;

        /**
         * Instantiates a new Expand item adapter.
         *
         * @param context      the context
         * @param checkItemMap the check item map
         */
        ExpandItemAdapter(Context context, Map<String, List<CheckItemVO>> checkItemMap) {
            this.context = context;
            this.checkItemMap = checkItemMap;
        }

        @Override
        public int getGroupCount() {
            return Globals.groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String key = Globals.groups.get(groupPosition);
            return checkItemMap.get(key).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return Globals.groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = Globals.groups.get(groupPosition);
            return checkItemMap.get(key).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(context, R.layout.checktem_list_group_item, null);
                holder.tv = view.findViewById(R.id.ch_tv_list_item_parent);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tv.setText(Globals.groups.get(groupPosition));
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(context, R.layout.checktem_list_child_item, null);
                holder.tv = view.findViewById(R.id.ch_tv_list_item_child);
                holder.tvSum = view.findViewById(R.id.ch_tv_list_item_sum);
                holder.tvPass = view.findViewById(R.id.ch_tv_list_item_passnum);
                holder.ll = view.findViewById(R.id.ch_ll_list_item_child);
                holder.iv = view.findViewById(R.id.ch_iv_list_item_status);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String key = Globals.groups.get(groupPosition);
            CheckItemVO item = checkItemMap.get(key).get(childPosition);
            holder.tv.setText(item.getName());

            CheckItemData itemData = MyApplication.getApplication().getDaoSession().getCheckItemDataDao().queryBuilder()
                    .where(CheckItemDataDao.Properties.QcId.eq(item.getId()),
                            CheckItemDataDao.Properties.RecordId.eq(Globals.recordID))
                    .build().unique();

            int result = itemData.getCheckResult();
            switch (result) {
                case 0:
                    holder.iv.setBackgroundResource(R.drawable.circle_item_status_unstart);
                    break;
                case 1:
                    holder.iv.setBackgroundResource(R.drawable.circle_item_status_pass);
                    break;
                case 2:
                    holder.iv.setBackgroundResource(R.drawable.circle_item_status_unpass);
                    break;
            }
            int sum = itemData.getCheckItemDetailDatas().size();
            int pass = 0;
            for (CheckItemDetailData checkItemDetailData : itemData.getCheckItemDetailDatas()) {
                if (checkItemDetailData.getCheckResult().equals(CheckItemDetailResultEnum.PASS.getCode())) {
                    pass++;
                }
            }
            holder.tvSum.setText(String.valueOf(sum));
            holder.tvPass.setText(String.valueOf(pass));

            if (Globals.groupPosition == groupPosition && Globals.childPosition == childPosition) {
                holder.ll.setBackgroundResource(R.color.zoomLionColor);
            } else {
                holder.ll.setBackgroundResource(R.color.itemNoSelect);
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class ViewHolder {
            ImageView iv;
            TextView tv;
            TextView tvSum;
            TextView tvPass;
            LinearLayout ll;
        }
    }

    /**
     * RecyclerView适配器
     */
    class RealTimeAdapter extends RecyclerView.Adapter {

        public RealTimeAdapter() {
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            FrameLayout fl;

            public ViewHolder(View root) {
                super(root);
                fl = root.findViewById(R.id.fl_gv_item);
            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(parent.getContext(), R.layout.rv_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder vh = (ViewHolder) holder;
            FrameLayout parent = (FrameLayout) inHomeRealTimeViews.get(position).getParent();
            if (parent != null) {
                parent.removeView(inHomeRealTimeViews.get(position));
            }
            vh.fl.removeAllViews();
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                    DeviceUtil.deviceWidth(CheckHomeActivity.this) / 6,
                    DeviceUtil.deviceWidth(CheckHomeActivity.this) / 17);
            vh.fl.addView(inHomeRealTimeViews.get(position), params);
        }

        @Override
        public int getItemCount() {
            return inHomeRealTimeViews.size();
        }
    }
}
