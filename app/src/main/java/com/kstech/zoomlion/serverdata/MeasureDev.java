package com.kstech.zoomlion.serverdata;


/**
 * @author 7yrs
 * @date 2017/12/18.
 */
public class MeasureDev {
    /**
     * 编号
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口号
     */
    private String port;
    /**
     * J1939网络结点号
     */
    private String canNodeId;
    /**
     * 授权设备分类集合
     */
    private String categoryIds;
    /**
     * 状态
     */
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCanNodeId() {
        return canNodeId;
    }

    public void setCanNodeId(String canNodeId) {
        this.canNodeId = canNodeId;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "名称：'" + name + '\'' +
                "\t\t\t终端IP：'" + ip + '\'' +
                "\t\t\t终端端口：'" + port + '\'' +
                "\t\t\t终端通讯ID：'" + canNodeId + '\'';
    }
}
