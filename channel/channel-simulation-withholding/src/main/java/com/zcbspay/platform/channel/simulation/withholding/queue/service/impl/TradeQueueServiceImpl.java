/* 
 * TradeQueueServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月26日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.simulation.withholding.queue.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zcbspay.platform.channel.common.bean.CMBCTradeQueueBean;
import com.zcbspay.platform.channel.common.enums.TradeQueueEnum;
import com.zcbspay.platform.channel.common.enums.TradeStatFlagEnum;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.pojo.PojoTxnsLog;
import com.zcbspay.platform.channel.simulation.withholding.queue.service.TradeQueueService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月26日 上午11:07:52
 * @since
 */
@Service("tradeQueueService")
public class TradeQueueServiceImpl implements TradeQueueService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private TxnsLogDAO txnsLogDAO;

	/**
	 *
	 * @param queueBean
	 */
	@Override
	public void addTradeQueue(CMBCTradeQueueBean queueBean) {
		// TODO Auto-generated method stub
		try {
			BoundListOperations<String, String> boundListOps = redisTemplate
					.boundListOps(TradeQueueEnum.TRADEQUEUE.getName());
			boundListOps.rightPush(JSON.toJSONString(queueBean));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 *
	 * @param txnseqno
	 * @param tradeStatFlagEnum
	 */
	@Override
	public void addTradeQueue(String txnseqno,
			TradeStatFlagEnum tradeStatFlagEnum) {
		// TODO Auto-generated method stub
		txnsLogDAO.updateTradeStatFlag(txnseqno, tradeStatFlagEnum);
		if(tradeStatFlagEnum==TradeStatFlagEnum.PAYING||tradeStatFlagEnum==TradeStatFlagEnum.OVERTIME){
			PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
			CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
			queueBean.setBusiType(txnsLog.getBusitype());
			queueBean.setPayInsti(txnsLog.getPayinst());
			queueBean.setTxnDateTime(txnsLog.getTxndate()+txnsLog.getTxntime());
			queueBean.setTxnseqno(txnseqno);
			BoundListOperations<String, String> boundListOps = redisTemplate
					.boundListOps(TradeQueueEnum.TRADEQUEUE.getName());
			boundListOps.rightPush(JSON.toJSONString(queueBean));
		}
	}



	/**
	 *
	 * @param txnseqno
	 */
	@Override
	public void addTradeQueue(String txnseqno) {
		// TODO Auto-generated method stub
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
		CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
		queueBean.setBusiType(txnsLog.getBusitype());
		queueBean.setPayInsti(txnsLog.getPayinst());
		queueBean.setTxnDateTime(txnsLog.getTxndate()+txnsLog.getTxntime());
		queueBean.setTxnseqno(txnseqno);
		BoundListOperations<String, String> boundListOps = redisTemplate
				.boundListOps(TradeQueueEnum.TRADEQUEUE.getName());
		boundListOps.rightPush(JSON.toJSONString(queueBean));
	}
	
	
}
