package com.zcbspay.platform.channel.unionpay.withholding.dao;

import com.zcbspay.platform.channel.common.dao.BaseDAO;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;

public interface TxnsUnionPayDao extends BaseDAO<PojoTxnsLogUp> {

    /**
     * 根据交易序列号和流水状态（成功和处理中）查询渠道流水记录
     * 
     * @param txnseqno
     * @return
     */
    public PojoTxnsLogUp findByTxnseqnoAndStatus(String txnseqno, String status, boolean isEqual);

    /**
     * 根据流水订单编号和流水状态（成功和处理中）查询渠道流水记录
     * 
     * @param txnseqno
     * @return
     */
    public PojoTxnsLogUp findByOrderIdAndStatus(String orderId, String status, boolean isEqual);

    /**
     * 创建流水记录
     * 
     * @param pojoTxnsLogUp
     * @return
     */
    public void createSeqRecord(PojoTxnsLogUp pojoTxnsLogUp);

    /**
     * 更新流水记录状态
     * 
     * @param txnseqno
     * @return
     */
    public void updateSeqRecStatus(String orderId, String status, String repCode, String repMsg);

    /**
     * 查询申请下载对账文件记录
     * 
     * @param transType
     * @param status
     * @param queryTmBegin
     * @param queryTmEnd
     */
    public PojoTxnsLogUp getCheckRecord(String transType, String status, String queryTmBegin, String queryTmEnd);
}
