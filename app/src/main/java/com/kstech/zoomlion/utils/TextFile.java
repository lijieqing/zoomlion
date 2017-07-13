package com.kstech.zoomlion.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TextFile {
    // 读取指定路径文本文件  
    public static String read(String filePath) {
        StringBuilder str = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));
            String s;
            try {
                while ((s = in.readLine()) != null)
                    str.append(s + '\n');
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        return str.toString();
    }

    // 读取指定路径文本文件转换为数字集合
    public static ArrayList<Float> read(String filePath, String filter) {
        ArrayList<Float> re = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));
            String s;
            try {
                while ((s = in.readLine()) != null){
                    if(s.contains(filter)){
                        s = s.replace(filter,"");
                        Log.e("LinkedList<Float>",s);
                        re = (ArrayList<Float>) JsonUtils.fromArrayJson(s.trim(),Float.class);
                    }
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return re;
    }


    // 写入指定的文本文件，append为true表示追加，false表示重头开始写，  
    //text是要写入的文本字符串，text为null时直接返回  
    public static String write(String filePath, boolean append, String text) {
        if (text == null)
            return null;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath, append));
            try {
                out.write(text);
            } finally {
                out.close();
            }
            return filePath;
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            return null;
        }
    }
}  