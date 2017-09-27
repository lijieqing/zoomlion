package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.ItemCheckCallBack;
import com.kstech.zoomlion.engine.ItemCheckTask;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.widget.CameraCapView;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;
import com.kstech.zoomlion.view.widget.ItemOperateView;
import com.kstech.zoomlion.view.widget.ItemShowViewInCheck;

import java.util.List;

public class ItemCheckActivity extends BaseFunActivity implements ItemCheckCallBack{
    private ItemOperateView iov;// 项目调试操作view
    private ItemShowViewInCheck isv;//项目调试记录展示view
    CameraCapView cameraCapView;//照片捕获view，包含拍照、保存、重新开始
    AlertDialog picCatchDialog;//照片捕获对话窗
    Bitmap bitmap;//当前图片的位图
    long detailID;//调试项目细节记录ID
    String itemID;//调试项目记录ID

    CheckItemDataDao itemDao;//调试项目记录操作类
    CheckItemDetailDataDao itemDetailDao;//调试项目细节操作类
    CheckItemVO itemvo;//调试项目vo类

    ItemCheckTask itemCheckTask;//调试项目异步任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check);
        itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        //根据ID，获取调试项目vo类
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");
        itemvo = Globals.modelFile.getCheckItemVO(itemID);

        iov = (ItemOperateView) findViewById(R.id.iov_test);
        isv = (ItemShowViewInCheck) findViewById(R.id.isv_check);
        iov.setCameraActivity(this);

        //更新调试项目参数操作区信息
        iov.update(itemvo);

        CheckItemData itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
        List<CheckItemDetailData> itemdetails = itemData.getCheckItemDetailDatas();

        //更新调试项目界面展示信息，包括调试记录、当前项目名称、机型编号等
        isv.updateHead(itemvo);
        isv.updateBody(itemdetails);
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
        if (itemCheckTask != null){
            //项目调试任务人工停止
            itemCheckTask.stopCheck();
            itemCheckTask.cancel(true);
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
        LogUtils.e("ItemCheckActivity",msg);
    }

    @Override
    public void onProgress(String progress) {
        LogUtils.e("ItemCheckActivity",progress);
    }

    @Override
    public void onSuccess(List<CheckItemParamValueVO> headers, String msg) {

    }

    @Override
    public void onResultError(List<CheckItemParamValueVO> headers, String msg) {

    }

    @Override
    public void onTimeOut(List<CheckItemParamValueVO> headers, String msg) {

    }

    @Override
    public void onTaskStop(final boolean canSave) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iov.updateCheckStatus(false,canSave);
                iov.chronometer.stop();
            }
        });
    }


    /**
     * ItemCheckCallBack回调 开始
     */

}
