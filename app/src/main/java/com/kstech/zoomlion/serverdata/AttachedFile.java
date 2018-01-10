package com.kstech.zoomlion.serverdata;

/**
 * @author 7yrs
 * @date 2017/12/25.
 */

public class AttachedFile {
    /**
     * 大数据对象类型,0PIC.1SPEC
     */
    private Integer type;
    /**
     * 逐个字节排列的对象数据
     */
    private String data;


    public void setType(Integer type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AttachedFile{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
