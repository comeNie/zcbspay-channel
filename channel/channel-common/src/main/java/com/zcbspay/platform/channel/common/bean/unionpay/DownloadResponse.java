package com.zcbspay.platform.channel.common.bean.unionpay;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XML解析对象
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
@XmlType(name = "Response", propOrder = { "Root", "signature" })
public class DownloadResponse implements Serializable {
	private static final long serialVersionUID = -6453403634916370907L;

	private DwnRspRoot Root;

	private String signature;

	public DwnRspRoot getRoot() {
		return Root;
	}

	public void setRoot(DwnRspRoot root) {
		Root = root;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
