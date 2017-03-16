package com.zcbspay.platform.channel.simulation.withholding.dao;

import java.util.List;

import com.zcbspay.platform.channel.common.dao.BaseDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectDetaDO;

public interface OrderCollectDetaDAO extends BaseDAO<OrderCollectDetaDO> {

	/**
	 * 通过批次标示获取批次明细数据集合
	 * @param batchId
	 * @return
	 */
	public List<OrderCollectDetaDO> getDetaListByBatchtid(Long batchId);
	
	/**
	 * 更新订单状态为失败
	 * @param txnseqno 交易序列号
	 */
    public void updateOrderToFail(String txnseqno,String rspCode,String rspMsg);
    /**
     * 更新订单状态为成功
     * @param txnseqno 交易序列号
     */
    public void updateOrderToSuccess(String txnseqno,String rspCode,String rspMsg) ;
}
