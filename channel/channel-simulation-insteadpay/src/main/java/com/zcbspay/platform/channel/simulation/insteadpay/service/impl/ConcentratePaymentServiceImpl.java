package com.zcbspay.platform.channel.simulation.insteadpay.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zcbspay.platform.channel.common.bean.InsteadPayTradeBean;
import com.zcbspay.platform.channel.common.bean.PayPartyBean;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.common.enums.ChannelEnmu;
import com.zcbspay.platform.channel.common.enums.ChnlTypeEnum;
import com.zcbspay.platform.channel.common.enums.TradeStatFlagEnum;
import com.zcbspay.platform.channel.dao.RspmsgDAO;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.pojo.PojoRspmsg;
import com.zcbspay.platform.channel.pojo.PojoTxnsLog;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimePayBean;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.OrderPaymentBatchDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.OrderPaymentDetaDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.OrderPaymentSingleDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.TxnsCmbcInstPayLogDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.OrderPaymentBatchDO;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.OrderPaymentDetaDO;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.PojoTxnsCmbcInstPayLog;
import com.zcbspay.platform.channel.simulation.insteadpay.queue.service.TradeQueueService;
import com.zcbspay.platform.channel.simulation.insteadpay.sequence.service.SerialNumberService;
import com.zcbspay.platform.channel.simulation.insteadpay.service.CMBCInsteadPayService;
import com.zcbspay.platform.channel.simulation.insteadpay.service.ConcentratePaymentService;
import com.zcbspay.platform.channel.utils.Constant;
import com.zcbspay.platform.channel.utils.DateUtil;
import com.zcbspay.platform.payment.trade.acc.service.InsteadPayAccountingService;
import com.zcbspay.platform.payment.trade.acc.service.TradeAccountingService;
import com.zcbspay.platform.support.task.service.TradeNotifyService;
@Service
public class ConcentratePaymentServiceImpl implements ConcentratePaymentService {

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
	@Autowired
	private TradeQueueService tradeQueueService;
	@Autowired
	private OrderPaymentSingleDAO orderPaymentSingleDAO;
	@Autowired
	private OrderPaymentBatchDAO orderPaymentBatchDAO;
	@Autowired
	private OrderPaymentDetaDAO orderPaymentDetaDAO;
	
	@Override
	public ResultBean realTimePayment(TradeBean tradeBean) {
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(tradeBean.getTxnseqno());
		if(txnsLog==null){
			return null;
		}
		if("0000".equals(txnsLog.getRetcode())){
			//throw new CMBCTradeException("");
		}
		PayPartyBean payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),
				"04", serialNumberService.generateCMBCInsteadPaySerialNo(), ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(),
				Constant.getInstance().getCmbc_insteadpay_merid(), "",
				DateUtil.getCurrentDateTime(), "", "");
		txnsLogDAO.updatePayInfo(payPartyBean);
		InsteadPayTradeBean insteadPayTradeBean = new InsteadPayTradeBean();
		insteadPayTradeBean.setTxnseqno(tradeBean.getTxnseqno());
		insteadPayTradeBean.setAcc_name(txnsLog.getInpanName());
		insteadPayTradeBean.setAcc_no(txnsLog.getInpan());
		insteadPayTradeBean.setBank_type(txnsLog.getIncardinstino());
		insteadPayTradeBean.setBank_name("TEST BANK");
		insteadPayTradeBean.setTrans_amt(txnsLog.getAmount().toString());
		
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = new PojoTxnsCmbcInstPayLog(insteadPayTradeBean);
		cmbcInstPayLog.setTranId(payPartyBean.getPayordno());
		txnsCmbcInstPayLogDAO.savePayLog(cmbcInstPayLog);
		RealTimePayBean realTimePayBean = new RealTimePayBean(insteadPayTradeBean);
		realTimePayBean.setTranId(payPartyBean.getPayordno());
		ResultBean resultBean = cmbcInsteadPayService.realTimeInsteadPay(realTimePayBean);
		txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.PAYING);
		resultBean = queryResult(payPartyBean.getPayordno());
		if(resultBean.isResultBool()){
			orderPaymentSingleDAO.updateOrderToSuccess(insteadPayTradeBean.getTxnseqno());
			txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.FINISH_SUCCESS);
		}else{
			if(!resultBean.getErrCode().equals("E")){
				orderPaymentSingleDAO.updateOrderToFail(insteadPayTradeBean.getTxnseqno());
				txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.FINISH_FAILED);
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
	
	public ResultBean dealWithInsteadPay(String tranId) {
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
		PayPartyBean payPartyBean = new PayPartyBean(cmbcInstPayLog.getTxnseqno(),"04", cmbcInstPayLog.getTranId(),ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(), Constant.getInstance().getCmbc_insteadpay_merid(), "", DateUtil.getCurrentDateTime(), "",cmbcInstPayLog.getAccNo(),cmbcInstPayLog.getBankTranId());
		payPartyBean.setPayretcode(cmbcInstPayLog.getRespCode());
        payPartyBean.setPayretinfo(cmbcInstPayLog.getRespMsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
        txnsLogDAO.updateAppInfo(cmbcInstPayLog.getTxnseqno());
       
		return null;
	}
	@Override
	public ResultBean batchPayment(TradeBean tradeBean) {
		OrderPaymentBatchDO paymentBatchOrder = orderPaymentBatchDAO.getPaymentBatchOrderByTn(tradeBean.getTn());
		List<OrderPaymentDetaDO> detaList = orderPaymentDetaDAO.getDetaListByBatchtid(paymentBatchOrder.getTid());
		for(OrderPaymentDetaDO orderDeta : detaList){
			PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(orderDeta.getRelatetradetxn());
			if(txnsLog==null){
				return null;
			}
			InsteadPayTradeBean insteadPayTradeBean = new InsteadPayTradeBean();
			insteadPayTradeBean.setTxnseqno(txnsLog.getTxnseqno());
			insteadPayTradeBean.setAcc_name(txnsLog.getInpanName());
			insteadPayTradeBean.setAcc_no(txnsLog.getInpan());
			insteadPayTradeBean.setBank_type(txnsLog.getIncardinstino());
			insteadPayTradeBean.setBank_name("TEST BANK");
			insteadPayTradeBean.setTrans_amt(txnsLog.getAmount().toString());
			
			PayPartyBean payPartyBean = new PayPartyBean(orderDeta.getRelatetradetxn(),
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
				orderPaymentDetaDAO.updateOrderToSuccess(insteadPayTradeBean.getTxnseqno(),"0000","交易成功");
				txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.FINISH_SUCCESS);
			}else{
				if(!resultBean.getErrCode().equals("E")){
					orderPaymentDetaDAO.updateOrderToFail(insteadPayTradeBean.getTxnseqno(),resultBean.getErrCode(),resultBean.getErrMsg());
					txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.FINISH_FAILED);
				}else{
					//加入交易查询队列
					tradeQueueService.addTradeQueue(txnsLog.getTxnseqno());
					return resultBean;
				}
				
			}
			dealWithInsteadPay(payPartyBean.getPayordno());
		}
		orderPaymentBatchDAO.updateOrderToSuccess(tradeBean.getTn());
		return null;
	}

}
