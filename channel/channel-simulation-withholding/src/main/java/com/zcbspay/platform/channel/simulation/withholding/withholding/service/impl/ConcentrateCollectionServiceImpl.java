package com.zcbspay.platform.channel.simulation.withholding.withholding.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zcbspay.platform.channel.common.bean.PayPartyBean;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.common.enums.CertifTypeEnmu;
import com.zcbspay.platform.channel.common.enums.ChannelEnmu;
import com.zcbspay.platform.channel.common.enums.TradeStatFlagEnum;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.pojo.PojoTxnsLog;
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectBatchDAO;
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectDetaDAO;
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectSingleDAO;
import com.zcbspay.platform.channel.simulation.withholding.dao.ProvinceDAO;
import com.zcbspay.platform.channel.simulation.withholding.dao.TxnsOrderinfoDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectBatchDO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectDetaDO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWithholding;
import com.zcbspay.platform.channel.simulation.withholding.queue.service.TradeQueueService;
import com.zcbspay.platform.channel.simulation.withholding.sequence.service.SerialNumberService;
import com.zcbspay.platform.channel.simulation.withholding.service.TxnsWithholdingService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.CMBCRealNameAuthService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.CMBCWithholdingService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.ConcentrateCollectionService;
import com.zcbspay.platform.channel.utils.Constant;
import com.zcbspay.platform.channel.utils.DateUtil;
import com.zcbspay.platform.payment.trade.acc.service.TradeAccountingService;
import com.zcbspay.platform.support.task.service.TradeNotifyService;

@Service
public class ConcentrateCollectionServiceImpl implements ConcentrateCollectionService {
	private static final Logger log = LoggerFactory.getLogger(ConcentrateCollectionServiceImpl.class);
	@Autowired
	private ProvinceDAO provinceDAO;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private TxnsOrderinfoDAO txnsOrderinfoDAO;
	@Autowired
	private TxnsWithholdingService txnsWithholdingService;
	@Reference(version="1.0")
	private TradeNotifyService tradeNotifyService;
	@Autowired
	private SerialNumberService serialNumberService;
	@Autowired
	private CMBCWithholdingService cmbcWithholdingService;
	@Autowired
	private CMBCRealNameAuthService cmbcRealNameAuthService;
	@Reference(version="1.0")
	private TradeAccountingService tradeAccountingService;
	@Autowired
	private TradeQueueService tradeQueueService;
	@Autowired
	private OrderCollectSingleDAO orderCollectSingleDAO;
	@Autowired
	private OrderCollectBatchDAO orderCollectBatchDAO;
	@Autowired
	private OrderCollectDetaDAO orderCollectDetaDAO;
	
	
	@Override
	public ResultBean realTimeCollection(TradeBean tradeBean) {
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(tradeBean.getTxnseqno());
		tradeBean.setCardNo(txnsLog.getPan());
		tradeBean.setAcctName(txnsLog.getPanName());
		tradeBean.setCertId("0000000000000000000");
		tradeBean.setMobile("00000000000");
		tradeBean.setBankCode(txnsLog.getCardinstino());
		tradeBean.setProvno("0000");
		tradeBean.setMerchId(txnsLog.getAccsecmerno());
		tradeBean.setOrderId(txnsLog.getAccordno());
		tradeBean.setAmount(txnsLog.getAmount()+"");
		ResultBean resultBean = null;
		try {
			log.info("Concentrate submit Pay start!");
			resultBean = null;
			// 更新支付方信息
			PayPartyBean payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),"01", serialNumberService.generateCMBCSerialNo(), ChannelEnmu.CMBCWITHHOLDING.getChnlcode(),
					Constant.getInstance().getCmbc_merid(), "",
					DateUtil.getCurrentDateTime(), "", tradeBean.getCardNo());
			txnsLogDAO.updatePayInfo(payPartyBean);
			tradeBean.setPayOrderNo(payPartyBean.getPayordno());
			tradeBean.setPayinstiId(ChannelEnmu.CMBCWITHHOLDING.getChnlcode());
			// 更新交易标志状态
			txnsLogDAO.updateTradeStatFlag(tradeBean.getTxnseqno(), TradeStatFlagEnum.PAYING);
			resultBean = cmbcWithholdingService.crossLineWithhold(tradeBean);
			if(resultBean.isResultBool()) {
				//更新订单状态
				orderCollectSingleDAO.updateOrderToSuccess(tradeBean.getTxnseqno());
	            
			} else {// 交易失败
				if("E".equals(resultBean.getErrCode())){
					orderCollectSingleDAO.updateOrderToFail(tradeBean.getTxnseqno());
					return resultBean;
				}else{
					//加入交易查询队列
					tradeQueueService.addTradeQueue(tradeBean.getTxnseqno());
					return resultBean;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			orderCollectSingleDAO.updateOrderToFail(tradeBean.getTxnseqno());
			resultBean = new ResultBean("T000", "交易失败");
		}
		dealWithAccounting(tradeBean.getTxnseqno(), resultBean);
		log.info("CMBC submit Pay end!");
		return resultBean;
	}
	public ResultBean dealWithAccounting(String txnseqno,ResultBean resultBean){
		
		PojoTxnsWithholding withholding = (PojoTxnsWithholding) resultBean.getResultObj();
		//PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
        PayPartyBean payPartyBean = null;
        if(StringUtils.isNotEmpty(withholding.getOrireqserialno())){
            PojoTxnsWithholding old_withholding = txnsWithholdingService.getWithholdingBySerialNo(withholding.getOrireqserialno());
            //更新支付方信息
            payPartyBean = new PayPartyBean(txnseqno,"01", withholding.getOrireqserialno(), old_withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",old_withholding.getAccno(),withholding.getPayserialno());
        }else{
            payPartyBean = new PayPartyBean(txnseqno,"01", withholding.getSerialno(), withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",withholding.getAccno(),withholding.getPayserialno());
        }
        payPartyBean.setPanName(withholding.getAccname());
        payPartyBean.setPayretcode(withholding.getExeccode());
        payPartyBean.setPayretinfo(withholding.getExecmsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
		return resultBean;
	}

	@Override
	public ResultBean batchCollection(TradeBean tradeBean) {
		ResultBean resultBean = null;
		try {
			log.info("concentrate batch collection submit pay start!");
			OrderCollectBatchDO orderCollectBatch = orderCollectBatchDAO.getCollectBatchOrderByTn(tradeBean.getTn());
			List<OrderCollectDetaDO> detaList = orderCollectDetaDAO.getDetaListByBatchtid(orderCollectBatch.getTid());
			for(OrderCollectDetaDO collectDeta : detaList){
				PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(collectDeta.getRelatetradetxn());
				tradeBean.setCardNo(txnsLog.getPan());
				tradeBean.setAcctName(txnsLog.getPanName());
				tradeBean.setCertId("0000000000000000000");
				tradeBean.setMobile("00000000000");
				tradeBean.setBankCode(txnsLog.getCardinstino());
				tradeBean.setProvno("0000");
				tradeBean.setMerchId(txnsLog.getAccsecmerno());
				tradeBean.setOrderId(txnsLog.getAccordno());
				tradeBean.setAmount(txnsLog.getAmount()+"");
				tradeBean.setTxnseqno(collectDeta.getRelatetradetxn());
				PayPartyBean payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),"01", serialNumberService.generateCMBCSerialNo(), ChannelEnmu.CMBCWITHHOLDING.getChnlcode(),
						Constant.getInstance().getCmbc_merid(), "",
						DateUtil.getCurrentDateTime(), "", tradeBean.getCardNo());
				txnsLogDAO.updatePayInfo(payPartyBean);
				tradeBean.setPayOrderNo(payPartyBean.getPayordno());
				tradeBean.setPayinstiId(ChannelEnmu.CMBCWITHHOLDING.getChnlcode());
				// 更新交易标志状态
				txnsLogDAO.updateTradeStatFlag(tradeBean.getTxnseqno(), TradeStatFlagEnum.PAYING);
				resultBean = cmbcWithholdingService.crossLineWithhold(tradeBean);
				if(resultBean.isResultBool()) {
					//更新订单状态
					orderCollectDetaDAO.updateOrderToSuccess(tradeBean.getTxnseqno(),"0000","交易成功");
				} else {// 交易失败
					if("E".equals(resultBean.getErrCode())){
						orderCollectDetaDAO.updateOrderToFail(tradeBean.getTxnseqno(),resultBean.getErrCode(),resultBean.getErrMsg());
						return resultBean;
					}else{
						//加入交易查询队列
						tradeQueueService.addTradeQueue(tradeBean.getTxnseqno());
						return resultBean;
					}
				}
				dealWithAccounting(tradeBean.getTxnseqno(), resultBean);
			}
			orderCollectBatchDAO.updateOrderToSuccess(tradeBean.getTn());
		} catch (Exception e) {
			e.printStackTrace();
			orderCollectSingleDAO.updateOrderToFail(tradeBean.getTxnseqno());
			resultBean = new ResultBean("T000", "交易失败");
		}
		log.info("concentrate batch collection submit pay end!");
		return resultBean;
	}

}
