package com.kstech.zoomlion.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.adapter.ExpandItemAdapter;
import com.kstech.zoomlion.view.widget.ItemShowView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_check_home)
public class CheckHomeActivity extends BaseActivity {

    @ViewInject(R.id.ch_elv_item)
    private ExpandableListView itemsList;//所有调试项目集合

    @ViewInject(R.id.ch_isv)
    private ItemShowView itemShowView;//调试项目展示组件

    private List<String> groups = new ArrayList<>();//调试项目类型集合

    private ExpandItemAdapter expandItemAdapter;//expand list view 适配器

    private CheckItemVO checkItemVO;

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
}
