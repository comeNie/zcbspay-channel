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
public class ApplyAccCheckUP implements Serializable, Cloneable {

    private static final long serialVersionUID = -4519704114610072867L;

    /** 交易类型 **/
    private String transType;
    /** 交易流水号(申请下载对账单文件流水号) **/
    private String queryId;
    /** 发送时间yyyyMMdd **/
    private String queryDt;
    /** 交易日期yyyyMMdd **/
    private String transDt;

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getTransDt() {
        return transDt;
    }

    public void setTransDt(String transDt) {
        this.transDt = transDt;
    }

    public String getQueryDt() {
        return queryDt;
    }

    public void setQueryDt(String queryDt) {
        this.queryDt = queryDt;
    }

    @Override
    public String toString() {
        return "ApplyAccCheckUP [transType=" + transType + ", queryId=" + queryId + ", queryDt=" + queryDt + ", transDt=" + transDt + "]";
    }

}
