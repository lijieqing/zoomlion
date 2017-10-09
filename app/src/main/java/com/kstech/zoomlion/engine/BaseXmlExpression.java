package com.kstech.zoomlion.engine;

/**
 * Created by lijie on 2017/10/3.
 */
//<!--getRealParam(paramName) 获取paramName的实时参数数值-->
//<!--getPicTime(paramName)获取paramName在数据库的图片保存日期-->
//<!--getCurrentParam(paramName)获取当前调试项目参数paramName的值-->
//<!--getMouthTemperature() 根据当前月份获取温度标准-->
//<!--expPower(paramName) 从dsitem内获取名称为paramName的数据集合-->

public interface BaseXmlExpression {
    /**
     * 获取本地数据库中该参数的最新一条数据
     *
     * @param qcID     qc ID 调试项目ID
     * @param param    the param 所查参数名称
     * @param dataType 数据类型
     * @return 数据库数值
     */
    String getDBParam(int qcID, String param, Class dataType);

    /**
     * 获取实时参数的数值
     *
     * @param param 实时参数名称
     * @return 实时参数数值
     */
    String getRealParam(String param);

    /**
     * 获取本次调试过程中的某个参数的值
     *
     * @param param 参数名称
     * @return 参数的数值
     */
    String getCurrentParam(String param);

    /**
     * 获取当前月份的标准温度
     *
     * @return the mouth temperature
     */
    String getMouthTemperature();

    /**
     * 获取某个参数对应的标准值数据集合
     *
     * @param param the param
     * @return the string
     */
    String expPower(String param);

}
