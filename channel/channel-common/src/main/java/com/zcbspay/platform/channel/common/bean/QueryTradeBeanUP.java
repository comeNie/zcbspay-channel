/* 
 * TradeBean.java  
 * 
 * version TODO
 *
 * 2015年8月27日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.common.bean;

import java.io.Serializable;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年8月27日 下午8:25:07
 * @since
 */
public class QueryTradeBeanUP implements Serializable, Cloneable {

    private static final long serialVersionUID = 7990669165684148748L;

    /** 代扣流水号 **/
    private String refOrderId;
    /** 订单发送时间 **/
    private String transTm;
    /** 交易类型 **/
    private String transType;

    public String getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(String refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getTransTm() {
        return transTm;
    }

    public void setTransTm(String transTm) {
        this.transTm = transTm;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Override
    public String toString() {
        return "QueryTradeBeanUP [origOrderId=" + refOrderId + ", transTm=" + transTm + ", transType=" + transType + "]";
    }

}
