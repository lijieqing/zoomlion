package com.kstech.zoomlion.model.enums;

/**
 * Created by lijie on 2017/8/10.
 */

public enum CheckItemResultEnum {
    UNSTART("未开始", 0),
    UNFINISH("未完成", 1),
    PASS("合格", 2),
    UNPASS("不合格", 3);

    private String desc;
    private int code;

    CheckItemResultEnum(String desc, int code) {
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
        String desc = UNFINISH.desc;
        switch (code) {
            case 0:
                desc = UNSTART.desc;
                break;
            case 1:
                desc = UNFINISH.desc;
                break;
            case 2:
                desc = PASS.desc;
                break;
            case 3:
                desc = UNPASS.desc;
                break;
        }
        return desc;
    }
}
