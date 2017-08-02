package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kstech.zoomlion.model.vo.CheckItemVO;

import java.util.List;
import java.util.Map;

/**
 * Created by lijie on 2017/8/2.
 */

public class ExpandItemAdapter extends BaseExpandableListAdapter {
    Map<String,List<CheckItemVO>> checkItemMap;
    Context context;
    List<String> groups;

    public ExpandItemAdapter(Context context, List<String> groups, Map<String,List<CheckItemVO>> checkItemMap) {
        this.context = context;
        this.checkItemMap = checkItemMap;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String key = groups.get(i);
        return checkItemMap.get(key).size();
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        String key = groups.get(i);
        return checkItemMap.get(key).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(context,android.R.layout.simple_list_item_1,null);
            holder.tv = view.findViewById(android.R.id.text1);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(groups.get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(context,android.R.layout.simple_list_item_1,null);
            holder.tv = view.findViewById(android.R.id.text1);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        String key = groups.get(i);
        holder.tv.setText(checkItemMap.get(key).get(i1).getName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    private class ViewHolder {
        TextView tv;
    }
}
