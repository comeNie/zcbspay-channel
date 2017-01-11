/* 
 * CMBCWhiteListServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.simulation.withholding.withholding.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.enums.CMBCCardTypeEnum;
import com.zcbspay.platform.channel.dao.TxnsLogDAO;
import com.zcbspay.platform.channel.simulation.withholding.exception.CMBCTradeException;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWhiteList;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWithholding;
import com.zcbspay.platform.channel.simulation.withholding.sequence.service.SerialNumberService;
import com.zcbspay.platform.channel.simulation.withholding.service.TxnsWhiteListService;
import com.zcbspay.platform.channel.simulation.withholding.service.TxnsWithholdingService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.bean.WhiteListMessageBean;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.CMBCWhiteListService;
import com.zcbspay.platform.channel.simulation.withholding.withholding.service.WithholdingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 下午3:19:03
 * @since 
 */
@Service("cmbcWhiteListService")
public class CMBCWhiteListServiceImpl implements CMBCWhiteListService {
	
	@Autowired
	private TxnsWhiteListService txnsWhiteListService;
	@Autowired
	private TxnsWithholdingService txnsWithholdingService;
	@Autowired
	private WithholdingService withholdingService;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private SerialNumberService serialNumberService;
	/**
	 *
	 * @param bankaccno
	 * @param bankaccname
	 * @param certno
	 * @param mobile
	 * @param certtype
	 * @return
	 */
	@Override
	public ResultBean whiteListCollection(String bankaccno, String bankaccname,
			String certno, String mobile, String certtype) {
		ResultBean resultBean = null;
        PojoTxnsWithholding withholding = null;
        try {
            PojoTxnsWhiteList whiteList = new PojoTxnsWhiteList(bankaccno, bankaccname, certno, mobile);
            PojoTxnsWhiteList bean = txnsWhiteListService.getByCardInfo(whiteList);
            if(bean==null){
                Map<String, Object> cardMap = txnsLogDAO.getCardInfo(bankaccno);
                String bankinscode=cardMap.get("BANKCODE")+"";
                String bankname=cardMap.get("BANKNAME")+""; 
                String bankacctype=cardMap.get("TYPE")+""; 
                WhiteListMessageBean whiteListMsg = new WhiteListMessageBean(bankinscode, bankname, bankaccno, bankaccname, CMBCCardTypeEnum.fromCardType(bankacctype).getCode(), certtype, certno, mobile, "", "");
                //withholdingService.whiteListCollection(whiteListMsg);
                withholding = new PojoTxnsWithholding(whiteListMsg.getBankinscode(),whiteListMsg.getBankname(),whiteListMsg.getBankaccno(),whiteListMsg.getBankaccname(),whiteListMsg.getBankacctype(),whiteListMsg.getCerttype(),whiteListMsg.getCertno(),whiteListMsg.getMobile());
                withholding.setSerialno(serialNumberService.generateCMBCSerialNo());
                whiteListMsg.setWithholding(withholding);
                
                //保存白名单采集流水
                txnsWithholdingService.saveWithholdingLog(withholding);
                //民生白名单采集
                withholdingService.whiteListCollection(whiteListMsg);
                resultBean = new ResultBean("success");
            }else{
                resultBean = new ResultBean("success");
            }
        } catch (CMBCTradeException e) {
            e.printStackTrace();
            withholding.setExecmsg(e.getMessage());
            txnsWithholdingService.updateWithholdingLogError(withholding);
            resultBean = new ResultBean(e.getCode(),e.getMessage());
        }    
        return resultBean;
	}

}
