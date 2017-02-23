package com.zcbspay.platform.channel.common.bean.unionpay;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Root")
@XmlType(name = "Root")
public class DwnReqRoot implements Serializable {
	private static final long serialVersionUID = 4948055921885130928L;

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
	// 流水号
	private String queryId;
	// 发送时间
	private String queryDt;
	// 交易日期
	private String transDt;

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

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	 
	public String getQueryDt() {
		return queryDt;
	}

	public void setQueryDt(String queryDt) {
		this.queryDt = queryDt;
	}

	public String getTransDt() {
		return transDt;
	}

	public void setTransDt(String transDt) {
		this.transDt = transDt;
	}

	@Override
	public String toString() {
		return "QReqRoot [version=" + version + ", encoding=" + encoding + ", certId=" + certId + ", encryptCertId="
				+ encryptCertId + ", signMethod=" + signMethod + ", transType=" + transType + ", mchntCd=" + mchntCd
				+ ", queryId=" + queryId + ", queryDt=" + queryDt + ", transDt=" + transDt + "]";
	}

}
