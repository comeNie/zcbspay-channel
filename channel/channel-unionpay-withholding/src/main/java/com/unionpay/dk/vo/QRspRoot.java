package com.unionpay.dk.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Root")
@XmlType(name = "Root")
public class QRspRoot implements Serializable {
	private static final long serialVersionUID = -6765100196395847463L;
	// 版本号
	private String version;
	// 编码方式
	private String encoding;
	// 证书ID
	private String certId;
	// 签名方式
	private String signMethod;
	// 交易类型
	private String transType;
	// 系统商户号
	private String mchntCd;
	// 系统订单号
	private String orderId;
	// 订单发送时间
	private String transTm;
	// 交易查询流水号
	private String queryId;
	// 清算金额
	private String settleAt;
	// 清算币种
	private String settleType;
	// 清算日期
	private String settleDate;
	// 清算汇率
	private String exchangeRate;
	// 原交易应答码
	private String origRespCode;
	// 原交易应答信息
	private String origRespMsg;
	// 应答码
	private String respCod;
	// 应答信息
	private String respMsg;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTransTm() {
		return transTm;
	}

	public void setTransTm(String transTm) {
		this.transTm = transTm;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getSettleAt() {
		return settleAt;
	}

	public void setSettleAt(String settleAt) {
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

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getOrigRespCode() {
		return origRespCode;
	}

	public void setOrigRespCode(String origRespCode) {
		this.origRespCode = origRespCode;
	}

	public String getOrigRespMsg() {
		return origRespMsg;
	}

	public void setOrigRespMsg(String origRespMsg) {
		this.origRespMsg = origRespMsg;
	}

	public String getRespCod() {
		return respCod;
	}

	public void setRespCod(String respCod) {
		this.respCod = respCod;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	@Override
	public String toString() {
		return "QRspRoot [version=" + version + ", encoding=" + encoding + ", certId=" + certId + ", signMethod="
				+ signMethod + ", transType=" + transType + ", mchntCd=" + mchntCd + ", orderId=" + orderId
				+ ", transTm=" + transTm + ", queryId=" + queryId + ", settleAt=" + settleAt + ", settleType="
				+ settleType + ", settleDate=" + settleDate + ", exchangeRate=" + exchangeRate + ", origRespCode="
				+ origRespCode + ", origRespMsg=" + origRespMsg + ", respCod=" + respCod + ", respMsg=" + respMsg + "]";
	}

}
