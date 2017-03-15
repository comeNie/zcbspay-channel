package com.zcbspay.platform.channel.simulation.withholding.dao;

import com.zcbspay.platform.channel.common.dao.BaseDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectSingleDO;

public interface OrderCollectSingleDAO extends BaseDAO<OrderCollectSingleDO>{

	/**
	 * 更新订单状态为失败
	 * @param txnseqno 交易序列号
	 */
    public void updateOrderToFail(String txnseqno);
    /**
     * 更新订单状态为成功
     * @param txnseqno 交易序列号
     */
    public void updateOrderToSuccess(String txnseqno) ;
    /**
     * 更新订单状态为成功
     * @param tn 受理订单号
     */
    public void updateOrderToSuccessByTN(String tn) ;
}
