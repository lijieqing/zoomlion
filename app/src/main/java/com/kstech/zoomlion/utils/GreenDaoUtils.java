package com.kstech.zoomlion.utils;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.vo.CheckItemVO;

import java.util.Date;

/**
 * Created by lijie on 2017/8/4.
 */

public final class GreenDaoUtils {
    public static void InitDBByXML(DeviceModelFile modelFile){
        CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        CheckItemDetailDataDao detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
        for (CheckItemVO checkItemVO : modelFile.getCheckItemList()) {
            CheckItemData itemData = new CheckItemData();
            itemData.setItemName(checkItemVO.getName());
            itemData.setQcId(Integer.valueOf(checkItemVO.getId()));
            long id = itemDao.insert(itemData);
            for (int i = 0; i < 3; i++) {
                CheckItemDetailData detailData = new CheckItemDetailData();
                detailData.setItemId(id);
                detailData.setParamsValues(checkItemVO.getJsonParams());
                detailData.setStartTime(new Date());
                detailDataDao.insert(detailData);
            }
        }
    }
}
