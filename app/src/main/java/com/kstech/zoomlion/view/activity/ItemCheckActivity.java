package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.check.CheckResultVerify;
import com.kstech.zoomlion.engine.check.ItemCheckCallBack;
import com.kstech.zoomlion.engine.check.ItemCheckTask;
import com.kstech.zoomlion.engine.check.XmlExpressionImpl;
import com.kstech.zoomlion.exception.MultiArithmeticException;
import com.kstech.zoomlion.model.db.CheckChartData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckChartDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.widget.CameraCapView;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;
import com.kstech.zoomlion.view.widget.ItemOperateView;
import com.kstech.zoomlion.view.widget.ItemShowViewInCheck;
import com.kstech.zoomlion.view.widget.TextProgressView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 项目调试界面，对单个项目调试的操作界面，主要包含两个大的组件ItemOperateView和ItemShowViewInCheck
 */
@ContentView(R.layout.activity_item_check)
public class ItemCheckActivity extends BaseFunActivity implements ItemCheckCallBack {
    /**
     * 项目调试操作view
     */
    @ViewInject(R.id.iov_test)
    private ItemOperateView iov;
    /**
     * 项目调试记录展示view
     */
    @ViewInject(R.id.isv_check)
    private ItemShowViewInCheck isv;
    /**
     * 照片捕获view，包含拍照、保存、重新开始
     */
    CameraCapView cameraCapView;
    /**
     * 照片捕获对话窗
     */
    AlertDialog picCatchDialog;
    /**
     * 调试项目细节记录ID
     */
    long detailID = -1;
    /**
     * 调试项目记录ID
     */
    String itemID;
    /**
     * 调试记录数据库ID
     */
    long itemDBID;
    /**
     * 调试项目记录操作类
     */
    CheckItemDataDao itemDao;
    /**
     * 调试项目细节操作类
     */
    CheckItemDetailDataDao itemDetailDao;
    /**
     * 调试项目谱图数据操作类
     */
    CheckChartDataDao chartDataDao;
    /**
     * 调试项目vo类
     */
    CheckItemVO itemvo;
    /**
     * 调试项目数据类
     */
    CheckItemData itemData;
    /**
     * 调试项目细节数据类
     */
    CheckItemDetailData detailData;
    /**
     * 调试项目谱图数据类集合
     */
    List<CheckChartData> chartDataList = new ArrayList<>();
    /**
     * 调试项目异步任务
     */
    ItemCheckTask itemCheckTask;

    /**
     * 表达式对象，用于参数对象的合格不合格判断
     */
    XmlExpressionImpl xmlExpression;

    /**
     * 调试项目 参数集合
     */
    List<CheckItemParamValueVO> valueVOList = new ArrayList<>();
    /**
     * 调试项目细节记录是否合格
     */
    boolean pass = false;
    /**
     * 信息提示弹窗
     */
    private AlertDialog dialog;
    /**
     * 带提示信息的进度条
     */
    private TextProgressView progressView;
    /**
     * 更新调试项目
     */
    private static final int NEW_CHECKITEM_REFRESH = 0;
    /**
     * 更新调试项目记录数据
     */
    private static final int NEW_DATA_REFRESH = 1;
    /**
     * 开始保存调试记录数据
     */
    private static final int START_SAVE_RECORD = 2;
    /**
     * 项目数据校验失败
     */
    private static final int RECORD_VERIFY_ERROR = 3;
    /**
     * 项目记录数据初始化
     */
    private static final int RECORD_DATA_INIT = 4;
    /**
     * 调试项目数据保存完成
     */
    private static final int RECORD_DATA_SAVED = 5;
    private static final String TAG = "ItemCheckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
        chartDataDao = MyApplication.getApplication().getDaoSession().getCheckChartDataDao();

        //根据ID，获取调试项目vo类
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");

        //设置回调
        iov.setCameraActivity(this);

        //初始化信息提示弹窗
        progressView = new TextProgressView(this);
        dialog = new AlertDialog.Builder(this).setView(progressView).create();

        //查询数据库
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                itemvo = Globals.modelFile.getCheckItemVO(itemID);
                itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                itemDBID = itemData.getCheckItemId();
                itemData.resetCheckItemDetailDatas();

                handler.sendEmptyMessage(NEW_CHECKITEM_REFRESH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            cameraCapView.takephoto.setVisibility(View.GONE);
            cameraCapView.imageshowlayout.setVisibility(View.VISIBLE);
            File tempF = new File(Environment.getExternalStorageDirectory() + "/workupload.jpg");
            LogUtils.e(TAG, Environment.getExternalStorageDirectory() + "/workupload.jpg");
            if (tempF.exists()) {
                LogUtils.e(TAG, Environment.getExternalStorageDirectory() + "-");
                Glide.with(this).load(tempF).thumbnail(0.5f).into(cameraCapView.photoshow);
            }
        }
    }

    @Override
    public void camera(CheckItemParamValueVO checkItemParamValueVO, ItemOperateBodyView iobv) {
        //初始化图片捕捉view
        cameraCapView = new CameraCapView(this, this);
        cameraCapView.itemParamInit(checkItemParamValueVO, detailID, iobv);

        //将图片捕捉view放到dialog中展示
        picCatchDialog = new AlertDialog.Builder(this)
                .setTitle("拍照")
                .setNegativeButton("结束", null)
                .setCancelable(false)
                .create();
        picCatchDialog.setView(cameraCapView);
        picCatchDialog.show();
    }

    /************
     * BaseFunActivity 回调
     */
    @Override
    public void startCheck() {
        //项目调试任务初始化和启动
        itemCheckTask = new ItemCheckTask(this);
        itemCheckTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void stopCheck() {
        if (itemCheckTask != null) {
            //项目调试任务人工停止
            itemCheckTask.stopCheck();
            itemCheckTask.cancel(true);
        }
    }

    @Override
    public void saveRecord(final String paramValues) {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                xmlExpression = new XmlExpressionImpl(detailData, paramValues);
                handler.sendEmptyMessage(START_SAVE_RECORD);
                String pValues;
                try {
                    pass = CheckResultVerify.itemVerify(paramValues, xmlExpression);
                    pValues = CheckResultVerify.upDateParamValues();

                    if (pass) {
                        detailData.setCheckResult(CheckItemDetailResultEnum.PASS.getCode());
                    } else {
                        detailData.setCheckResult(CheckItemDetailResultEnum.UNPASS.getCode());
                    }

                    handler.sendEmptyMessage(RECORD_DATA_INIT);
                    //将数值存入detail data对象
                    detailData.setParamsValues(pValues);

                    detailData.setEndTime(new Date());

                    //谱图数据保存
                    if (itemvo.getSpectrum() != null) {
                        for (CheckChartData chartData : chartDataList) {
                            //设置detailID和创建时间
                            chartData.setItemDetailId(detailID);
                            chartData.setCreateTime(new Date());
                            chartDataDao.insert(chartData);
                        }
                        //清空谱图数据集合
                        chartDataList.clear();
                    }
                    //重置itemDetailData的谱图数据
                    detailData.resetCheckChartDatas();

                    //更新
                    itemDetailDao.update(detailData);
                    //重置 调试细节记录表 为的是保持与数据库同步
                    itemData.resetCheckItemDetailDatas();

                    handler.sendEmptyMessage(RECORD_DATA_SAVED);
                } catch (MultiArithmeticException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(RECORD_VERIFY_ERROR);
                }
            }
        });

    }

    @Override
    public void toForward() {
        CheckItemVO temp = Globals.forwardCheckItem();
        if (temp != null) {
            itemvo = temp;
            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                    itemDBID = itemData.getCheckItemId();
                    itemData.resetCheckItemDetailDatas();

                    handler.sendEmptyMessage(NEW_CHECKITEM_REFRESH);
                }
            });
        } else {
            Toast.makeText(this, "当前已是第一项", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toNext() {
        CheckItemVO temp = Globals.nextCheckItem();
        if (temp != null) {
            itemvo = temp;
            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                    itemDBID = itemData.getCheckItemId();
                    itemData.resetCheckItemDetailDatas();

                    handler.sendEmptyMessage(NEW_CHECKITEM_REFRESH);
                }
            });
        } else {
            Toast.makeText(this, "当前已是最后一项", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initDetailData() {
        initNewDetailRecord();
    }

    @Override
    public void removeDetailData() {
        itemDetailDao.deleteByKey(detailID);
    }
    /**
     * BaseFunActivity 回调
     ****************
     */

    /**
     * 初始化调试项目细节记录表，并获取数据库索引ID，用于后来更新数据
     */
    private void initNewDetailRecord() {
        detailData = new CheckItemDetailData(null, itemDBID, 12l, "admin", 1l, "measure",
                1, itemvo.getJsonParams(), CheckItemDetailResultEnum.UNFINISH.getCode(),
                new Date(), null, null, false);
        detailID = itemDetailDao.insert(detailData);
    }

    /**
     * ItemCheckCallBack回调 开始
     */
    @Override
    public void onStart(ItemCheckTask task) {
        //在每次切换调试项目时，要给itemID重新赋值
        itemID = Globals.currentCheckItem.getId();

        task.qcID = Integer.parseInt(itemID);
        task.times = 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iov.chronometerReset(R.color.zoomLionColor, true);
            }
        });
    }

    @Override
    public void onStartError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ItemCheckActivity.this, msg, Toast.LENGTH_SHORT).show();
                iov.chronometerReset(null, false);
            }
        });
        LogUtils.e(TAG, msg);
    }

    @Override
    public void onProgress(String progress) {
        LogUtils.e(TAG, progress);
    }

    @Override
    public void onSuccess(List<CheckItemParamValueVO> headers, Map<String, LinkedList<Float>> specMap, String msg) {
        //将获得的参数数据添加到集合
        valueVOList.addAll(headers);
        //处理谱图
        if (specMap != null) {
            //谱图数据不为空时，生成谱图数据，并添加到集合中
            for (Map.Entry<String, LinkedList<Float>> sle : specMap.entrySet()) {
                CheckChartData chartData = new CheckChartData();

                String specName = sle.getKey();
                LinkedList<Float> specValue = sle.getValue();
                String unit = Globals.modelFile.dataSetVO.getDSItem(specName).sUnit;
                String data = JsonUtils.toJson(specValue);

                chartData.setParamName(specName);
                chartData.setUnit(unit);
                chartData.setChartData(data);

                chartDataList.add(chartData);
            }
        }

        handler.sendEmptyMessage(NEW_DATA_REFRESH);
    }

    @Override
    public void onResultError(List<CheckItemParamValueVO> headers, String msg) {
        //将获得的参数数据添加到集合
        valueVOList.addAll(headers);

        handler.sendEmptyMessage(NEW_DATA_REFRESH);
    }

    @Override
    public void onTimeOut(List<CheckItemParamValueVO> headers, String msg, Map<String, LinkedList<Float>> specMap) {
        //利用超时 模拟接收数据
        for (CheckItemParamValueVO header : headers) {
            if (header.getValueReq() && "Auto".equals(header.getValMode())) {
                double val = Math.random() * 100;
                CheckItemParamValueVO vo = new CheckItemParamValueVO(header);

                vo.setValue(val + "");
                valueVOList.add(vo);
            }
        }
        if (specMap != null) {
            //谱图处理
            for (Map.Entry<String, LinkedList<Float>> sle : specMap.entrySet()) {
                CheckChartData chartData = new CheckChartData();

                String specName = sle.getKey();
                LinkedList<Float> specValue = sle.getValue();
                String unit = Globals.modelFile.dataSetVO.getDSItem(specName).sUnit;
                String data = JsonUtils.toJson(specValue);

                chartData.setParamName(specName);
                chartData.setUnit(unit);
                chartData.setChartData(data);

                chartDataList.add(chartData);
            }
        }

        handler.sendEmptyMessage(NEW_DATA_REFRESH);
    }

    @Override
    public void onTaskStop(final boolean canSave) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iov.updateCheckStatus(false, canSave);
                iov.chronometerReset(null, false);
            }
        });
    }

    /**
     * ItemCheckCallBack回调 结束
     */

    @Override
    public void onBackPressed() {
        if (!iov.isInBlur()) {
            iov.changeBlur();
            iov.chronometerReset(R.color.whiteColor, false);
            iov.resetBodyViews();
            itemDetailDao.deleteByKey(detailID);
        } else {
            super.onBackPressed();
        }
    }


    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        WeakReference<ItemCheckActivity> reference;
        ItemCheckActivity activity;

        private InnerHandler(ItemCheckActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case NEW_CHECKITEM_REFRESH:
                        //更新调试项目参数操作区信息
                        activity.iov.update(activity.itemvo);
                        //更新调试项目界面展示信息，包括调试记录、当前项目名称、机型编号等
                        activity.isv.updateHead(activity.itemvo);
                        activity.isv.updateBody(activity.itemDBID);
                        break;
                    case NEW_DATA_REFRESH:
                        //此处更新iov组件 并传入detailData中
                        activity.iov.updateBodyAutoView(activity.valueVOList);
                        activity.detailData.setParamsValues(JsonUtils.toJson(activity.valueVOList));
                        activity.valueVOList.clear();
                        break;
                    case START_SAVE_RECORD:
                        activity.dialog.show();
                        activity.progressView.updateProgress("校验数据是否合格", 20);
                        break;
                    case RECORD_DATA_INIT:
                        activity.progressView.updateProgress("校验数据完成，调试数据保存中", 45);
                        break;
                    case RECORD_DATA_SAVED:
                        activity.progressView.updateProgress("数据保存成功！", 90);
                        //更新调试项目展示内容
                        activity.isv.updateBody(activity.itemDBID);
                        //计时器复位
                        activity.iov.chronometerReset(R.color.whiteColor, false);
                        activity.dialog.cancel();
                        break;
                    case RECORD_VERIFY_ERROR:
                        activity.progressView.updateProgress("校验数据失败，请确保机型配置正确", 100);
                        activity.dialog.cancel();
                        break;
                }


            }
        }
    }
}
