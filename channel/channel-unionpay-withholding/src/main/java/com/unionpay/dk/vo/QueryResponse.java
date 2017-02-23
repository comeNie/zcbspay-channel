package com.unionpay.dk.vo;

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
@XmlType(name = "Response", propOrder = {"Root", "signature"})
public class QueryResponse implements Serializable {
	
	private static final long serialVersionUID = -6765100196395847462L;
	
	private QRspRoot Root;
	
	private String signature;

	public QRspRoot getRoot() {
		return Root;
	}

	public void setRoot(QRspRoot root) {
		Root = root;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}	
}
