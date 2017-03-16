package com.zcbspay.platform.channel.simulation.insteadpay.dao;

import com.zcbspay.platform.channel.common.dao.BaseDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.OrderPaymentBatchDO;

public interface OrderPaymentBatchDAO extends BaseDAO<OrderPaymentBatchDO> {

	
	/**
	 * 通过tn获取代付批次数据
	 * @param tn
	 * @return
	 */
	public OrderPaymentBatchDO getPaymentBatchOrderByTn(String tn);
	
	public void updateOrderToSuccess(String tn);
}
