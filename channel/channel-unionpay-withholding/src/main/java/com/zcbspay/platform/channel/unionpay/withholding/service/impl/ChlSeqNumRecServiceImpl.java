package com.zcbspay.platform.channel.unionpay.withholding.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.ReturnInfo;
import com.zcbspay.platform.channel.common.enums.UPRespInfo;
import com.zcbspay.platform.channel.common.enums.UPRespStatus;
import com.zcbspay.platform.channel.unionpay.withholding.dao.TxnsUnionPayDao;
import com.zcbspay.platform.channel.unionpay.withholding.enums.TransType;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;
import com.zcbspay.platform.channel.unionpay.withholding.service.ChlSeqNumRecService;
import com.zcbspay.platform.channel.unionpay.withholding.service.SerialNumberService;
import com.zcbspay.platform.channel.unionpay.withholding.utils.DateStyle;
import com.zcbspay.platform.channel.unionpay.withholding.utils.DateUtil;
import com.zcbspay.platform.channel.unionpay.withholding.utils.ParamsUtil;

@Service("chlSeqNumRecService")
public class ChlSeqNumRecServiceImpl implements ChlSeqNumRecService {

    @Autowired
    private TxnsUnionPayDao txnsUnionPayDao;
    @Autowired
    private SerialNumberService serialNumberService;

    @Override
    public String recordSeqNum(PojoTxnsLogUp pojoTxnsLogUp) {
        String serialNum = null;
        BeanUtils.copyProperties(ParamsUtil.getInstance(), pojoTxnsLogUp);
        pojoTxnsLogUp.setMchntCd(ParamsUtil.getInstance().getMerchantId());
        if (!TransType.WITHDRAW.getValue().equals(pojoTxnsLogUp.getTransType())) {
            pojoTxnsLogUp.setDkType(null);
            pojoTxnsLogUp.setAtType(null);
            pojoTxnsLogUp.setBackUrl(null);
        }
        serialNum = serialNumberService.generateTxnseqno();
        String sendTm = DateUtil.DateToString(new Date(), DateStyle.YYYYMMDDHHMMSS.getValue());
        pojoTxnsLogUp.setOrderId(serialNum);
        pojoTxnsLogUp.setSendTm(sendTm);
        txnsUnionPayDao.createSeqRecord(pojoTxnsLogUp);
        return serialNum;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateSeqNumStatusWithhold(String orderId, ResultBean resultBean) {
        String resultStatus = null;
        String repCode = null;
        String repMsg = null;
        if (resultBean.isResultBool()) {
            resultStatus = UPRespStatus.SUCESS.getValue();
            List<ReturnInfo> retInfos = (List<ReturnInfo>) resultBean.getResultObj();
            repCode = retInfos.get(1).getRepCode();
            repMsg = retInfos.get(1).getRepMsg();
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
            repCode = resultBean.getErrCode();
            repMsg = resultBean.getErrMsg();
        }
        txnsUnionPayDao.updateSeqRecStatus(orderId, resultStatus, repCode, repMsg);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateWthDrAndQrySeqNumStatus(String orderId, String origOrderId, ResultBean resultBean) {
        String resultStatus = null;
        String repCode = null;
        String repMsg = null;
        String origRepCode = null;
        String origRepMsg = null;
        if (resultBean.isResultBool()) {
            resultStatus = UPRespStatus.SUCESS.getValue();
            List<ReturnInfo> retInfos = (List<ReturnInfo>) resultBean.getResultObj();
            repCode = retInfos.get(0).getRepCode();
            repMsg = retInfos.get(0).getRepMsg();
            origRepCode = retInfos.get(1).getRepCode();
            origRepMsg = retInfos.get(1).getRepMsg();
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
            repCode = resultBean.getErrCode();
            repMsg = resultBean.getErrMsg();
        }
        txnsUnionPayDao.updateSeqRecStatus(orderId, resultStatus, repCode, repMsg);
        txnsUnionPayDao.updateSeqRecStatus(origOrderId, resultStatus, origRepCode, origRepMsg);
    }

    @Override
    public void updateSeqNumInfoAlyAccChk(String orderId, ResultBean resultBean) {
        String resultStatus = null;
        String repCode = null;
        String repMsg = null;
        String downloadUrl = null;
        if (resultBean.isResultBool()) {
            resultStatus = UPRespStatus.SUCESS.getValue();
            downloadUrl = (String) resultBean.getResultObj();
        }
        else {
            resultStatus = UPRespStatus.FAILURE.getValue();
            repCode = resultBean.getErrCode();
            repMsg = resultBean.getErrMsg();
        }
        PojoTxnsLogUp pojoTxnsLogUp = txnsUnionPayDao.findByOrderId(orderId);
        pojoTxnsLogUp.setRespcod(repCode);
        pojoTxnsLogUp.setRespmsg(repMsg);
        pojoTxnsLogUp.setTradeStatus(resultStatus);
        pojoTxnsLogUp.setDownloadUrl(downloadUrl);
        txnsUnionPayDao.updateEntity(pojoTxnsLogUp);
    }

}
