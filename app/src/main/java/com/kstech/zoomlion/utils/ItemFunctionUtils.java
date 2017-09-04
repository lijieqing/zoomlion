package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.AlterData;
import com.kstech.zoomlion.model.xmlbean.DataCollectParam;
import com.kstech.zoomlion.model.xmlbean.DialogParam;
import com.kstech.zoomlion.model.xmlbean.Handwrite;
import com.kstech.zoomlion.model.xmlbean.NoValueParam;
import com.kstech.zoomlion.model.xmlbean.PICParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/8/17.
 */
public final class ItemFunctionUtils {
    private ItemFunctionUtils(){
    }

    /**
     * Is no value param boolean.
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isNoValueParam(@NonNull String paramName, String qcID){
        boolean isNoValueParam = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        for (NoValueParam noValueParam : itemVO.getFunction().getNoValueParams()) {
            if (paramName.equals(noValueParam.getName())){
                isNoValueParam = true;
            }
        }
        return isNoValueParam;
    }

    /**
     * Is dialog param boolean.
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isDialogParam(@NonNull String paramName, String qcID){
        boolean isDialogParam = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        for (DialogParam dialogParam : itemVO.getFunction().getDialogParams()) {
            if (paramName.equals(dialogParam.getName())){
                isDialogParam = true;
            }
        }
        return isDialogParam;
    }


    /**
     * Is hand writing param boolean.
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isHandWritingParam(@NonNull String paramName, String qcID){
        boolean isHandWriting = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        for (Handwrite handwrite : itemVO.getFunction().getHandwrites()) {
            if (paramName.equals(handwrite.getName())){
                isHandWriting = true;
            }
        }
        return isHandWriting;
    }


    /**
     * Is collect param boolean.
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isCollectParam(@NonNull String paramName, int qcID){
        boolean isCollectParam = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        for (DataCollectParam dataCollectParam : itemVO.getFunction().getDataCollectParams()) {
            LogUtils.e("ItemShowView",dataCollectParam.getName()+"||"+paramName);
            if (paramName.equals(dataCollectParam.getName())){
                isCollectParam = true;
                break;
            }
        }
        return isCollectParam;
    }

    /**
     * Is pic param boolean.
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isPICParam(@NonNull String paramName, int qcID){
        boolean isPICParam = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        for (PICParam picParam : itemVO.getFunction().getPicParams()) {
            if (paramName.equals(picParam.getName())){
                isPICParam = true;
            }
        }

        return isPICParam;
    }

    /**
     * Get item pic params list.
     *
     * @param qcID the qc id
     * @return the list of params which need take photos
     */
    public static List<PICParam> getItemPicParams(int qcID){
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        List<PICParam> list = itemVO.getFunction().getPicParams();
        if (list == null){
            list  = new ArrayList<>();
        }
        return list;
    }

    /**
     * Get item handwrite params list.
     *
     * @param qcID the qc id
     * @return the list of params which need to record result by handwriting
     */
    public static List<Handwrite> getItemHandwriteParams(int qcID){
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        List<Handwrite> list = itemVO.getFunction().getHandwrites();
        if (list == null){
            list  = new ArrayList<>();
        }
        return list;
    }

    /**
     * Get item data collect params list.
     *
     * @param qcID the qc id
     * @return the list of params which need to record data change
     */
    public static List<DataCollectParam> getItemDataCollectParams(int qcID){
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        List<DataCollectParam> list = itemVO.getFunction().getDataCollectParams();
        if (list == null){
            list  = new ArrayList<>();
        }
        return list;
    }

    /**
     * Get item alter data list.
     *
     * @param qcID the qc id
     * @return the list of datas for item to alter device
     */
    public static List<AlterData> getItemAlterData(int qcID){
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        List<AlterData> list;
        if (itemVO.getFunction().getAlter()){
            if (itemVO.getFunction().getAlterDatas() == null){
                throw new NullPointerException("检测项"+itemVO.getName()+"配置信息中没有AlterDatas标签");
            }else {
                list = itemVO.getFunction().getAlterDatas().getAlterDatas();
            }
        }else {
            list = new ArrayList<>();
        }

        return list;
    }
}
