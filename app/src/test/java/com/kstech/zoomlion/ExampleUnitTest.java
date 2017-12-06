package com.kstech.zoomlion;

import com.kstech.zoomlion.model.session.DeviceCatSession;
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
//        String s = "{\"data\":{\"last_login_time\":\"yyyy-MM-dd hh:mm:ss\",\"user_id\":\"1\",\"name\":\"ZS\",\"Cversion\":\"11\"}}";
//        UserSession session = JsonUtils.fromJson(s, UserSession.class);
//        UserSession d = session.getData();
//        DeviceCatSession deviceCatSession = new DeviceCatSession();
//        deviceCatSession.setId(1);
//        deviceCatSession.setLevel(3);
//        deviceCatSession.setName("top");
//        deviceCatSession.setParent_id(0);
//        deviceCatSession.setFull_code("GSFUTYOPP");
//        deviceCatSession.setRemark("remark_content");
        String s = "{\"data\":[{\"id\":1,\"parent_id\":0,\"level\":1,\"name\":\"root1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}," +
                "{\"id\":2,\"parent_id\":0,\"level\":1,\"name\":\"root2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":3,\"parent_id\":1,\"level\":2,\"name\":\"second1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":4,\"parent_id\":2,\"level\":2,\"name\":\"second2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":5,\"parent_id\":3,\"level\":3,\"name\":\"third1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":6,\"parent_id\":4,\"level\":3,\"name\":\"third2\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"},\n" +
                "{\"id\":7,\"parent_id\":6,\"level\":4,\"name\":\"fourth1\",\"remark\":\"remark_content\",\"full_code\":\"GSFUTYOPP\"}]}";
        DeviceCatSession baseSession = JsonUtils.fromJson(s, DeviceCatSession.class);
        List<DeviceCatSession> list = baseSession.getData();
        for (DeviceCatSession deviceCatSession : list) {
            System.out.println(deviceCatSession.toString());
        }
    }
}