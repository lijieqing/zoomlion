package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.session.MeasureTerminal;
import com.kstech.zoomlion.model.session.UserSession;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.view.widget.IRecyclerScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/14.
 */
public class Globals {
    /**
     * 用户相关变量声明
     */
    public static String LAST_USER = "last_user";//上次登陆用户名

    public static String USER_LOGIN_RECORD = "user_record";//历史登陆用户

    public static String PAD_HAS_REGISTER = "pad_has_register";//平板是否已注服务器注册


    //com.lee.xml 标签 对应实体类的存放路径 后边加  .
    public static String CLASSNAME = "com.kstech.zoomlion.model.xmlbean.";

    /**
     * ItemShowView、ItemBodyShowView相关变量
     */
    public static List<CheckItemParamValueVO> paramHeadVOs = new ArrayList<>();
    public static List<IRecyclerScrollListener> headerListener = new ArrayList<>();
    public static List<IRecyclerScrollListener> bodyListener = new ArrayList<>();

    public static void addHeadScrollListener(@NonNull IRecyclerScrollListener listener) {
        if (!headerListener.contains(listener)) {
            headerListener.add(listener);
        }
    }

    public static void removeHeadListener(@NonNull IRecyclerScrollListener listener) {
        if (headerListener.contains(listener)) {
            headerListener.remove(listener);
        }
    }

    public static void onHeadScroll(int x, int y) {
        for (IRecyclerScrollListener recyclerFlingListener : headerListener) {
            recyclerFlingListener.onScroll(x, y);
        }
    }

    /**
     * DeviceModelFile 实体类存放
     */
    public static DeviceModelFile modelFile;

    /**
     * 当前登陆用户
     */
    public static UserSession currentUser;

    /**
     * 当前测量终端
     */
    public static MeasureTerminal currentTerminal;
}
