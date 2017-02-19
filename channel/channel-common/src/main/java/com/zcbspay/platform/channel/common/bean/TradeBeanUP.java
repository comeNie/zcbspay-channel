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
 * 代扣交易参数
 * 
 * @author AlanMa
 *
 */
public class TradeBeanUP implements Serializable, Cloneable {

    private static final long serialVersionUID = 341743717923561517L;

    /** 交易序列号 **/
    private String txnseqno;
    /** 银行卡号 **/
    private String priAcctId;
    /** 账户名称 **/
    private String name;
    /** 手机号 **/
    private String phone;
    /** 身份证号 **/
    private String idCard;
    /** 交易金额 **/
    private String transAt;
    /** 订单发送时间 **/
    private String transTm;
    /** 后台通知地址 **/
    private String backUrl;
    /** 交易要素——默认：0107 卡号+姓名+身份证号+手机号 **/
    private String factorId;
    /** 交易类型 **/
    private String transType;

    public String getTxnseqno() {
        return txnseqno;
    }

    public void setTxnseqno(String txnseqno) {
        this.txnseqno = txnseqno;
    }

    public String getPriAcctId() {
        return priAcctId;
    }

    public void setPriAcctId(String priAcctId) {
        this.priAcctId = priAcctId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTransAt() {
        return transAt;
    }

    public void setTransAt(String transAt) {
        this.transAt = transAt;
    }

    public String getTransTm() {
        return transTm;
    }

    public void setTransTm(String transTm) {
        this.transTm = transTm;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }
    
    public String getTransType() {
        return transType;
    }

    
    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Override
    public String toString() {
        return "TradeBeanUP [txnseqno=" + txnseqno + ", priAcctId=" + priAcctId + ", name=" + name + ", phone=" + phone + ", idCard=" + idCard + ", transAt=" + transAt + ", transTm=" + transTm
                + ", backUrl=" + backUrl + ", factorId=" + factorId + ", transType=" + transType + "]";
    }


}
