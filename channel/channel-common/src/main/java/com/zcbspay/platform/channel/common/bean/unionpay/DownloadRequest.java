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
@XmlRootElement(name = "Request")
@XmlType(name = "Request", propOrder = { "Root", "signature" })
public class DownloadRequest implements Serializable {
	private static final long serialVersionUID = -6052600283260525027L;

	private DwnReqRoot Root;

	private String signature;

	public DwnReqRoot getRoot() {
		return Root;
	}

	public void setRoot(DwnReqRoot root) {
		this.Root = root;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
