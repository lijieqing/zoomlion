package com.kstech.zoomlion.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
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
import com.kstech.zoomlion.model.session.MeasureTerminal;
import com.kstech.zoomlion.model.session.URLCollections;
import com.kstech.zoomlion.model.session.UserSession;
import com.kstech.zoomlion.utils.DeviceUtil;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;
import com.kstech.zoomlion.utils.MyHttpUtils;
import com.kstech.zoomlion.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户登陆
 */
public class LoginActivity extends BaseActivity {

    private UserLoginTask mAuthTask = null;
    private TerminalLoadTask mTermTask = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mTerminalView;
    private TextView mPadInfoView;

    private AlertDialog terminalDialog;
    private MeasureTerminal mMT = null;

    //Login Data
    private List<MeasureTerminal> terminalList = new ArrayList<>();
    private List<String> user_record;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化view
        mNameView = (AutoCompleteTextView) findViewById(R.id.tv_name);
        mPasswordView = (EditText) findViewById(R.id.password);
        mTerminalView = (TextView) findViewById(R.id.terminal);
        mPadInfoView = (TextView) findViewById(R.id.tv_pad_id);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        //点击空白收回键盘
        findViewById(R.id.ll_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
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

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mTerminalView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getTerminalTask();
            }
        });

        //平板mac地址显示
        String id = DeviceUtil.getMacid(this);
        StringBuilder sb = new StringBuilder("平板MAC：");
        sb.append(id);
        mPadInfoView.setText(sb.toString());

    }

    private void isPadRegister() {
        boolean register = (boolean) SharedPreferencesUtils.getParam(this, Globals.PAD_HAS_REGISTER, false);
        if (!register) {

        }
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
        return password.length() > 4;
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

        UserLoginTask(String name, String password) {
            mName = name;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String macid = DeviceUtil.getMacid(LoginActivity.this);
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", mName);
            maps.put("password", mPassword);
            maps.put("pad_mac", macid);
            maps.put("terminal_id", mMT.getId() + "");
            new MyHttpUtils().xutilsPost(null, URLCollections.USER_LOGIN, maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    LogUtils.e("LoginActivity", "onSuccess  " + result);
                    UserSession session = JsonUtils.fromJson((String) result, UserSession.class);
                    if (session.isError()) {
                        mError = session.getError();
                        //// TODO: 2017/9/11 登录失败时间 status ID为1
                        status = 2;
                        onProgressUpdate();
                    } else {
                        status = 2;
                        //用户信息赋值给全局变量
                        //Globals.currentUser = session.getData(UserBean.class);
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
            showProgress(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            switch (status) {
                case 1://密码错误
                    Toast.makeText(LoginActivity.this, mError, Toast.LENGTH_SHORT).show();
                    mNameView.setError(mError);
                    mPasswordView.setError(mError);
                    mNameView.requestFocus();
                    break;
                case 2://登陆成功
                    //测量终端信息赋值给全局变量
                    Globals.currentTerminal = mMT;
                    Toast.makeText(LoginActivity.this, "登陆成功，准备跳转", Toast.LENGTH_SHORT).show();
                    //保存用户到记录
                    //SharedPreferencesUtils.setParam(LoginActivity.this,Globals.LAST_USER,mName);
                    user_record.add(mName);
                    String sp = JsonUtils.toJson(user_record);
                    //添加到缓存列表
                    //SharedPreferencesUtils.setParam(LoginActivity.this,Globals.USER_LOGIN_RECORD,sp);
                    Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    Toast.makeText(LoginActivity.this, "与服务器通讯失败,错误信息:\n" + mError, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * 测量终端获取线程
     */
    private class TerminalLoadTask extends AsyncTask<Void, Void, List<MeasureTerminal>> {
        ArrayAdapter<MeasureTerminal> adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ListView view = new ListView(LoginActivity.this);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mMT = terminalList.get(i);
                    terminalDialog.cancel();
                    onProgressUpdate();
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
        protected List<MeasureTerminal> doInBackground(Void... voids) {
            terminalList.clear();
            MeasureTerminal mt;
            for (int i = 0; i < 5; i++) {
                mt = new MeasureTerminal(i, "192.168.0." + i, "400" + i, "name" + i, "3" + i);
                terminalList.add(mt);
            }

            SystemClock.sleep(10000);

            return terminalList;
        }

        @Override
        protected void onPostExecute(List<MeasureTerminal> measureTerminals) {
            super.onPostExecute(measureTerminals);
            terminalDialog.setMessage("");

            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mTerminalView.setText(mMT.getName());
        }

        @Override
        protected void onCancelled(List<MeasureTerminal> measureTerminals) {
            super.onCancelled(measureTerminals);
            mTermTask = null;
        }

    }

    /**
     * 平板注册
     */
    private class PadRegister extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int result = -1;


            String id = DeviceUtil.getMacid(LoginActivity.this);
            HashMap<String, String> maps = new HashMap<>();
            maps.put("username", "test");
            maps.put("password", "123");
            maps.put("pad_mac", id);
            new MyHttpUtils().xutilsPost(null, URLCollections.REGISTER_PAD, maps, new MyHttpUtils.MyHttpCallback() {
                @Override
                public void onSuccess(Object result, String whereRequest) {
                    LogUtils.e("RegisterTest", "onSuccess  " + result);

                }

                @Override
                public void onError(Object errorMsg, String whereRequest) {
                    LogUtils.e("RegisterTest", "onError  " + errorMsg);
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    LogUtils.e("RegisterTest", "onLoading");
                }
            });
            return result;
        }

        @Override
        protected void onPostExecute(Integer values) {
            super.onPostExecute(values);
            switch (values) {
                case 1://注册成功
                    break;
                case 2://注册失败
                    break;
                case 3://请求失败
                    break;
            }
        }
    }

}

