package com.kstech.zoomlion.utils;

import android.support.annotation.NonNull;

import com.kstech.zoomlion.model.vo.CheckItemVO;
import com.kstech.zoomlion.model.xmlbean.SpecParam;

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

}
