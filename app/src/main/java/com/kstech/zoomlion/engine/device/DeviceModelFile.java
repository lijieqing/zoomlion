package com.kstech.zoomlion.engine.device;


import com.kstech.zoomlion.ExcException;
import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.vo.DataSetVO;
import com.kstech.zoomlion.model.vo.J1939PgSetVO;
import com.kstech.zoomlion.model.vo.RealTimeParamVO;
import com.kstech.zoomlion.model.xmlbean.DSItem;
import com.kstech.zoomlion.model.xmlbean.DTC;
import com.kstech.zoomlion.model.xmlbean.Data;
import com.kstech.zoomlion.model.xmlbean.Device;
import com.kstech.zoomlion.model.xmlbean.ENVParam;
import com.kstech.zoomlion.model.xmlbean.PG;
import com.kstech.zoomlion.model.xmlbean.QCErr;
import com.kstech.zoomlion.model.xmlbean.QCItem;
import com.kstech.zoomlion.model.xmlbean.QCParam;
import com.kstech.zoomlion.model.xmlbean.QCProgress;
import com.kstech.zoomlion.model.xmlbean.QCType;
import com.kstech.zoomlion.model.xmlbean.RealTimeParam;
import com.kstech.zoomlion.model.xmlbean.RealTimeSet;
import com.kstech.zoomlion.model.xmlbean.SP;
import com.kstech.zoomlion.utils.Globals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import J1939.J1939_Context;
import J1939.J1939_DTCfg_ts;
import J1939.J1939_DataVar_ts;
import J1939.J1939_PGCfg_ts;
import J1939.J1939_SPCfg_ts;

/**
 * 机型文件对应的值对象，封装了一个机型文件的完整数据
 */
public class DeviceModelFile {
    /**
     * 机型Id
     */
    private String deviceId;

    /**
     * 机型名称
     */
    private String deviceName;
    /**
     * 机型启用日期，格式为"yyyy-mm"
     */
    private String devBornDate;
    /**
     * 机型停产日期，格式为"yyyy-mm"
     */
    private String devDieDate;

    /**
     * 机型当前状态
     */
    private String devStatus;

    /**
     * J1939配置的变量数组，通过解析配置文件的<DataSet>标记及其子标记形成
     */
    public DataSetVO dataSetVO;

    /**
     * J1939配置，通过解析配置文件形成 在解析配置文件时实例化
     */
    public J1939PgSetVO j1939PgSetVO;

    /**
     * 检查项全集合，通过解析配置文件的<QCSet>标记及其子标记形成
     */
    public List<CheckItemVO> allCheckItemList = new ArrayList<>();

    /**
     * 检测项目集合MAP形式，key为QCType中name的值
     */
    public Map<String, List<CheckItemVO>> checkItemMap = new HashMap<>();

    /**
     * 在调试引导界面中需要实时显示的参数集合，解析<RealTimeSet/>标签得到
     */
    private List<RealTimeParamVO> realTimeParamList = new ArrayList<>();


    /**
     * XML文件标签类对象
     */
    public Device device;

    /**
     * Gets j 1939 pg set vo.
     *
     * @return the j 1939 pg set vo
     */
    public J1939PgSetVO getJ1939PgSetVO() {
        return j1939PgSetVO;
    }

    /**
     * Gets check item list.
     *
     * @return the check item list
     */
    public List<CheckItemVO> getCheckItemList() {
        return allCheckItemList;
    }

    /**
     * 根据某个调试参数名获取对应的调试项目对象。
     * 注意，此方法仅对于 在机型中是唯一的调试参数 有效
     *
     * @param param 调试参数名称
     * @return 调试项目对象
     */
    public CheckItemVO getCheckItemVOByParam(String param) {
        for (CheckItemVO checkItemVO : allCheckItemList) {
            for (CheckItemParamValueVO checkItemParamValueVO : checkItemVO.getParamNameList()) {
                if (param.equals(checkItemParamValueVO.getParamName())) {
                    return checkItemVO;
                }
            }
        }
        return null;
    }

    /**
     * Gets check item vo.
     *
     * @param checkItemId the check item id
     * @return the check item vo
     */
    public CheckItemVO getCheckItemVO(String checkItemId) {
        for (CheckItemVO item : allCheckItemList) {
            if (item.getId().equals(checkItemId)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据调试项目的qcId获取调试项目
     *
     * @param checkItemId the check item id
     * @return the check item vo
     */
    public CheckItemVO getCheckItemVO(int checkItemId) {
        String id = checkItemId + "";
        for (CheckItemVO item : allCheckItemList) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets data set vo.
     *
     * @return the data set vo
     */
    public DataSetVO getDataSetVO() {
        return dataSetVO;
    }

    /**
     * Sets data set vo.
     *
     * @param dataSetVO the data set vo
     */
    public void setDataSetVO(DataSetVO dataSetVO) {
        this.dataSetVO = dataSetVO;
    }

    /**
     * Add check item.
     *
     * @param checkItemVO the check item vo
     */
    public void addCheckItemInAll(CheckItemVO checkItemVO) {
        allCheckItemList.add(checkItemVO);
    }

    /**
     * Gets device id.
     *
     * @return the device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets device id.
     *
     * @param deviceId the device id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets device name.
     *
     * @param deviceName the device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Gets dev born date.
     *
     * @return the dev born date
     */
    public String getDevBornDate() {
        return devBornDate;
    }

    /**
     * Sets dev born date.
     *
     * @param devBornDate the dev born date
     */
    public void setDevBornDate(String devBornDate) {
        this.devBornDate = devBornDate;
    }

    /**
     * Gets dev die date.
     *
     * @return the dev die date
     */
    public String getDevDieDate() {
        return devDieDate;
    }

    /**
     * Sets dev die date.
     *
     * @param devDieDate the dev die date
     */
    public void setDevDieDate(String devDieDate) {
        this.devDieDate = devDieDate;
    }

    /**
     * Gets dev status.
     *
     * @return the dev status
     */
    public String getDevStatus() {
        return devStatus;
    }

    /**
     * Sets dev status.
     *
     * @param devStatus the dev status
     */
    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    /**
     * Add real time param.
     *
     * @param realTimeVO the real time vo
     */
    public void addRealTimeParam(RealTimeParamVO realTimeVO) {
        realTimeParamList.add(realTimeVO);
    }

    /**
     * Gets real time param list.
     *
     * @return the real time param list
     */
    public List<RealTimeParamVO> getRealTimeParamList() {
        return realTimeParamList;
    }

    /**
     * Read from file device model file.
     *
     * @param device 成功读取后的机型文件
     * @return the device model file
     * @throws ExcException the exc exception
     */
    public static DeviceModelFile readFromFile(Device device) throws ExcException {
        DeviceModelFile result = new DeviceModelFile();

        result.device = device;
        // 解析device的基本属性
        result.setDeviceId(device.getId());
        result.setDeviceName(device.getName());
        result.setDevBornDate(device.getDevBornDate());
        result.setDevDieDate(device.getDevDieDate());
        result.setDevStatus(device.getDevStatus());

        //解析机型文件时，要按照如下顺序解析，如果打乱，会造成空指针异常。因为后续的参数可能会用到之前解析出的
        //先解析<DataSet/>集合
        parseDataSet(result, device);

        //解析<J1939/>标签
        parseJ1939(result, device);

        //解析<QCSet/>标签
        parseQCSet(result, device);

        //解析<RealTimeSet/>标签
        parseRealTimeSet(result, device);

        return result;
    }

//	private static InputStream getRefInpuStream(String deviceName) throws FileNotFoundException {
//		InputStream is = null;
//		File file = new File(Globals.MODELPATH+deviceName);
//		if(file.exists()){
//			is = new FileInputStream(file);
//			Log.i("FILE","找到文件");
//		}
//		return is ;
//	}

    /**
     * 解析实时参数结合
     *
     * @param result 解析结果对象
     * @param device 待解析机型对象
     */
    private static void parseRealTimeSet(DeviceModelFile result, Device device) {
        RealTimeSet realTimeSet = device.getRealTimeSet();
        List<RealTimeParam> reals = realTimeSet.getRealTimeParams();
        for (RealTimeParam real : reals) {
            RealTimeParamVO realTimeParam = new RealTimeParamVO();
            realTimeParam.setName(real.getName());
            realTimeParam.setUnit(result.getDataSetVO().getDSItem(real.getName()).sUnit);
            realTimeParam.setDataType(result.getDataSetVO().getDSItem(real.getName()).dataType);
            result.addRealTimeParam(realTimeParam);
        }
    }

    /**
     * 解析J939集合
     *
     * @param result 解析结果对象
     * @param device 待解析机型对象
     */
    private static void parseJ1939(DeviceModelFile result, Device device) {
        J1939PgSetVO j1939Cfg = new J1939PgSetVO();
        // 赋值给父类对象 whb
        J1939_Context.j1939_Cfg = j1939Cfg;
        j1939Cfg.bNodeAddr = Byte.valueOf(hexToInteger(device.getJ1939().getNodeAddr()));
        j1939Cfg.bTaskPrio = Byte.valueOf(device.getJ1939().getTaskPrio());
        j1939Cfg.wCycle = Integer.valueOf(device.getJ1939().getCycle());

        List<J1939_PGCfg_ts> pgList = new ArrayList<J1939_PGCfg_ts>();
        List<PG> pgs = device.getJ1939().getPgs();

        j1939Cfg.wPGNums = (short) pgs.size();

        for (short i = 0; i < pgs.size(); i++) {
            PG pgxml = pgs.get(i);

            J1939_PGCfg_ts pg = new J1939_PGCfg_ts();
            pg.bPGType = Byte.valueOf(pgxml.getType());
            pg.bDir = Byte.valueOf(pgxml.getDir().equals("Rx") ? "0" : "1");
            pg.bPrio = Byte.valueOf(pgxml.getPrio());
            pg.wLen = Short.valueOf(pgxml.getLen());
            pg.dwRate = Integer.valueOf(pgxml.getRate());
            pg.dwPGN = Integer.valueOf(hexToInteger(pgxml.getPGN()));
            pg.bSA = Byte.valueOf(hexToInteger(pgxml.getSA()));
            pg.bReq = Byte.valueOf(pgxml.getReq());
            pg.dwReqCycle = Integer.valueOf(pgxml.getReqCyc());
            List<J1939_SPCfg_ts> spList = new ArrayList<J1939_SPCfg_ts>();
            List<SP> spxmls = pgxml.getSps();
            for (SP spxml : spxmls) {
                J1939_SPCfg_ts sp = new J1939_SPCfg_ts();
                sp.bSPType = Byte.valueOf(spxml.getType());
                sp.dwSPN = Integer.valueOf(spxml.getSPN() == null ? "0" : spxml.getSPN());
                sp.wStartByte = Short.valueOf(spxml.getSByte());
                sp.bStartBit = Byte.valueOf(spxml.getSBit());
                try {
                    sp.bBytes = Byte.valueOf(spxml.getBytes());
                } catch (NumberFormatException e) {
                    // 从关联项item中，读取字节大小
                    J1939_DataVar_ts dsItem = result.dataSetVO.getDSItem(spxml.getBytes());
                    sp.bBytes = (byte) dsItem.getValue();
                }
                sp.bBits = Byte.valueOf(spxml.getBits());
                sp.fRes = Float.valueOf(spxml.getRes());
                sp.fOffset = Float.valueOf(spxml.getOff());
                // 参数关联的变量名。当从接收PGN中解析出参数值后赋值给该变量，
                // 或要发送PGN时根据该变量值得到参数发送的原始值填入PGN数据区相应位置
                String refValue = spxml.getRef();
                short itemIndex = result.dataSetVO.getItemIndex(refValue);
                sp.wRefDataIdx = itemIndex;
                sp.bRefDataType = result.dataSetVO.getJ1939_DataVarCfg()[itemIndex]
                        .getDataType();

                List<DTC> dtcxmls = spxml.getDtcs();
                List<J1939_DTCfg_ts> dtcList = new ArrayList<J1939_DTCfg_ts>();
                for (DTC dtcxml : dtcxmls) {
                    J1939_DTCfg_ts dtc = new J1939_DTCfg_ts();
                    dtc.bFMI = Byte.valueOf(dtcxml.getFMI());
                    dtc.wDescId = Short.valueOf(dtcxml.getMsgId());
                    dtcList.add(dtc);
                }
                sp.pDTCfg = dtcList.toArray(new J1939_DTCfg_ts[dtcList.size()]);
                // 统计dtc节点个数 whb
                if (dtcList.size() > 0) {
                    sp.bDTCNums = (byte) dtcList.size();
                }
                // whb
                sp.pPGCfg = pg;
                j1939Cfg.putSpRef(refValue, sp);
                // 统计sp节点个数 whb
                spList.add(sp);
            }

            pg.pSPCfg = spList.toArray(new J1939_SPCfg_ts[spList.size()]);
            // 统计sp节点个数 whb
            if (spxmls.size() > 0) {
                pg.bSPNums = (short) spxmls.size();
            }
            j1939Cfg.putPgIndex(pg.dwPGN, i);
            pgList.add(pg);
        }

        j1939Cfg.pPGCfg = pgList.toArray(new J1939_PGCfg_ts[pgList.size()]);
        result.j1939PgSetVO = j1939Cfg;
    }

    /**
     * 解析DataSet集合
     *
     * @param result 解析结果对象
     * @param device 待解析机型对象
     */
    private static void parseDataSet(DeviceModelFile result, Device device) {
        DataSetVO dataSetVO = new DataSetVO();
        List<DSItem> dsitemxmls = device.getDataSet().getDsItems();

        Map<String, Short> itemMap = new HashMap<String, Short>();
        for (short i = 0; i < dsitemxmls.size(); i++) {
            String name = dsitemxmls.get(i).getName();
            itemMap.put(name, i);
        }
        dataSetVO.setItemIntexMap(itemMap);
        List<J1939_DataVar_ts> j1939_DataVarCfg = new ArrayList<J1939_DataVar_ts>(dsitemxmls.size());
        for (int i = 0; i < dsitemxmls.size(); i++) {
            J1939_DataVar_ts itemData = null;

            String dataType = dsitemxmls.get(i).getDataType();
            String value = dsitemxmls.get(i).getValue();
            short rows = -1;
            if (dsitemxmls.get(i).getRows() != null) {
                rows = Short.valueOf(dsitemxmls.get(i).getRows());
            }
            if (rows > 0) {// 说明是向量
                List<Data> datas = dsitemxmls.get(i).getDatas();
                List<Object> dataValueList = new ArrayList<>();
                for (Data data : datas) {
                    dataValueList.add(convertValue(dataType, data.getValue()));
                }
                Object[] values = dataValueList.toArray();
                itemData = new J1939_DataVar_ts(dataType, rows, (short) -1, values);

            } else {// 说明是基础类型
                itemData = new J1939_DataVar_ts(dataType, value);
            }
            itemData.setName(dsitemxmls.get(i).getName());
            itemData.setUnit(dsitemxmls.get(i).getUnit());
            itemData.setDecLen(dsitemxmls.get(i).getDecLen());

            String linkTo = dsitemxmls.get(i).getLinkTo();
            if (linkTo != null) {
                Short index = itemMap.get(linkTo);
                if (index != null) {
                    itemData.setLinkTo(index);
                }
            }
            j1939_DataVarCfg.add(itemData);
        }
        dataSetVO.setJ1939_DataVarCfg(j1939_DataVarCfg);
        // whb
        J1939_Context.j1939_DataVarCfg = j1939_DataVarCfg.toArray(new J1939_DataVar_ts[j1939_DataVarCfg.size()]);
        result.dataSetVO = dataSetVO;
    }

    /**
     * 解析QCSet集合
     *
     * @param result 解析结果对象
     * @param device 待解析机型对象
     * @throws ExcException
     */
    private static void parseQCSet(DeviceModelFile result, Device device) throws ExcException {
        List<QCType> qcTypes = device.getQcSet().getQcTypes();
        for (QCType qcType : qcTypes) {
            Globals.groups.add(qcType.getName());
            List<CheckItemVO> values = new ArrayList<>();
            List<QCItem> qcitemxmls = qcType.getQcItems();
            // 遍历xml 检测项目 标签类 生成到vo中
            for (QCItem qcitemxml : qcitemxmls) {
                CheckItemVO checkItem = new CheckItemVO();
                checkItem.setDictId(qcitemxml.getDictID());
                checkItem.setId(qcitemxml.getId());
                checkItem.setName(qcitemxml.getName());
                checkItem.setRequire(qcitemxml.getRequire());
                checkItem.setTimes(qcitemxml.getQCTimes());
                checkItem.setReadyTimeout(qcitemxml.getReadyTimeout());
                checkItem.setQcTimeout(qcitemxml.getQCTimeout());
                // 解析Msgs
                checkItem.setReadyMsg(qcitemxml.getMsgs().getReadyMsg());
                checkItem.setNotReadyMsg(qcitemxml.getMsgs().getNotReadyMsg());
                checkItem.setAbortMsg(qcitemxml.getMsgs().getAbortMsg());
                checkItem.setOkMsg(qcitemxml.getMsgs().getOkMsg());

                checkItem.setSpectrum(qcitemxml.getSpectrum());
                List<QCProgress> qcprogress = qcitemxml.getMsgs().getQcProgressMsg().getQcProgresss();
                for (QCProgress progressqc : qcprogress) {
                    checkItem.putProgressMsg(progressqc.getCode(), progressqc.getMsg());
                }
                List<QCErr> qcerrss = qcitemxml.getMsgs().getQcErrMsg().getqcErrs();
                for (QCErr errqc : qcerrss) {
                    checkItem.putErrorMsg(errqc.getCode(), errqc.getMsg());
                }
                List<QCParam> qcparams = qcitemxml.getQcParams().getQcParams();
                List<ENVParam> qcenvs = qcitemxml.getEnvParams().getEnvParams();
                List<RealTimeParam> qcrealtimes = qcitemxml.getRealTimeParams().getRealTimeParams();
                for (QCParam qcparam : qcparams) {
                    checkItem.addQcParam(qcparam.getDictID(), qcparam.getParam(),
                            qcparam.getValueReq(), qcparam.getPicReq(), qcparam.getValMode(), qcparam.getQCMode(),
                            qcparam.getXParam(), qcparam.getXRange(),
                            qcparam.getValidMin(),
                            qcparam.getValidMax(),
                            qcparam.getValidAvg(), result.dataSetVO.getDSItem(qcparam.getParam()).sUnit);
                }
                for (ENVParam env : qcenvs) {
                    checkItem.addEnvParam(env.getParam(),
                            env.getValidMin(),
                            env.getValidMax(),
                            env.getValidAvg());
                }
                for (RealTimeParam real : qcrealtimes) {
                    RealTimeParamVO rtp = new RealTimeParamVO();
                    rtp.setName(real.getName());
                    rtp.setUnit(result.getDataSetVO().getDSItem(real.getName()).sUnit);
                    rtp.setDataType(result.getDataSetVO().getDSItem(real.getName()).dataType);
                    checkItem.addRtParam(rtp);
                }
                checkItem.sortParamList();
                result.addCheckItemInAll(checkItem);//添加到 所有检测项目集合中
                values.add(checkItem); //添加到map对应的list集合中
            }
            result.checkItemMap.put(qcType.getName(), values);
        }
    }

    /**
     * 根据数据类型进行数值转换
     *
     * @param dataType  数据类型
     * @param dataValue 数据值
     * @return 转换后的数据对象
     */
    private static Object convertValue(String dataType, String dataValue) {
        if (dataType.equals("BYTE")) {
            return Byte.valueOf(dataValue);
        } else if (dataType.equals("FLOAT")) {
            return Float.valueOf(dataValue);
        } else if (dataType.equals("WORD") || dataType.equals("DWORD")
                || dataType.equals("INT")) {
            return Integer.valueOf(dataValue);
        } else if (dataType.equals("SHORT")) {
            return Short.valueOf(dataValue);
        }
        return null;
    }

    /**
     * 十六进制转十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制字符串
     */
    private static String hexToInteger(String hex) {
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        return String.valueOf(Integer.valueOf(hex, 16));
    }

}
