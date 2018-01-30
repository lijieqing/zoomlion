package com.kstech.zoomlion.engine.server;

import android.os.Handler;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.engine.check.CheckResultVerify;
import com.kstech.zoomlion.engine.check.XmlExpressionImpl;
import com.kstech.zoomlion.exception.MultiArithmeticException;
import com.kstech.zoomlion.model.db.CheckChartData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.enums.CheckRecordResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.ItemCheckActivity;

import org.json.JSONObject;
import org.xutils.http.RequestParams;

import java.util.Date;
import java.util.List;

/**
 * Created by lijie on 2018/1/9.
 * 调试项目数据保存、上传任务
 */
public class QCItemDataSaveUploadTask extends AbstractDataTransferTask {
    /**
     * 调试项目细节记录数据ID
     */
    private long detailID;
    /**
     * 当前调试项目VO类
     */
    private CheckItemVO itemvo;

    private CheckRecord record;
    /**
     * 当前调试项目数据
     */
    private CheckItemData itemData;
    /**
     * 当前调试项目细节数据
     */
    private CheckItemDetailData detailData;
    /**
     * 当前调试项目细节数据的参数值JSON串
     */
    private String paramValues;
    /**
     * 当前调试项目细节数据的谱图集合
     */
    private List<CheckChartData> chartDataList;
    /**
     * 泵车总调试次数
     */
    private int recordCount;
    /**
     * 泵车调试未通过次数
     */
    private int recordUnPassCount;
    /**
     * 服务器请求次数
     */
    private int requestTimes = 0;

    public QCItemDataSaveUploadTask(Handler handler) {
        super(handler);
    }

    /**
     * 初始化基本数据
     *
     * @param chartDataList 谱图数据集合
     * @param itemData      调试项目数据对象
     * @param detailData    调试项目细节数据对象
     * @param paramValues   参数值集合JSON串
     * @param detailID      调试项目细节数据ID
     * @param itemvo        调试项目VO类
     */
    public void init(List<CheckChartData> chartDataList, CheckItemData itemData,
                     CheckItemDetailData detailData, String paramValues,
                     long detailID, CheckItemVO itemvo) {
        this.chartDataList = chartDataList;
        this.itemData = itemData;
        this.detailData = detailData;
        this.paramValues = paramValues;
        this.detailID = detailID;
        this.itemvo = itemvo;
        record = itemData.getCheckRecord();
        requestTimes = 0;
    }

    @Override
    protected boolean needRequest() {
        return requestTimes < 1;
    }

    @Override
    protected void beforeRequest() {
        //创建表达式处理对象
        XmlExpressionImpl xmlExpression = new XmlExpressionImpl(detailData, paramValues);
        handler.sendEmptyMessage(ItemCheckActivity.START_SAVE_RECORD);
        //获取车辆总调试次数和未通过次数
        recordCount = itemData.getCheckRecord().getSumCounts() + 1;
        recordUnPassCount = itemData.getCheckRecord().getUnpassCounts();
        String pValues;
        try {
            //判断是否合格
            boolean pass = CheckResultVerify.itemVerify(paramValues, xmlExpression);
            //获取判定后的参数数据集合
            pValues = CheckResultVerify.upDateParamValues();
            //根据是否合格，更新itemData和detailData
            if (pass) {
                int passCount = itemData.getPassCounts() + 1;
                //当前调试合格，更新连续通过次数
                itemData.setPassCounts(passCount);
                detailData.setCheckResult(CheckItemDetailResultEnum.PASS.getCode());
            } else {
                itemData.setPassCounts(0);
                detailData.setCheckResult(CheckItemDetailResultEnum.UNPASS.getCode());
                recordUnPassCount++;
            }
            handler.sendEmptyMessage(ItemCheckActivity.RECORD_DATA_INIT);
            //将数值存入detail data对象
            detailData.setParamsValues(pValues);

            detailData.setEndTime(new Date());

            //谱图数据保存
            if (itemvo.getSpectrum() != null) {
                for (CheckChartData chartData : chartDataList) {
                    //设置detailID和创建时间
                    chartData.setItemDetailId(detailID);
                    chartData.setCreateTime(new Date());
                    MyApplication.getApplication().getDaoSession().getCheckChartDataDao().insert(chartData);
                }
                //清空谱图数据集合
                chartDataList.clear();
            }
            //重置itemDetailData的谱图数据
            detailData.resetCheckChartDatas();

            //设置第几次调试
            detailData.setCheckTimes(ItemCheckPrepareTask.serverItemDoneTimes++);

            //更新
            MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao().update(detailData);
            //重置 调试细节记录表 为的是保持与数据库同步
            itemData.resetCheckItemDetailDatas();

            //对itemData判定是否合格
            int sumCount = itemData.getSumCounts() + 1;
            if (sumCount >= itemvo.getTimes()) {
                //连续通过次数达到标准次数，合格，否则不合格
                if (itemData.getPassCounts() >= itemvo.getTimes()) {
                    itemData.setCheckResult(CheckItemResultEnum.PASS.getCode());
                } else {
                    itemData.setCheckResult(CheckItemResultEnum.UNPASS.getCode());
                }
            } else {
                itemData.setCheckResult(CheckItemResultEnum.UNFINISH.getCode());
            }
            itemData.setSumCounts(sumCount);

            record.setSumCounts(recordCount);
            record.setUnpassCounts(recordUnPassCount);

        } catch (MultiArithmeticException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(ItemCheckActivity.RECORD_VERIFY_ERROR);
        }
    }

    @Override
    protected String getRequestMessage() {
        return "数据本地已保存，开始同步服务器";
    }

    @Override
    protected String getURL() {
        return URLCollections.UPDATE_CHECK_ITEM_DETAIL_DATA;
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        //将调试记录数据打包，并添加到param中
        CompleteQCItemJSON qcitemJson = packageQCItemData(detailData, itemData);
        String result = JsonUtils.toJson(qcitemJson);
        params.setBodyContent(result);
        requestTimes++;
        return true;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) {
        if (data.has("success")) {
            detailData.setUploaded(true);
        } else {
            detailData.setUploaded(false);
        }
    }

    @Override
    protected boolean onReLogin() {
        return false;
    }

    @Override
    protected void onRequestFinish() {
        //更新数据库
        itemData.update();
        detailData.update();

        record.resetCheckItemDatas();

        boolean finish = true;
        for (CheckItemData checkItemData : record.getCheckItemDatas()) {
            int result = checkItemData.getCheckResult();
            if (result < 2) {
                finish = false;
            }
        }
        if (finish) {
            record.setCurrentStatus(CheckRecordResultEnum.FINISH.getCode());
        } else {
            record.setCurrentStatus(CheckRecordResultEnum.UNFINISH.getCode());
        }
        MyApplication.getApplication().getDaoSession().getCheckRecordDao().update(record);
        handler.sendEmptyMessage(ItemCheckActivity.RECORD_DATA_SAVED);
    }

}
