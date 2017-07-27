package com.kstech.zoomlion;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.JsonUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        String source = "{\"itemName\":\"压力检测\",\"paramName\":\"温度\",\"value\":\"40\",\"unit\":\"℃\",\"type\":\"环境参数\",\"validMin\":\"30\",\"validMax\":\"50\",\"validAvg\":\"40\",\"imgIds\":\"[8,9]\",\"chartIds\":\"[1,4,6]\"}";
        CheckItemParamValueVO checkItemParamValueVO = (CheckItemParamValueVO) JsonUtils.fromJson(source, CheckItemParamValueVO.class);
        List<Long> imglist = checkItemParamValueVO.getIMGs();
        imglist.add(100l);
        checkItemParamValueVO.setImgIds(imglist);
        String s = JsonUtils.toJson(checkItemParamValueVO);
        System.out.println(s);
    }
}