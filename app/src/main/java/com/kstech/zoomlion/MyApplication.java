package com.kstech.zoomlion;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.kstech.zoomlion.model.db.greendao.DaoMaster;
import com.kstech.zoomlion.model.db.greendao.DaoSession;
import com.kstech.zoomlion.utils.DateUtil;

import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Created by lijie on 2017/7/5.
 */

public class MyApplication extends Application {
    private static MyApplication application;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
        application = this;
        setDatabase();
        // 设置未捕获异常的处理器
        Thread.setDefaultUncaughtExceptionHandler(new MyHandler());
    }
    public static MyApplication getApplication() {
        return application;
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "duty-green", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }

    /**
     * 自定义Myhandler 异常捕获类 捕获未知异常.
     */
    private class MyHandler implements Thread.UncaughtExceptionHandler {

        // 一旦有未捕获的异常,就会回调此方法
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            // 收集崩溃日志, 可以在后台上传给服务器,供开发人员分析
            try {
                //将crash log写入文件
                File file = new File(Environment.getExternalStorageDirectory()+"/zoomlion_log.txt");
                if (!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                PrintStream printStream = new PrintStream(fileOutputStream);
                printStream.println(DateUtil.getDateTimeFormat(new Date())+"---------------------------------------------");
                ex.printStackTrace(printStream);
                printStream.flush();
                printStream.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 提示，然后结束程序
                Toast.makeText(getApplicationContext(),
                        "很抱歉，程序出错，即将退出:\r\n" + ex.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
            // 停止当前进程，防止下次进入白屏
            android.os.Process.killProcess(android.os.Process.myPid());
//	            System.exit(-1);
        }

    }
}
