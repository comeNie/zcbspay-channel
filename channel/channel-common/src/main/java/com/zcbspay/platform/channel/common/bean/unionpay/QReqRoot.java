package com.zcbspay.platform.channel.common.bean.unionpay;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Root")
@XmlType(name = "Root")
public class QReqRoot implements Serializable {

	private static final long serialVersionUID = -6765100196395847463L;

	// 版本号
	private String version;
	// 编码方式
	private String encoding;
	// 证书ID
	private String certId;
	// 加密证书ID
	private String encryptCertId;
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
	// 查询交易流水号
	private String transQueryId;

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

	public String getEncryptCertId() {
		return encryptCertId;
	}

	public void setEncryptCertId(String encryptCertId) {
		this.encryptCertId = encryptCertId;
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

	public String getTransQueryId() {
		return transQueryId;
	}

	public void setTransQueryId(String transQueryId) {
		this.transQueryId = transQueryId;
	}

	@Override
	public String toString() {
		return "QReqRoot [version=" + version + ", encoding=" + encoding + ", certId=" + certId + ", encryptCertId="
				+ encryptCertId + ", signMethod=" + signMethod + ", transType=" + transType + ", mchntCd=" + mchntCd
				+ ", orderId=" + orderId + ", transTm=" + transTm + ", transQueryId=" + transQueryId + "]";
	}

}
