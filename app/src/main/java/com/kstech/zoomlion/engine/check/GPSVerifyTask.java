package com.kstech.zoomlion.engine.check;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.engine.comm.CommandResp;
import com.kstech.zoomlion.engine.comm.CommandSender;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.Data;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijie on 2018/3/25.
 * <p>
 * GPS 验证任务
 * 大体流程：分别与测量终端和服务器通讯，将获取到的数据对比，得出结论
 */
public class GPSVerifyTask extends AsyncTask<Void, Integer, Void> {
    /**
     * GPS服务器数据 是否已加载完毕
     */
    private boolean severInfoLoaded = false;
    /**
     * GPS服务器数据获取任务运行状态
     */
    private boolean serverTaskRunning = false;
    /**
     * 测量终端故障
     */
    private static final int GPS_TERMINAL_ERROR = 0;
    /**
     * 测量终端通讯超时
     */
    private static final int GPS_TERMINAL_TIMEOUT = 1;
    /**
     * 服务器故障
     */
    private static final int GPS_SERVER_ERROR = 2;
    /**
     * 配置文件未找到 GPS 相关信息
     */
    public static final int GPS_XML_INFO_ERROR = 3;
    /**
     * GPS 结果比对完成
     */
    public static final int GPS_VERIFY_SUCCESS = 4;
    /**
     * GPS 调试信息
     */
    private CheckItemVO gpsItemVO;
    /**
     * 服务器 GPS 数据
     */
    private Map<String, String> serverGPSInfo;
    /**
     * 测量终端 GPS 数据
     */
    private Map<String, String> terminalGPSInfo;
    /**
     * GPS 是否匹配
     */
    private boolean resultMatch;
    /**
     * 开始时间
     */
    private Date start;

    private Handler handler;

    public GPSVerifyTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        start = new Date();

        List<CheckItemVO> gpsS = Globals.modelFile.checkItemMap.get("GPS");
        if (gpsS != null && gpsS.size() > 0) {
            gpsItemVO = gpsS.get(0);
        }

        terminalGPSInfo = new HashMap<>();
        serverGPSInfo = new HashMap<>();
        List<CheckItemParamValueVO> paramList = gpsItemVO.getParamNameList();
        for (CheckItemParamValueVO param : paramList) {
            String name = param.getParamName();
            terminalGPSInfo.put(name, "");
            serverGPSInfo.put(name, "");
        }
    }

    /**
     * 调试用时时间，单位：S
     */
    private int remainSeconds;

    @Override
    protected Void doInBackground(Void... voids) {
        if (gpsItemVO == null) {
            publishProgress(GPS_XML_INFO_ERROR);
            return null;
        }

        //启动获取服务器 GPS 信息任务
        new ServerGPSTask().start();

        //与测量终端通讯任务

        //是否进入准备状态
        boolean prepared = false;
        remainSeconds = 0;
        CommandSender.sendReadyToCheckCommand(gpsItemVO.getId(), 1);

        Timer prepareTimer = new Timer();
        prepareTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainSeconds++;
            }
        }, 500, 1000);

        String prepareResp = "";

        while (remainSeconds < 60) {
            prepareResp = CommandResp.getReadyToCheckCommandResp(gpsItemVO.getId(), 1);
            if ("准备就绪".equals(prepareResp)) {
                prepared = true;
                prepareTimer.cancel();
                break;
            } else if ("传感器故障".equals(prepareResp) || "检测失败".equals(prepareResp)) {
                prepareTimer.cancel();
                publishProgress(GPS_TERMINAL_ERROR);
                return null;
            }

        }
        //准备调试完成，进入项目调试流程
        if (prepared) {
            //重置数据
            remainSeconds = 0;

            // 发送开始调试命令
            CommandSender.sendStartCheckCommand(gpsItemVO.getId(), 1);
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
                checkResp = CommandResp.getStartCheckCommandResp(gpsItemVO.getId(), 1);
                if ("检测完成".equals(checkResp)) {
                    checkTimer.cancel();
                    //填装数据
                    terminalGPSInfo = new HashMap<>();
                    List<CheckItemParamValueVO> paramList = gpsItemVO.getParamNameList();
                    for (CheckItemParamValueVO param : paramList) {
                        String name = param.getParamName();
                        float v = Globals.modelFile.dataSetVO.getDSItem(name).getFloatValue();
                        terminalGPSInfo.put(name, v + "");
                    }
                    // TODO: 2018/1/29 初始化完成后，保存并上传记录
                    while (serverTaskRunning) {

                    }
                    if (severInfoLoaded) {
                        //比较数据
                        resultMatch = verifyGPSInfo();
                        //保存数据
                        saveAndUpload();
                        publishProgress(GPS_VERIFY_SUCCESS);
                    } else {
                        //通知异常
                        publishProgress(GPS_SERVER_ERROR);
                    }
                    return null;
                } else if ("传感器故障".equals(checkResp) || "检测失败".equals(checkResp)) {
                    checkTimer.cancel();
                    publishProgress(GPS_TERMINAL_ERROR);
                    return null;
                }
            }

            checkTimer.cancel();
            publishProgress(GPS_TERMINAL_TIMEOUT);
            return null;
        } else {
            //准备调试超时通知
            prepareTimer.cancel();
            publishProgress(GPS_TERMINAL_TIMEOUT);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int val = values[0];
        String msg = "";
        int arg1=0;
        switch (val) {
            case GPS_TERMINAL_ERROR:
                msg = "调试终端获取 GPS 信息失败";
                break;
            case GPS_TERMINAL_TIMEOUT:
                msg = "调试终端获取 GPS 信息超时";
                break;
            case GPS_SERVER_ERROR:
                msg = "服务器获取 GPS 信息超时";
                break;
            case GPS_XML_INFO_ERROR:
                msg = "XML 机型文件无 GPS 配置";
                break;
            case GPS_VERIFY_SUCCESS:
                arg1 = -1;
                msg = resultMatch ? "GPS 信息校验完成，结果：匹配" : "GPS 信息校验完成，结果：不匹配";
                break;
        }
        Message message = Message.obtain();
        message.what = CheckHomeActivity.GPS_VERIFY_MSG;
        message.obj = msg;
        message.arg1 = arg1;
        handler.sendMessage(message);
    }

    /**
     * 比较 GPS 信息，并赋值
     */
    private boolean verifyGPSInfo() {
        boolean result = true;
        for (CheckItemParamValueVO param : gpsItemVO.getParamNameList()) {
            String key = param.getParamName();

            String serverValue = serverGPSInfo.get(key);
            String terminalValue = terminalGPSInfo.get(key);
            if (serverValue == null || terminalValue == null || !serverValue.equals(terminalValue)) {
                result = false;
            }
            param.setValue(terminalValue);
            param.setValidMax(serverValue);
            param.setValidMin(serverValue);
        }
        return result;
    }

    /**
     * 保存数据到本地
     */
    private void saveAndUpload() {
        CheckItemDataDao itemDao = MyApplication.getApplication().getDaoSession().getCheckItemDataDao();
        CheckItemDetailDataDao itemDetailDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();

        List<CheckItemData> itemDatas = itemDao.queryBuilder().where(CheckItemDataDao.Properties.RecordId.eq(Globals.recordID),
                CheckItemDataDao.Properties.ItemName.eq(gpsItemVO.getName())).build().list();

        if (itemDatas != null && itemDatas.size() > 0) {
            CheckItemData itemData = itemDatas.get(0);
            //第几次调试
            int times = itemData.getSumCounts()+1;
            List<CheckItemParamValueVO> list = gpsItemVO.getParamNameList();
            //基本参数数据信息
            String paramValues = JsonUtils.toJson(list);
            //调试结果状态
            int checkResult = resultMatch ? CheckItemDetailResultEnum.PASS.getCode() : CheckItemDetailResultEnum.UNPASS.getCode();
            CheckItemDetailData detailData = new CheckItemDetailData(null, itemData.getCheckItemId(),
                    123l, Globals.currentUser.getName(), Globals.currentTerminal.getId(),
                    Globals.currentTerminal.getName(), times, paramValues,
                    checkResult, start, new Date(), "", false, false);
            itemDetailDao.insert(detailData);
        }

    }

    /**
     * 向服务器请求 GPS 相关信息
     */
    class ServerGPSTask extends Thread {
        @Override
        public void run() {
            serverTaskRunning = true;

            SystemClock.sleep(5 * 1000);
            // 向服务器请求数据
            //x.http().getSync();
            severInfoLoaded = true;


            serverTaskRunning = false;
        }
    }
}
