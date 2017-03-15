package com.zcbspay.platform.channel.simulation.withholding.withholding.service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;

public interface ConcentrateCollectionService {

	/**
	 * 
	 * @param tradeBean
	 * @return
	 */
	public ResultBean realTimeCollection(TradeBean tradeBean);
	
	/**
	 * 
	 * @param tradeBean
	 * @return
	 */
	public ResultBean batchCollection(TradeBean tradeBean);
}
