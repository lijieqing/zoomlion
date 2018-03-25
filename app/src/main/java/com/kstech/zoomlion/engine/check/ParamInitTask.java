package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.model.enums.CheckItemResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.serverdata.AttachedFile;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.serverdata.QCDataRecordCreateForm;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.IndexActivity;

import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2018/1/29.
 */

public class ParamInitTask extends AsyncTask<Void, Integer, Void> {
    private boolean prepared;
    private int remainSeconds;
    private Handler handler;
    private CheckItemVO initItem;
    private boolean initSuccess = false;

    public ParamInitTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        List<CheckItemVO> initList = Globals.modelFile.checkItemMap.get("Init");
        if (initList!=null && initList.size()>0){
            initItem = initList.get(0);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (initItem == null){
            sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "XML 文件未找到初始化信息",true);
            return null;
        }

        prepared = false;
        remainSeconds = 0;
        CommandSender.sendReadyToCheckCommand(initItem.getId(), 1);
        //创建计时器任务，延时1s后启动
        sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送准备命令 --准备阶段",false);
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
                sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "终端准备就绪",true);
                break;
            } else if ("传感器故障".equals(prepareResp)) {
                sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "初始化失败-准备调试未通过",true);
                prepareTimer.cancel();
                return null;
            }
        }

        //准备调试完成，进入项目调试流程
        if (prepared) {
            //重置数据
            remainSeconds = 0;

            // 发送开始调试命令
            CommandSender.sendStartCheckCommand(initItem.getId(), 1);
            sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "发送初始化命令",true);
            //启动计时器
            Timer checkTimer = new Timer();
            checkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    remainSeconds++;
                }
            }, 500, 1000);

            String checkResp = "";
            while (remainSeconds < 60) {
                checkResp = CommandResp.getStartCheckCommandResp(initItem.getId(), 1);
                if ("".equals(checkResp)) {

                } else if ("正在检测".equals(checkResp)) {
                    sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "参数初始化中",true);
                    SystemClock.sleep(500);

                } else if ("检测完成".equals(checkResp)) {
                    // TODO: 2018/1/29 初始化完成后，保存并上传记录
                    int v = (int) Globals.modelFile.dataSetVO.getDSItem("初始化").getFloatValue();
                    if (v==1){
                        initSuccess = true;
                        packageInitData();
                    }else {
                        sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化失败",true);
                    }

                    checkTimer.cancel();
                    return null;
                } else if ("传感器故障".equals(checkResp) || "检测失败".equals(checkResp)) {

                    sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化失败",true);
                    checkTimer.cancel();
                    return null;
                }
            }
            sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "初始化超时",true);

            checkTimer.cancel();
            return null;
        } else {
            sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "准备初始化超时",true);
            //准备调试超时通知
            prepareTimer.cancel();
            return null;
        }
    }

    /**
     * 打包数据上传服务器
     */
    private void packageInitData(){
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

        RequestParams params = new RequestParams(URLCollections.UPDATE_CHECK_ITEM_DETAIL_DATA);
        params.setConnectTimeout(1000 * 60);
        params.addHeader("Cookie", Globals.SID);
        String json = JsonUtils.toJson(itemJSON);
        params.setBodyContent(json);

        boolean again = true;
        int count = 0;
        try {
            while (again){
                if (count>10){
                    again = false;
                }
                String result = x.http().postSync(params, String.class);
                JSONObject object = new JSONObject(result);
                if (object.has("error") ){
                    count++;
                    sendMsg(IndexActivity.UPDATE_PARAM_INIT_INFO, "数据上传失败，正在重新上传",true);
                    break;
                }
                if (object.has("success")){
                    sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, null,true);
                    again = false;
                }
            }

        } catch (Throwable throwable) {
            sendMsg(IndexActivity.PARAM_INIT_ANIM_CLEAR, "数据上传失败",true);
            throwable.printStackTrace();
        }
    }

    /**
     * 发送信息
     *
     * @param what    目标
     * @param content 内容
     */
    private void sendMsg(int what, String content,boolean forceShow) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = content;
        message.arg1 = forceShow?-1:0;
        handler.sendMessage(message);
    }
}
