package com.zcbspay.platform.channel.unionpay.withholding.service;

import com.zcbspay.platform.channel.common.bean.TradeBeanUP;
import com.zcbspay.platform.channel.common.exception.BaseException;
import com.zcbspay.platform.channel.unionpay.withholding.exception.UnionPayException;

public interface ChlSeqNumCheckService {

	/**
	 * 是否为重复请求
	 * @param txnseqno
	 * @throws BaseException
	 */
	public void isRepeatRequest(String txnseqno) throws UnionPayException;
	
	/**
	 * 是否超出限额
	 * @param tradeBean
	 * @throws BaseException
	 */
	public void isOverlimit(TradeBeanUP tradeBean) throws UnionPayException;
	
	/**
     * 是否需要查询同步状态
     * @param tradeBean
     */
    public boolean isSysthronizeStatus(String orderId);
    
}
