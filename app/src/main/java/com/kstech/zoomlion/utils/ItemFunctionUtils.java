package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.model.vo.CheckItemParamValueVO;
import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/8/17.
 */
public final class ItemFunctionUtils {
    private ItemFunctionUtils() {
    }


    /**
     * 是否是谱图收集参数
     *
     * @param paramName the param name
     * @param qcID      the qc id
     * @return the boolean
     */
    public static boolean isSpectrumParam(@NonNull String paramName, int qcID) {
        boolean isSpectrumParam = false;
        CheckItemVO itemVO = Globals.modelFile.getCheckItemVO(qcID + "");
        if (itemVO.getSpectrum() != null) {
            for (SpecParam specParam : itemVO.getSpectrum().getSpecParams()) {
                LogUtils.e("ItemShowView", specParam.getParam() + "||" + paramName);
                if (paramName.equals(specParam.getParam())) {
                    isSpectrumParam = true;
                    break;
                }
            }
        }

        return isSpectrumParam;
    }

    /**
     * 判断项目是否为无值项目，是返回true
     *
     * @param qcID
     * @return
     */
    public static boolean isNoValueItem(int qcID) {
        boolean isNoValue = true;
        for (CheckItemParamValueVO checkItemParamValueVO : Globals.modelFile.getCheckItemVO(qcID).getParamNameList()) {
            if (checkItemParamValueVO.getValueReq()) {
                isNoValue = false;
            }
        }
        return isNoValue;
    }

    /**
     * 判断项目是否是 不包含图片数据的项目，是返回true
     *
     * @param qcID
     * @return
     */
    public static boolean isNoPICItem(int qcID) {
        boolean isNoPIC = true;
        for (CheckItemParamValueVO checkItemParamValueVO : Globals.modelFile.getCheckItemVO(qcID).getParamNameList()) {
            if (checkItemParamValueVO.getPicReq()) {
                isNoPIC = false;
            }
        }
        return isNoPIC;
    }

    /**
     * 获取指定项目数据库中带值参数集合
     *
     * @param values 调试项目细节表中的paramsvalues
     * @return 带值参数集合
     */
    public static List<CheckItemParamValueVO> getValueReqParam(String values) {
        List<CheckItemParamValueVO> result = new ArrayList<>();
        List<CheckItemParamValueVO> list = JsonUtils.fromArrayJson(values, CheckItemParamValueVO.class);
        for (CheckItemParamValueVO checkItemParamValueVO : list) {
            if (checkItemParamValueVO.getValueReq()) {
                result.add(checkItemParamValueVO);
            }
        }
        return result;
    }


}
