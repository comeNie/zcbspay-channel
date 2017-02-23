package com.zcbspay.platform.channel.common.bean.unionpay;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Root")
@XmlType(name = "Root")
public class ReqRoot implements Serializable {

	private static final long serialVersionUID = -6765100196395847463L;

	// 版本号
	private String version;
	// 编码方式
	private String encoding;
	// 证书ID
	private String certId;
	// 签名方式
	private String signMethod;
	// 加密证书ID
	private String encryptCertId;
	// 交易类型
	private String transType;
	// 系统订单号
	private String orderId;
	// 系统商户号
	private String mchntCd;
	// 交易要素id
	private String factorId;
	// 卡号
	private String priAcctId;
	// 姓名
	private String name;
	// 手机号
	private String phone;
	// 身份证号
	private String idCard;
	// 扣款类型
	private String dkType;
	// 交易金额
	private String transAt;
	// 交易币种
	private String atType;
	// 订单发送时间
	private String transTm;
	// 后台通知地址
	private String backUrl;

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

	public String getEncryptCertId() {
		return encryptCertId;
	}

	public void setEncryptCertId(String encryptCertId) {
		this.encryptCertId = encryptCertId;
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

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	@Override
	public String toString() {
		return "Root [version=" + version + ", encoding=" + encoding + ", certId=" + certId + ", signMethod="
				+ signMethod + ", encryptCertId=" + encryptCertId + ", transType=" + transType + ", orderId=" + orderId
				+ ", mchntCd=" + mchntCd + ", factorId=" + factorId + ", priAcctId=" + priAcctId + ", name=" + name
				+ ", phone=" + phone + ", idCard=" + idCard + ", dkType=" + dkType + ", transAt=" + transAt
				+ ", atType=" + atType + "]";
	}

}
