package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.IRecyclerScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/14.
 */
public class Globals {
    //com.lee.xml 标签 对应实体类的存放路径 后边加  .
    public static String CLASSNAME = "com.kstech.zoomlion.model.xmlbean.";

    public static List<CheckImageData> values = new ArrayList<>();

    /**
     * ItemShowView、ItemBodyShowView相关变量
     */
    public static List<CheckItemParamValueVO> paramHeadVOs = new ArrayList<>();
    public static List<IRecyclerScrollListener> headerListener = new ArrayList<>();
    public static List<IRecyclerScrollListener> bodyListener = new ArrayList<>();

    public static void addHeadScrollListener(@NonNull IRecyclerScrollListener listener){
        if (!headerListener.contains(listener)){
            headerListener.add(listener);
        }
    }

    public static void removeHeadListener(@NonNull IRecyclerScrollListener listener){
        if (headerListener.contains(listener)){
            headerListener.remove(listener);
        }
    }

    public static void onHeadScroll(int x, int y){
        for (IRecyclerScrollListener recyclerFlingListener : headerListener) {
            recyclerFlingListener.onScroll(x,y);
        }
    }
    /**
     * DeviceModelFile 实体类存放
     */
    public static DeviceModelFile modelFile;
}
