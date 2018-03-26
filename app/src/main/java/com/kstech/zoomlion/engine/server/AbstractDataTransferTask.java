package com.kstech.zoomlion.engine.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.kstech.zoomlion.model.db.CheckChartData;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.serverdata.AttachedFile;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.serverdata.QCDataRecordCreateForm;
import com.kstech.zoomlion.serverdata.QCDataStatusEnum;
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
        //弹出dialog
        sendMsg(BaseActivity.DIALOG_SHOW, null, 0);
        //更新进度
        sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, getTaskTitle(), 5);

        //请求前执行任务
        beforeRequest();

        RequestParams params;
        while (needRequest()) {
            String result = "";
            sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, getRequestMessage(), 30);

            //初始化请求参数
            params = new RequestParams(getURL());
            params.setConnectTimeout(1000 * 60);
            params.addHeader("Cookie", Globals.SID);
            post = initRequestParam(params);

            try {
                if (post) {
                    result = x.http().postSync(params, String.class);
                } else {
                    result = x.http().getSync(params, String.class);
                }
                LogUtils.e("QCItemDataSaveUploadTask", result);
                if (!onResponse(result)) {
                    JSONObject object = new JSONObject(result);
                    if (!object.has("error")) {
                        //请求成功后调用
                        onRequestSuccess(object);
                        sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, "请求成功！", 50);
                    } else {
                        //请求返回error
                        onRequestError();
                        sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, object.getString("error"), 50);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

                if (URLCollections.isReLogin(result)) {
                    if (onReLogin()) {
                        sendMsg(BaseActivity.USER_RELOGIN, null, 80);
                    } else {
                        sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, "用户登录无效，请求失败", 80);
                    }
                } else {
                    sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, "未能正确解析服务器数据,请求失败", 80);
                }

                break;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(BaseActivity.UPDATE_PROGRESS_CONTENT, throwable.getMessage(), 100);
                break;
            }
        }

        onRequestFinish();

        SystemClock.sleep(1000);
        handler.sendEmptyMessage(BaseActivity.DIALOG_CANCEL);
        afterDialogCancel();
        return null;
    }

    /**
     * 发送更新信息
     *
     * @param what     信息标记
     * @param obj      信息内容
     * @param progress 进度指示
     */
    protected void sendMsg(int what, Object obj, int progress) {
        if (showDialog()) {
            if (obj == null) {
                handler.sendEmptyMessage(what);
            } else {
                message = Message.obtain();
                message.what = what;
                message.obj = obj;
                message.arg1 = progress;

                handler.sendMessage(message);
                SystemClock.sleep(1000);
            }
        }
    }

    /**
     * 获取message描述信息
     *
     * @return 字符串
     */
    protected String getRequestMessage() {
        return "";
    }

    /**
     * 是否需要向服务器请求
     *
     * @return TRUE、FALSE
     */
    protected abstract boolean needRequest();

    /**
     * 请求前所做的数据准备
     */
    protected void beforeRequest() {
    }

    /**
     * 获取URL
     *
     * @return 需要请求的URL地址
     */
    protected abstract String getURL();

    /**
     * 请求参数数据初始化
     *
     * @param params 需要初始化的参数
     *               <p>
     *               return 是否是POST请求
     */
    protected abstract boolean initRequestParam(RequestParams params);

    /**
     * 请求成功回调
     *
     * @param data 返回的Json对象
     */
    protected void onRequestSuccess(JSONObject data) throws JSONException {
    }

    /**
     * 请求失败回调
     */
    protected void onRequestError() {
    }

    /**
     * 是否提示重新登录回调
     *
     * @return 是否重新登录
     */
    protected abstract boolean onReLogin();

    /**
     * 请求完成回调
     */
    protected void onRequestFinish() {
    }

    /**
     * 在dialog取消后回调此方法，子类可根据需求去实现
     */
    protected void afterDialogCancel() {
    }

    /**
     * 在收到服务器返回的字符串后回调此方法，默认返回FALSE，继续进行json数据转换
     * 子类若不想进行json转换，返回TRUE即可
     *
     * @param response 服务器数据
     * @return 是否进行json转换
     */
    protected boolean onResponse(String response) throws JSONException {
        return false;
    }

    protected String getTaskTitle() {
        return "任务开始";
    }

    /**
     * 是否展示弹窗进度
     *
     * @return 默认展示
     */
    protected boolean showDialog() {
        return true;
    }

    /**
     * 将调试项目细节数据打包成服务器传输格式
     *
     * @param detailData 调试项目细节数据
     * @return 打包完成后的对象
     */
    public static CompleteQCItemJSON packageQCItemData(CheckItemDetailData detailData, CheckItemData itemData) {
        //将调试项目数据打包为传输格式数据
        CompleteQCItemJSON qcItemJSON = new CompleteQCItemJSON();

        //需要处理出来的数据
        List<QCDataRecordCreateForm> qcDataRecordCreateFormList = new ArrayList<>();
        List<AttachedFile> attachedFileList = new ArrayList<>();

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
            QCDataRecordCreateForm qcDataRecordCreateForm = new QCDataRecordCreateForm();
            //设置基本信息
            qcDataRecordCreateForm.setCheckNo(detailData.getCheckTimes());
            qcDataRecordCreateForm.setQcdataDictId(Long.valueOf(paramData.getDictID()));
            //获取当前参数数值，并判断参数结果状态
            String value = paramData.getValue();
            Float fv;
            Float max;
            Float min;
            int status;
            if (paramData.getValueReq()) {
                fv = TextUtils.isEmpty(value) ? 0 : Float.valueOf(value);
                max = TextUtils.isEmpty(paramData.getValidMax()) || !TextUtils.isDigitsOnly(paramData.getValidMax()) ? 0 : Float.valueOf(paramData.getValidMax());
                min = TextUtils.isEmpty(paramData.getValidMin()) || !TextUtils.isDigitsOnly(paramData.getValidMin()) ? 0 : Float.valueOf(paramData.getValidMin());
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
            qcDataRecordCreateForm.setData(fv);
            qcDataRecordCreateForm.setValidMax(max);
            qcDataRecordCreateForm.setValidMin(min);
            qcDataRecordCreateForm.setStatus(status);
            //加入参数数据集合
            qcDataRecordCreateFormList.add(qcDataRecordCreateForm);

            //添加图片和谱图附件数据,0-pic.1-spec
            int pos = attachedFileList.size();
            for (CheckImageData checkImageData : detailData.getCheckImageDatas()) {
                if (paramData.getParamName().equals(checkImageData.getParamName())) {
                    AttachedFile picFile = new AttachedFile();
                    picFile.setType(0);
                    picFile.setData(FileUtils.getImageStr(checkImageData.getImgPath()));

                    attachedFileList.add(picFile);

                    qcDataRecordCreateForm.setPictureIndex(pos);
                }
            }
            pos = attachedFileList.size();

            if (i == 0) {
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

                    qcDataRecordCreateForm.setSpectrogramIndex(pos);
                }
            }

        }
        //设置调试项目状态数据，包括已调次数、连续通过次数、和调试项目说明
        qcItemJSON.setDoneTimes(itemData.getSumCounts());
        qcItemJSON.setPassTimes(itemData.getPassCounts());
        qcItemJSON.setStatus(itemData.getCheckResult());

        //组装QCItemResults，并赋值给qcItemJSON
        qcItemJSON.setAttachedFiles(attachedFileList);
        qcItemJSON.setQcdataRecordCreateForms(qcDataRecordCreateFormList);

        return qcItemJSON;
    }
}
