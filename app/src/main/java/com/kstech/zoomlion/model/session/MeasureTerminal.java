package com.kstech.zoomlion.model.session;

import java.util.List;

/**
 * 测量终端对应实体类
 */
public class MeasureTerminal extends BaseSession<List<MeasureTerminal>>{
    private int id;//对于服务器唯一标识ID
    private String ip_addr;//通讯IP
    private String port = "4004";//通讯端口号
    private String name;//测量终端名称
    private String can_node_id;//J1939通讯ID

    public MeasureTerminal(int id, String ip_addr, String port, String name, String can_node_id) {
        this.id = id;
        this.ip_addr = ip_addr;
        this.port = port;
        this.name = name;
        this.can_node_id = can_node_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip_addr;
    }

    public void setIp(String ip_addr) {
        this.ip_addr = ip_addr;
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
        return can_node_id;
    }

    public void setJ1939ID(String can_node_id) {
        this.can_node_id = can_node_id;
    }

    @Override
    public String toString() {
        return "名称：'" + name + '\'' +
                "\t\t\t终端IP：'" + ip_addr + '\'' +
                "\t\t\t终端端口：'" + port + '\'' +
                "\t\t\t终端通讯ID：'" + can_node_id + '\'';
    }
}
