/* 
 * BaseException.java  
 * 
 * version TODO
 *
 * 2015年9月6日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.unionpay.withholding.exception;

import com.zcbspay.platform.channel.unionpay.withholding.enums.ErrorCodeUP;

/**
 * @author AlanMa
 *
 */
public class UnionPayException extends Exception {

	private static final long serialVersionUID = 8564355573270250157L;

	private String errCode;

	private String errMsg;

	public UnionPayException() {
		super();
	}

	public UnionPayException(String errCode, String errMsg) {
		super(errCode + errMsg);
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public UnionPayException(String errCode) {
		super(errCode + ErrorCodeUP.parseOf(errCode).getDisplayName());
		this.errCode = errCode;
		this.errMsg = ErrorCodeUP.parseOf(errCode).getDisplayName();
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return "UnionPayException [errCode=" + errCode + ", errMsg=" + errMsg + "]";
	}

}
