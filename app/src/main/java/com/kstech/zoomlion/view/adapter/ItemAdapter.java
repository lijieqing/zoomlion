package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;

import java.util.List;

/**
 * Created by lijie on 2017/8/2.
 */

public class ItemAdapter extends AbstractRecyclerAdapter<CheckItemVO> {

    public ItemAdapter(List<CheckItemVO> data, Context mContext) {
        super(data, mContext);
    }

    @Override
    protected MyHolder onCreateNormalViewHolder(ViewGroup parent) {
        View v = getLayout(parent, R.layout.checktem_list_child_item);
        return new MyHolder(v);
    }

    @Override
    protected void onBindNormalViewHolder(List<CheckItemVO> datas, MyHolder holder, int position) {
        TextView tvValue = holder.itemView.findViewById(R.id.ch_tv_list_item_child);
        TextView tvSum = holder.itemView.findViewById(R.id.ch_tv_list_item_sum);
        TextView tvPass = holder.itemView.findViewById(R.id.ch_tv_list_item_passnum);
        LinearLayout ll = holder.itemView.findViewById(R.id.ch_ll_list_item_child);
        ImageView iv = holder.itemView.findViewById(R.id.ch_iv_list_item_status);
        CheckItemVO item = datas.get(position);
        tvValue.setText(item.getName());

        CheckItemData itemData = MyApplication.getApplication().getDaoSession().getCheckItemDataDao().queryBuilder()
                .where(CheckItemDataDao.Properties.QcId.eq(item.getId()),
                        CheckItemDataDao.Properties.RecordId.eq(Globals.recordID))
                .build().unique();

        int result = itemData.getCheckResult();
        switch (result) {
            case 0:
                iv.setBackgroundResource(R.drawable.circle_item_status_unstart);
                break;
            case 1:
                iv.setBackgroundResource(R.drawable.circle_item_status_pass);
                break;
            case 2:
                iv.setBackgroundResource(R.drawable.circle_item_status_unpass);
                break;
        }
        int sum = itemData.getCheckItemDetailDatas().size();
        int pass = 0;
        for (CheckItemDetailData checkItemDetailData : itemData.getCheckItemDetailDatas()) {
            if (checkItemDetailData.getCheckResult().equals(CheckItemDetailResultEnum.PASS.getCode())) {
                pass++;
            }
        }
        tvSum.setText(String.valueOf(sum));
        tvPass.setText(String.valueOf(pass));

        if (Globals.childPosition == position) {
            ll.setBackgroundResource(R.color.zoomLionColor);
        } else {
            ll.setBackgroundResource(R.color.itemNoSelect);
        }

    }

    public String getGroupName(int position) {
        int sumCount = -1;
        for (int i = 0; i < Globals.groups.size(); i++) {
            String key = Globals.groups.get(i);
            List<CheckItemVO> itemList = Globals.modelFile.checkItemMap.get(key);
            sumCount += itemList.size();

            if (position <= sumCount) {
                return key;
            }
        }
        return null;
    }

}
