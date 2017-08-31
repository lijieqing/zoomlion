package com.kstech.zoomlion.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kstech.zoomlion.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by lijie on 2017/4/14.
 */

public class FileUtils {
    public static void dbcopy() {
        String dbpath = MyApplication.getApplication().getDb().getPath();
        File db = new File(dbpath);
        copyDataBaseToSD(db);
    }
    private static void copyDataBaseToSD(File dbFile){
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return ;
        }
        File file  = new File(Environment.getExternalStorageDirectory(), "duty-green.db");

        FileChannel inChannel = null,outChannel = null;

        try {
            file.createNewFile();
            inChannel = new FileInputStream(dbFile).getChannel();
            outChannel = new FileOutputStream(file).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            Log.e("duty-green.db", "copy dataBase to SD error.");
            e.printStackTrace();
        }finally{
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if(outChannel != null){
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
                Log.e("duty.db", "file close error.");
                e.printStackTrace();
            }
        }
    }

    public static void copyToDB(Context context){
        String dbpath = MyApplication.getApplication().getDb().getPath();
        FileChannel outChannel = null,inChannel = null;
        File out = new File(dbpath);
        File in = new File(Environment.getExternalStorageDirectory(),"duty-green.db");
        try {
            outChannel = new FileOutputStream(out).getChannel();
            inChannel = new FileInputStream(in).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if(outChannel != null){
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
