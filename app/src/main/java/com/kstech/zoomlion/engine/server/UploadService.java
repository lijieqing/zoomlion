package com.kstech.zoomlion.engine.server;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.engine.check.ItemCheckTask;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.enums.CheckItemDetailResultEnum;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.CompleteQCItemJSON;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.ItemFunctionUtils;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.view.activity.CheckHomeActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.kstech.zoomlion.engine.server.AbstractDataTransferTask.packageQCItemData;

public class UploadService extends Service {
    private Timer timer;
    private UploadTask task;
    private Handler handler;

    public UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        task = new UploadTask();
        Log.d("UploadService", "---------onCreate---------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("UploadService", "---------scheduledExecutionTime---" + task.scheduledExecutionTime());
        if (task.scheduledExecutionTime() <= 0) {
            timer.schedule(task, 0, 1000 * 60 * 10);
        } else {
            new QCItemDataReLoadTask(handler).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UploadService", "---------onDestroy---------");
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UploadBinder(this);
    }

    public class UploadBinder extends Binder {
        UploadService service;

        public UploadBinder(UploadService service) {
            this.service = service;
        }

        public void setHandler(Handler handler) {
            service.handler = handler;
        }
    }

    class UploadTask extends TimerTask {

        private CheckItemDetailDataDao detailDataDao;
        private LinkedList<CheckItemDetailData> unUploadDetailDatas;
        private CheckItemDetailData uploadData;
        int successCount = 0;
        int failCount = 0;
        int total = 0;

        public UploadTask() {
            unUploadDetailDatas = new LinkedList<>();
        }

        @Override
        public void run() {
            //如果正在调试，不进行上传
            if (ItemCheckTask.isRunning){
                return;
            }
            while (handler == null) {
                Log.d("UploadService", "---------upload---等待连接---" + this);
                SystemClock.sleep(200);
            }
            handler.sendEmptyMessage(CheckHomeActivity.RECORD_UPDATE_START);
            Log.d("UploadService", "---------upload---start---" + this);

            unUploadDetailDatas.clear();
            detailDataDao = MyApplication.getApplication().getDaoSession().getCheckItemDetailDataDao();
            List<CheckItemDetailData> temp = detailDataDao.queryBuilder().where(CheckItemDetailDataDao.Properties.Uploaded.eq(false)).build().list();
            if (temp != null && temp.size() > 0) {
                total = temp.size();
                unUploadDetailDatas.addAll(temp);
            }

            while (unUploadDetailDatas.size() > 0) {
                uploadData = unUploadDetailDatas.pop();
                CheckItemData itemData = uploadData.getItemData();
                //更新本地数据
                int serverCount = itemData.getSumCounts();
                int localNum = uploadData.getCheckTimes() + 1;

                if (localNum >= serverCount) {
                    itemData.setSumCounts(itemData.getSumCounts() + 1);
                    if (uploadData.getCheckResult().equals(CheckItemDetailResultEnum.PASS.getCode())) {
                        itemData.setPassCounts(itemData.getPassCounts() + 1);
                    }
                    itemData.update();
                }
                //初始化请求参数
                RequestParams params = new RequestParams(URLCollections.getUpdateCheckItemDetailDataURL());
                params.setConnectTimeout(1000 * 60);
                params.addHeader("Cookie", Globals.SID);

                CompleteQCItemJSON data = packageQCItemData(uploadData, itemData);
                String result = JsonUtils.toJson(data);
                params.setBodyContent(result);

                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.has("success")) {
                                uploadData.setUploaded(true);
                                detailDataDao.update(uploadData);
                                uploadData.refresh();
                                Log.d("UploadService", "success"+result);

                                successCount++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (ex instanceof HttpException) {
                            HttpException httpEx = (HttpException) ex;
                            //数据备份失败
                            String code = "数据备份失败，异常码：" + httpEx.getCode();
                            String errorResult = "\n错误内容：" + httpEx.getResult();
                            Log.d("UploadService", code + errorResult);

                            failCount++;
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
            Message message = Message.obtain();
            message.what = CheckHomeActivity.RECORD_UPDATE_INFO;
            message.obj = total;
            message.arg1 = successCount;
            message.arg2 = failCount;
            handler.sendMessage(message);

            Log.d("UploadService", "---------upload---end---" + this);
            handler.sendEmptyMessage(CheckHomeActivity.RECORD_UPDATE_FINISH);
        }
    }
}
