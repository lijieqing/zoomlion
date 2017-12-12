package com.kstech.zoomlion.model.xmlbean;

import com.kstech.zoomlion.model.ServerData;

/**
 * Created by lijie on 2017/6/13.
 */
public class QCParam extends ServerData{
//    ValueReq: 检测参数项是否有值
//          true 	-- 有值（默认）
//          false 	-- 无值，比如外观检测项
//
//    PicReq: 检测参数项是否要拍照留存
//          true     -- 要拍照留存
//          false      -- 不需要拍照留存（默认）
//
//    ValMode: 检测参数值的获取方式,只当ValReq="Yes"时有意义
//              Auto    -- 通过向测量终端发送检测命令得到数据 作为结果（默认）
//
//              Mann    -- 人工在输入框中输入检测参数值作为结果
//
//              RealParam -- 获取当前参数的实时数值作为结果
//
//              Express -- 根据表达式获取数据，描述规则在ValidAvg中
//
//    QCMode: 检测参数是否合格的判定方式
//              Auto -- 得到参数值(自动得到或手式输入）后通过所配置的合格范围属性值来自动判定是否合格，
//            只适用于ValueReq="Yes"且配置了有效的合格范围时（默认）
//
//            Mann -- 通过人工点选复选框来判定是否合格，适用于：
//            ValueReq="No"或未配置有效的合格值范围时
//
//            None -- 只记录，无需判定是否合格
//
//     XParam: 对以折线形式给出的最大最小值配置，以此值为横坐标，按插值方式计算出最大、最小折线对应的点作为检测参数的合格值范围
//            当自动得到或手输检测参数值后依据插值计算出的合格值范围判定是否合格

    private String Param;

    private Boolean ValueReq;
    private Boolean PicReq;
    private String ValMode;
    private String QCMode;

    private String XParam;
    private String XRange;

    private String ValidAvg;
    private String ValidMax;
    private String ValidMin;

    public QCParam() {
    }

    public String getParam() {
        return Param;
    }

    public void setParam(String param) {
        Param = param;
    }

    public Boolean getValueReq() {
        return ValueReq;
    }

    public void setValueReq(Boolean valueReq) {
        ValueReq = valueReq;
    }

    public Boolean getPicReq() {
        if (PicReq == null){
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

    public String getXParam() {
        return XParam;
    }

    public void setXParam(String XParam) {
        this.XParam = XParam;
    }

    public String getXRange() {
        return XRange;
    }

    public void setXRange(String XRange) {
        this.XRange = XRange;
    }

    public String getValidAvg() {
        return ValidAvg;
    }

    public void setValidAvg(String validAvg) {
        ValidAvg = validAvg;
    }

    public String getValidMax() {
        return ValidMax;
    }

    public void setValidMax(String validMax) {
        ValidMax = validMax;
    }

    public String getValidMin() {
        return ValidMin;
    }

    public void setValidMin(String validMin) {
        ValidMin = validMin;
    }

    @Override
    public String toString() {
        return "QCParam{" +
                "Param='" + Param + '\'' +
                ", ValueReq=" + ValueReq +
                ", PicReq=" + PicReq +
                ", ValMode='" + ValMode + '\'' +
                ", QCMode='" + QCMode + '\'' +
                ", XParam='" + XParam + '\'' +
                ", XRange='" + XRange + '\'' +
                ", ValidAvg='" + ValidAvg + '\'' +
                ", ValidMax='" + ValidMax + '\'' +
                ", ValidMin='" + ValidMin + '\'' +
                '}';
    }
}
