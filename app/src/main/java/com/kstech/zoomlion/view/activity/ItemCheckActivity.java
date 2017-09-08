package com.kstech.zoomlion.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.view.widget.CameraCapView;
import com.kstech.zoomlion.view.widget.ItemOperateBodyView;
import com.kstech.zoomlion.view.widget.ItemOperateView;
import com.kstech.zoomlion.view.widget.ItemShowViewInCheck;

import java.util.List;

public class ItemCheckActivity extends BaseFunActivity {

    private ItemOperateView iov;
    private ItemShowViewInCheck isv;
    CameraCapView cameraCapView;
    AlertDialog picCatchDialog;
    Bitmap bitmap;
    long detailID;
    String itemID;
    CheckItemDataDao itemDao;
    CheckItemDetailDataDao itemDetailDao;
    CheckItemVO itemvo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check);
        itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");
        itemvo = Globals.modelFile.getCheckItemVO(itemID);

        iov = (ItemOperateView) findViewById(R.id.iov_test);
        isv = (ItemShowViewInCheck) findViewById(R.id.isv_check);
        iov.setCameraActivity(this);

        iov.update(itemvo);

        CheckItemData itemData = itemDao.queryBuilder().where(CheckItemDataDao.Properties.QcId.eq(Integer.parseInt(itemvo.getId()))).build().unique();
        List<CheckItemDetailData> itemdetails = itemData.getCheckItemDetailDatas();

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
        cameraCapView = new CameraCapView(this, this);
        cameraCapView.itemParamInit(checkItemParamValueVO, detailID, iobv);

        picCatchDialog = new AlertDialog.Builder(this)
                .setTitle("拍照")
                .setNegativeButton("结束", null)
                .setCancelable(false)
                .create();
        picCatchDialog.setView(cameraCapView);
        picCatchDialog.show();
    }
}
