package com.kstech.zoomlion.engine.server;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.serverdata.AttachedFile;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.serverdata.QCDataRecordCreateForm;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2018/3/25.
 * 参数初始化线程
 */
public class ParamInitUploadTask extends AbstractDataTransferTask {
    private Handler indexHandler;
    /**
     * 初始化用时单位：秒
     */
    private int remainSeconds;
    /**
     * 初始化信息
     */
    private CheckItemVO initItem;
    /**
     * 已请求次数
     */
    private int requestTimes = 0;

    public ParamInitUploadTask(Handler handler) {
        super(handler);
        this.indexHandler = handler;
    }

    @Override
    protected boolean showDialog() {
        return false;
    }

    @Override
    protected void beforeRequest() {
        List<CheckItemVO> initList = Globals.modelFile.checkItemMap.get("Init");
        if (initList != null && initList.size() > 0) {
            initItem = initList.get(0);
        } else {
            requestTimes = 1;
            updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "XML 文件未找到初始化信息", true);
        }
    }

    @Override
    protected boolean needRequest() {
        return requestTimes++ < 1;
    }

    @Override
    protected String getURL() {
        return URLCollections.getNotifyServerGotoCheckURL();
    }

    @Override
    protected boolean initRequestParam(RequestParams params) {
        params.addBodyParameter("sn", Globals.deviceSN);
        params.addBodyParameter("processId", Globals.PROCESSID);
        return true;
    }

    @Override
    protected boolean onReLogin() {
        return true;
    }

    @Override
    protected void onRequestSuccess(JSONObject data) throws JSONException {
        paramInit();
    }


    /**
     * 发送信息
     *
     * @param what    目标
     * @param content 内容
     */
    private void updateMsg(int what, String content, boolean forceShow) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = content;
        message.arg1 = forceShow ? -1 : 0;
        handler.sendMessage(message);
    }

    /**
     * 参数初始化流程
     */
    private void paramInit() {
        boolean prepared = false;
        remainSeconds = 0;
        CommandSender.sendReadyToCheckCommand(initItem.getId(), 1);
        //创建计时器任务，延时1s后启动
        updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送准备命令 --准备阶段", false);
        Timer prepareTimer = new Timer();
        prepareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 500, 1000);

        String prepareResp = "";

        while (remainSeconds < 60) {
            prepareResp = CommandResp.getReadyToCheckCommandResp(initItem.getId(), 1);
            if ("".equals(prepareResp)) {

            } else if ("准备就绪".equals(prepareResp)) {
                prepared = true;
                prepareTimer.cancel();
                updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "终端准备就绪", true);
                break;
            } else if ("传感器故障".equals(prepareResp)) {
                updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "初始化失败-准备调试未通过", true);
                prepareTimer.cancel();
                break;
            }
        }

        //准备调试完成，进入项目调试流程
        if (prepared) {
            //休眠，让测量终端确认收到
            SystemClock.sleep(2000);
            //重置数据
            remainSeconds = 0;

            // 发送开始调试命令
            CommandSender.sendStartCheckCommand(initItem.getId(), 1);
            updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送初始化命令", true);
            //启动计时器
            Timer checkTimer = new Timer();
            checkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    remainSeconds++;
                }
            }, 500, 1000);

            String checkResp = "";
            boolean timeout = true;
            while (remainSeconds < 60) {
                checkResp = CommandResp.getStartCheckCommandResp(initItem.getId(), 1);
                if ("".equals(checkResp)) {

                } else if ("正在检测".equals(checkResp)) {
                    updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "参数初始化中", true);
                    SystemClock.sleep(500);

                } else if ("检测完成".equals(checkResp)) {
                    int v = (int) Globals.modelFile.dataSetVO.getDSItem("初始化").getFloatValue();
                    if (v == 1) {
                        packageInitData();
                    } else {
                        updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化失败", true);
                    }
                    timeout = false;
                    checkTimer.cancel();
                    break;
                } else if ("传感器故障".equals(checkResp) || "检测失败".equals(checkResp)) {
                    updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化失败", true);

                    timeout = false;
                    checkTimer.cancel();
                    break;
                }
            }
            if (timeout) {
                updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化超时", true);
                checkTimer.cancel();
            }
        } else {
            updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "准备初始化超时", true);
            //准备调试超时通知
            prepareTimer.cancel();
        }
    }

    /**
     * 打包数据上传服务器
     */
    private void packageInitData() {
        CompleteQCItemJSON itemJSON = new CompleteQCItemJSON();
        itemJSON.setSn(Globals.deviceSN);
        itemJSON.setStatus(CheckItemResultEnum.PASS.getCode());
        itemJSON.setPassTimes(1);
        itemJSON.setDoneTimes(1);
        itemJSON.setQcitemDictId(Long.parseLong(initItem.getDictId()));
        //服务器 bug
        itemJSON.setAttachedFiles(new ArrayList<AttachedFile>());

        List<QCDataRecordCreateForm> dataForm = new ArrayList<>();
        QCDataRecordCreateForm data = new QCDataRecordCreateForm();
        data.setCheckNo(1);
        data.setData(1f);
        long dictID = Long.parseLong(initItem.getParamNameList().get(0).getDictID());
        data.setQcdataDictId(dictID);
        dataForm.add(data);

        itemJSON.setQcdataRecordCreateForms(dataForm);

        RequestParams params = new RequestParams(URLCollections.getUpdateCheckItemDetailDataURL());
        params.setConnectTimeout(1000 * 60);
        params.addHeader("Cookie", Globals.SID);
        String json = JsonUtils.toJson(itemJSON);
        params.setBodyContent(json);

        try {
            String result = x.http().postSync(params, String.class);
            LogUtils.e("ParamInitTask", result);
            JSONObject object = new JSONObject(result);
            if (object.has("error")) {
                updateMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "数据上传失败，正在重新上传", true);
            } else if (object.has("success")) {
                updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, null, true);
            }

        } catch (Throwable throwable) {
            updateMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "数据上传失败", true);
            throwable.printStackTrace();
        }
    }
}
