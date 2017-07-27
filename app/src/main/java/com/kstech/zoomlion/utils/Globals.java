package com.kstech.zoomlion.utils;

import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/14.
 */
public class Globals {
    //com.lee.xml 标签 对应实体类的存放路径 后边加  .
    public static String CLASSNAME = "com.kstech.zoomlion.model.xmlbean.";

    public static List<CheckImageData> values = new ArrayList<>();

    public static List<CheckItemParamValueVO> paramValueVOs = new ArrayList<>();
}
