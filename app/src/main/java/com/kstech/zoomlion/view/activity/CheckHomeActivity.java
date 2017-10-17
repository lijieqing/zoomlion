package com.kstech.zoomlion.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.vo.RealTimeParamVO;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.widget.ItemShowView;
import com.kstech.zoomlion.view.widget.RealTimeView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_check_home)
public class CheckHomeActivity extends BaseActivity {

    @ViewInject(R.id.ch_elv_item)
    private ExpandableListView itemsList;//所有调试项目集合

    @ViewInject(R.id.ch_isv)
    private ItemShowView itemShowView;//调试项目展示组件
    
    @ViewInject(R.id.ch_rv_realtimes)
    private RecyclerView realTimes;//实时显示参数集合

    private List<String> groups = new ArrayList<>();//调试项目类型集合
    
    private List<RealTimeView> inHomeRealTimeViews = new ArrayList<>();//实时参数集合

    private GridLayoutManager gridLayoutManager;//recycler view layout管理器

    private ExpandItemAdapter expandItemAdapter;//expand list view 适配器

    private CheckItemVO checkItemVO;

    private List<CheckItemDetailData> ls;
    private int gPosition = -1;
    private int cPosition = -1;
    private MyAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        groups.addAll(Globals.modelFile.checkItemMap.keySet());
        expandItemAdapter = new ExpandItemAdapter(this, groups, Globals.modelFile.checkItemMap);

        itemsList.setAdapter(expandItemAdapter);
        itemsList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                gPosition = i;
                cPosition = i1;
                expandItemAdapter.notifyDataSetChanged();

                String key = groups.get(i);
                CheckItemVO item = Globals.modelFile.checkItemMap.get(key).get(i1);
                itemShowView.updateHead(item);
                checkItemVO = item;
                ThreadManager.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
                        CheckItemData itemdb = itemDao.queryBuilder()
                                .where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(checkItemVO.getId())))
                                .build().unique();
                        if (itemdb != null) {
                            ls = itemdb.getCheckItemDetailDatas();
                        }
                        handler.sendEmptyMessage(0);
                    }
                });
                return false;
            }
        });

        for (RealTimeParamVO realTimeParamVO : Globals.modelFile.getRealTimeParamList()) {
            inHomeRealTimeViews.add(new RealTimeView(this,realTimeParamVO));
        }
        rvAdapter = new MyAdapter();

        if (inHomeRealTimeViews.size()>9){
            gridLayoutManager = new GridLayoutManager(this,2, LinearLayoutManager.HORIZONTAL,false);
        }else {
            gridLayoutManager = new GridLayoutManager(this,6, LinearLayoutManager.VERTICAL,false);
        }
        realTimes.setAdapter(rvAdapter);
        realTimes.setLayoutManager(gridLayoutManager);
        realTimes.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));
    }


    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        final WeakReference<CheckHomeActivity> reference;

        InnerHandler(CheckHomeActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CheckHomeActivity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
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
        Map<String, List<CheckItemVO>> checkItemMap;
        Context context;
        List<String> groups;

        ExpandItemAdapter(Context context, List<String> groups, Map<String, List<CheckItemVO>> checkItemMap) {
            this.context = context;
            this.checkItemMap = checkItemMap;
            this.groups = groups;
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String key = groups.get(groupPosition);
            return checkItemMap.get(key).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String key = groups.get(groupPosition);
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
            holder.tv.setText(groups.get(groupPosition));
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(context, R.layout.checktem_list_child_item, null);
                holder.tv = view.findViewById(R.id.ch_tv_list_item_child);
                holder.ll = view.findViewById(R.id.ch_ll_list_item_child);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String key = groups.get(groupPosition);
            holder.tv.setText(checkItemMap.get(key).get(childPosition).getName());

            if (gPosition == groupPosition && cPosition == childPosition) {
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
            TextView tv;
            LinearLayout ll;
        }
    }

    /**
     * RecyclerView适配器
     */
    class MyAdapter extends RecyclerView.Adapter {

        public MyAdapter() {
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            FrameLayout fl;

            public ViewHolder(View root) {
                super(root);
                fl = (FrameLayout) root.findViewById(R.id.fl_gv_item);
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
            if (parent != null){
                parent.removeView(inHomeRealTimeViews.get(position));
            }
            vh.fl.removeAllViews();
            vh.fl.addView(inHomeRealTimeViews.get(position));
        }

        @Override
        public int getItemCount() {
            return inHomeRealTimeViews.size();
        }
    }
}
