/* 
 * InsteadPayServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月17日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.simulation.insteadpay.service.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zcbspay.platform.channel.common.bean.CMBCTradeQueueBean;
import com.zcbspay.platform.channel.common.bean.InsteadPayTradeBean;
import com.zcbspay.platform.channel.common.bean.PayPartyBean;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.SingleReexchangeBean;
import com.zcbspay.platform.channel.common.enums.ChannelEnmu;
import com.zcbspay.platform.channel.common.enums.ChnlTypeEnum;
import com.zcbspay.platform.channel.common.enums.TradeStatFlagEnum;
import com.zcbspay.platform.channel.dao.RspmsgDAO;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.pojo.PojoRspmsg;
import com.zcbspay.platform.channel.pojo.PojoTxnsLog;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimePayBean;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimeQueryBean;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.InsteadPayRealtimeDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.TxnsCmbcInstPayLogDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.exception.CMBCTradeException;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.PojoTxnsCmbcInstPayLog;
import com.zcbspay.platform.channel.simulation.insteadpay.queue.service.TradeQueueService;
import com.zcbspay.platform.channel.simulation.insteadpay.sequence.service.SerialNumberService;
import com.zcbspay.platform.channel.simulation.insteadpay.service.CMBCInsteadPayService;
import com.zcbspay.platform.channel.simulation.insteadpay.service.InsteadPayService;
import com.zcbspay.platform.channel.utils.BeanCopyUtil;
import com.zcbspay.platform.channel.utils.Constant;
import com.zcbspay.platform.channel.utils.DateUtil;
import com.zcbspay.platform.support.task.service.TradeNotifyService;
import com.zcbspay.platform.support.trade.acc.service.InsteadPayAccountingService;
import com.zcbspay.platform.support.trade.acc.service.TradeAccountingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月17日 下午12:14:49
 * @since
 */
@Service("insteadPayService")
public class InsteadPayServiceImpl implements InsteadPayService {

	@Autowired
	private CMBCInsteadPayService cmbcInsteadPayService;
	@Autowired
	private SerialNumberService serialNumberService;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private TxnsCmbcInstPayLogDAO txnsCmbcInstPayLogDAO;
	@Autowired
	private RspmsgDAO rspmsgDAO;
	@Reference(version="1.0")
	private TradeAccountingService tradeAccountingService;
	@Autowired
	private InsteadPayRealtimeDAO insteadPayRealtimeDAO;
	@Reference(version="1.0")
	private TradeNotifyService tradeNotifyService;
	@Reference(version="1.0")
	private InsteadPayAccountingService insteadPayAccountingService;
	@Autowired
	private TradeQueueService tradeQueueService;
	/**
	 *
	 * @param insteadPayTradeBean
	 * @return
	 * @throws CMBCTradeException 
	 */
	@Override
	public ResultBean realTimeSingleInsteadPay(InsteadPayTradeBean insteadPayTradeBean) throws CMBCTradeException {
		/**
		 * 实时代付业务流程：
		 * 1.获取交易日志数据
		 * 2.校验交易日志数据，如果是成功的交易拒绝，失败的交易或者未交易的通过
		 * 3.更新支付方数据
		 * 4.记录渠道交易流水
		 */
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(insteadPayTradeBean.getTxnseqno());
		if(txnsLog==null){
			throw new CMBCTradeException("");
		}
		if("0000".equals(txnsLog.getRetcode())){
			//throw new CMBCTradeException("");
		}
		PayPartyBean payPartyBean = new PayPartyBean(insteadPayTradeBean.getTxnseqno(),
				"04", serialNumberService.generateCMBCInsteadPaySerialNo(), ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(),
				Constant.getInstance().getCmbc_insteadpay_merid(), "",
				DateUtil.getCurrentDateTime(), "", "");
		txnsLogDAO.updatePayInfo(payPartyBean);
		
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = new PojoTxnsCmbcInstPayLog(insteadPayTradeBean);
		cmbcInstPayLog.setTranId(payPartyBean.getPayordno());
		txnsCmbcInstPayLogDAO.savePayLog(cmbcInstPayLog);
		RealTimePayBean realTimePayBean = new RealTimePayBean(insteadPayTradeBean);
		realTimePayBean.setTranId(payPartyBean.getPayordno());
		ResultBean resultBean = cmbcInsteadPayService.realTimeInsteadPay(realTimePayBean);
		txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.PAYING);
		resultBean = queryResult(payPartyBean.getPayordno());
		if(resultBean.isResultBool()){
			insteadPayRealtimeDAO.updateInsteadSuccess(insteadPayTradeBean.getTxnseqno());
		}else{
			if(!resultBean.getErrCode().equals("E")){
				insteadPayRealtimeDAO.updateInsteadFail(insteadPayTradeBean.getTxnseqno(), resultBean.getErrCode(), resultBean.getErrMsg());
			}else{
				//加入交易查询队列
				tradeQueueService.addTradeQueue(txnsLog.getTxnseqno());
				return resultBean;
			}
			
		}
		dealWithInsteadPay(payPartyBean.getPayordno());
		return resultBean;
	}
	
	private ResultBean queryResult(String tranId){
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
		ResultBean resultBean = null;
        int[] timeArray = new int[]{1, 2, 8, 16, 32};
        try {
            for (int i = 0; i < 5; i++) {
            	cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
                if(StringUtils.isNotEmpty(cmbcInstPayLog.getRespType())){
                    if("S".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                        resultBean = new ResultBean(cmbcInstPayLog);
                        return resultBean;
                    }else if("E".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                    	PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, cmbcInstPayLog.getRespCode());
                        resultBean = new ResultBean(msg.getWebrspcode(),msg.getRspinfo());
                        return resultBean;
                    }else if("R".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                        resultBean = new ResultBean("R","正在付款中");
                        continue;
                    }
                }
                TimeUnit.SECONDS.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("T000", e.getMessage());
        }
        resultBean = new ResultBean("T000", "交易超时，请稍后查询交易结果");
		return resultBean;
	}

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean batchInsteadPay(String batchNo) {
		
		return null;
	}

	/**
	 *
	 * @param ori_tran_date
	 * @param ori_tran_id
	 * @return
	 */
	@Override
	public ResultBean queryRealTimeInsteadPay(String ori_tran_date,
			String ori_tran_id) {
		RealTimeQueryBean queryBean = new RealTimeQueryBean(ori_tran_date, ori_tran_id);
		
		PojoTxnsCmbcInstPayLog cmbcInstPayQueryLog = new PojoTxnsCmbcInstPayLog();
		cmbcInstPayQueryLog.setOriTranDate(ori_tran_date);
		cmbcInstPayQueryLog.setOriTranId(ori_tran_id);
		cmbcInstPayQueryLog.setTranId(serialNumberService.generateCMBCInsteadPaySerialNo());
		cmbcInstPayQueryLog.setTranDate(DateUtil.getCurrentDate());
		cmbcInstPayQueryLog.setTranTime(DateUtil.getCurrentDateTime());
		txnsCmbcInstPayLogDAO.savePayLog(cmbcInstPayQueryLog);
		cmbcInsteadPayService.queryInsteadPay(queryBean);
		return queryResult(ori_tran_id);
	}

	/**
	 *
	 * @param tranId
	 * @return
	 */
	@Override
	public ResultBean dealWithInsteadPay(String tranId) {
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
		PayPartyBean payPartyBean = new PayPartyBean(cmbcInstPayLog.getTxnseqno(),"04", cmbcInstPayLog.getTranId(),ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(), Constant.getInstance().getCmbc_insteadpay_merid(), "", DateUtil.getCurrentDateTime(), "",cmbcInstPayLog.getAccNo(),cmbcInstPayLog.getBankTranId());
		payPartyBean.setPayretcode(cmbcInstPayLog.getRespCode());
        payPartyBean.setPayretinfo(cmbcInstPayLog.getRespMsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
        txnsLogDAO.updateAppInfo(cmbcInstPayLog.getTxnseqno());
        tradeAccountingService.accountingFor(cmbcInstPayLog.getTxnseqno());
        if("S".equals(cmbcInstPayLog.getRespType())){
        	tradeNotifyService.notify(cmbcInstPayLog.getTxnseqno());
        }
		return null;
	}

	/**
	 *
	 * @param reexchangeBean
	 * @throws CMBCTradeException 
	 */
	@Override
	public void reexchange(SingleReexchangeBean reexchangeBean) throws CMBCTradeException {
		// TODO Auto-generated method stub
		/**
		 * 退汇流程：
		 * 1.根据流水号查询民生实时代付流水数据，有无此交易
		 * 2.由民生代付流水取得交易序列号，以此取得交易日志，代付订单
		 * 3.更新代付订单表 状态 05 退汇， 新增交易流水4000， 4000002退汇类交易
		 * 4.退汇账务处理
		 */
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(reexchangeBean.getTranId());
		if(cmbcInstPayLog==null){
			throw new CMBCTradeException("");
		}
		insteadPayRealtimeDAO.updateInsteadReexchange(cmbcInstPayLog.getTxnseqno(), reexchangeBean.getRespCode(), reexchangeBean.getRespMsg());
		PojoTxnsLog txnsLog = generateReexchangTxnsLog(cmbcInstPayLog.getTxnseqno(), reexchangeBean);
		txnsLogDAO.saveTxnsLog(txnsLog);
		//账务处理
		insteadPayAccountingService.reexchangeAccounting(txnsLog.getTxnseqno());
	}
	
	private PojoTxnsLog generateReexchangTxnsLog(String txnseqno_og,SingleReexchangeBean reexchangeBean){
		String txnseqno = serialNumberService.generateTxnseqno();
		PojoTxnsLog txnsLog = BeanCopyUtil.copyBean(PojoTxnsLog.class, txnsLogDAO.getTxnsLogByTxnseqno(txnseqno_og));
		txnsLog.setTxnseqno(txnseqno);
		txnsLog.setTxndate(DateUtil.getCurrentDate());
		txnsLog.setTxntime(DateUtil.getCurrentTime());
		txnsLog.setBusitype("4000");
		txnsLog.setBusicode("40000002");
		//支付方信息
		txnsLog.setPayordno(reexchangeBean.getTranId());
		txnsLog.setPayordcomtime(DateUtil.getCurrentDateTime());
		txnsLog.setPayordfintime(DateUtil.getCurrentDateTime());
		txnsLog.setPayrettsnseqno(reexchangeBean.getBankTranId());
		txnsLog.setPayretcode(reexchangeBean.getRespCode());
		txnsLog.setPayretinfo(reexchangeBean.getRespMsg());
		//中心应答信息
		PojoRspmsg rspmsg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, reexchangeBean.getRespCode());
		if(rspmsg!=null){
			txnsLog.setRetcode(rspmsg.getWebrspcode());
			txnsLog.setRetinfo(rspmsg.getRspinfo());
		}else{
			txnsLog.setRetcode("01HH");
			txnsLog.setRetinfo("交易失败，详情请咨询证联金融客服010-84298418");
		}
		txnsLog.setTradestatflag(TradeStatFlagEnum.FINISH_SUCCESS.getStatus());
		txnsLog.setRetdatetime(DateUtil.getCurrentDateTime());
		txnsLog.setTxnseqnoOg(txnseqno_og);
		//应用方信息
		txnsLog.setAppordcommitime(DateUtil.getCurrentDateTime());
		txnsLog.setApporderstatus("");
		txnsLog.setApporderinfo("");
		txnsLog.setAccbusicode("40000002");
		return txnsLog;
	}

	/**
	 *
	 * @param txnseqno
	 */
	@Override
	public void queryAndAccounting(String txnseqno) {
		// TODO Auto-generated method stub
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(txnseqno);
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
		ResultBean resultBean = queryRealTimeInsteadPay(cmbcInstPayLog.getTranDate(), cmbcInstPayLog.getTranId());
		if(resultBean.isResultBool()){
			PojoTxnsCmbcInstPayLog cmbcInstPayLog_query = (PojoTxnsCmbcInstPayLog) resultBean.getResultObj();
			if("S".equals(cmbcInstPayLog_query.getOriRespType())){
				insteadPayRealtimeDAO.updateInsteadSuccess(txnseqno);
			}else if("E".equals(cmbcInstPayLog_query.getOriRespType())){
				insteadPayRealtimeDAO.updateInsteadFail(txnseqno, resultBean.getErrCode(), resultBean.getErrMsg());
			}else if("R".equals(cmbcInstPayLog_query.getOriRespType())){
				CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
				queueBean.setTxnseqno(txnseqno);
				queueBean.setPayInsti(ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode());
				queueBean.setBusiType(txnsLog.getBusitype());
				queueBean.setTxnDateTime(txnsLog.getTxntime());
				//加入交易查询队列
				tradeQueueService.addTradeQueue(queueBean);
				return;
			}
			
		}else{
			if(resultBean.getErrCode().equals("R")){
				CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
				queueBean.setTxnseqno(txnseqno);
				queueBean.setPayInsti(ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode());
				queueBean.setBusiType(txnsLog.getBusitype());
				queueBean.setTxnDateTime(txnsLog.getTxntime());
				//加入交易查询队列
				tradeQueueService.addTradeQueue(queueBean);
				return;
			}
		}
		dealWithInsteadPay(txnsLog.getPayordno());
	}

}
