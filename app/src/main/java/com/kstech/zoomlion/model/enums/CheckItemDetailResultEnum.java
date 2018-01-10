package com.kstech.zoomlion.model.enums;

/**
 * Created by lijie on 2017/8/10.
 */

public enum CheckItemDetailResultEnum {
    FAILED("测量失败", 3), UNPASS("结果不合格", 1), PASS("合格", 0), UNFINISH("未完成", 2);
    private String desc;
    private int code;

    CheckItemDetailResultEnum(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }

    public static String getDescByCode(int code) {
        String result = null;
        switch (code) {
            case 0:
                result = PASS.getDesc();
                break;
            case 1:
                result = UNPASS.getDesc();
                break;
            case 2:
                result = UNFINISH.getDesc();
                break;
            case 3:
                result = FAILED.getDesc();
                break;
        }
        return result;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}
