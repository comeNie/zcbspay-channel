package com.zcbspay.platform.channel.unionpay.withholding.withholding.service;

import com.zcbspay.platform.channel.common.bean.ApplyAccCheckUP;
import com.zcbspay.platform.channel.common.bean.QueryTradeBeanUP;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;

public interface ZcbsToUnionPayWithholdingService {

	/**
	 * 代扣(单笔)
	 * @param tradeBean 交易数据bean
	 */
	public ResultBean withholding(TradeBean tradeBean);
	
	/**
     * 交易状态查询交易
     * 
     * @param tradeBean
     * @return
     */ 
    public ResultBean queryTrade(QueryTradeBeanUP queryTradeBean);
   
    /**
     * 申请下载对账文件
     * @param applyAccCheckUP
     * @return
     */
    public ResultBean applyAccChecking(ApplyAccCheckUP applyAccCheckUP);
}
