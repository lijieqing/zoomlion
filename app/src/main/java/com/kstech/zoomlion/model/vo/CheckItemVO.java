package com.kstech.zoomlion.model.vo;


import com.kstech.zoomlion.ExcException;
import com.kstech.zoomlion.model.xmlbean.Spectrum;
import com.kstech.zoomlion.utils.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CheckItemVO implements Serializable {
    private static final long serialVersionUID = 530906464911285765L;

    /**
     * 项目检测ID 与测量终端通讯使用
     */
    private String id;

    /**
     * 调试项目对应的服务器字典ID
     */
    private String dictId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * true：必检项目,false：非必检项目
     */
    private boolean require;

    /**
     * 连续检验times次都合格才算总结果合格
     */
    private int times;

    /**
     * 就绪超时时间, 发出准备检测命令到收到准备就绪应答之间的最大时长
     */
    private int readyTimeout;

    /**
     * 检测超时时间，发出开始检测命令到收至检测完成应答之间的最大时长
     */
    private int qcTimeout;

    /**
     * 谱图功能描述类
     */
    private Spectrum spectrum;

    /**
     * 附加pgn
     */
    private String attachPGN;

    /**
     * 信息提示类，对应标签<Msgs/>,定义不同阶段对应的信息内容
     */
    private Msgs msgs = new Msgs();

    /**
     * 当前项目包含的参数集合，包含QCParam和EnvParam
     */
    private List<CheckItemParamValueVO> paramList = new ArrayList<CheckItemParamValueVO>();

    /**
     * 在本检测项目中需要实时显示参数集合，通过解析<RealTimeParams/>标签得到
     */
    private List<RealTimeParamVO> rtParamList = new ArrayList<RealTimeParamVO>();

    public String getJsonParams() {
        return JsonUtils.toJson(paramList);
    }

    public void sortParamList() {
        Collections.sort(paramList);
    }

    public List<CheckItemParamValueVO> getParamNameList() {
        return paramList;
    }

    public void addRtParam(RealTimeParamVO param) {
        rtParamList.add(param);
    }

    public List<RealTimeParamVO> getRtParamList() {
        return rtParamList;
    }

    /**
     * 添加调试参数
     *
     * @param dictID   字典ID
     * @param param    参数名
     * @param valueReq 是否需要值
     * @param picReq   是否需要图片
     * @param valMode  值获取方式
     * @param qcMode   判定方式
     * @param xParam   折线图横坐标参数
     * @param xRange   xParam参数范围
     * @param validMin 最小值
     * @param validMax 最大值
     * @param validAvg 平均值
     * @param unit     单位
     * @throws ExcException
     */
    public void addQcParam(String dictID, String param,
                           Boolean valueReq, Boolean picReq, String valMode, String qcMode,
                           String xParam, String xRange,
                           String validMin, String validMax, String validAvg, String unit) throws ExcException {
        CheckItemParamValueVO vo = new CheckItemParamValueVO();
        vo.setDictID(dictID);
        vo.setItemName(name);
        vo.setParamName(param);

        vo.setValueReq(valueReq);
        vo.setPicReq(picReq);
        vo.setValMode(valMode);
        vo.setQCMode(qcMode);

        vo.setXParamName(xParam);
        vo.setXRange(xRange);

        vo.setValidAvg(validAvg);
        vo.setValidMax(validMax);
        vo.setValidMin(validMin);
        vo.setType("主参数");
        vo.setUnit(unit);
        paramList.add(vo);
    }

    public void addEnvParam(String param, String validMin, String validMax,
                            String validAvg) throws ExcException {
        CheckItemParamValueVO vo = new CheckItemParamValueVO();
        vo.setItemName(name);
        vo.setParamName(param);
        vo.setValidAvg(validAvg);
        vo.setValidMax(validMax);
        vo.setValidMin(validMin);
        vo.setType("环境参数");
        paramList.add(vo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(String strRequire) {
        this.require = Boolean.valueOf(strRequire);
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(String strTimes) {
        this.times = Integer.valueOf(strTimes);
    }

    public int getReadyTimeout() {
        return readyTimeout;
    }

    public void setReadyTimeout(String readyTimeout) {
        this.readyTimeout = Integer.valueOf(readyTimeout);
    }

    public int getQcTimeout() {
        return qcTimeout;
    }

    public void setQcTimeout(String qcTimeout) {
        this.qcTimeout = Integer.valueOf(qcTimeout);
    }

    public String getNotReadyMsg() {
        return msgs.notReadyMsg;
    }

    public void setNotReadyMsg(String notReadyMsg) {
        msgs.notReadyMsg = notReadyMsg;
    }

    public String getAbortMsg() {
        return msgs.abortMsg;
    }

    public void setAbortMsg(String abortMsg) {
        msgs.abortMsg = abortMsg;
    }

    public String getOkMsg() {
        return msgs.okMsg;
    }

    public void setOkMsg(String okMsg) {
        msgs.okMsg = okMsg;
    }

    public String getReadyMsg() {
        return msgs.readyMsg;
    }

    public void setReadyMsg(String readyMsg) {
        msgs.readyMsg = readyMsg;
    }

    public void putProgressMsg(String code, String msg) {
        msgs.progressMap.put(code, msg);
    }

    public String getProgressMsg(String code) {
        return msgs.progressMap.get(code);
    }

    public void putErrorMsg(String code, String msg) {
        msgs.errMap.put(code, msg);
    }

    public String getErrorMsg(String code) {
        return msgs.errMap.get(code);
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getAttachPGN() {
        return attachPGN;
    }

    public void setAttachPGN(String attachPGN) {
        this.attachPGN = attachPGN;
    }

    private static class Msgs {
        /**
         * 检测准备就绪提示,在发出“准备检测”命令并收到“准备就绪”应答后显示
         */
        String readyMsg;
        /**
         * 检测准备未就绪（未达检测条件）提示，在发出“准备检测”命令并收到“传感器故障”应答时显示
         */
        String notReadyMsg;
        /**
         * 检测中止提示，在检测过程中选择“中止检测”发出中止命令且收到“本次测量中止”时显示
         */
        String abortMsg;
        /**
         * 检测完成提示，在收到“检测完成”应答时显示
         */
        String okMsg;

        /**
         * 当收到“正在检测”应答时显示的提示信息，具体内容根据应答帧中的“检测指示码”而定
         */
        Map<String, String> progressMap = new HashMap<String, String>();

        /**
         * 当收到“检测失败”应答时显示的提示信息，具体内容根据应答帧中的“检测指示码”而定
         */
        Map<String, String> errMap = new HashMap<String, String>();

    }

}
