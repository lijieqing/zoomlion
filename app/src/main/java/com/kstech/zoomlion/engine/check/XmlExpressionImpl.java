package com.kstech.zoomlion.engine.check;

import com.kstech.zoomlion.MyApplication;
import com.kstech.zoomlion.model.db.CheckImageData;
import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.greendao.CheckItemDataDao;
import com.kstech.zoomlion.model.db.greendao.CheckItemDetailDataDao;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.DSItem;
import com.kstech.zoomlion.model.xmlbean.Data;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import J1939.J1939_DataVar_ts;

/**
 * Created by lijie on 2017/12/21.
 */

public class XmlExpressionImpl implements BaseXmlExpression {
    /**
     * 当前的调试项目细节数据对象，用于获取图片数据信息
     */
    private CheckItemDetailData currentItemDetailData;
    /**
     * 调试完成后，点击保存数据时产生的参数数据集合
     */
    private List<CheckItemParamValueVO> list;

    public XmlExpressionImpl(CheckItemDetailData currentItemDetailData, String paramValues) {
        this.currentItemDetailData = currentItemDetailData;
        list = JsonUtils.fromArrayJson(paramValues, CheckItemParamValueVO.class);
    }

    @Override
    public String getDBParam(String param) {
        //根据param获取CheckItemVO的QCID
        CheckItemVO checkItem = Globals.modelFile.getCheckItemVOByParam(param);
        String qcid = checkItem.getId();
        CheckItemDataDao itemDataDao = MyApplication.getApplication()
                .getDaoSession().getCheckItemDataDao();
        CheckItemDetailDataDao itemDetailDataDao = MyApplication.getApplication()
                .getDaoSession().getCheckItemDetailDataDao();
        //根据QCID获取对应的调试项目数据
        CheckItemData itemData = itemDataDao.queryBuilder()
                .where(CheckItemDataDao.Properties.RecordId.eq(1),
                        CheckItemDataDao.Properties.QcId.eq(qcid)).build().unique();
        if (itemData != null) {
            //按时间顺序降序排列获取调试项目细节记录
            List<CheckItemDetailData> list = itemDetailDataDao.queryBuilder()
                    .where(CheckItemDetailDataDao.Properties.ItemId.eq(itemData.getCheckItemId()))
                    .orderDesc(CheckItemDetailDataDao.Properties.StartTime).build().list();

            if (list != null) {
                //获取最新的调试细节数据
                String paramValues = list.get(0).getParamsValues();
                List<CheckItemParamValueVO> valueList = JsonUtils.fromArrayJson(paramValues, CheckItemParamValueVO.class);
                //循环查找与param对应的数据并返回
                for (CheckItemParamValueVO checkItemParamValueVO : valueList) {
                    if (param.equals(checkItemParamValueVO.getParamName())) {
                        return checkItemParamValueVO.getValue();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String getPicTime(String param) {
        //获取当前调试项目图片数据集合
        List<CheckImageData> imgs = currentItemDetailData.getCheckImageDatas();
        for (CheckImageData img : imgs) {
            //查询对应参数的图片信息，并返回总分钟数
            if (param.equals(img.getParamName())) {
                Date date = img.getCreateTime();
                long min = date.getTime() / 60000;
                return String.valueOf(min);
            }
        }
        return null;
    }

    @Override
    public String getRealParam(String param) {
        J1939_DataVar_ts dataVar = Globals.modelFile.dataSetVO.getDSItem(param);
        return ItemCheckTask.getDataVarValue(dataVar);
    }

    @Override
    public String getCurrentParam(String param) {
        //获取当前调试项目中的某个参数值
        for (CheckItemParamValueVO checkItemParamValueVO : list) {
            if (param.equals(checkItemParamValueVO.getParamName())) {
                return checkItemParamValueVO.getValue();
            }
        }
        return null;
    }

    @Override
    public String getMaxMouthTemperature() {
        //计算月份
        int mouth = new Date().getMonth() + 1;
        //获取本月最高温度索引
        int maxValuePosition = 2 * (mouth - 1) + 1;
        DSItem tempar = null;
        for (DSItem dsItem : Globals.modelFile.device.getDataSet().getDsItems()) {
            if (dsItem.getName().equals("月温度标准")) {
                tempar = dsItem;
                break;
            }
        }
        if (tempar == null) {
            return null;
        }
        return tempar.getDatas().get(maxValuePosition).getValue();
    }

    @Override
    public String getMinMouthTemperature() {
        //计算月份
        int mouth = new Date().getMonth() + 1;
        //获取本月最低温度索引
        int minValuePosition = 2 * (mouth - 1);
        DSItem tempar = null;
        for (DSItem dsItem : Globals.modelFile.device.getDataSet().getDsItems()) {
            if (dsItem.getName().equals("月温度标准")) {
                tempar = dsItem;
                break;
            }
        }
        if (tempar == null) {
            return null;
        }

        return tempar.getDatas().get(minValuePosition).getValue();
    }

    @Override
    public String expPower(String param) {
        //获取当前操作需要的基本数据
        String xParamName = null;
        String xRangeName = null;
        String paramStr = "${expPower('" + param + "')}";
        for (CheckItemParamValueVO checkItemParamValueVO : list) {
            String max = checkItemParamValueVO.getValidMax() == null ? "" : checkItemParamValueVO.getValidMax();
            String min = checkItemParamValueVO.getValidMin() == null ? "" : checkItemParamValueVO.getValidMin();

            if (param.equals(checkItemParamValueVO.getParamName())) {
                xParamName = checkItemParamValueVO.getXParamName();
                xRangeName = checkItemParamValueVO.getXRange();
            }
            if (max.equals(paramStr)) {
                xParamName = checkItemParamValueVO.getXParamName();
                xRangeName = checkItemParamValueVO.getXRange();
            }
            if (min.equals(paramStr)) {
                xParamName = checkItemParamValueVO.getXParamName();
                xRangeName = checkItemParamValueVO.getXRange();
            }
        }
        //获取到X坐标参数的变量名称，和坐标数据集合的变量名称
        String xParam = CheckResultVerify.getParamName(xParamName);
        String xRange = CheckResultVerify.getParamName(xRangeName);

        //根据X坐标变量名称获取到x坐标值
        String xParamValue = getCurrentParam(xParam);
        float xValue = Float.valueOf(xParamValue);

        return getYValue(xValue, xRange, param);
    }

    /**
     * 获取指定x坐标对应的y值
     *
     * @param xValue     x值
     * @param xRangeName x值的范围的索引名称，类型为String，可根据xRangeName在DSItem中找到对应的数据集合
     * @param yRangeName y值的范围的索引名称，类型为String，可根据yRangeName在DSItem中找到对应的数据集合
     * @return xValue对应的y值
     */
    public static String getYValue(float xValue, String xRangeName, String yRangeName) {
        //根据X坐标参数集合变量名称获取X坐标集合数据
        LinkedList<Float> xRangeValues = new LinkedList<>();
        //根据param获取Y值集合
        LinkedList<Float> paramValues = new LinkedList<>();
        for (DSItem dsItem : Globals.modelFile.device.getDataSet().getDsItems()) {
            //获取X坐标集合数据
            if (xRangeName.equals(dsItem.getName())) {
                for (Data data : dsItem.getDatas()) {
                    float v = Float.valueOf(data.getValue());
                    xRangeValues.add(v);
                }
            }
            //获取Y坐标集合数据
            if (yRangeName.equals(dsItem.getName())) {
                for (Data data : dsItem.getDatas()) {
                    float v = Float.valueOf(data.getValue());
                    paramValues.add(v);
                }
            }
        }
        //默认实现用二元一次方程，
        //思路先去找到xValue的参照坐标 x1,x2,y1,y2
        float x1, x2, y1, y2;
        int startPosition = -1;
        int endPosition = -1;
        for (int i = 0; i < xRangeValues.size(); i++) {
            float v = xRangeValues.get(i);
            if (xValue >= v) {
                startPosition = i;
            }
            if (xValue < v) {
                endPosition = i;
                break;
            }
        }
        //存在三种情况，
        //xValue的值比集合中最小的小，比最大的大，位于二者之间
        if (startPosition == -1) {
            //比最小值小
            x1 = xRangeValues.get(endPosition);
            y1 = paramValues.get(endPosition);
            x2 = xRangeValues.get(endPosition + 1);
            y2 = paramValues.get(endPosition + 1);
        } else {
            if (endPosition == -1) {
                //比最大值大
                x1 = xRangeValues.get(startPosition);
                y1 = paramValues.get(startPosition);
                x2 = xRangeValues.get(startPosition - 1);
                y2 = paramValues.get(startPosition - 1);
            } else {
                //位于两者之间
                x1 = xRangeValues.get(startPosition);
                y1 = paramValues.get(startPosition);
                x2 = xRangeValues.get(endPosition);
                y2 = paramValues.get(endPosition);
            }
        }

        //二元一次方程求系数
        float a = (y1 - y2) / (x1 - x2);
        float b = (y1 * x2 - y2 * x1) / (x2 - x1);
        //根据系数求解
        float result = a * xValue + b;

        return String.valueOf(result);
    }
}
