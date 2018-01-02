package com.kstech.zoomlion.model.enums;

/**
 * Created by lijie on 2017/8/10.
 */

public enum CheckRecordResultEnum {
    UNFINISH("未完成", 0),
    PASS("合格", 1),
    UNPASS("不合格", 2);

    private String desc;
    private int code;

    CheckRecordResultEnum(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public static String getDescByCode(int code) {
        String desc = "";
        switch (code) {
            case 0:
                desc = UNFINISH.getDesc();
                break;
            case 1:
                desc = PASS.getDesc();
                break;
            case 2:
                desc = UNPASS.getDesc();
                break;
        }
        return desc;
    }
}
