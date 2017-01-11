/* 
 * IWithholdingService.java  
 * 
 * version TODO
 *
 * 2015年11月23日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.simulation.withholding.withholding.service;

import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.simulation.withholding.exception.CMBCTradeException;
import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWithholding;
import com.zcbspay.platform.channel.simulation.withholding.withholding.bean.CardMessageBean;
import com.zcbspay.platform.channel.simulation.withholding.withholding.bean.WhiteListMessageBean;
import com.zcbspay.platform.channel.simulation.withholding.withholding.bean.WithholdingMessageBean;

/**
 * 民生跨行代扣报文发送service
 *
 * @author guojia
 * @version
 * @date 2015年11月23日 下午2:28:36
 * @since 
 */
public interface WithholdingService{
    public ResultBean realNameAuthentication(String json) throws CMBCTradeException;
    public ResultBean realNameAuthentication(CardMessageBean card ) throws CMBCTradeException;
    public ResultBean realNameAuthQuery( String oritransdate, String orireqserialno )
            throws CMBCTradeException;
    public ResultBean realTimeWitholding(WithholdingMessageBean withholdingMsg)
            throws CMBCTradeException;
    public ResultBean realTimeWitholdinghQuery( String oritransdate, String orireqserialno )
            throws CMBCTradeException;
    public ResultBean realTimeWitholdinghQuery(PojoTxnsWithholding withholding)
            throws CMBCTradeException;
    public ResultBean whiteListCollection(WhiteListMessageBean whiteListMsg)
            throws CMBCTradeException;
    public ResultBean whiteListCollectionQuery(String bankaccno)
            throws CMBCTradeException;
    public ResultBean realTimeWitholdinghResult(PojoTxnsWithholding withholding) throws CMBCTradeException;
}
