package com.kstech.zoomlion.model.enums;

/**
 * Created by lijie on 2017/8/10.
 */

public enum CheckRecordResultEnum {
    UNSTART("未开始", 0),
    UNFINISH("未完成", 1),
    PASS("合格", 2),
    UNPASS("不合格", 3),
    FINISH("所有项目已完成，需要判定", 4);

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
                desc = UNSTART.getDesc();
                break;
            case 1:
                desc = UNFINISH.getDesc();
                break;
            case 2:
                desc = PASS.getDesc();
                break;
            case 3:
                desc = UNPASS.getDesc();
                break;
            case 4:
                desc = FINISH.getDesc();
                break;
        }
        return desc;
    }
}
