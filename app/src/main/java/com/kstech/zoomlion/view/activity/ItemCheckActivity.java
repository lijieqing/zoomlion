package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.ItemCheckCallBack;
import com.kstech.zoomlion.engine.ItemCheckTask;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.widget.CameraCapView;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;
import com.kstech.zoomlion.view.widget.ItemOperateView;
import com.kstech.zoomlion.view.widget.ItemShowViewInCheck;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目调试界面，对单个项目调试的操作界面，主要包含两个大的组件ItemOperateView和ItemShowViewInCheck
 */
@ContentView(R.layout.activity_item_check)
public class ItemCheckActivity extends BaseFunActivity implements ItemCheckCallBack {
    @ViewInject(R.id.iov_test)
    private ItemOperateView iov;// 项目调试操作view

    @ViewInject(R.id.isv_check)
    private ItemShowViewInCheck isv;//项目调试记录展示view

    CameraCapView cameraCapView;//照片捕获view，包含拍照、保存、重新开始
    AlertDialog picCatchDialog;//照片捕获对话窗
    Bitmap bitmap;//当前图片的位图
    long detailID = -1;//调试项目细节记录ID
    String itemID;//调试项目记录ID
    long itemDBID;//调试记录数据库ID
    int checkStatus;//0 未开始，刚刚进入页面,1 未完成,存在参数未调试或者存在参数未保存，2，已保存成功，生成记录

    CheckItemDataDao itemDao;//调试项目记录操作类
    CheckItemDetailDataDao itemDetailDao;//调试项目细节操作类
    CheckItemVO itemvo;//调试项目vo类
    CheckItemData itemData;//调试项目数据类
    CheckItemDetailData detailData;//调试项目细节数据类

    ItemCheckTask itemCheckTask;//调试项目异步任务

    List<CheckItemParamValueVO> valueVOList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        //根据ID，获取调试项目vo类
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");

        iov.setCameraActivity(this);

        checkStatus = 0;

        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                itemvo = Globals.modelFile.getCheckItemVO(itemID);
                itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                itemDBID = itemData.getCheckItemId();
                itemData.resetCheckItemDetailDatas();

                handler.sendEmptyMessage(0);
            }
        });
    }

    /**
     * 初始化调试项目细节记录表，并获取数据库索引ID，用于后来更新数据
     */
    private void initNewDetailRecord() {
        detailData = new CheckItemDetailData(null, itemDBID, 12l, "admin", 1l, "measure", itemvo.getJsonParams(), CheckItemDetailResultEnum.UNFINISH.getCode(), new Date(), null, null, false);
        detailID = itemDetailDao.insert(detailData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            cameraCapView.takephoto.setVisibility(View.GONE);
            cameraCapView.imageshowlayout.setVisibility(View.VISIBLE);
            Bitmap camorabitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/workupload.jpg");
            if (null != camorabitmap) {

                // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                bitmap = BitmapUtils.getInstance(this).getZoomBitmap(camorabitmap, 500, 600);

                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                camorabitmap.recycle();
                //将处理过的图片显示在界面上
                cameraCapView.photoshow.setImageBitmap(bitmap);
                if (null != cameraCapView.bitmap)
                    cameraCapView.bitmap.recycle();
                cameraCapView.bitmap = bitmap;
                bitmap = null;
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
    public void saveRecord(String paramValues) {
        detailData.setParamsValues(paramValues);
        itemDetailDao.update(detailData);
        itemData.resetCheckItemDetailDatas();
        isv.updateBody(itemData.getCheckItemDetailDatas());

        initNewDetailRecord();
    }

    @Override
    public void toForward() {
        CheckItemVO temp = Globals.forwardCheckItem();
        if (temp != null){
            itemvo = temp;
            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                    itemDBID = itemData.getCheckItemId();
                    itemData.resetCheckItemDetailDatas();

                    handler.sendEmptyMessage(0);
                }
            });
        }else {
            Toast.makeText(this,"当前已是第一项",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toNext() {
        CheckItemVO temp = Globals.nextCheckItem();
        if (temp != null){
            itemvo = temp;
            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
                    itemDBID = itemData.getCheckItemId();
                    itemData.resetCheckItemDetailDatas();

                    handler.sendEmptyMessage(0);
                }
            });
        }else {
            Toast.makeText(this,"当前已是最后一项",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * BaseFunActivity 回调
     ****************
     */


    /**
     * ItemCheckCallBack回调 开始
     */
    @Override
    public void onStart(ItemCheckTask task) {
        task.qcID = Integer.parseInt(itemID);
        task.times = 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iov.chronometer.setBase(SystemClock.elapsedRealtime());//设置计时器起点，00：00
                iov.chronometer.setBackgroundResource(R.color.zoomLionColor);//设置背景色
                iov.chronometer.start();//启动计时器
            }
        });
    }

    @Override
    public void onStartError(String msg) {
        LogUtils.e("ItemCheckActivity", msg);
    }

    @Override
    public void onProgress(String progress) {
        LogUtils.e("ItemCheckActivity", progress);
    }

    @Override
    public void onSuccess(List<CheckItemParamValueVO> headers, String msg) {

    }

    @Override
    public void onResultError(List<CheckItemParamValueVO> headers, String msg) {

    }

    @Override
    public void onTimeOut(List<CheckItemParamValueVO> headers, String msg) {
        //利用超时 模拟接收数据
        for (CheckItemParamValueVO header : headers) {
            if(header.getValueReq() && "Auto".equals(header.getValMode())){
                double val = Math.random() * 100;
                CheckItemParamValueVO vo = new CheckItemParamValueVO(header);

                vo.setValue(val+"");
                valueVOList.add(vo);
            }
        }


        handler.sendEmptyMessage(1);
    }

    @Override
    public void onTaskStop(final boolean canSave) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iov.updateCheckStatus(false, canSave);
                iov.chronometer.stop();
            }
        });
    }

    /**
     * ItemCheckCallBack回调 结束
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        itemDetailDao.deleteByKey(detailID);
    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler {
        WeakReference<ItemCheckActivity> reference;
        ItemCheckActivity activity;

        public InnerHandler(ItemCheckActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        if (activity.detailID != -1){
                            activity.itemDetailDao.deleteByKey(activity.detailID);
                        }
                        //更新调试项目参数操作区信息
                        activity.iov.update(activity.itemvo);
                        //更新调试项目界面展示信息，包括调试记录、当前项目名称、机型编号等
                        activity.isv.updateHead(activity.itemvo);
                        activity.isv.updateBody(activity.itemData.getCheckItemDetailDatas());

                        //初始化记录表
                        activity.initNewDetailRecord();
                        break;
                    case 1:
                        //此处更新iov组件 并传入detailData中
                        activity.iov.updateBodyAutoView(activity.valueVOList);
                        activity.detailData.setParamsValues(JsonUtils.toJson(activity.valueVOList));
                        activity.valueVOList.clear();
                        break;
                }


            }
        }
    }
}
