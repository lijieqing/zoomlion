package com.kstech.zoomlion.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.session.RegisterSession;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.MeasureDev;
import com.kstech.zoomlion.serverdata.UserInfo;
import com.kstech.zoomlion.utils.APKVersionCodeUtils;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;
import com.kstech.zoomlion.utils.SharedPreferencesUtils;
import com.kstech.zoomlion.view.widget.TextProgressView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户登陆
 */
public class LoginActivity extends BaseActivity {

    private UserLoginTask mAuthTask = null;
    private TerminalLoadTask mTermTask = null;
    private PadRegister padRegister = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mTerminalView;
    private TextView mPadInfoView;
    private Button mSignInButton;

    private AlertDialog terminalDialog;
    private MeasureDev mMT = null;
    private AlertDialog registerDialog;

    //Login Data
    private List<MeasureDev> terminalList = new ArrayList<>();
    private List<String> user_record;
    private int registerID;
    private String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zoomlion_update.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化view
        mNameView = findViewById(R.id.tv_name);
        mPasswordView = findViewById(R.id.password);
        mTerminalView = findViewById(R.id.terminal);
        mPadInfoView = findViewById(R.id.tv_pad_id);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignInButton = findViewById(R.id.email_sign_in_button);

        //点击空白收回键盘
        findViewById(R.id.ll_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        String s;
        //获取用户登陆历史列表
        s = (String) SharedPreferencesUtils.getParam(this, Globals.USER_LOGIN_RECORD, "[\"赵高\",\"李斯\",\"胡亥\"]");
        user_record = JsonUtils.fromArrayJson(s, String.class);
        //获取上次登陆用户
        s = (String) SharedPreferencesUtils.getParam(this, Globals.LAST_USER, "");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, user_record);
        mNameView.setAdapter(adapter);
        mNameView.setThreshold(0);
        mNameView.setText(s);
        //登录按钮
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        //获取测量终端
        mTerminalView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getTerminalTask();
            }
        });

        //平板mac地址显示
        //isRegister = isPadRegister();
        changeRegisterStatus(true);

        //获取文件读写权限
        initExternalPermission();
    }

    /**
     * 判断平板是否已注册
     *
     * @return
     */
    private boolean isPadRegister() {
        boolean regist = false;

        registerID = (int) SharedPreferencesUtils.getParam(this, Globals.PAD_HAS_REGISTER, -1);

        if (registerID == -1) {
            mPadInfoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //创建view从当前activity获取loginactivity
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View logview = inflater.inflate(R.layout.ll_admin_login, null);

                    EditText username = logview.findViewById(R.id.txt_username);
                    EditText password = logview.findViewById(R.id.txt_password);

                    String name = username.getText().toString();
                    String pass = password.getText().toString();

                    padRegister = new PadRegister(name, pass);

                    registerDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("平板注册")
                            .setNeutralButton("注册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    padRegister.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }
                            })
                            .setView(logview)
                            .setNegativeButton("取消", null)
                            .create();
                    registerDialog.show();
                }
            });
        } else {
            regist = true;
        }
        return regist;
    }

    /**
     * 根据是否已注册 改变相关控件的状态
     *
     * @param isRegister
     */
    private void changeRegisterStatus(boolean isRegister) {
        String id;
        try {
            id = DeviceUtil.macAddress();
        } catch (SocketException e) {
            e.printStackTrace();
            id = "未能查询到Mac地址";
        }
        StringBuilder sb = new StringBuilder("平板MAC：");
        sb.append(id);
        if (isRegister) {
            mPadInfoView.setTextColor(Color.DKGRAY);
            mSignInButton.setClickable(true);
            mNameView.setEnabled(true);
            mPasswordView.setEnabled(true);
            mTerminalView.setEnabled(true);
            mPadInfoView.setOnClickListener(null);

        } else {
            sb.append("(未注册，点击注册)");
            mPadInfoView.setTextColor(Color.RED);
            mSignInButton.setClickable(false);
            mNameView.setEnabled(false);
            mPasswordView.setEnabled(false);
            mTerminalView.setEnabled(false);
        }
        mPadInfoView.setText(sb.toString());
    }


    /**
     * 登陆处理
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid name address.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_email));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_length_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (mMT == null) {
            focusView = mTerminalView;
            cancel = true;
            Toast.makeText(this, R.string.error_no_terminal, Toast.LENGTH_SHORT).show();
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(name, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * 获取测量终端信息
     */
    private void getTerminalTask() {
        mTermTask = new TerminalLoadTask();
        mTermTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private boolean isNameValid(String name) {
        return name.trim().length() > 1;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void showMenu(final View view) {
        PopupMenu menu = new PopupMenu(this,view);
        final EditText value = new EditText(this);
        menu.inflate(R.menu.menu_setting);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.server_ip:
                        value.setText((String) SharedPreferencesUtils
                                .getParam(LoginActivity.this,"ServerIP","192.168.32.54"));
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("服务器 IP 设置")
                                .setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String ip = value.getText().toString();
                                        if (!TextUtils.isEmpty(ip)){
                                            if (ip.split("\\.").length!=4){
                                                Snackbar.make(view,"无效的 IP",Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Log.e("LoginActivity",ip);
                                            SharedPreferencesUtils.setParam(LoginActivity.this,"ServerIP",ip);

                                            Snackbar.make(view, URLCollections.getGetDeviceBySnURL(),Snackbar.LENGTH_SHORT).show();

                                        }
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .setView(value)
                                .setCancelable(true)
                                .show();
                        break;
                    case R.id.server_port:
                        value.setText((String) SharedPreferencesUtils
                                .getParam(LoginActivity.this,"Port","9080"));
                        value.setInputType(InputType.TYPE_CLASS_NUMBER);
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("服务器 端口 设置")
                                .setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String port = value.getText().toString();
                                        if (!TextUtils.isEmpty(port)){
                                            Log.e("LoginActivity",port);
                                            SharedPreferencesUtils.setParam(LoginActivity.this,"Port",port);
                                        }
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .setView(value)
                                .setCancelable(true)
                                .show();
                        break;
                    case R.id.goto_debug:
                        Intent intent = new Intent(LoginActivity.this,DebugActivity.class);
                        startActivity(intent);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                        break;
                    case R.id.apk_check_update:
                        checkVerson();
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    /**
     * 请求网络 检查版本是否一致
     */
    private void checkVerson(){
        new AppUpdateTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
    /**
     * 版本更新对话框
     */
    private void showUpdateDialog(final AppUpdateTask task,String versionName, String desc, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本：" + versionName);
        builder.setMessage(desc);
        builder.setPositiveButton("马上升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(task,url);
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    TextProgressView updateProgress;
    private AlertDialog showUpdateProgress(){
        updateProgress = new TextProgressView(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("软件更新")
                .setView(updateProgress)
                .create();
        dialog.show();
        return dialog;
    }
    /**
     * 文件下载
     */
    private void downLoadApk(final AppUpdateTask task, String url){
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                FileChannel outChannel = null;
                FileChannel inChannel = null;
                try {
                    outChannel = new FileOutputStream(apkPath).getChannel();
                    inChannel = new FileInputStream(result).getChannel();
                    outChannel.transferFrom(inChannel, 0, inChannel.size());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outChannel != null)
                            outChannel.close();
                        if (inChannel != null)
                            inChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                task.onProgressUpdate(task.FINISH_UPDATE);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                task.onProgressUpdate(task.UPDATE_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                task.onProgressUpdate(task.START_UPDATE);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                int percent = (int) (current/total *100);
                int downloading = isDownloading?1:0;
                task.onProgressUpdate(task.UPDATING,percent,downloading);
            }
        });
    }

    private class AppUpdateTask extends AsyncTask<Void,Integer,Void>{
        /**软件需要更新*/
        private final int UPDATE = 0;
        /**软件已是最新*/
        private final int NORMAL = 1;
        /**更新出现异常*/
        private final int ERROR = 2;
        private final int START_UPDATE = 3;
        private final int UPDATING = 4;
        private final int FINISH_UPDATE = 5;
        private final int UPDATE_ERROR = 6;
        String versionName;
        String desc;
        String url;
        String error;
        AlertDialog updateDialog = null;
        @Override
        protected Void doInBackground(Void... voids) {
            RequestParams params = new RequestParams(URLCollections.getAppUpdateURL());
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(result);
                        if (!data.has("error")){
                            int versionCode = data.getInt("versionCode");
                            versionName = data.getString("versionName");
                            desc = data.getString("desc");
                            url = data.getString("url");

                            int localCode = APKVersionCodeUtils.getVersionCode(MyApplication.getApplication());
                            if (localCode < versionCode){
                                publishProgress(UPDATE);
                            }else {
                                publishProgress(NORMAL);
                            }
                        }else {
                            error = data.getString("error");
                            publishProgress(ERROR);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = e.getMessage();
                        publishProgress(ERROR);
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    error = ex.getMessage();
                    publishProgress(ERROR);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int status = values[0];
            switch (status){
                case UPDATE:
                    showUpdateDialog(this,versionName,desc,url);
                    break;
                case NORMAL:
                    Toast.makeText(LoginActivity.this,"当前已是最新版本",Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(LoginActivity.this,"更新失败\n 错误原因："+error,Toast.LENGTH_SHORT).show();
                    break;
                case START_UPDATE:
                    updateDialog = showUpdateProgress();
                    break;
                case UPDATING:
                    if (updateDialog!=null){
                        int percent = values[1];
                        updateProgress.updateProgress("已下载：%"+percent,percent);
                    }
                    break;
                case FINISH_UPDATE:
                    dialog.cancel();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                    finish();
                    break;
                case UPDATE_ERROR:
                    dialog.cancel();
                    Toast.makeText(LoginActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    /**
     * 登陆线程
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private String mName;
        private String mPassword;
        private String mError;
        private int status = -1;

        /**
         * Instantiates a new User login task.
         *
         * @param name     the name
         * @param password the password
         */
        UserLoginTask(String name, String password) {
            mName = name;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String mac = null;
            try {
                mac = DeviceUtil.macAddress();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", mName);
            maps.put("password", mPassword);
            // TODO: 2018/1/23 此处用于模拟器调试，正式版需要删除
            if (mac == null) mac = "FF:FF:FF:FF:FF:FF";
            maps.put("mac", mac);
            maps.put("measureDevId", mMT.getId() + "");

            new MyHttpUtils().xutilsPost(null, URLCollections.getUserLoginURL(), maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    LogUtils.e("LoginActivity", "onSuccess  " + result);
                    try {
                        JSONObject object = new JSONObject((String) result);
                        if (URLCollections.isRequestSuccess(object)) {
                            status = 2;
                            String userInfo = object.getJSONObject("userinfo").toString();

                            //用户信息赋值给全局变量
                            Globals.currentUser = JsonUtils.fromJson(userInfo, UserInfo.class);
                            URLCollections.initSID();
                            onProgressUpdate();
                        } else {
                            JSONObject eObj = (JSONObject) object.get("error");
                            String msg = eObj.getString("message");
                            mError = TextUtils.isEmpty(msg)?"未知错误":msg;
                            status = 1;
                            onProgressUpdate();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mError = "数据格式错误";
                        status = 1;
                        onProgressUpdate();
                    }
                }

                @Override
                public void onError(Object errorMsg, String whereRequest) {
                    LogUtils.e("LoginActivity", "onError  " + errorMsg);
                    status = 3;
                    mError = (String) errorMsg;
                    onProgressUpdate();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                }
            });
            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            mAuthTask = null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            switch (status) {
                case 1://密码错误
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, mError, Toast.LENGTH_SHORT).show();
                    mNameView.setError(mError);
                    mPasswordView.setError(mError);
                    mNameView.requestFocus();
                    break;
                case 2://登陆成功
                    //测量终端信息赋值给全局变量
                    Globals.currentTerminal = mMT;
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    //保存用户到记录
                    SharedPreferencesUtils.setParam(LoginActivity.this, Globals.LAST_USER, mName);
                    if (!user_record.contains(mName)) {
                        user_record.add(mName);
                    }
                    String sp = JsonUtils.toJson(user_record);
                    //添加到缓存列表
                    SharedPreferencesUtils.setParam(LoginActivity.this, Globals.USER_LOGIN_RECORD, sp);

                    Intent intent = new Intent(LoginActivity.this, IndexActivity.class);

                    showProgress(false);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    Toast.makeText(LoginActivity.this, "与服务器通讯失败,错误信息:\n" + mError, Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


    /**
     * 测量终端获取线程
     */
    private class TerminalLoadTask extends AsyncTask<Void, Integer, List<MeasureDev>> {
        /**
         * The Adapter.
         */
        ArrayAdapter<MeasureDev> adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            terminalList.clear();
            ListView view = new ListView(LoginActivity.this);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mMT = terminalList.get(i);
                    terminalDialog.cancel();
                    onProgressUpdate(2);
                }
            });

            adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, terminalList);

            view.setAdapter(adapter);

            terminalDialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("测量终端选择")
                    .setView(view)
                    .create();

            terminalDialog.show();
            terminalDialog.setMessage("loading terminal");
        }

        @Override
        protected List<MeasureDev> doInBackground(Void... voids) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("status", "0");
            new MyHttpUtils().xutilsGet(null, URLCollections.getTerminalListURL(), maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    terminalList.clear();
                    List<MeasureDev> temp;
                    LogUtils.e("ServerTest", "onSuccess  " + result);
                    temp = JsonUtils.fromArrayJson((String) result, MeasureDev.class);
                    if (temp.size() == 0) {
                        onProgressUpdate(3);
                        Toast.makeText(LoginActivity.this, "可用测量终端数为0  ！！", Toast.LENGTH_SHORT).show();
                    } else {
                        terminalList.addAll(temp);
                        onProgressUpdate(1);
                    }
                }

                @Override
                public void onError(Object errorMsg, String whereRequest) {
                    LogUtils.e("ServerTest", "onError  " + errorMsg);
                    onProgressUpdate(3);
                    Toast.makeText(LoginActivity.this, "与服务器通讯失败,错误信息:\n" + errorMsg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    LogUtils.e("ServerTest", "onLoading");
                }
            });

            return terminalList;
        }

        @Override
        protected void onPostExecute(List<MeasureDev> measureTerminals) {
            super.onPostExecute(measureTerminals);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case 1:
                    terminalDialog.setTitle("测量终端选择");
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    mTerminalView.setText(mMT.getName());
                    break;
                case 3:
                    terminalDialog.setTitle("无可用测量终端");
                    break;
            }
        }

        @Override
        protected void onCancelled(List<MeasureDev> measureTerminals) {
            super.onCancelled(measureTerminals);
            mTermTask = null;
        }

    }

    /**
     * 平板注册
     */
    private class PadRegister extends AsyncTask<Void, Integer, Void> {
        /**
         * The Name.
         */
        String name;
        /**
         * The Pass.
         */
        String pass;
        /**
         * The Progress dialog.
         */
        ProgressDialog progressDialog;

        /**
         * Instantiates a new Pad register.
         *
         * @param name the name
         * @param pass the pass
         */
        PadRegister(String name, String pass) {
            this.name = name;
            this.pass = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("调试终端注册");
            progressDialog.setMessage("验证管理员身份。。。。");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SystemClock.sleep(2000);
            String id = null;
            try {
                id = DeviceUtil.macAddress();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", name);
            maps.put("password", pass);
            maps.put("mac_addr", id);
            new MyHttpUtils().xutilsPost(null, URLCollections.REGISTER_PAD, maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    LogUtils.e("RegisterTest", "onSuccess  " + result);
                    RegisterSession session = JsonUtils.fromJson((String) result, RegisterSession.class);

                    if (session.isError()) {
                        String error = session.getError();
                        switch (error) {
                            case "200":
                                LogUtils.e("RegisterTest", "onSuccess  调试终端已注册 ");
                                int padID = session.getData();
                                SharedPreferencesUtils.setParam(LoginActivity.this, Globals.PAD_HAS_REGISTER, padID);
                                publishProgress(2);
                                break;
                        }
                    } else {
                        int padID = session.getData();
                        SharedPreferencesUtils.setParam(LoginActivity.this, Globals.PAD_HAS_REGISTER, padID);
                        publishProgress(1);
                        LogUtils.e("RegisterTest", "onSuccess  padID: " + padID);
                    }
                }

                @Override
                public void onError(Object errorMsg, String whereRequest) {
                    LogUtils.e("RegisterTest", "onError  " + errorMsg);
                    Toast.makeText(LoginActivity.this, "与服务器通讯失败,错误信息:\n" + errorMsg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    LogUtils.e("RegisterTest", "onLoading");
                }
            });

            SystemClock.sleep(1000);
            publishProgress(0);

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case 1://注册成功
                    progressDialog.setMessage("调试终端注册成功");
                    changeRegisterStatus(true);
                    break;
                case 2://已注册
                    progressDialog.setMessage("调试终端已注册");
                    changeRegisterStatus(true);
                    break;
                case 3://请求失败
                    break;
                case 0://取消进度条
                    progressDialog.cancel();
                    break;
            }
        }
    }

}

