package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kstech.zoomlion.R;

import java.util.List;

/**
 * Created by lijie on 2017/12/27.
 */

public class ResultAdapter extends AbstractRecyclerAdapter<Float> {
    public ResultAdapter(List<Float> data, Context mContext) {
        super(data, mContext);
    }

    @Override
    protected MyHolder onCreateNormalViewHolder(ViewGroup parent) {
        View v = getLayout(parent, R.layout.rv_check_item);
        return new MyHolder(v);
    }

    @Override
    protected void onBindNormalViewHolder(List<Float> datas, MyHolder holder, int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_value);
        Float data = datas.get(position);
        tv.setText(String.valueOf(data));
    }
}
