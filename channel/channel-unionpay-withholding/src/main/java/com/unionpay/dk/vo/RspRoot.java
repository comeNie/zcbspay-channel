package com.unionpay.dk.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Root")
@XmlType(name = "Root")
public class RspRoot implements Serializable {
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
	// 系统订单号
	private String orderId;
	// 系统商户号
	private String mchntCd;
	// 交易金额
	private String transAt;
	// 交易币种
	private String atType;
	// 订单发送时间
	private String transTm;
	// 交易查询流水号
	private String queryId;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMchntCd() {
		return mchntCd;
	}

	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}

	public String getTransAt() {
		return transAt;
	}

	public void setTransAt(String transAt) {
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

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
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
		return "RspRoot [version=" + version + ", encoding=" + encoding + ", certId=" + certId + ", signMethod="
				+ signMethod + ", transType=" + transType + ", orderId=" + orderId + ", mchntCd=" + mchntCd
				+ ", transAt=" + transAt + ", atType=" + atType + ", transTm=" + transTm + ", queryId=" + queryId
				+ ", respCod=" + respCod + ", respMsg=" + respMsg + "]";
	}

}
