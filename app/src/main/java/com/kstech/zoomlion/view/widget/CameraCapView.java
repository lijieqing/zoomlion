package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.content.Intent;
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
    /**
     * 基础功能activity
     */
    private BaseFunActivity activity;
    /**
     * 图片捕获布局
     */
    public RelativeLayout takephoto, imageshowlayout;
    /**
     * 图片展示view
     */
    public ImageView photoshow;
    /**
     * 图片捕获操作view
     */
    public Button Camerabtn, agin, finish, save;
    /**
     * 图片参数名
     */
    public TextView tvName;
    /**
     * 调试项目操作view
     */
    public ItemOperateBodyView iobv;
    /**
     * 参数名称
     */
    public String paramName;
    /**
     * 参数对应的服务器字典ID
     */
    private String paramDictId;
    /**
     * 图片储存路径基本路径
     */
    private static final String PICPATH = "/zoomlion/pic/";
    /**
     * 图片数据关联的调试项目细节记录ID
     */
    private long detailID;
    /**
     * 图片数据操作类
     */
    private CheckImageDataDao imageDataDao;

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

    /**
     * 图片操作view初始化
     *
     * @param checkItemParamValueVO 需要图片捕获参数信息
     * @param detailID              需要绑定的调试项目细节记录ID
     * @param iobv                  需要更新的调试项目操作view
     */
    public void itemParamInit(@NonNull CheckItemParamValueVO checkItemParamValueVO, long detailID, ItemOperateBodyView iobv) {
        paramName = checkItemParamValueVO.getParamName();
        paramDictId = checkItemParamValueVO.getDictID();
        this.detailID = detailID;
        this.iobv = iobv;
        tvName.setText(paramName);
    }

    private void initView() {
        imageDataDao = MyApplication.getApplication().getDaoSession().getCheckImageDataDao();

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
                long imgDBId = copyPic();
                takephoto.setVisibility(View.VISIBLE);
                imageshowlayout.setVisibility(View.GONE);

                iobv.updateCameraInfo(imageDataDao.load(imgDBId));
                break;
        }
    }

    /**
     * 图片拷贝，将临时图片拷贝到指定路径下，并将路径信息保存到数据库中
     *
     * @return 调试图片数据记录的ID
     */
    public long copyPic() {
        long userID = 12;
        String Status = Environment.getExternalStorageState();
        if (!Status.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v("TestFile",
                    "SD card is not avaiable/writeable right now.");
            return -1;
        }
        String date = DateUtil.getDateTimeFormat14(new Date());
        String fname = Environment.getExternalStorageDirectory() + PICPATH + date + "-" + userID + "-" + detailID + "-" + paramName + ".jpg";
        FileUtil.copy(Environment.getExternalStorageDirectory() + "/workupload.jpg", fname);
        CheckImageData imgdata = new CheckImageData(null, paramDictId, new Date(), detailID, paramName, fname);
        long imgDBId = imageDataDao.insert(imgdata);
        Toast.makeText(activity, "已保存", Toast.LENGTH_SHORT).show();
        return imgDBId;
    }

}
