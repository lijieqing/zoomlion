package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.greendao.CheckImageDataDao;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.view.activity.BaseFunActivity;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by lijie on 2017/7/25.
 */

public class CameraCapView extends RelativeLayout implements View.OnClickListener {
    private BaseFunActivity activity;
    public RelativeLayout takephoto, imageshowlayout;
    public ImageView photoshow;
    public Button Camerabtn, agin, finish, save;
    public TextView tvName;
    public Bitmap bitmap;
    public ItemOperateBodyView iobv;

    public String paramName;
    private long detailID;

    public CameraCapView(Context context) {
        super(context);
    }

    public CameraCapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraCapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CameraCapView(Context context, BaseFunActivity activity) {
        super(context);
        this.activity = activity;
        initView();
    }

    public void itemParamInit(@NonNull CheckItemParamValueVO checkItemParamValueVO, long detailID, ItemOperateBodyView iobv) {
        paramName = checkItemParamValueVO.getParamName();
        this.detailID = detailID;
        this.iobv = iobv;
        tvName.setText(paramName);
    }

    private void initView() {

        View view = View.inflate(activity, R.layout.camera_cap_view, null);
        photoshow = view.findViewById(R.id.photoshow);
        takephoto = view.findViewById(R.id.takephoto);
        imageshowlayout = view.findViewById(R.id.imageshowlayout);
        Camerabtn = view.findViewById(R.id.Camerabtn);
        agin = view.findViewById(R.id.agin);
        finish = view.findViewById(R.id.finish);
        save = view.findViewById(R.id.btn_save);
        tvName = view.findViewById(R.id.tv_current_name);

        Camerabtn.setOnClickListener(this);
        agin.setOnClickListener(this);
        save.setOnClickListener(this);
        finish.setOnClickListener(this);


        this.addView(view);
    }

    @Override
    public void onClick(View v) {
        Intent cameraIntent = null;
        Uri imageUri = null;

        switch (v.getId()) {
            case R.id.Camerabtn:
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "workupload.jpg"));
                //指定照片保存路径（SD卡），workupload.jpg为一个临时文件，每次拍照后这个图片都会被替换
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(cameraIntent, 1);
                break;
            case R.id.agin:
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "workupload.jpg"));
                //指定照片保存路径（SD卡），workupload.jpg为一个临时文件，每次拍照后这个图片都会被替换
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(cameraIntent, 1);
                break;
            case R.id.finish:
                takephoto.setVisibility(View.VISIBLE);
                imageshowlayout.setVisibility(View.GONE);
                break;
            case R.id.btn_save:
                copyPic();
                takephoto.setVisibility(View.VISIBLE);
                imageshowlayout.setVisibility(View.GONE);

                iobv.updateCameraInfo();
                break;
        }
    }


    public void copyPic() {
        long userID = 12;
        String Status = Environment.getExternalStorageState();
        if (!Status.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v("TestFile",
                    "SD card is not avaiable/writeable right now.");
            return;
        }
        String date = DateUtil.getDateTimeFormat14(new Date());
        String fname = Environment.getExternalStorageDirectory() + "/photograph/test/" + date + "-" + userID + "-" + detailID + "-" + paramName + ".jpg";
        FileUtil.copy(Environment.getExternalStorageDirectory() + "/workupload.jpg", fname);
        CheckImageDataDao imgDao = MyApplication.getApplication().getDaoSession().getCheckImageDataDao();
        imgDao.insert(new CheckImageData(null, detailID, paramName, fname));
        Toast.makeText(activity, "已保存", Toast.LENGTH_SHORT).show();
    }

}
