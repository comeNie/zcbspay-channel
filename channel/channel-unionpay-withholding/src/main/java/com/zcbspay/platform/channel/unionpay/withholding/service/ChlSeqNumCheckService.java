package com.zcbspay.platform.channel.unionpay.withholding.service;

import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.common.exception.BaseException;
import com.zcbspay.platform.channel.unionpay.withholding.exception.UnionPayException;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;

public interface ChlSeqNumCheckService {

    /**
     * 是否为重复请求
     * 
     * @param txnseqno
     * @throws BaseException
     */
    public void isRepeatRequest(String txnseqno) throws UnionPayException;

    /**
     * 是否超出限额
     * 
     * @param tradeBean
     * @throws BaseException
     */
    public void isOverlimit(TradeBean tradeBean) throws UnionPayException;

    /**
     * 是否需要查询同步状态
     * 
     * @param tradeBean
     */
    public PojoTxnsLogUp isSysthronizeStatus(String orderId);

}
