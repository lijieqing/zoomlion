package com.kstech.zoomlion.model.vo;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kstech.zoomlion.ExcException;
import com.kstech.zoomlion.utils.Globals;
import com.kstech.zoomlion.utils.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查项目参数值对象
 */
public class CheckItemParamValueVO implements Comparable<CheckItemParamValueVO>,Serializable {
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
	private String itemName;
	private String paramName;
	private String value="";
	private String unit="";
	private String type;

	private Boolean ValueReq;
	private Boolean PicReq;
	private String ValMode;
	private String QCMode;

	private String XParam;
	private String XRange;

	private String validMin;
	private String validMax;
	private String validAvg;
	private String imgIds;

	public String getItemName() {
		return itemName;
	}

	public String getImgIds() {
		return imgIds;
	}

	public List<Long> getIMGs(){
		if (imgIds != null){
			if (imgIds.trim().equals("")){
				return new ArrayList<>();
			}
			return JsonUtils.fromArrayJson(imgIds,Long.class);
		} else{
			return new ArrayList<>();
		}
	}

	public List<Long> getCharts(){
		if (chartIds != null){
			if (chartIds.trim().equals("")){
				return new ArrayList<>();
			}
			return JsonUtils.fromArrayJson(chartIds,Long.class);
		} else{
			return new ArrayList<>();
		}
	}

	public void setImgIds(String imgIds) {
		this.imgIds = imgIds;
	}

	public void setImgIds(@NonNull List<Long> imgIds) {
		this.imgIds = JsonUtils.toJson(imgIds);
	}

	public String getChartIds() {
		return chartIds;
	}

	public void setChartIds(String chartIds) {
		this.chartIds = chartIds;
	}
	public void setChartIds(@NonNull List<Long> chartIds) {
		this.chartIds = JsonUtils.toJson(chartIds);
	}

	private String chartIds;

	public void setItemName(String itemname){
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
//		try {
//			Float.valueOf(validMin);
//		}catch (NumberFormatException e){
//			throw new ExcException(e,"配置信息异常！\n检测项目:"+ itemName +"\n 参数"+ paramName +"最小值 "+validMin+" 为非数字");
//		}
	}
	public String getValidMax() {
		return validMax;
	}
	public void setValidMax(String validMax) {
		this.validMax = validMax;
//		try {
//			Float.valueOf(validMax);
//		}catch (NumberFormatException e){
//			throw new ExcException(e,"配置信息异常！\n检测项目:"+ itemName +"\n 参数"+ paramName +"最大值 "+validMax+" 为非数字");
//		}
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
		if (i==0) {
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


}
