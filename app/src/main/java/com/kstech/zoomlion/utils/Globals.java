package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.IRecyclerFlingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/14.
 */
public class Globals {
    //com.lee.xml 标签 对应实体类的存放路径 后边加  .
    public static String CLASSNAME = "com.kstech.zoomlion.model.xmlbean.";

    public static List<CheckImageData> values = new ArrayList<>();

    public static List<CheckItemParamValueVO> paramHeadVOs = new ArrayList<>();

    public static List<IRecyclerFlingListener> recyclerFlingListeners = new ArrayList<>();

    public static void addFlingListener(@NonNull IRecyclerFlingListener listener){
        if (!recyclerFlingListeners.contains(listener)){
            recyclerFlingListeners.add(listener);
        }
    }

    public static void removeFlingListener(@NonNull IRecyclerFlingListener listener){
        if (recyclerFlingListeners.contains(listener)){
            recyclerFlingListeners.remove(listener);
        }
    }

    public static void onScroll(int x, int y){
        for (IRecyclerFlingListener recyclerFlingListener : recyclerFlingListeners) {
            recyclerFlingListener.onFling(x,y);
        }
    }
}
