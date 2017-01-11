package com.zcbspay.platform.channel.simulation.withholding.withholding.service.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.common.enums.BankEnmu;
import com.zcbspay.platform.channel.common.enums.ChannelEnmu;
import com.zcbspay.platform.channel.common.enums.ChnlTypeEnum;
import com.zcbspay.platform.channel.dao.RspmsgDAO;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.pojo.PojoRspmsg;
import com.zcbspay.platform.channel.pojo.PojoTxnsLog;
import com.zcbspay.platform.channel.simulation.withholding.exception.CMBCTradeException;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWithholding;
import com.zcbspay.platform.channel.simulation.withholding.sequence.service.SerialNumberService;
import com.zcbspay.platform.channel.simulation.withholding.service.TxnsWithholdingService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.bean.WithholdingMessageBean;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.CMBCRealNameAuthService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.CMBCWithholdingService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.WithholdingService;

/**
 * 
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年12月16日 下午1:58:17
 * @since
 */
@Service("cmbcWithholdingService")
public class CMBCWithholdingServiceImpl implements CMBCWithholdingService{

    //private static final Log log = LogFactory.getLog(CMBCQuickPayServiceImpl.class);
    @Autowired
    private CMBCRealNameAuthService cmbcRealNameAuthService;
    @Autowired
    private WithholdingService withholdingService;
    @Autowired
    private TxnsWithholdingService txnsWithholdingService;
    @Autowired
    private RspmsgDAO rspmsgDAO;
    @Autowired
    private TxnsLogDAO txnsLogService;
    @Autowired
    private SerialNumberService serialNumberService;
    /**
     * 跨行代扣
     * @param withholdingMsg
     * @return
     */
    public ResultBean crossLineWithhold(TradeBean trade){
        ResultBean resultBean = null;
        try {
            PojoTxnsWithholding withholding = new PojoTxnsWithholding(trade,ChannelEnmu.CMBCWITHHOLDING);
            //处理平安银行bankcode不匹配
            dealWithPingAn(withholding);
            withholding.setSerialno(trade.getPayOrderNo());
            txnsWithholdingService.saveWithholdingLog(withholding);
            WithholdingMessageBean withholdingMsg = new WithholdingMessageBean(withholding);
            withholdingMsg.setWithholding(withholding);
            withholdingService.realTimeWitholding(withholdingMsg);
            
            resultBean = queryResult(withholding.getSerialno());
            
            return resultBean;
        }  catch (CMBCTradeException e) {
            e.printStackTrace();
            //txnsLogService.updateTradeStatFlag(trade.getTxnseqno(), TradeStatFlagEnum.OVERTIME);
            resultBean = new ResultBean(e.getCode(), e.getMessage());
        }
        return resultBean;
    }
    
    
    
    public ResultBean queryResult(String serialno) {
        PojoTxnsWithholding withholding = null;
        ResultBean resultBean = null;
        int[] timeArray = new int[]{1, 2, 8, 16, 32};
        try {
            for (int i = 0; i < 5; i++) {
                withholding = txnsWithholdingService.getWithholdingBySerialNo(serialno);
                if(StringUtils.isNotEmpty(withholding.getExectype())){
                    if("S".equalsIgnoreCase(withholding.getExectype())){
                        resultBean = new ResultBean(withholding);
                        return resultBean;
                    }else if("E".equalsIgnoreCase(withholding.getExectype())){
                    	PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, withholding.getExeccode());
                        resultBean = new ResultBean("E",msg.getRspinfo());
                        resultBean.setResultObj(withholding);
                        return resultBean;
                    }else if("R".equalsIgnoreCase(withholding.getExectype())){
                        resultBean = new ResultBean("R","正在支付中");
                        continue;
                    }
                }
                TimeUnit.SECONDS.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("09", e.getMessage());
        }
        resultBean = new ResultBean("T000", "交易超时，无法再规定时间内取得交易结果");
        return resultBean;
    }
    
    public void queryTradeResult(String oritransdate,String orireqserialno,String txnseqno){
        try {
            PojoTxnsWithholding withholding = new PojoTxnsWithholding(oritransdate, orireqserialno,txnseqno,ChannelEnmu.CMBCWITHHOLDING);
            txnsWithholdingService.saveWithholdingLog(withholding);
            withholdingService.realTimeWitholdinghQuery(withholding);
        } catch (CMBCTradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    public ResultBean queryCrossLineTrade(String txnseqno){
    	ResultBean resultBean = null;
    	try {
			PojoTxnsLog txnsLog = txnsLogService.getTxnsLogByTxnseqno(txnseqno);
			PojoTxnsWithholding withholding_old = txnsWithholdingService.getWithholdingBySerialNo(txnsLog.getPayordno());
			PojoTxnsWithholding withholding = new PojoTxnsWithholding(withholding_old.getTransdate(),withholding_old.getSerialno(),txnseqno,ChannelEnmu.CMBCWITHHOLDING);
			withholding.setSerialno(serialNumberService.generateCMBCSerialNo());
			txnsWithholdingService.saveWithholdingLog(withholding);
			withholdingService.realTimeWitholdinghQuery(withholding);
			resultBean = queryCrossLineTradeResult(withholding.getSerialno());
			
		} catch (CMBCTradeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultBean = new ResultBean(e.getCode(), e.getMessage());
		}
    	return resultBean;
    }
    
    public ResultBean queryCrossLineTradeResult(String serialno) {
        PojoTxnsWithholding withholding = null;
        ResultBean resultBean = null;
        int[] timeArray = new int[]{1000, 2000, 4000, 4000, 4000};
        try {
            for (int i = 0; i < 5; i++) {
                withholding = txnsWithholdingService.getWithholdingBySerialNo(serialno);
                if(!StringUtils.isEmpty(withholding.getExectype())){
                    if("S".equalsIgnoreCase(withholding.getOriexectype())){
                        resultBean = new ResultBean(withholding);
                        break;
                    }else if("E".equalsIgnoreCase(withholding.getOriexectype())){
                    	PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, withholding.getOriexeccode());
                        resultBean = new ResultBean(msg.getWebrspcode(),msg.getRspinfo());
                        break;
                    }else if("R".equalsIgnoreCase(withholding.getOriexectype())){
                        resultBean = new ResultBean("R","正在支付中");
                        break;
                    }
                }
                Thread.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("09", e.getMessage());
        }
        
        return resultBean;
    }
    
    
    
    
    
    
    
    /**
     * 民生银行本行代扣（确认支付）
     * @param trade
     * @return
     */
    public ResultBean innerLineWithhold(TradeBean trade){
        /*ResultBean resultBean = null;
        try {
            PojoTxnsWithholding withholding = new PojoTxnsWithholding(trade,ChannelEnmu.CMBCSELFWITHHOLDING);
            withholding.setSerialno(generateSerialDateNumber("SEQ_CMBC_REALNAME_QUERY_NO"));
            txnsWithholdingService.saveWithholdingLog(withholding);
            WithholdingMessageBean withholdingMsg = new WithholdingMessageBean(withholding);
            withholdingMsg.setWithholding(withholding);
            //withholdingService.realTimeWitholdingSelf(withholdingMsg);
            
            resultBean = queryResult(withholding.getSerialno());
            
            return resultBean;
        } catch (CMBCTradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        return new ResultBean("success");
    }
    
    private void dealWithPingAn(PojoTxnsWithholding withholding){
    	String payerbankinscode = withholding.getPayerbankinscode();
    	if("05100000".equals(payerbankinscode)||"04100000".equals(payerbankinscode)){
    		withholding.setPayerbankinscode("03070000");
    		withholding.setPayerbankname(BankEnmu.fromValue(payerbankinscode).getBankName());
    	}
    	if("04010000".equals(payerbankinscode)){
    		withholding.setPayerbankinscode("04012900");
    		withholding.setPayerbankname(BankEnmu.fromValue("04012900").getBankName());
    	}
    }
}
