/**
 *
 */
package com.kstech.zoomlion.engine.comm;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import J1939.J1939;
import J1939.J1939_CANID_ts;
import J1939.J1939_Context;
import J1939.can_Message_ts;

/**
 * 负责在通讯层收发数据
 *
 * @author wuwanbo、zhaopuqing
 * @since 2016-10-19 下午6:13:48
 */
public class CommunicationWorker extends Thread {


    /**
     * Tcp服务器(检测终端）的IP地址
     */
    public String serverIPAddress;

    /**
     * Tcp服务器（检测终端）侦听端口号
     */
    public int serverListenPort;

    /**
     * 最近一次从服务器接收数据的时间
     */
    private long lastRecvTime;

    /**
     * 临时变量
     */
    private J1939_CANID_ts canID;

    /**
     * 线程终止标志
     */
    private boolean stop = false;

    private Context context;

    /**
     * 线程终止状态
     */
    public boolean isRunning = false;


    /**
     * 超过该时间间隔没有收到消息视作通讯中断，需要重新建立链接
     */
    private static long TIME_OUT_TIME = 5 * 1000;

    private static String TAG = "CommunicationWorker";


    @Override
    public synchronized void start() {
        stop = false;
        super.start();
    }

    /*
     * 构造函数
     *
     * 根据服务器IP地址和侦听端口号实例化本对象
     */
    public CommunicationWorker(String serverIPAddress, int serverListenPort, Context context) {
        super();
        this.serverIPAddress = serverIPAddress;
        this.serverListenPort = serverListenPort;
        this.context = context;
        stop = false;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    /**
     * 生成一串字节的校验码（异或）
     *
     * @param pData     校验数组
     * @param iStartPos 校验起始位置
     * @param iLen      校验字节数
     * @return 异或结果字节
     */
    private byte GenVerifyByte(byte[] pData, int iStartPos, int iLen) {
        byte b = 0;
        for (int i = 0; i < iLen; i++) {
            b = (byte) (b ^ pData[iStartPos + i]);
        }
        return (b);
    }

    /**
     * 处理通过TCP收到的CAN帧：将对本节点的的PGN请求帧放入 can_ReqFIFO链，将其它扩展帧放入can_RxFIFO链
     *
     * @param RecvBuf    ： 接收缓冲区
     * @param iByteStart : CAN帧在接收缓冲区中的位置
     */
    private void RecvCanMessage(byte[] RecvBuf, int iByteStart) {

        byte bFrameInfo;
        int dwCanId;

        bFrameInfo = RecvBuf[iByteStart + 3];

        if ((bFrameInfo & 0xC0) != 0x80) {
            // 忽略标准帧或非数据帧
            return;
        }

        // 处理扩展数据帧，
        if (canID == null)
            canID = new J1939_CANID_ts(0);

        dwCanId = ((RecvBuf[iByteStart + 4] << 24) & 0xFF000000)
                | ((RecvBuf[iByteStart + 5] << 16) & 0x00FF0000)
                | ((RecvBuf[iByteStart + 6] << 8) & 0x0000FF00)
                | (RecvBuf[iByteStart + 7] & 0x000000FF);

        canID.setID(dwCanId);

        // 帧数据长度
        bFrameInfo &= 0x0F;

        if ((canID.PF() > (short) J1939.PF_PRIV)
                || (canID.PS() == (short) J1939.DA_GLOBAL)
                || (canID.PS() == J1939_Context.j1939_CommCfg.ownAddr_u8)) {

        } else {

        }

        can_Message_ts rxCanMsg = new can_Message_ts();

        rxCanMsg.id_u32 = (dwCanId & 0x03FFFFFF); // 去掉29位ID中的优先级位
        rxCanMsg.numBytes_u8 = bFrameInfo; // 帧数据长度
        rxCanMsg.format_u8 = J1939.CAN_EXD; // 帧格式

        System.arraycopy(RecvBuf, iByteStart + 8, // 读帧数据到帧数据接收缓冲区
                rxCanMsg.data_au8, 0, 8);

        if (canID.PF() == (byte) (J1939.PF_REQPGN)) { //
            J1939_Context.j1939_CommCfg.can_ReqFIFO.add(rxCanMsg); // 将读到的消息放入请求FIFO中
        } //
        else {
            //判断条件为 添加一个元素，若失败进入方法体
            if (!J1939_Context.j1939_CommCfg.can_RxFIFO.offer(rxCanMsg)) {
                Log.e(TAG, rxCanMsg + "-------已满--------");
                //去掉头部元素
                J1939_Context.j1939_CommCfg.can_RxFIFO.poll();
                //将最新元素加入 此方法添加 若失败 会报异常
                J1939_Context.j1939_CommCfg.can_RxFIFO.add(rxCanMsg);
            }
        }

    }

    /*
     * 将待发送的CAN帧组装进发送缓冲区
     *
     * @param SendBuf: 发送缓冲区
     *
     * @param iSendLen: 发送缓冲区已有数据长度
     *
     * @param canMsg: 待发送的CAN帧
     */
    private void SendCanMessage(byte[] SendBuf, int iSendLen, can_Message_ts canMsg) {

        long tm;

        SendBuf[iSendLen + 0] = (byte) 0xFE;
        SendBuf[iSendLen + 1] = (byte) 0xFD;
        SendBuf[iSendLen + 2] = (byte) 0x00;
        SendBuf[iSendLen + 3] = (byte) (0x80 | canMsg.numBytes_u8);
        SendBuf[iSendLen + 4] = (byte) (canMsg.id_u32 >> 24);
        SendBuf[iSendLen + 5] = (byte) (canMsg.id_u32 >> 16);
        SendBuf[iSendLen + 6] = (byte) (canMsg.id_u32 >> 8);
        SendBuf[iSendLen + 7] = (byte) (canMsg.id_u32);

        System.arraycopy(canMsg.data_au8, 0, SendBuf, iSendLen + 8, 8);

        tm = System.currentTimeMillis();
        SendBuf[iSendLen + 16] = (byte) (tm >> 16);
        SendBuf[iSendLen + 17] = (byte) (tm >> 8);
        SendBuf[iSendLen + 18] = (byte) (tm);

        SendBuf[iSendLen + 19] = GenVerifyByte(SendBuf, iSendLen, 19);

    }

    @Override
    public void run() {

        Socket sockTcp = null;
        InputStream in = null;
        OutputStream Out = null;

        byte[] RecvBuf = new byte[1000]; // TCP数据接收缓冲区
        byte[] SendBuf = new byte[1000]; // TCP发送缓冲区

        int iRecvLen = 0; // 缓冲区中有效数据（还未处理数据）字节数
        int iByteStart = 0; // 缓冲区中有效数据（还未处理数据）的起始位置
        int iRecvBytes = 0; // 单次调用 read（）读到的字节数

        int iSendLen = 0;
        boolean bNop; // 空任务循环周期标志。

        // 当循环过程中接收到数据或发送了CAN帧时置为false
        isRunning = true;

        ArrayBlockingQueue<can_Message_ts> msgList = J1939_Context.j1939_CommCfg.can_TxFIFO;

        // 任务循环
        while (true) {
            try {
                // 线程中止
                if (stop) {
                    break;
                }

                bNop = true;

                if (sockTcp == null) {
                    //Log.e("hahah", "sockTcp == null" + Globals.netWorkStatusListeners.size());
                    //Globals.notifyListener(true);
                    String ssid = "";
                    WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiinfo = manager.getConnectionInfo();
                    if (wifiinfo != null) {
                        ssid = wifiinfo.getSSID();
                        Log.e(TAG, "---S S I D---" + ssid);
                    }
                    Log.e(TAG, "before create socket");
                    if ("\"DLINK_DWL2000_01\"".equals(ssid)|| "\"TP-LINK_Outdoor_E85A88\"".equals(ssid) || "\"DLINK_DWL_2000_01\"".equals(ssid) || "\"dlink_zoomlion_test\"".equals(ssid)) {
                        Log.e(TAG, "serverIPAddress" + serverIPAddress);
                        sockTcp = new Socket(serverIPAddress, serverListenPort);
                        Log.e(TAG, "create socket " + sockTcp);
                    }
                    Log.e(TAG, "after create socket" + sockTcp);
                }
                if (sockTcp != null) {
                    in = sockTcp.getInputStream();
                    Out = sockTcp.getOutputStream();
                    lastRecvTime = System.currentTimeMillis();
                    //Globals.notifyListener(false);
                } else {
                    //Globals.notifyListener(true);
                    Thread.sleep(1000);
                    continue;
                }


                if ((System.currentTimeMillis() - lastRecvTime) > TIME_OUT_TIME) {
                    // CAN接收中断超过1分钟，通讯错误
                    /*
                    conn.close();
					conn.setToInterrupted();
					*/
                    //Globals.notifyListener(true);
                    if (sockTcp != null) {
                        sockTcp.close();
                        in = null;
                        Out = null;
                        sockTcp = null;
                        continue;
                    }
                }

                if (in != null && in.available() > 0) {
                    //Globals.notifyListener(false);

                    iRecvBytes = in.read(RecvBuf, iRecvLen, 1000 - iRecvLen);
                    iRecvLen += iRecvBytes;

                    bNop = false; // 标示空操作周期（）

                    // 最近一次读通讯数据的时间
                    lastRecvTime = System.currentTimeMillis();

                    // 缓冲区数据处理循环
                    while (true) {

                        if (iRecvLen >= 8) {
                            if ((RecvBuf[iByteStart] == (byte) 0xAA)
                                    && (RecvBuf[iByteStart + 1] == 0)
                                    && (RecvBuf[iByteStart + 7] == (byte) 0x55)) {
                                // CAN通道状态指示数据
                                iByteStart += 8;
                                iRecvLen -= 8;
                            }
                        }

                        if (iRecvLen >= 20) {
                            if ((RecvBuf[iByteStart] == (byte) 0xFE)
                                    && (RecvBuf[iByteStart + 1] == (byte) 0xFD)
                                    && (RecvBuf[iByteStart + 19] == GenVerifyByte(
                                    RecvBuf, iByteStart, 19))) {

                                // 完整的CAN数据帧
                                RecvCanMessage(RecvBuf, iByteStart);

                                iByteStart += 20;
                                iRecvLen -= 20;

                            } else {
                                // 坏数据帧，丢弃开头字节
                                iByteStart += 1;
                                iRecvLen -= 1;
                            }
                        }

                        if (iRecvLen < 20) {
                            // 还未处理的字节数据小于一帧数据字节数，
                            if (iByteStart > 0) {
                                // 将剩余数据移至缓冲区起始位置
                                System.arraycopy(RecvBuf, iByteStart, RecvBuf,
                                        0, iRecvLen);
                                iByteStart = 0;
                            }
                            break; // 跳出处理循环，继续接收数据
                        }
                    } // 处理循环尾

                } else {
                    // 套接口无可读数据，判断发送缓冲区状态
                    while (msgList.size() > 0) {
                        // 线程中止，或者链接断开
                        if (stop) {
                            isRunning = false;
                            break;
                        }
                        //poll() 方法 直接移除头元素 并返回
                        can_Message_ts canMsg = msgList.poll();

                        bNop = false; // 标示非空操作周期
                        SendCanMessage(SendBuf, iSendLen, canMsg); // 组装发送帧到发送数据区
                        iSendLen += 20; //
                        if (iSendLen >= 100) { // 发送数据区足够长了
                            Out.write(SendBuf, 0, iSendLen); // 送出
                            iSendLen = 0; //
                        }
                    }
                    // 发送数据区还有未发送数据则发送
                    if (iSendLen > 0) {
                        Out.write(SendBuf, 0, iSendLen);
                        iSendLen = 0;
                    }
                }

                if (bNop) {
                    // 任务循环中未收发数据，则休眠5ms, 否则继续任务循环
                    Thread.sleep(5);
                }

            } catch (Exception e) {
                if (stop) {
                    break;
                }
                e.printStackTrace();
                Log.e(TAG, "----- create exception ----");
                Log.e(TAG, "----- create exception ----" + e.toString());
                if (sockTcp != null) {
                    in = null;
                    Out = null;
                    sockTcp = null;
                }
                SystemClock.sleep(1000);
                if (stop) {
                    break;
                }
                // conn.close();
            }
        }
        isRunning = false;
        // 线程终止，关闭套接口
        // conn.close();
        try {
            if (sockTcp != null) {
                sockTcp.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
