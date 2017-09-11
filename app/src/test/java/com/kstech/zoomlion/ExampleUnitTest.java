package com.kstech.zoomlion;

import com.kstech.zoomlion.model.session.BaseSession;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.JsonUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TestJson() {
        String source1 = "{\"itemName\":\"压力检测\",\"paramName\":\"压力\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source2 = "{\"itemName\":\"压力检测\",\"paramName\":\"底盘\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source3 = "{\"itemName\":\"压力检测\",\"paramName\":\"灯管\",\"value\":\"40\",\"unit\":\"\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        String source4 = "{\"itemName\":\"压力检测\",\"paramName\":\"温度\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"\",\"chartIds\":\"\"}";
        CheckItemParamValueVO checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source3, CheckItemParamValueVO.class);
        List<Long> imglist = checkItemParamValueVO.getIMGs();
        imglist.add(100l);
        checkItemParamValueVO.setImgIds(imglist);
        String s = JsonUtils.toJson(checkItemParamValueVO);
        System.out.println(s);
    }

    @Test
    public void JsonCreate(){
        ArrayList<String> v = new ArrayList<>();
        v.add("赵高");
        v.add("李斯");
        v.add("胡亥");
        String s = JsonUtils.toJson(v);
        System.out.println(s);
    }
}