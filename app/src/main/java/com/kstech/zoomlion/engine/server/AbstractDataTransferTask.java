package com.kstech.zoomlion.engine.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.model.db.CheckChartData;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.serverdata.AttachedFile;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.serverdata.QCDataRecordForm;
import com.kstech.zoomlion.serverdata.QCDataStatusEnum;
import com.kstech.zoomlion.serverdata.QCItemResults;
import com.kstech.zoomlion.serverdata.QCItemStatus;
import com.kstech.zoomlion.utils.FileUtils;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lijie on 2018/1/9.
 * 服务器数据传输抽象任务Task
 * 包含了基本的弹窗，进度的刷新，请求成功、失败的回调
 */
public abstract class AbstractDataTransferTask extends AsyncTask<Void, Integer, Void> {
    /**
     * 消息发送handler
     */
    protected Handler handler;
    /**
     * 消息数据对象
     */
    protected Message message;

    private boolean post = true;

    public AbstractDataTransferTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        handler.sendEmptyMessage(BaseActivity.DIALOG_SHOW);

        message = Message.obtain();
        message.obj = getTaskTitle();
        message.arg1 = 5;
        message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
        handler.sendMessage(message);
        SystemClock.sleep(500);

        //请求前执行任务
        beforeRequest();

        RequestParams params;
        while (needRequest()) {
            String result = "";

            message = Message.obtain();
            message.obj = getRequestMessage();
            message.arg1 = 30;
            message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
            handler.sendMessage(message);
            SystemClock.sleep(1000);

            //初始化请求参数
            params = new RequestParams(getURL());
            params.addHeader("Cookie", Globals.SID);
            post = initRequestParam(params);

            try {
                if (post){
                    result = x.http().postSync(params, String.class);
                }else {
                    result = x.http().getSync(params, String.class);
                }
                LogUtils.e("QCItemDataSaveUploadTask", result);
                JSONObject object = new JSONObject(result);
                if (!object.has("error")) {
                    //请求成功后调用
                    onRequestSuccess(object);
                    message = Message.obtain();
                    message.arg1 = 50;
                    message.obj = "请求成功！";
                    message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                } else {
                    //请求返回error
                    onRequestError();
                    message = Message.obtain();
                    message.obj = object.getString("error");
                    message.arg1 = 50;
                    message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                }
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
                message = Message.obtain();
                if (URLCollections.isReLogin(result)) {
                    if (onReLogin(message)) {
                        message.what = BaseActivity.USER_RELOGIN;
                        handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);
                    } else {
                        message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                        message.obj = "用户登录无效，请求失败";
                        message.arg1 = 80;
                    }
                } else {
                    message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                    message.obj = "未能正确解析服务器数据,请求失败";
                    message.arg1 = 80;
                }
                handler.sendMessage(message);
                SystemClock.sleep(1000);
                break;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                message = Message.obtain();
                message.what = BaseActivity.UPDATE_PROGRESS_CONTENT;
                message.obj = throwable.getMessage();
                message.arg1 = 100;
                handler.sendMessage(message);
                SystemClock.sleep(1000);
                break;
            }
        }

        onRequestFinish(true);

        SystemClock.sleep(1000);
        handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);
        afterDialogCancel();
        return null;
    }

    /**
     * 获取message描述信息
     *
     * @return 字符串
     */
    abstract String getRequestMessage();

    /**
     * 是否需要向服务器请求
     *
     * @return TRUE、FALSE
     */
    abstract boolean needRequest();

    /**
     * 请求前所做的数据准备
     */
    abstract void beforeRequest();

    /**
     * 获取URL
     *
     * @return 需要请求的URL地址
     */
    abstract String getURL();

    /**
     * 请求参数数据初始化
     *
     * @param params 需要初始化的参数
     *
     * return 是否是POST请求
     */
    abstract boolean initRequestParam(RequestParams params);

    /**
     * 请求成功回调
     *
     * @param data 返回的Json对象
     */
    abstract void onRequestSuccess(JSONObject data) throws JSONException;

    /**
     * 请求失败回调
     */
    abstract void onRequestError();

    /**
     * 重新登录回调
     *
     * @param message 消息对象
     * @return 是否重新登录
     */
    abstract boolean onReLogin(Message message);

    /**
     * 请求完成回调
     *
     * @param success 请求是否成功
     */
    abstract void onRequestFinish(boolean success);

    protected void afterDialogCancel() {
    }

    protected String getTaskTitle() {
        return "任务开始";
    }

    /**
     * 将调试项目细节数据打包成服务器传输格式
     *
     * @param detailData 调试项目细节数据
     * @return 打包完成后的对象
     */
    CompleteQCItemJSON packageQCItemData(CheckItemDetailData detailData, CheckItemData itemData) {
        //将调试项目数据打包为传输格式数据
        CompleteQCItemJSON qcItemJSON = new CompleteQCItemJSON();

        //需要处理出来的数据
        QCItemResults qcItemResults;
        List<QCDataRecordForm> qcDataRecordFormList = new ArrayList<>();
        List<AttachedFile> attachedFileList = new ArrayList<>();
        QCItemStatus qcItemStatus = new QCItemStatus();

        //设置基本数据信息
        qcItemJSON.setSn(Globals.deviceSN);
        qcItemJSON.setQcitemDictId(Long.valueOf(itemData.getDictId()));

        //解析调试项目参数数据集合
        String paramValues = detailData.getParamsValues();
        List<CheckItemParamValueVO> paramDatas = JsonUtils.fromArrayJson(paramValues, CheckItemParamValueVO.class);
        CheckItemParamValueVO paramData;
        //遍历每个参数数据
        for (int i = 0; i < paramDatas.size(); i++) {
            paramData = paramDatas.get(i);
            /*生成QCDataRecordForm对象*/
            QCDataRecordForm qcDataRecordForm = new QCDataRecordForm();
            //设置基本信息
            qcDataRecordForm.setCheckNo(detailData.getCheckTimes());
            qcDataRecordForm.setQcdataDictId(Long.valueOf(paramData.getDictID()));
            //获取当前参数数值，并判断参数结果状态
            String value = paramData.getValue();
            Float fv;
            Float max;
            Float min;
            int status;
            if (paramData.getValueReq()) {
                fv = Float.valueOf(value);
                max = Float.valueOf(paramData.getValidMax());
                min = Float.valueOf(paramData.getValidMin());
                if (fv >= min && fv <= max) {
                    status = QCDataStatusEnum.QUALIFIED.getValue();
                } else {
                    status = QCDataStatusEnum.DISQUALIFIED.getValue();
                }
            } else {
                fv = null;
                max = null;
                min = null;
                if ("合格".equals(value)) {
                    status = QCDataStatusEnum.QUALIFIED.getValue();
                } else {
                    status = QCDataStatusEnum.DISQUALIFIED.getValue();
                }
            }
            //为当前参数赋值
            qcDataRecordForm.setData(fv);
            qcDataRecordForm.setValidMax(max);
            qcDataRecordForm.setValidMin(min);
            qcDataRecordForm.setStatus(status);
            //加入参数数据集合
            qcDataRecordFormList.add(qcDataRecordForm);

            //添加图片和谱图附件数据,0-pic.1-spec
            int pos = attachedFileList.size();
            if (detailData.getCheckImageDatas().size() > 0) {
                CheckImageData imgData = detailData.getCheckImageDatas().get(0);
                AttachedFile picFile = new AttachedFile();
                picFile.setType(0);
                picFile.setData(FileUtils.getImageStr(imgData.getImgPath()));
                attachedFileList.add(picFile);

                qcDataRecordForm.setAttachedFileIndex(pos);
            }
            pos = attachedFileList.size();
            if (detailData.getCheckChartDatas().size() > 0) {
                Map<String, List<Float>> listMap = new HashMap<>();
                for (CheckChartData chartData : detailData.getCheckChartDatas()) {
                    String name = chartData.getParamName();
                    String str = chartData.getChartData();
                    List<Float> listValue = JsonUtils.fromArrayJson(str, Float.class);
                    listMap.put(name, listValue);
                }
                String charts = JsonUtils.toJson(listMap);
                AttachedFile chartFile = new AttachedFile();
                chartFile.setType(1);
                chartFile.setData(charts);
                attachedFileList.add(chartFile);

                qcDataRecordForm.setAttachedFileIndex(pos);
            }

        }
        //设置调试项目状态数据，包括已调次数、连续通过次数、和调试项目说明
        qcItemStatus.setDoneTimes(itemData.getSumCounts());
        qcItemStatus.setPassTimes(itemData.getPassCounts());
        qcItemStatus.setStatus(itemData.getCheckResult());
        qcItemStatus.setRemark(itemData.getItemDesc());

        //组装QCItemResults，并赋值给qcItemJSON
        qcItemResults = new QCItemResults(qcItemStatus, qcDataRecordFormList, attachedFileList);

        qcItemJSON.setQcitemResults(qcItemResults);

        return qcItemJSON;
    }
}
