package com.kstech.zoomlion.engine.server;

import com.kstech.zoomlion.model.db.CheckItemData;
import com.kstech.zoomlion.model.db.CheckItemDetailData;
import com.kstech.zoomlion.model.db.CheckRecord;
import com.kstech.zoomlion.model.treelist.TreeViewAdapter;

/**
 * 设备调试记录相关操作
 */
public interface IDeviceCheckEngine {
    /**
     * 获取调试设备列表
     *
     * @param userID     the user id
     * @param terminalID the terminal id
     */
    TreeViewAdapter getDeviceModelList(int userID, int terminalID);

    /**
     * 通过出厂编号获取设备配置信息
     *
     * @param userID         the user id
     * @param deviceIdentity 设备出厂编号
     * @param url            the url
     */
    void getDeviceModelFile(int userID, String deviceIdentity, String url);

    /**
     * 通过设备ID获取设备配置信息
     *
     * @param userID   the user id
     * @param deviceID 设备ID （从可调式机型列表中获得）
     * @param url      the url
     */
    void getDeviceModelFile(int userID, int deviceID, String url);

    /**
     * 调试项目细节记录上传
     *
     * @param itemDetailData the item detail data
     * @param url            the url
     */
    void uploadCheckItemDetailData(CheckItemDetailData itemDetailData, String url);

    /**
     * 调试项目数据上传
     *
     * @param itemData the item data
     * @param url      the url
     */
    void uploadCheckItemData(CheckItemData itemData, String url);

    /**
     * 整机调试记录上传
     *
     * @param record the record
     * @param url    the url
     */
    void uploadCheckRecord(CheckRecord record, String url);

    /**
     * 获取指定调试项目的服务器调试项目细节表
     *
     * @param deviceIdentity 调试机型的出厂编号
     * @param itemId         the item id
     */
    void getCheckItemDetailList(String deviceIdentity, int itemId, String url);

    /**
     * 获取指定一条调试项目细节记录
     *
     * @param itemDetailId the item id
     * @param url          the url
     */
    void getCheckItemDetailRecord(int itemDetailId, String url);
}
