package com.kstech.zoomlion.model.session;

import android.text.TextUtils;

import com.kstech.zoomlion.utils.JsonUtils;

import java.util.List;

/**
 * Created by lijie on 2017/9/11.
 * <p>
 * 基础通讯类结构
 *
 */
public class BaseSession {
    private String error;
    private String data;
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

    /**
     * 获取数据信息json串
     *
     * @return json字符串
     */
    public String getData() {
        return data;
    }

    /**
     * 获取数据信息Object
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return 数据对象 data object
     */
    public <T> Object getDataObject(Class<T> clazz) {
        return JsonUtils.fromJson(data,clazz);
    }


    /**
     * 获取数据信息集合
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return 对象集合 data array
     */
    public <T> List<T> getDataArray(Class<T> clazz) {
        return JsonUtils.fromArrayJson(data,clazz);
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
        return "BaseSession{" +
                "error='" + error + '\'' +
                ", data='" + data + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
