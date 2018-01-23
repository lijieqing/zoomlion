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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.model.session.RegisterSession;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.serverdata.MeasureDev;
import com.kstech.zoomlion.serverdata.UserInfo;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;
import com.kstech.zoomlion.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
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

        mPasswordView.setText("52tanwanlanyue");

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
        //TODO: Replace this with your own logic
        return name.trim().length() > 1;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
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
            // TODO: attempt authentication against a network service.
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
            maps.put("measure_dev_id", mMT.getId() + "");

            new MyHttpUtils().xutilsPost(null, URLCollections.USER_LOGIN, maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    LogUtils.e("LoginActivity", "onSuccess  " + result);
                    try {
                        JSONObject object = new JSONObject((String) result);
                        if (URLCollections.isRequestSuccess(object)) {
                            status = 2;
                            String userInfo = object.getString("userInfo");

                            //用户信息赋值给全局变量
                            Globals.currentUser = JsonUtils.fromJson(userInfo, UserInfo.class);
                            URLCollections.initSID();
                            onProgressUpdate();
                        } else {
                            mError = "用户名或密码错误";
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
            new MyHttpUtils().xutilsGet(null, URLCollections.TERMINAL_LIST_GET, maps, new MyHttpUtils.MyHttpCallback() {
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

