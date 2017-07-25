package com.kstech.zoomlion.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.utils.BitmapUtils;
import com.kstech.zoomlion.utils.DateUtil;
import com.kstech.zoomlion.utils.LogUtils;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lijie on 2017/7/25.
 */

public class CameraRecordView extends RelativeLayout implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private Context context;
    private Activity activity;
    public RelativeLayout takephoto,imageshowlayout;
    public ImageView photoshow;
    public Button Camerabtn,agin,finish;
    public RadioGroup rgParams;
    private List<RadioButton> paramsRB = new ArrayList<>();
    public RadioButton rbPic,rbZoomPic;

    private List<String> params = new ArrayList<>();

    public Bitmap bitmap;

    public String paramName;

    public CameraRecordView(Context context) {
        super(context);
        this.context = context;
    }

    public CameraRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CameraRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public CameraRecordView(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        this.context = context;
        initView();
    }

    private void initView(){
        params.add("温度");
        params.add("压力");
        params.add("底盘");
        params.add("灯管");

        View view = View.inflate(context, R.layout.camera_view, null);
        photoshow=view.findViewById(R.id.photoshow);
        takephoto=view.findViewById(R.id.takephoto);
        imageshowlayout=view.findViewById(R.id.imageshowlayout);
        Camerabtn=view.findViewById(R.id.Camerabtn);
        agin = view.findViewById(R.id.agin);
        finish = view.findViewById(R.id.finish);
        rgParams = view.findViewById(R.id.rg_params);
        rbPic = view.findViewById(R.id.rb_pic);
        rbZoomPic = view.findViewById(R.id.rb_zoom_pic);

        Camerabtn.setOnClickListener(this);
        agin.setOnClickListener(this);
        finish.setOnClickListener(this);
        rgParams.setOnCheckedChangeListener(this);

        for (int i = 0; i < params.size(); i++) {
            RadioButton rb = new RadioButton(activity);
            rb.setText(params.get(i));
            rb.setId(i);
            paramsRB.add(rb);
            RadioGroup.LayoutParams rblayoutparams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            rblayoutparams.weight = 1;
            rgParams.addView(rb,rblayoutparams);
        }

        this.addView(view);
    }
    @Override
    public void onClick(View v) {
        Intent cameraIntent=null;
        Uri imageUri=null;

        switch (v.getId()) {
            case R.id.Camerabtn:
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"workupload.jpg"));
                //指定照片保存路径（SD卡），workupload.jpg为一个临时文件，每次拍照后这个图片都会被替换
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(cameraIntent, 1);
                break;
            case R.id.agin:
                cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri  = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"workupload.jpg"));
                //指定照片保存路径（SD卡），workupload.jpg为一个临时文件，每次拍照后这个图片都会被替换
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(cameraIntent, 1);
                break;
            case R.id.finish:
                enableParams(true);
                takephoto.setVisibility(View.VISIBLE);
                imageshowlayout.setVisibility(View.GONE);
                break;
        }
    }

    public void enableParams(boolean enable){
        for (RadioButton radioButton : paramsRB) {
            radioButton.setEnabled(enable);
        }
    }

    public boolean hasButtonChecked(){
        for (RadioButton radioButton : paramsRB) {
            if (radioButton.isChecked()){
                return true;
            }
        }
        return false;
    }

    public void copyPic(){
        int userID = 12;
        int itemID = 40;
        String Status = Environment.getExternalStorageState();
        if (!Status.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v("TestFile",
                    "SD card is not avaiable/writeable right now.");
            return;
        }
        String date = DateUtil.getDateTimeFormat14(new Date());
        String fname= Environment.getExternalStorageDirectory()+"/photograph/test/"+date+"-"+userID+"-"+itemID+"-"+paramName+".jpg";
        FileUtil.copy(Environment.getExternalStorageDirectory()+"/workupload.jpg",fname);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
        paramName = rb.getText().toString();
        LogUtils.e("CAMERAVIEW",rb.getText().toString()+"");
    }
}
