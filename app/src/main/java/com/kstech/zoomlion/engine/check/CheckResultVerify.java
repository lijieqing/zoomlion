package com.kstech.zoomlion.engine.check;

import com.kstech.zoomlion.exception.MultiArithmeticException;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.utils.JsonUtils;
import com.kstech.zoomlion.utils.LogUtils;

import java.util.List;

/**
 * Created by lijie on 2017/12/19.
 */

public class CheckResultVerify {
    private CheckResultVerify() {
    }

    /**
     * 调试项目数据合格判定
     * @param paramValues 待判断的调试参数数据集合
     * @param expression 判断所用到的表达式
     * @return 是否合格
     * @throws MultiArithmeticException 多次运算异常
     */
    public static boolean itemVerify(String paramValues, BaseXmlExpression expression) throws MultiArithmeticException {
        //将调试参数数据集合转换为JAVA对象
        List<CheckItemParamValueVO> paramValueVOList = JsonUtils.fromArrayJson(paramValues, CheckItemParamValueVO.class);
        //是否合格
        boolean pass = true;
        //最大值标准值
        float maxV;
        //最小值标准值
        float minV;
        //参数数据
        float paramV;
        //循环对每个调试参数进行判断，出现一次不合格整体为不合格
        for (CheckItemParamValueVO checkItemParamValueVO : paramValueVOList) {
            //获取基本数据
            String value = checkItemParamValueVO.getValue();
            String maxValue = checkItemParamValueVO.getValidMax();
            String minValue = checkItemParamValueVO.getValidMin();
            //判断参数是否为带值参数
            if (checkItemParamValueVO.getValueReq()) {
                //当未检测到最大值数据，赋予默认数据1000
                if (maxValue == null || "".equals(maxValue.trim())) {
                    maxValue = "10000";
                }
                try {
                    //进行类型转换，对于描述性的字符串会出现转换异常，此时在try catch中处理
                    maxV = Float.valueOf(maxValue);
                } catch (NumberFormatException e) {
                    maxV = parseValue(maxValue, checkItemParamValueVO, expression);
                }
                if (minValue == null || "".equals(minValue.trim())) {
                    minValue = "0";
                }
                try {
                    minV = Float.valueOf(minValue);
                } catch (NumberFormatException e) {
                    minV = parseValue(minValue, checkItemParamValueVO, expression);
                }

                paramV = Float.valueOf(value);

                if (paramV < minV || paramV > maxV) {
                    pass = false;
                }

            } else {
                if ("不合格".equals(value)) {
                    pass = false;
                }
            }
        }

        return pass;
    }

    /**
     * 从字符串中取出参数名称
     * @param str 待处理字符串 例：str = getRealParam('paramName')
     * @return  参数名称 例：paramName
     */
    public static String getParamName(String str) {
        int start = str.indexOf("'");
        int end = str.lastIndexOf("'");
        return str.substring(start + 1, end);
    }

    /**
     * 数值解析，对于${} 描述的value字符串进行解析，返回计算后的数值
     * @param value 待解析字符串
     * @param paramValueVO 调试项目参数对象
     * @param expression 字符串解析表达式
     * @return 解析后的数据
     * @throws MultiArithmeticException 多次运算异常
     */
    private static float parseValue(String value, CheckItemParamValueVO paramValueVO, BaseXmlExpression expression) throws MultiArithmeticException {
        float result = -1;
        //判断如果是表达式标记，将字符串进行过滤
        if (value.startsWith("${")) {
            value = value.replace("${", "").replace("}", "");
            //包含运算符，将字符串进行split
            if (value.contains("+")) {
                String[] strs = value.split("\\+");
                //当前只能进行一次运算
                if (strs.length > 2) throw new MultiArithmeticException("目前只能解析一次加减运算，😭");
                result = valueArithmeticInit(strs, true, expression);
            } else {
                result = Float.valueOf(valueInit(value, expression));
            }
            if (value.contains("-")) {
                String[] strs = value.split("-");
                if (strs.length > 2) throw new MultiArithmeticException("目前只能解析一次加减运算，😭");
                result = valueArithmeticInit(strs, false, expression);
            } else {
                result = Float.valueOf(valueInit(value, expression));
            }
        }

        return result;
    }

    //<!--getRealParam(paramName) 获取paramName的实时参数数值-->
    //<!--getPicTime(paramName)获取paramName在数据库的图片保存日期-->
    //<!--getCurrentParam(paramName)获取当前调试项目参数paramName的值-->
    //<!--getMouthTemperature() 根据当前月份获取温度标准-->
    //<!--expPower(paramName) 从dsitem内获取名称为paramName的数据集合-->

    /**
     * 根据字符串集合进行数学运算，并返回结果
     * @param strs 字符串集合
     * @param add 是否进行加法运算，否则进行减法
     * @param expression 表达式对象
     * @return 运算结果
     */
    private static float valueArithmeticInit(String[] strs, boolean add, BaseXmlExpression expression) {
        float mv = 0;
        //对每个字符串进行float转换，捕获转换异常
        for (String str : strs) {
            float temp = 0;
            try {
                temp = Float.valueOf(str);
            } catch (NumberFormatException e) {
                temp = Float.valueOf(valueInit(str, expression));
            }
            if (add) {
                mv += temp;
            } else {
                float max = Math.max(mv, temp);
                float min = Math.min(mv, temp);
                mv = max - min;
            }
        }
        return mv;
    }

    /**
     * 非数字字符串，根据表达式转换为数值
     * @param str 非数字字符串
     * @param expression 表达式对象
     * @return 数字字符串
     */
    private static String valueInit(String str, BaseXmlExpression expression) {
        String result = "";
        String param;
        if (str.contains("getRealParam")) {
            param = getParamName(str);
            LogUtils.e("DaoTest", "getRealParam" + param);
            result = expression.getRealParam(param);
        } else if (str.contains("getPicTime")) {
            param = getParamName(str);
            LogUtils.e("DaoTest", "getPicTime" + param);
            result = expression.getPicTime(param);
        } else if (str.contains("getDBParam")) {
            param = getParamName(str);
            LogUtils.e("DaoTest", "getDBParam" + param);
            result = expression.getDBParam(param);
        } else if (str.contains("getCurrentParam")) {
            param = getParamName(str);
            LogUtils.e("DaoTest", "getCurrentParam" + param);
            result = expression.getCurrentParam(param);
        } else if (str.contains("getMaxMouthTemperature")) {
            LogUtils.e("DaoTest", "getMaxMouthTemperature");
            result = expression.getMaxMouthTemperature();
        } else if (str.contains("getMinMouthTemperature")) {
            LogUtils.e("DaoTest", "getMinMouthTemperature");
            result = expression.getMinMouthTemperature();
        } else if (str.contains("expPower")) {
            // TODO: 2017/12/20 需要处理根据XRange、XParam来计算
            param = getParamName(str);
            LogUtils.e("DaoTest", "expPower" + param);
            result = expression.expPower(param);
        }
        return result;
    }
}
