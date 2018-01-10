package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2018/1/9.
 */
public enum QCDataStatusEnum {
    /**
     * 调试项参数状态: 合格
     */
    QUALIFIED(0, "合格"),
    /**
     * 调试项参数状态: 不合格
     */
    DISQUALIFIED(1, "不合格");

    int value;
    String name;

    QCDataStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static QCDataStatusEnum nameOf(int value) {
        for (QCDataStatusEnum statusEnum : QCDataStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }
}
