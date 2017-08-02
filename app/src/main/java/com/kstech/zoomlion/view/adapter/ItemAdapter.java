package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kstech.zoomlion.model.vo.CheckItemVO;

import java.util.List;

/**
 * Created by lijie on 2017/8/2.
 */

public class ItemAdapter extends BaseAdapter {
    List<CheckItemVO> itemVOs;
    Context context;

    public ItemAdapter(List<CheckItemVO> itemVOs, Context context) {
        this.itemVOs = itemVOs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemVOs.size();
    }

    @Override
    public CheckItemVO getItem(int i) {
        return itemVOs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(context,android.R.layout.simple_list_item_1,null);
            holder.tv = view.findViewById(android.R.id.text1);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(itemVOs.get(i).getName());
        return view;
    }

    private class ViewHolder {
        TextView tv;
    }

}
