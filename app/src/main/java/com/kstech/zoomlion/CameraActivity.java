package com.kstech.zoomlion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.view.CameraRecordView;

public class CameraActivity extends AppCompatActivity {
    AlertDialog dialog;
    CameraRecordView cameraRecordView;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
    public void camera(View view) {
        cameraRecordView = new CameraRecordView(this,this);
        dialog = new AlertDialog.Builder(this)
                .setTitle("拍照")
                .setNegativeButton("结束",null)
                .setCancelable(false)
                .create();
        dialog.setView(cameraRecordView);
        dialog.show();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
            if (cameraRecordView.imageshowlayout.getVisibility() == View.VISIBLE) {
                cameraRecordView.imageshowlayout.setVisibility(View.GONE);
                cameraRecordView.takephoto.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
