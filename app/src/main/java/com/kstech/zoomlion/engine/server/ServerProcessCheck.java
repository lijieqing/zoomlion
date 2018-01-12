package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckRecordDao;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.serverdata.CommissioningStatistics;
import com.kstech.zoomlion.serverdata.QCItemStatus;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.BaseActivity;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lijie on 2018/1/10.
 */

public class ServerProcessCheck extends AbstractDataTransferTask {
    /**
     * 是否存在泵车调试记录
     */
    private boolean hasRecord = false;
    /**
     * 当前泵车记录
     */
    private CheckRecord record;
    /**
     * 是否能够进入调试页面
     */
    private boolean skip = false;
    /**
     * 已请求次数
     */
    private int requestTimes = 0;
    /**
     * 服务器 调试项目状态对象集合
     */
    List<QCItemStatus> itemStatus;

    CommissioningStatistics deviceStatus;

    public ServerProcessCheck(Handler handler) {
        super(handler);
    }

    public void setDeviceStatus(CommissioningStatistics deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    @Override
    String getRequestMessage() {
        return "服务器机型流程校验";
    }

    @Override
    boolean needRequest() {
        return requestTimes++ < 1;
    }

    @Override
    void beforeRequest() {

    }

    @Override
    String getURL() {
        return URLCollections.NOTIFY_SERVER_GOTO_CHECK;
    }

    @Override
    void initRequestParam(RequestParams params) {
        params.addBodyParameter("sn", Globals.deviceSN);
    }

    @Override
    void onRequestSuccess(JSONObject data) throws JSONException {
        if (data.has("processId")) {
            message = Message.obtain();
            String id = data.getString("processId");
            //判断当前processID与服务器processID是否一致
            if (!TextUtils.isEmpty(Globals.PROCESSID) && Globals.PROCESSID.equals(id)) {

                //查询本地是否存在记录
                CheckRecord cr = MyApplication.getApplication().getDaoSession().getCheckRecordDao()
                        .queryBuilder().where(CheckRecordDao.Properties.DeviceIdentity
                                .eq(Globals.deviceSN)).build().unique();
                //当为null时，说明此机型第一次调试，需要为其创建对应的数据
                if (cr == null) {
                    //当cr为空 arg1为负值
                    hasRecord = false;
                } else {
                    //当cr不为空 arg1为正值
                    hasRecord = true;
                    record = cr;
                }
                //设置为可以跳转
                skip = true;
            }
        }
        if (data.has("qcitemStatusList")) {
            String statusList = data.getString("qcitemStatusList");
            itemStatus = JsonUtils.fromArrayJson(statusList, QCItemStatus.class);
        }
    }

    @Override
    void onRequestError() {

    }

    @Override
    boolean onReLogin(Message message) {
        return false;
    }

    @Override
    void onRequestFinish(boolean success) {
        if (skip) {
            if (hasRecord) {
                verifyCheckRecord(record);
            } else {
                initRecord();
            }
            updateItemStatus();
        } else {
            //不一致提示异常，停止进入
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "当前流程与服务器流程不一致，无法进入调试";
            message.arg1 = 100;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void afterDialogCancel() {
        super.afterDialogCancel();
        if (skip) {
            handler.sendEmptyMessage(IndexActivity.SKIP_TO_CHECK);
        }
    }

    /**
     * 更新调试项目服务器状态数据
     */
    private void updateItemStatus() {
        if (itemStatus != null) {
            Message message;
            CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
            CheckRecordDao recordDao = MyApplication.getApplication().getDaoSession().getCheckRecordDao();
            CheckRecord cr = recordDao.queryBuilder()
                    .where(CheckRecordDao.Properties.DeviceIdentity.eq(Globals.deviceSN))
                    .build().unique();
            if (cr != null) {
                //更新整机调试状态到本地
                if (deviceStatus != null) {
                    cr.setSumCounts(deviceStatus.getCompleteNumber() + deviceStatus.getDoingNumber());
                    cr.setCurrentStatus(deviceStatus.getStatus());
                    recordDao.update(cr);
                }

                message = Message.obtain();
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "更新调试项目状态";
                message.arg1 = 75;
                handler.sendMessage(message);
                SystemClock.sleep(1000);

                for (QCItemStatus itemStatus : itemStatus) {
                    Long dictId = itemStatus.getQcitemDictId();
                    String d = String.valueOf(dictId);
                    CheckItemData item = itemDao.queryBuilder()
                            .where(CheckItemDataDao.Properties.RecordId.eq(cr.getCheckRecordId()),
                                    CheckItemDataDao.Properties.DictId.eq(d))
                            .build().unique();
                    if (item != null) {
                        item.setSumCounts(itemStatus.getDoneTimes());
                        item.setPassCounts(itemStatus.getPassTimes());
                        item.setCheckResult(itemStatus.getStatus());
                        itemDao.update(item);
                    }
                }

                message = Message.obtain();
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = "调试项目状态数据更新完成";
                message.arg1 = 95;
                handler.sendMessage(message);
            }
        }
    }

    /**
     * 初始化当前机型调试记录数据
     */
    private void initRecord() {
        CheckRecord record = new CheckRecord(null, "", Globals.deviceSN,
                "test_record_name", 123l,
                CheckRecordResultEnum.UNFINISH.getCode(), new Date(),
                null, 0, 0
                , null, 0, false);

        //定义调试项目数据实体类
        CheckItemData itemData;
        Message message;
        try {
            //插入
            Globals.recordID = MyApplication.getApplication().getDaoSession().getCheckRecordDao().insert(record);

            //遍历配置信息中的调试项目，并依次存入数据库
            for (CheckItemVO checkItemVO : Globals.modelFile.allCheckItemList) {
                itemData = new CheckItemData(null, Integer.parseInt(checkItemVO.getId()),
                        checkItemVO.getDictId(), checkItemVO.getName(), 0, 0, 0,
                        Globals.recordID, false, false, null);
                MyApplication.getApplication().getDaoSession().getCheckItemDataDao().insert(itemData);
                //handler 信息封装
                message = Message.obtain();

                String s = "初始化配置：" + checkItemVO.getName() + " 数据信息";
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = s;
                message.arg1 = 70;

                //发送msg 更新UI
                handler.sendMessage(message);
                SystemClock.sleep(50);
            }

            //发送成功状态 更新UI
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "数据初始化完成";
            message.arg1 = 100;
            handler.sendMessage(message);
        } catch (Exception e) {
            //异常 进行提示
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = e.getMessage();
            message.arg1 = 100;
            handler.sendMessage(message);
        }
    }

    /**
     * 校验机型文件中的调试项目与数据库中的调试项目是否一致
     *
     * @param cr 整机调试记录实体类
     */

    private void verifyCheckRecord(CheckRecord cr) {
        Globals.recordID = cr.getCheckRecordId();
        List<CheckItemVO> newItemList = new ArrayList<>();
        Message message;
        message = Message.obtain();
        message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
        message.obj = "数据校验中";
        message.arg1 = 50;
        handler.sendMessage(message);

        //对当前的调试项目遍历，与数据库比对
        for (CheckItemVO checkItemVO : Globals.modelFile.allCheckItemList) {
            //在数据库已存在，默认false
            boolean inDB = false;
            //获取机型文件中的调试项目ID
            String qcID = checkItemVO.getId();
            //遍历数据库中的调试项目记录
            for (CheckItemData checkItemData : cr.getCheckItemDatas()) {
                //比较数据库记录中的调试项目ID和机型文件中的调试项目ID
                String qcIDInDB = String.valueOf(checkItemData.getQcId());
                //ID相同，则更改inDB状态为true
                if (qcID.equals(qcIDInDB)) {
                    inDB = true;
                }
            }
            //如果当前的CheckItemVO找不到数据库记录，将其加入新调试项目集合
            if (!inDB) {
                newItemList.add(checkItemVO);
            }

            message = Message.obtain();
            String s = "数据校验：" + checkItemVO.getName();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = s;
            message.arg1 = 70;
            handler.sendMessage(message);
            SystemClock.sleep(50);
        }
        //发送校验完成状态
        message = Message.obtain();
        message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
        message.arg1 = 75;
        message.obj = "数据校验完成";
        handler.sendMessage(message);

        //最后判断 新调试项目集合是否为空
        if (newItemList.size() > 0) {
            //发送 更新数据 状态
            message = Message.obtain();
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            message.obj = "数据库更新中";
            message.arg1 = 78;
            handler.sendMessage(message);
            //初始化新的调试项目记录
            CheckItemData itemData;
            try {
                for (CheckItemVO checkItemVO : newItemList) {
                    itemData = new CheckItemData(null, Integer.parseInt(checkItemVO.getId()),
                            checkItemVO.getDictId(), checkItemVO.getName(), 0, 0, 0,
                            Globals.recordID, false, false, null);
                    MyApplication.getApplication().getDaoSession().getCheckItemDataDao().insert(itemData);
                }
            } catch (Exception e) {
                //异常 进行提示
                message = Message.obtain();
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = e.getMessage();
                message.arg1 = 100;
                handler.sendMessage(message);
            }
        }
    }

}
