package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.ItemFunctionUtils;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;

import java.util.List;

/**
 * Created by lijie on 2017/7/27.
 */

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.MyViewHolder> {
    private Context context;
    private List<CheckItemParamValueVO> list;
    private CheckItemDetailData itemDetailData;

    public BodyAdapter(Context context, @NonNull CheckItemDetailData itemDetailData) {
        this.context = context;
        this.itemDetailData = itemDetailData;
        this.list = JsonUtils.fromArrayJson(itemDetailData.getParamsValues(), CheckItemParamValueVO.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.rv_check_item, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int qcID = itemDetailData.getItemData().getQcId();
        CheckItemParamValueVO param = list.get(position);
        String paramName = param.getParamName();
        LogUtils.d("BodyAdapter",param.getParamName()+param.getPicReq());
        holder.view.setMinimumWidth(DeviceUtil.deviceWidth(context) / 15);
        if (param.getPicReq()) {
            holder.imageView.setBackgroundResource(R.drawable.pic);
            holder.textView.setText(list.get(position).getValue());
        } else if (ItemFunctionUtils.isSpectrumParam(paramName, qcID)) {
            holder.textView.setText(list.get(position).getValue());
            holder.imageView.setBackgroundResource(R.drawable.chart_line);
        } else {
            holder.imageView.setBackgroundResource(0);
            holder.textView.setText(list.get(position).getValue());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.tv_value);
            imageView = itemView.findViewById(R.id.iv_chart);
        }
    }
}
