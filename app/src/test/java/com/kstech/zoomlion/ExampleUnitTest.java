package com.kstech.zoomlion;

import com.kstech.zoomlion.model.session.UserSession;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.JsonUtils;

import org.junit.Test;

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
    public void JsonCreate() {
//        BaseSession<UserBean> b = new BaseSession<>();
        String s = "{\"data\":{\"last_login_time\":\"yyyy-MM-dd hh:mm:ss\",\"user_id\":\"1\",\"name\":\"ZS\",\"Cversion\":\"11\"}}";
        UserSession session = JsonUtils.fromJson(s, UserSession.class);
        UserSession d = session.getData();
    }
}