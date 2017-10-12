package com.kstech.zoomlion.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.widget.ItemShowView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_check_home)
public class CheckHomeActivity extends BaseActivity {

    @ViewInject(R.id.ch_elv_item)
    private ExpandableListView itemsList;//所有调试项目集合

    @ViewInject(R.id.ch_isv)
    private ItemShowView itemShowView;//调试项目展示组件

    private List<String> groups = new ArrayList<>();//调试项目类型集合

    private ExpandItemAdapter expandItemAdapter;//expand list view 适配器

    private CheckItemVO checkItemVO;

    private int gPosition = -1;
    private int cPosition = -1;

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
                CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
                CheckItemData itemdb = itemDao.queryBuilder()
                        .where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(item.getId())))
                        .build().unique();
                List<CheckItemDetailData> ls = new ArrayList<>();
                if (itemdb != null) {
                    ls = itemdb.getCheckItemDetailDatas();
                }
                itemShowView.updateBody(ls);
                return false;
            }
        });

    }

    /**
     * Created by lijie on 2017/8/2.
     */

    private class ExpandItemAdapter extends BaseExpandableListAdapter {
        Map<String, List<CheckItemVO>> checkItemMap;
        Context context;
        List<String> groups;

        public ExpandItemAdapter(Context context, List<String> groups, Map<String, List<CheckItemVO>> checkItemMap) {
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

            if (gPosition == groupPosition && cPosition ==childPosition){
                holder.ll.setBackgroundResource(R.color.zoomLionColor);
            }else {
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
}
