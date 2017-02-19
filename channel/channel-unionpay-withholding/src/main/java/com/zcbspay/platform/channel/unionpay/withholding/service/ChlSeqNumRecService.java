package com.zcbspay.platform.channel.unionpay.withholding.service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;

public interface ChlSeqNumRecService {

    /**
     * 记录银联渠道流水号
     * 
     * @param tradeBean
     * @return
     */
    public String recordSeqNum(PojoTxnsLogUp pojoTxnsLogUp);

    /**
     * 更新流水状态
     * 
     * @param seqNum
     * @param resultBean
     * @return
     */
    public void updateSeqNumStatus(String orderId, ResultBean resultBean);

    /**
     * 更新代扣流水和查询流水状态
     * @param orderId
     * @param resultBean
     */
    public void updateWthDrAndQrySeqNumStatus(String orderId,String origOrderId, ResultBean resultBean);
}
