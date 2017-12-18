package com.kstech.zoomlion.model.vo;


import java.io.Serializable;

/**
 * 检查项目参数值对象
 */
public class CheckItemParamValueVO implements Comparable<CheckItemParamValueVO>, Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 数据库存储内容如下
     */
//	[{"paramName":"主溢流压力","value":"30","unit":"Mpa",type:"主参数"},
//	 {"paramName":"液压油温度","value":"30","unit":"℃",type:"环境参数"}]
    /**
     * 配置文件配置如下
     */
//	<QCParam Param="主溢流压力" ValidMin="" ValidMax="" ValidAvg=""/>
//  <ENVParam Param="液压油温度" ValidMin="" ValidMax="" ValidAvg=""/>
    /**
     * 参数对应的服务器字典ID
     */
    private String dictID;
    /**
     * 参数所属的调试项目名称
     */
    private String itemName;
    /**
     * 当前参数名称
     */
    private String paramName;
    /**
     * 当前参数对应的数据
     */
    private String value = "";
    /**
     * 当前参数的数值单位
     */
    private String unit = "";
    /**
     * 当前参数的类型
     */
    private String type;
    /**
     * 当前参数有无值
     */
    private Boolean ValueReq;
    /**
     * 当前参数是否需要图片数据
     */
    private Boolean PicReq;
    /**
     * 当前参数，值的获取方式
     * Auto（测量终端获取）
     * Mann（人工输入）
     * RealParam（通过实时参数获取）
     * Express（根据表达式获取）
     */
    private String ValMode;
    /**
     * 当前参数的合格判定方式
     * Auto（系统自动判断）
     * Mann（人工判断合格、不合格）
     * None（只记录，不判断）
     */
    private String QCMode;
    /**
     * XParam变量参数的名称
     */
    private String XParamName;
    /**
     * XParam变量参数取到的数值
     */
    private String XParamValue;
    /**
     * XParam对应服务器的数据库字典ID
     */
    private String XParamDictId;
    /**
     * 当前参数的一段标准范围，通过XRange可以找到对应的DSItem中的Data集合
     */
    private String XRange;
    /**
     * 该参数最小标准值
     */
    private String validMin;
    /**
     * 该参数最大标准值
     */
    private String validMax;
    /**
     * 该参数平均值，某些参数的平均值。当ValModel=Express时内容描述的是表达式
     */
    private String validAvg;

    public CheckItemParamValueVO() {
    }

    public CheckItemParamValueVO(CheckItemParamValueVO paramValue) {
        this.dictID = paramValue.getDictID();
        this.itemName = paramValue.getItemName();
        this.paramName = paramValue.getParamName();
        this.value = paramValue.getValue();
        this.unit = paramValue.getUnit();
        this.type = paramValue.getType();
        this.ValueReq = paramValue.getValueReq();
        this.PicReq = paramValue.getPicReq();
        this.ValMode = paramValue.getValMode();
        this.QCMode = paramValue.getQCMode();
        this.XParamName = paramValue.getXParamName();
        this.XParamValue = paramValue.getXParamValue();
        this.XRange = paramValue.getXRange();
        this.validMin = paramValue.getValidMin();
        this.validMax = paramValue.getValidMax();
        this.validAvg = paramValue.getValidAvg();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemname) {
        this.itemName = itemname;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValidMin() {
        return validMin;
    }

    public void setValidMin(String validMin) {
        this.validMin = validMin;
    }

    public String getValidMax() {
        return validMax;
    }

    public void setValidMax(String validMax) {
        this.validMax = validMax;
    }

    public String getValidAvg() {
        return validAvg;
    }

    public void setValidAvg(String validAvg) {
        this.validAvg = validAvg;
    }

    @Override
    public int compareTo(CheckItemParamValueVO arg0) {
        int i = this.type.compareTo(arg0.type);
        if (i == 0) {
            i = this.paramName.compareTo(arg0.paramName);
        }
        return i;
    }

    public Boolean getValueReq() {
        return ValueReq;
    }

    public void setValueReq(Boolean valueReq) {
        ValueReq = valueReq;
    }

    public Boolean getPicReq() {
        if (PicReq == null) {
            PicReq = false;
        }
        return PicReq;
    }

    public void setPicReq(Boolean picReq) {
        PicReq = picReq;
    }

    public String getValMode() {
        return ValMode;
    }

    public void setValMode(String valMode) {
        ValMode = valMode;
    }

    public String getQCMode() {
        return QCMode;
    }

    public void setQCMode(String QCMode) {
        this.QCMode = QCMode;
    }

    public String getXParamName() {
        return XParamName;
    }

    public void setXParamName(String XParamName) {
        this.XParamName = XParamName;
    }

    public String getXParamValue() {
        return XParamValue;
    }

    public void setXParamValue(String XParamValue) {
        this.XParamValue = XParamValue;
    }

    public String getXRange() {
        return XRange;
    }

    public void setXRange(String XRange) {
        this.XRange = XRange;
    }

    public String getDictID() {
        return dictID;
    }

    public void setDictID(String dictID) {
        this.dictID = dictID;
    }

    public String getXParamDictId() {
        return XParamDictId;
    }

    public void setXParamDictId(String XParamDictId) {
        this.XParamDictId = XParamDictId;
    }
}
