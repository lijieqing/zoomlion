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
import android.view.View;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.manager.XMLAPI;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.widget.CameraCapView;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;
import com.kstech.zoomlion.view.widget.ItemOperateView;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class ItemCheckActivity extends BaseFunActivity {

    private InnerHandler handler = new InnerHandler(this);
    private ItemOperateView iov;
    CameraCapView cameraCapView;
    AlertDialog picCatchDialog;
    Bitmap bitmap;
    long detailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check);
        iov = findViewById(R.id.iov_test);
        iov.setCameraActivity(this);

        new Thread(){
            @Override
            public void run() {
                try {
                    Globals.modelFile = DeviceModelFile.readFromFile((Device) XMLAPI.readXML(getAssets().open("temp.xml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            cameraCapView.takephoto.setVisibility(View.GONE);
            cameraCapView.imageshowlayout.setVisibility(View.VISIBLE);
            Bitmap camorabitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/workupload.jpg");
            if(null != camorabitmap ){

                // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
                bitmap = BitmapUtils.getInstance(this).getZoomBitmap(camorabitmap,500,600);

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
    public void camera(CheckItemParamValueVO checkItemParamValueVO,ItemOperateBodyView iobv) {
        cameraCapView = new CameraCapView(this,this);
        cameraCapView.itemParamInit(checkItemParamValueVO,detailID);

        picCatchDialog = new AlertDialog.Builder(this)
                .setTitle("拍照")
                .setNegativeButton("结束",null)
                .setCancelable(false)
                .create();
        picCatchDialog.setView(cameraCapView);
        picCatchDialog.show();
    }

    @Override
    public void onPicSave() {

    }

    private static class InnerHandler extends Handler {
        private final WeakReference<ItemCheckActivity> itemActivity;

        private InnerHandler(ItemCheckActivity activity) {
            this.itemActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ItemCheckActivity mActivity = itemActivity.get();
            if (mActivity != null){
                CheckItemVO vo = Globals.modelFile.getCheckItemVO("10");
                mActivity.iov.update(vo);
            }
        }
    }
}
