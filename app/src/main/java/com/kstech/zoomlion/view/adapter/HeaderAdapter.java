package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;

/**
 * Created by lijie on 2017/7/27.
 */

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.MyViewHolder> {
    private Context context;

    public HeaderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.rv_check_item,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.view.setMinimumWidth(DeviceUtil.deviceWidth(context)/15);
        holder.textView.setText(Globals.paramHeadVOs.get(position).getParamName());
    }

    @Override
    public int getItemCount() {
        return Globals.paramHeadVOs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.tv_value);
        }
    }
}
