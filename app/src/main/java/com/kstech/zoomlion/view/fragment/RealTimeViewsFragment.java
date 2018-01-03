package com.kstech.zoomlion.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.vo.RealTimeParamVO;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.adapter.DividerItemDecoration;
import com.kstech.zoomlion.view.widget.RealTimeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2018/1/3.
 */
public class RealTimeViewsFragment extends Fragment {
    RecyclerView recyclerView;
    List<RealTimeView> realTimeViewList;
    MyRealTimeAdapter adapter;
    GridLayoutManager layoutManager;

    public void init(Activity activity) {
        realTimeViewList = new ArrayList<>();
        //实例化实时展示参数组件
        for (RealTimeParamVO realTimeParamVO : Globals.modelFile.getRealTimeParamList()) {
            realTimeViewList.add(new RealTimeView(activity, realTimeParamVO));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_check, container, false);
        recyclerView = view.findViewById(R.id.fragment_rv_realtime);
        adapter = new MyRealTimeAdapter();
        layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.HORIZONTAL,
                false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        return view;
    }

    class MyRealTimeAdapter extends RecyclerView.Adapter<MyRealTimeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(View.inflate(parent.getContext(), R.layout.rv_item, null));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            FrameLayout parent = (FrameLayout) realTimeViewList.get(position).getParent();
            if (parent != null) {
                parent.removeView(realTimeViewList.get(position));
            }
            holder.fl.removeAllViews();
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                    DeviceUtil.deviceWidth(getActivity()) / 8 + 20,
                    DeviceUtil.deviceHeight(getActivity()) / 9);
            holder.fl.addView(realTimeViewList.get(position), params);
        }

        @Override
        public int getItemCount() {
            return realTimeViewList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            FrameLayout fl;

            public MyViewHolder(View root) {
                super(root);
                fl = root.findViewById(R.id.fl_gv_item);
            }

        }
    }
}
