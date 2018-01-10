package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2018/1/9.
 */
public enum CommissioningStatusEnum {
    /**
     * 调试状态：待调试
     */
    NOTSTARTED(0, "待调试"),
    /**
     * 调试状态：调试中
     */
    DOING(1, "调试中"),
    /**
     * 调试状态: 合格
     */
    QUALIFIED(2, "合格"),
    /**
     * 调试状态: 不合格
     */
    DISQUALIFIED(3, "不合格");

    int value;
    String name;

    CommissioningStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static CommissioningStatusEnum nameOf(int value) {
        for (CommissioningStatusEnum statusEnum : CommissioningStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }
}
