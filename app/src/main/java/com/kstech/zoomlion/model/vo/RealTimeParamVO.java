package com.kstech.zoomlion.model.vo;

import java.io.Serializable;

/**
 * 实时显示参数vo类,包含数值参数和开关量参数
 * @author lijie
 */
public class RealTimeParamVO implements Serializable {
	private static final long serialVersionUID = 4943630941521202379L;
	//参数名称
	private String name;
	//参数单位
	private String unit;
	//参数值
	private String value;
	//参数数据类型
	private String dataType;

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets unit.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets unit.
	 *
	 * @param unit the unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets value.
	 *
	 * @param value the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets data type.
	 *
	 * @param dataType the data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
