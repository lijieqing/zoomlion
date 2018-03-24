package com.kstech.zoomlion.view.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.charts.LineChart;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.engine.base.BaseXmlExpression;
import com.kstech.zoomlion.engine.server.ItemRecordLoadTask;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.QCDataRecordDetails;
import com.kstech.zoomlion.serverdata.QCDataStatusEnum;
import com.kstech.zoomlion.serverdata.QCItemRecordDetails;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.ThreadManager;
import com.kstech.zoomlion.view.adapter.LineChartAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_view_record)
public class ViewRecordActivity extends BaseActivity {

    @ViewInject(R.id.vr_lv)
    private ListView lvTimeList;

    @ViewInject(R.id.vr_lv_result)
    private ListView lvResultList;

    @ViewInject(R.id.vr_lv_pic)
    private ListView lvPicList;

    @ViewInject(R.id.vr_tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.vr_tv_record_checker)
    private TextView tvRecordsChecker;

    @ViewInject(R.id.vr_tv_record_no)
    private TextView tvRecordNum;

    @ViewInject(R.id.vr_tv_record_result)
    private TextView tvRecordResult;

    @ViewInject(R.id.vr_line_chart)
    private LineChart lineChart;

    /**
     * 调试项目服务器数据集合
     */
    private List<QCItemRecordDetails> itemRecordDetails = new ArrayList<>();
    /**
     * 调试项目时间集合
     */
    private List<String> itemTimeList = new ArrayList<>();
    /**
     * 时间集合adapter
     */
    private ArrayAdapter<String> timeListAdapter;
    /**
     * 当前选中的调试项目记录
     */
    private QCItemRecordDetails currentRecord;
    /**
     * 图片数据集合
     */
    private List<ImageData> imgList = new ArrayList<>();
    /**
     * 参数数值数据集合
     */
    private List<ResultData> dataList = new ArrayList<>();
    /**
     * 图片集合adapter
     */
    private ImgAdapter imgAdapter;
    /**
     * 数据集合adapter
     */
    private DataAdapter dataAdapter;
    /**
     * 图片展示弹窗
     */
    private AlertDialog picShowDialog;
    /**
     * 调试项目记录加载完成
     */
    public static final int ITEM_RECORD_LOADED = 0;
    /**
     * 图片大图加载完成
     */
    private static final int PIC_LOAD_FINISH = 1;
    /**
     * 谱图数据加载完成
     */
    public static final int SPEC_LOAD_FINISH = 2;
    /**
     * 谱图请求错误
     */
    public static final int SPEC_LOAD_ERROR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        String dictID = getIntent().getStringExtra("dictID");
        tvTitle.setText(Globals.currentCheckItem.getName());
        tvTitle.setTextSize(24);

        picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                .create();

        timeListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, itemTimeList);
        lvTimeList.setAdapter(timeListAdapter);
        lvTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentRecord = itemRecordDetails.get(position);
                updateLists();
            }
        });

        imgAdapter = new ImgAdapter();
        dataAdapter = new DataAdapter();
        lvPicList.setAdapter(imgAdapter);
        lvResultList.setAdapter(dataAdapter);

        lvPicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Glide.with(ViewRecordActivity.this).asBitmap().load(imgList.get(position).url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Message message = Message.obtain();
                                message.what = PIC_LOAD_FINISH;
                                message.obj = resource;
                                handler.sendMessage(message);
                            }
                        });
            }
        });

        ItemRecordLoadTask loadTask = new ItemRecordLoadTask(handler);
        loadTask.setDictID(dictID);
        loadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    /**
     * 更新图片、数值、谱图组件
     */
    private void updateLists() {
        imgList.clear();
        dataList.clear();

        final Long specID = currentRecord.getSpectrogramId();
        if (specID != null) {
            //当存在谱图ID时,请求谱图数据
            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    String url = URLCollections.SPEC_URL_PREFIX + specID;
                    RequestParams params = new RequestParams(url);
                    try {
                        Map<String, List<Float>> map = new HashMap<>();
                        String result = x.http().getSync(params, String.class);
                        JSONObject object = new JSONObject(result);
                        Iterator<String> keys = object.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONArray array = (JSONArray) object.get(key);
                            String value = array.toString();
                            map.put(key, JsonUtils.fromArrayJson(value, Float.class));
                        }
                        Message message = Message.obtain();
                        message.obj = map;
                        message.what = SPEC_LOAD_FINISH;
                        handler.sendMessage(message);
                    } catch (Throwable throwable) {
                        handler.sendEmptyMessage(SPEC_LOAD_ERROR);
                        throwable.printStackTrace();
                    }
                }
            });
        }
        if (currentRecord.getQcDaraRecordDetailList() != null) {
            //遍历当前调试项目细节记录各个参数
            for (QCDataRecordDetails qcData : currentRecord.getQcDaraRecordDetailList()) {
                String name = qcData.getName();
                Long picID = qcData.getPictureId();
                if (picID != null) {
                    //存在图片ID，生成图片数据对象
                    String url = URLCollections.PIC_URL_PREFIX + picID + URLCollections.PIC_URL_SUFFIX;
                    ImageData imageData = new ImageData(name, url);
                    imgList.add(imageData);
                }
                Float data = qcData.getData();
                if (data != null) {
                    //存在参数数值，生成参数数值对象
                    String unit = qcData.getUnit();
                    String max = qcData.getValidMax() + "";
                    String min = qcData.getValidMin() + "";
                    ResultData resultData = new ResultData(name, unit, String.valueOf(data), max, min);
                    dataList.add(resultData);
                }
            }
        }
        imgAdapter.notifyDataSetChanged();
        dataAdapter.notifyDataSetChanged();

        tvRecordsChecker.setText(currentRecord.getOperatorName());
        String model = getResources().getString(R.string.view_record_check_times);
        String times = String.format(model, currentRecord.getCheckNO() + 1);
        tvRecordNum.setText(String.valueOf(times));
        tvRecordResult.setText(QCDataStatusEnum.nameOf(currentRecord.getStatus()).getName());
    }

    /**
     * 更新谱图信息
     *
     * @param map 谱图数据对象
     */
    private void updateSpec(Map<String, List<Float>> map) {
        LineChartAdapter chartAdapter = new LineChartAdapter(lineChart, map);
        chartAdapter.setYAxis(500, 0, 10);
    }

    /**
     * 更新图片展示界面
     */
    public void updatePicDialog(Bitmap bitmap) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);
        iv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (picShowDialog.isShowing()) {
            picShowDialog.cancel();
            picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        } else {
            picShowDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                    .create();
            picShowDialog.setView(iv);
            picShowDialog.show();
        }

    }


    private InnerHandler handler = new InnerHandler(this);

    private static class InnerHandler extends BaseInnerHandler {
        InnerHandler(ViewRecordActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ViewRecordActivity vrActivity = (ViewRecordActivity) reference.get();
            switch (msg.what) {
                case ITEM_RECORD_LOADED:
                    vrActivity.itemTimeList.clear();
                    vrActivity.itemRecordDetails.clear();
                    //对调试记录数据进行排序
                    List<QCItemRecordDetails> datas = (List<QCItemRecordDetails>) msg.obj;
                    Collections.sort(datas);
                    //将时间提取到列表中
                    for (QCItemRecordDetails data : datas) {
                        vrActivity.itemTimeList.add(data.getDoneDate());
                    }
                    vrActivity.itemRecordDetails.addAll(datas);
                    vrActivity.timeListAdapter.notifyDataSetChanged();
                    break;
                case PIC_LOAD_FINISH:
                    vrActivity.updatePicDialog((Bitmap) msg.obj);
                    break;
                case SPEC_LOAD_FINISH:
                    vrActivity.updateSpec((Map<String, List<Float>>) msg.obj);
                    break;
                case SPEC_LOAD_ERROR:
                    Toast.makeText(vrActivity, "谱图请求错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 图片列表适配器，实现图片数据的详细展示
     */
    class ImgAdapter extends BaseAdapter {

        public ImgAdapter() {
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public ImageData getItem(int i) {
            return imgList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(ViewRecordActivity.this, R.layout.list_camera_data_item, null);
                holder.tvParamName = view.findViewById(R.id.tv_param_name);
                holder.tvDesc = view.findViewById(R.id.tv_desc);
                holder.imageView = view.findViewById(R.id.iv_param_img);
                view.setTag(holder);
            }
            holder = (ViewHolder) view.getTag();
            holder.tvParamName.setText(getItem(i).name);
            holder.tvDesc.setText("点击查看大图");
            Glide.with(ViewRecordActivity.this).load(getItem(i).url).thumbnail(0.1f).into(holder.imageView);

            view.setMinimumHeight(DeviceUtil.deviceHeight(ViewRecordActivity.this) / 7);

            return view;
        }


        class ViewHolder {
            TextView tvParamName;
            TextView tvDesc;
            ImageView imageView;
        }
    }

    /**
     * 图片数据对象
     */
    private class ImageData {
        String name;
        String url;

        public ImageData(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }

    /**
     * 参数数值数据对象
     */
    private class ResultData {
        String name;
        String unit;
        String data;
        String max;
        String min;

        public ResultData(String name, String unit, String data, String max, String min) {
            this.name = name;
            this.unit = unit;
            this.data = data;
            this.max = max;
            this.min = min;
        }
    }

    /**
     * 值列表适配器，实现项目细节记录中含值参数的展示
     */
    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public ResultData getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(ViewRecordActivity.this, R.layout.list_result_data_item, null);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.detail_item_tv_param_name);
                holder.tvValue = convertView.findViewById(R.id.detail_item_tv_param_value);
                holder.tvMax = convertView.findViewById(R.id.detail_item_tv_param_max);
                holder.tvMin = convertView.findViewById(R.id.detail_item_tv_param_min);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String name = getItem(position).name;
            holder.tvName.setText(name);
            holder.tvValue.setText(getItem(position).data + " " + getItem(position).unit);
            Float fMax = Float.valueOf(getItem(position).max);
            Float fMin = Float.valueOf(getItem(position).min);
            String max = BaseXmlExpression.NOVALUE.equals(fMax) ? "未解析出最大值" : getItem(position).max;
            String min = BaseXmlExpression.NOVALUE.equals(fMin) ? "未解析出最小值" : getItem(position).min;
            holder.tvMax.setText("最大值：" + max);
            holder.tvMin.setText("最小值：" + min);

            return convertView;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvValue;
            TextView tvMax;
            TextView tvMin;
        }
    }
}
