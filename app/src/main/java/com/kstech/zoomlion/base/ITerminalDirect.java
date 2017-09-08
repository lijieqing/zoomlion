package com.kstech.zoomlion.base;

/**
 * 测量终端初始化引导类（伪代码）
 */
public abstract class ITerminalDirect {

    /**
     * 解析机型文件xml 生成xml标签类
     *
     * @param device xml标签类Device
     */
    abstract void deviceParse(Object device);

    /**
     * 开启1939协议任务
     */
    abstract void startJ1939ProTask();

    /**
     * 开启J1939通讯任务
     */
    abstract void startJ1939CommTask();

    /**
     * 开启心跳服务
     */
    abstract void startHeartBeatService();

    /**
     * 停止1939协议线程
     */
    abstract void stopJ1939ProTask();

    /**
     * 停止1939通讯线程
     */
    abstract void stopJ1939CommTask();

    /**
     * 与测量终端通讯模块初始化流程，伪代码，android中需要与Service进行关联操作
     *
     * @param device xml标签类Device
     * @param first  是否第一次初始化
     */
    public void terminalCommInit(Object device,boolean first){
        if (first){
            //先进行机型解析
            deviceParse(device);

            //再进行1939协议任务的启动--主线程启动
            startJ1939ProTask();

            //最后启动1939通讯任务--主线程启动
            startJ1939CommTask();

            //开启心跳通讯--主线程启动
            startHeartBeatService();
        }else {
            //先停止通讯线程
            stopJ1939CommTask();

            //再停止协议线程
            stopJ1939ProTask();

            //先进行机型解析
            deviceParse(device);

            //再进行1939协议任务的启动--主线程启动
            startJ1939ProTask();

            //最后启动1939通讯任务--主线程启动
            startJ1939CommTask();

            //开启心跳通讯--主线程启动
            startHeartBeatService();
        }

    }
}
