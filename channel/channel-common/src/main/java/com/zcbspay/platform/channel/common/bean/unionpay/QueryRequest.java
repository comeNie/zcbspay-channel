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
@XmlType(name = "Request", propOrder = {"Root", "signature"})
public class QueryRequest implements Serializable {
	
	private static final long serialVersionUID = -6765100196395847462L;
	
	private QReqRoot Root;
	
	private String signature;

	public QReqRoot getRoot() {
		return Root;
	}

	public void setRoot(QReqRoot root) {
		this.Root = root;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}	
}
