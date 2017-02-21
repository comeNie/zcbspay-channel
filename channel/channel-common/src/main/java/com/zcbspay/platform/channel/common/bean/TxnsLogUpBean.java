package com.zcbspay.platform.channel.common.bean;

import java.io.Serializable;

public class TxnsLogUpBean implements Serializable {

    private static final long serialVersionUID = 3220369539565868419L;
    private String orderId;
    private String version;
    private String refOrderId;
    private String txnseqno;
    private String tradeStatus;
    private String encoding;
    private String certId;
    private String signMethod;
    private String transType;
    private String mchntCd;
    private String factorId;
    private String priAcctId;
    private String name;
    private String phone;
    private String idCard;
    private String dkType;
    private long transAt;
    private String atType;
    private String transTm;
    private String backUrl;
    private String respcod;
    private String respmsg;
    private long settleAt;
    private String settleType;
    private String settleDate;
    private String exchangeDate;
    private String exchangeRate;
    private String sendTm;
    private String queryDt;
    private String transDt;
    private String downloadUrl;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRefOrderId() {
        return refOrderId;
    }

    public void setRefOrderId(String refOrderId) {
        this.refOrderId = refOrderId;
    }

    public String getTxnseqno() {
        return txnseqno;
    }

    public void setTxnseqno(String txnseqno) {
        this.txnseqno = txnseqno;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getMchntCd() {
        return mchntCd;
    }

    public void setMchntCd(String mchntCd) {
        this.mchntCd = mchntCd;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
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

    public String getDkType() {
        return dkType;
    }

    public void setDkType(String dkType) {
        this.dkType = dkType;
    }

    public long getTransAt() {
        return transAt;
    }

    public void setTransAt(long transAt) {
        this.transAt = transAt;
    }

    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
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

    public String getRespcod() {
        return respcod;
    }

    public void setRespcod(String respcod) {
        this.respcod = respcod;
    }

    public String getRespmsg() {
        return respmsg;
    }

    public void setRespmsg(String respmsg) {
        this.respmsg = respmsg;
    }

    public long getSettleAt() {
        return settleAt;
    }

    public void setSettleAt(long settleAt) {
        this.settleAt = settleAt;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getTransDt() {
        return transDt;
    }

    public void setTransDt(String transDt) {
        this.transDt = transDt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    
    public String getSendTm() {
        return sendTm;
    }

    
    public void setSendTm(String sendTm) {
        this.sendTm = sendTm;
    }

    
    public String getQueryDt() {
        return queryDt;
    }

    
    public void setQueryDt(String queryDt) {
        this.queryDt = queryDt;
    }

    @Override
    public String toString() {
        return "TxnsLogUpBean [orderId=" + orderId + ", version=" + version + ", refOrderId=" + refOrderId + ", txnseqno=" + txnseqno + ", tradeStatus=" + tradeStatus + ", encoding=" + encoding
                + ", certId=" + certId + ", signMethod=" + signMethod + ", transType=" + transType + ", mchntCd=" + mchntCd + ", factorId=" + factorId + ", priAcctId=" + priAcctId + ", name=" + name
                + ", phone=" + phone + ", idCard=" + idCard + ", dkType=" + dkType + ", transAt=" + transAt + ", atType=" + atType + ", transTm=" + transTm + ", backUrl=" + backUrl + ", respcod="
                + respcod + ", respmsg=" + respmsg + ", settleAt=" + settleAt + ", settleType=" + settleType + ", settleDate=" + settleDate + ", exchangeDate=" + exchangeDate + ", exchangeRate="
                + exchangeRate + ", sendTm=" + sendTm + ", queryDt=" + queryDt + ", transDt=" + transDt + ", downloadUrl=" + downloadUrl + "]";
    }

}
