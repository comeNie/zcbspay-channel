package com.zcbspay.platform.channel.unionpay.withholding.utils;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.enums.UPRespInfo;
import com.zcbspay.platform.channel.common.enums.UPRespStatus;

public class RespStatusUtil {

    /**
     * 根据报文应答信息获取校验状态
     * 
     * @param resultBean
     * @return
     */
    public static String getTradeStatus(ResultBean resultBean) {
        String resultStatus = null;
        if (resultBean.isResultBool()) {
            resultStatus = UPRespStatus.SUCESS.getValue();
        }
        else {
            if (UPRespInfo.UNKNOWN_DATAP.getValue().equals(resultBean.getErrCode())) {
                resultStatus = UPRespStatus.UNKNOWN.getValue();
            }
            else if (UPRespInfo.UNKNOWN_TIMEOUT.getValue().equals(resultBean.getErrCode())) {
                resultStatus = UPRespStatus.UNKNOWN.getValue();
            }
            else if (UPRespInfo.UNKNOWN.getValue().equals(resultBean.getErrCode())) {
                resultStatus = UPRespStatus.UNKNOWN.getValue();
            }
            else {
                resultStatus = UPRespStatus.FAILURE.getValue();
            }
        }
        return resultStatus;
    }

    /**
     * 根据报文应答码获取校验状态
     * 
     * @param resultBean
     * @return
     */
    public static String getTradeStatus(String respCode) {
        String resultStatus = null;

        if (UPRespInfo.SUCESS.getValue().equals(respCode)) {
            resultStatus = UPRespStatus.UNKNOWN.getValue();
        }
        else if (UPRespInfo.UNKNOWN_DATAP.getValue().equals(respCode)) {
            resultStatus = UPRespStatus.UNKNOWN.getValue();
        }
        else if (UPRespInfo.UNKNOWN_TIMEOUT.getValue().equals(respCode)) {
            resultStatus = UPRespStatus.UNKNOWN.getValue();
        }
        else if (UPRespInfo.UNKNOWN.getValue().equals(respCode)) {
            resultStatus = UPRespStatus.UNKNOWN.getValue();
        }
        else {
            resultStatus = UPRespStatus.FAILURE.getValue();
        }
        return resultStatus;
    }
}
