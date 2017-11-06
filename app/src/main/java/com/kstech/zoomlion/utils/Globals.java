package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.manager.DeviceModelFile;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.session.MeasureTerminal;
import com.kstech.zoomlion.model.session.UserSession;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
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
     * 调试项目列表当前选中项目的组 位置 position 对应map中key的位置
     */
    public static int groupPosition = -1;
    /**
     * 调试项目列表当前选中项目的子 位置 position map中组位置下的子位置
     */
    public static int childPosition = -1;
    /**
     * 调试项目类型groups集合
     */
    public static List<String> groups = new ArrayList<>();
    /**
     * 当前选中的调试项目
     */
    public static CheckItemVO currentCheckItem;

    /**
     * 获取前一个调试项目信息
     * @return 前一个调试项目vo类 第一项返回null
     */
    public static CheckItemVO forwardCheckItem(){
        List<CheckItemVO> list;
        CheckItemVO temp;
        if (childPosition == 0){
            //当前组位置不在第一组
            if (groupPosition >= 1){
                groupPosition -=1;
                String group = groups.get(groupPosition);
                list = modelFile.checkItemMap.get(group);
                childPosition = list.size()-1;
                currentCheckItem = list.get(Globals.childPosition);
                temp = currentCheckItem;
            }else {
                //提示当前已经是第一条
                temp = null;
            }
        }else {
            //直接减一 获取child 的位置
            childPosition -= 1;
            String group = groups.get(groupPosition);
            list = modelFile.checkItemMap.get(group);
            currentCheckItem = list.get(Globals.childPosition);
            temp = currentCheckItem;
        }
        return temp;
    }

    /**
     * 获取下一个调试项目信息
     * @return 下一调试项目VO类 最后一项返回Null
     */
    public static CheckItemVO nextCheckItem(){
        List<CheckItemVO> list;
        CheckItemVO temp;
        String group = groups.get(groupPosition);
        list = modelFile.checkItemMap.get(group);
        if (childPosition == list.size() -1){
            if (groupPosition == groups.size()-1){
                //当前已是最后一项
                temp = null;
            }else {
                //跳到下一个组的第一项
                groupPosition += 1;
                childPosition = 0;
                group = groups.get(groupPosition);
                list = modelFile.checkItemMap.get(group);
                currentCheckItem = list.get(0);
                temp = currentCheckItem;
            }
        }else {
            childPosition += 1;
            currentCheckItem = list.get(childPosition);
            temp = currentCheckItem;
        }
        return temp;
    }

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
