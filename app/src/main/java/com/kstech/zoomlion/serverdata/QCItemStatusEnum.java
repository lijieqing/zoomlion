package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2018/1/11.
 */
public enum QCItemStatusEnum {
    /**
     * 调试项状态：待调试
     */
    NOTSTARTED(0, "待调试"),
    /**
     * 调试项状态：调试中
     */
    DOING(1, "调试中"),
    /**
     * 调试项状态: 合格
     */
    QUALIFIED(2, "合格"),
    /**
     * 调试项状态: 不合格
     */
    DISQUALIFIED(3, "不合格");

    int value;
    String name;

    QCItemStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static QCItemStatusEnum nameOf(int value) {
        for (QCItemStatusEnum statusEnum : QCItemStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }
}
