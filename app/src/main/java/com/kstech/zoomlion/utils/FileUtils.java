package com.kstech.zoomlion.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.kstech.zoomlion.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by lijie on 2017/4/14.
 */

public class FileUtils {
    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return 将base64编码字符串转换为图片
     */
    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null) return false;
        try {
            // 解密
            byte[] b = Base64.decode(imgStr, Base64.DEFAULT);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param imgFile 图片路径-具体到文件
     * @return 根据图片地址转换为base64编码字符串
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static void dbcopy() {
        String dbpath = MyApplication.getApplication().getDb().getPath();
        File db = new File(dbpath);
        copyDataBaseToSD(db);
    }

    private static void copyDataBaseToSD(File dbFile) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), "duty-green.db");

        FileChannel inChannel = null, outChannel = null;

        try {
            file.createNewFile();
            inChannel = new FileInputStream(dbFile).getChannel();
            outChannel = new FileOutputStream(file).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            Log.e("duty-green.db", "copy dataBase to SD error.");
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if (outChannel != null) {
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
                Log.e("duty.db", "file close error.");
                e.printStackTrace();
            }
        }
    }

    public static void copyToDB(Context context) {
        String dbpath = MyApplication.getApplication().getDb().getPath();
        FileChannel outChannel = null, inChannel = null;
        File out = new File(dbpath);
        File in = new File(Environment.getExternalStorageDirectory(), "duty-green.db");
        try {
            outChannel = new FileOutputStream(out).getChannel();
            inChannel = new FileInputStream(in).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if (outChannel != null) {
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
                Log.e("duty.db", "file close error.");
                e.printStackTrace();
            }
        }

    }
}
