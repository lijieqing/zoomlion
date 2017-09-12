package com.kstech.zoomlion.model.session;

import android.text.TextUtils;

/**
 * Created by lijie on 2017/9/11.
 * <p>
 * 基础通讯类结构
 */
public class BaseSession<T> {
    private String error;
    private T data;
    private String version;

    /**
     * 请求是否成功
     *
     * @return the boolean
     */
    public boolean isError() {
        boolean isError = true;
        if (TextUtils.isEmpty(error)) {
            isError = false;
        }
        return isError;
    }

    /**
     * 获取错误信息
     *
     * @return the error
     */
    public String getError() {
        return error;
    }


    public T getData() {
        return data;
    }

    /**
     * 获取信息版本，用于列表缓存更新
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "error='" + error +
                ", data=" + data +
//                ", datas=" + datas +
                ", version='" + version;
    }
}
