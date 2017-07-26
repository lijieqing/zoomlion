package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;

/**
 * Created by lijie on 2017/7/26.
 */

public class ImgDataListAdapter extends BaseAdapter {
    private Context context;

    public ImgDataListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Globals.values.size();
    }

    @Override
    public CheckImageData getItem(int i) {
        return Globals.values.get(i);
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
            view = View.inflate(context, R.layout.list_camera_data_item,null);
            holder.tvParamName = view.findViewById(R.id.tv_param_name);
            holder.tvDesc = view.findViewById(R.id.tv_desc);
            holder.imageView = view.findViewById(R.id.iv_param_img);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        holder.tvParamName.setText(getItem(i).getParamName());
        holder.tvDesc.setText(getItem(i).getImgPath());
        Glide.with(context).load(getItem(i).getImgPath()).thumbnail(0.1f).into(holder.imageView);

        view.setMinimumHeight(DeviceUtil.deviceHeight(context)/7);

        return view;
    }


    class ViewHolder{
        TextView tvParamName;
        TextView tvDesc;
        ImageView imageView;
    }
}
