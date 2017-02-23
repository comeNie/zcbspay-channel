package com.unionpay.dk.webservice;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * 
 * @author Luke
 *
 */
@WebService
public interface TranWebService {

	/**
	 * 发起交易接口
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	public String launchTran(String message);

	/**
	 * 查询交易接口
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(targetNamespace = "")
	public String queryTranResult(String message);

	/**
	 * 下载对账文件
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@WebResult(targetNamespace = "")
	public String downloadTrans(String message);

}
