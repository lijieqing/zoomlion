package com.kstech.zoomlion.base;

/**
 * 测量终端对应实体类
 */
public class MeasureTerminal {
    private int id;//对于服务器唯一标识ID
    private String ip;//通讯IP
    private String port;//通讯端口号
    private String name;//测量终端名称
    private String j1939ID;//J1939通讯ID

    public MeasureTerminal(int id, String ip, String port, String name, String j1939ID) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.j1939ID = j1939ID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJ1939ID() {
        return j1939ID;
    }

    public void setJ1939ID(String j1939ID) {
        this.j1939ID = j1939ID;
    }
}
