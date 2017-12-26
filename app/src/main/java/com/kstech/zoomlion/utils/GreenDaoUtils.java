package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.engine.device.DeviceModelFile;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;

import java.util.Date;

/**
 * Created by lijie on 2017/8/4.
 */

public final class GreenDaoUtils {
    public static void InitDBByXML(DeviceModelFile modelFile) {
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

    public static void initCheckRecord(@NonNull DeviceModelFile modelFile) {
        CheckRecordDao recordDao = MyApplication.getApplication().getDaoSession().getCheckRecordDao();
        CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        CheckItemDetailDataDao itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        CheckRecord record = new CheckRecord();

        //传入出厂编号
        record.setDeviceIdentity("000000test000");

        //传入检验记录名称
        record.setCheckRecordName(modelFile.getDeviceName());

        //// TODO: 2017/8/10 需要传入检验员ID
        record.setCheckerId(6l);

        //传入当前检测记录状态
        record.setCurrentStatus(CheckRecordResultEnum.UNFINISH.getCode());

        //传入记录创建时间
        record.setCreateTime(new Date());

        //该检测记录内所有检测次数统计，复位
        record.setSumCounts(0);

        //该检测记录内所有检测不合格次数统计 复位
        record.setUnpassCounts(0);

        //服务器上纪录的机型第几次检测
        record.setCheckTimes(0);

        //该检测记录是否已上传,初始值为false,为true意味着关联的所有数据已上传
        record.setUploaded(false);
        //插入测试记录
        long recordID = recordDao.insert(record);

        for (CheckItemVO checkItemVO : modelFile.getCheckItemList()) {
            CheckItemData itemData = new CheckItemData();

            //传入检测记录ID
            itemData.setRecordId(recordID);
            //设置通讯时用到的检测项目ID
            itemData.setQcId(Integer.parseInt(checkItemVO.getId()));
            //设置检测项目名称
            itemData.setItemName(checkItemVO.getName());
            //设置当前检测结果
            itemData.setCheckResult(CheckItemResultEnum.UNFINISH.getCode());
            //初始化总检测次数
            itemData.setSumCounts(0);
            //初始化未合格次数
            itemData.setPassCounts(0);
            //默认不跳过检测
            itemData.setSkipCheck(!checkItemVO.isRequire());
            //默认未上传
            itemData.setUploaded(false);

            long itemID = itemDao.insert(itemData);
            /**
             * 向下内容为检测项目细节表的插入，纯粹是测试数据，正常插入是在检测完成后才进行的
             */
            CheckItemDetailData itemDetailData = new CheckItemDetailData();
            //传入检测项目ID
            itemDetailData.setItemId(itemID);
            //传入检验员ID
            itemDetailData.setCheckerId(6l);
            //传入检验员名称
            itemDetailData.setCheckerName("检验员-测试");
            //传入测量终端ID
            itemDetailData.setMeasureDeviceId(123l);
            //传入测量终端名称
            itemDetailData.setMeasureDeviceName("测量终端-测试机");
            //模拟参数数据赋值
            for (CheckItemParamValueVO checkItemParamValueVO : checkItemVO.getParamNameList()) {
                if (checkItemParamValueVO.getType().contains("参数")) {
                    checkItemParamValueVO.setValue("11");
                }
            }
            String values = checkItemVO.getJsonParams();
            //将参数传入
            itemDetailData.setParamsValues(values);
            //重置检测结果，未完成
            itemDetailData.setCheckResult(CheckItemDetailResultEnum.UNFINISH.getCode());
            //记录开始时间
            itemDetailData.setStartTime(new Date());
            //模拟结束时间
            long endtimes = System.currentTimeMillis() + 1000l;
            //存入结束时间
            itemDetailData.setEndTime(new Date(endtimes));
            //默认检测记录未上传
            itemDetailData.setUploaded(false);

            itemDetailDao.insert(itemDetailData);
        }
    }
}
