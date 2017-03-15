package com.zcbspay.platform.channel.simulation.insteadpay.service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;

public interface ConcentratePaymentService {

	/**
	 * 
	 * @param tradeBean
	 * @return
	 */
	public ResultBean realTimePayment(TradeBean tradeBean);
	
	/**
	 * 
	 * @param tradeBean
	 * @return
	 */
	public ResultBean batchPayment(TradeBean tradeBean);
}
