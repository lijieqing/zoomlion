package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;

import java.util.List;

/**
 * Created by lijie on 2017/7/27.
 */

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.MyViewHolder> {
    private Context context;
    private List<CheckItemParamValueVO> list;

    public BodyAdapter(Context context,@NonNull List<CheckItemParamValueVO> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.rv_check_item,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.view.setMinimumWidth(DeviceUtil.deviceWidth(context)/15);
        holder.textView.setText(list.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
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
