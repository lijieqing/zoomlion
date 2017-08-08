package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.widget.CameraRecordView;
import com.kstech.zoomlion.view.adapter.ImgDataListAdapter;

import java.lang.ref.WeakReference;


public class CameraActivity extends AppCompatActivity {
    AlertDialog picCatchDialog;
    AlertDialog picShowDialog;
    CameraRecordView cameraRecordView;
    Bitmap bitmap;
    Bitmap bp;
    ListView listView;
    ImgDataListAdapter imgDataListAdapter;
    Activity activity;
    CheckImageDataDao imgDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        activity = this;
        imgDao = MyApplication.getApplication().getDaoSession().getCheckImageDataDao();
        //先查询已存在的记录项目集合
        Globals.values = imgDao.queryBuilder().where(CheckImageDataDao.Properties.ItemDetailId.eq(40)).build().list();

        picShowDialog = new AlertDialog.Builder(activity,android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                .create();
        imgDataListAdapter = new ImgDataListAdapter(this);

        listView = (ListView) findViewById(R.id.lv_param);
        listView.setAdapter(imgDataListAdapter);


        //实现item点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new Thread(){
                    @Override
                    public void run() {
                        if (bp != null)
                            bp.recycle();
                        bp = BitmapFactory.decodeFile(Globals.values.get(i).getImgPath());
                        handler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
    }

    //更新图片展示界面
    public void updateDialog(){
        ImageView iv = new ImageView(activity);

        iv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setImageBitmap(bp);
        if (picShowDialog.isShowing()){
            picShowDialog.cancel();
            picShowDialog = new AlertDialog.Builder(activity,android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        }else {
            picShowDialog = new AlertDialog.Builder(activity,android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        }
    }

    public void camera(View view) {
        cameraRecordView = new CameraRecordView(this,this);
        picCatchDialog = new AlertDialog.Builder(this)
                .setTitle("拍照")
                .setNegativeButton("结束",null)
                .setCancelable(false)
                .create();
        picCatchDialog.setView(cameraRecordView);
        picCatchDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            cameraRecordView.takephoto.setVisibility(View.GONE);
            cameraRecordView.imageshowlayout.setVisibility(View.VISIBLE);
            Bitmap camorabitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/workupload.jpg");
            if(null != camorabitmap ){

                // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                bitmap = BitmapUtils.getInstance(this).getZoomBitmap(camorabitmap,500,600);

                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                camorabitmap.recycle();
                //将处理过的图片显示在界面上
                cameraRecordView.photoshow.setImageBitmap(bitmap);
                if (null != cameraRecordView.bitmap)
                    cameraRecordView.bitmap.recycle();
                cameraRecordView.bitmap = bitmap;
                cameraRecordView.enableParams(false);
                bitmap = null;
            }
        }
    }
    public void updateList(){
        Globals.values = imgDao.queryBuilder().where(CheckImageDataDao.Properties.ItemDetailId.eq(40)).build().list();
        imgDataListAdapter.notifyDataSetChanged();
    }

    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends Handler{
        private final WeakReference<CameraActivity> cameraActivity;

        private InnerHandler(CameraActivity activity) {
            this.cameraActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraActivity mActivity = cameraActivity.get();
            if (mActivity != null){
                mActivity.updateDialog();
            }
        }
    }
}
