package com.kstech.zoomlion.model.session;

/**
 * @author 7yrs
 * @date 2017/12/25.
 */

public class SessionBlob {
    /**
     * 大数据对象类型
     */
    private Integer type;
    /**
     * 逐个字节排列的对象数据
     */
    private String data;

    public Integer getType() {
        return type;
    }

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
        return "Blob{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}