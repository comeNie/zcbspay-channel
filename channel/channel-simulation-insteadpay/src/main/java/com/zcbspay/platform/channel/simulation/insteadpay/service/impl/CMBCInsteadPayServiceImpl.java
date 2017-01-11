/* 
 * CMBCInsteadPayServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.simulation.insteadpay.service.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zcbspay.platform.channel.common.bean.CMBCRealTimeInsteadPayResultBean;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimePayBean;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimePayResultBean;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimeQueryBean;
import com.zcbspay.platform.channel.simulation.insteadpay.bean.RealTimeQueryResultBean;
import com.zcbspay.platform.channel.simulation.insteadpay.dao.TxnsCmbcInstPayLogDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.service.CMBCInsteadPayService;
import com.zcbspay.platform.channel.utils.BeanCopyUtil;
import com.zcbspay.platform.channel.utils.Constant;
import com.zcbspay.platform.channel.utils.DateUtil;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 上午11:26:32
 * @since 
 */
@Service("cmbcInsteadPayService")
public class CMBCInsteadPayServiceImpl implements CMBCInsteadPayService {

	private static final Logger logger = LoggerFactory.getLogger(CMBCInsteadPayServiceImpl.class);
	
	@Autowired
	private TxnsCmbcInstPayLogDAO txnsCmbcInstPayLogDAO;
	/**
	 *
	 * @param realTimePayBean
	 * @return
	 */
	@Override
	public ResultBean realTimeInsteadPay(final RealTimePayBean realTimePayBean) {
		
		
		/*int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
						byte[] bytes = socketChannelHelper.getMessageHandler().pack(realTimePayBean);
						String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS");// 主机名称
						int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", 9108);// 主机端口
						NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
						bootstrap.sendMessage(bytes);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();*/
		RealTimePayResultBean realTimePayResultBean = new RealTimePayResultBean();
		long amount = Long.valueOf(realTimePayBean.getTransAmt()).longValue();
		if (amount % 2 == 0) {//偶数交易成功
			realTimePayResultBean.setResp_type("S");
			realTimePayResultBean.setResp_code("00");
			realTimePayResultBean.setResp_msg("交易成功");
			realTimePayResultBean.setBank_tran_id(System.currentTimeMillis()+"");
			realTimePayResultBean.setBank_tran_date(DateUtil.getCurrentDate());
			realTimePayResultBean.setTran_id(realTimePayBean.getTranId());
		} else{
			realTimePayResultBean.setResp_type("E");
			realTimePayResultBean.setResp_code("01");
			realTimePayResultBean.setResp_msg("户名错误");
			realTimePayResultBean.setBank_tran_id("");
			realTimePayResultBean.setBank_tran_date("");
			realTimePayResultBean.setTran_id(realTimePayBean.getTranId());
		}
		txnsCmbcInstPayLogDAO.updateInsteadPayResult(BeanCopyUtil.copyBean(CMBCRealTimeInsteadPayResultBean.class, realTimePayResultBean));
		
		return null;
	}
	/*public ResultBean realTimeInsteadPay(final RealTimePayBean realTimePayBean) {
		final SocketAsyncLongOutputAdapter adapter = new SocketAsyncLongOutputAdapter();
		adapter.start();
		int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						byte[] bytes = adapter.getMessageHandler().pack(realTimePayBean);
						if (bytes != null) {
							adapter.getSendQueue().put(bytes);
						} else {
							logger.error("打包失败:{}", new Object[] { JSON.toJSONString(realTimePayBean) });
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
		
		// 初始化线程池
		int resPoolSize = 1;// 线程池
		executors = Executors.newFixedThreadPool(resPoolSize);
		for (int i = 0; i < resPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					while (adapter.getMessageConfigService().getBoolean("CAN_RUN")) {
						try {
							byte[] bytes = adapter.getReceiveQueue().take();
							Map<String, Object> dataContainer = adapter.getMessageHandler().unpack(bytes);
							if (dataContainer == null) {
								continue;
							}
							String respType = StringUtils.trimToNull((String) dataContainer.get("YHYDLX"));
							if ("FAIL".equalsIgnoreCase(respType)) {
								logger.error("解包失败:{}", new Object[] { dataContainer });
							}else{
								//具体业务处理代码
								
								 * 1.代付结果
								 * 2.代付查询结果
								 
								if(Constant.REALTIME_INSTEADPAY.equals(dataContainer.get("messagecode").toString())){
									RealTimePayResultBean realTimePayResultBean = (RealTimePayResultBean) dataContainer.get("result");
									txnsCmbcInstPayLogDAO.updateInsteadPayResult(BeanCopyUtil.copyBean(CMBCRealTimeInsteadPayResultBean.class, realTimePayResultBean));
									break;
								}
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
		}
		executors.shutdown();
		return null;
	}*/

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean realtimeBatchInsteadPay(String batchNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean batchInsteadPay(String batchNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @param queryBean
	 */
	@Override
	public void queryInsteadPay(final RealTimeQueryBean queryBean) {
		// TODO Auto-generated method stub
		/*final SocketAsyncLongOutputAdapter adapter = new SocketAsyncLongOutputAdapter();
		adapter.start();
		int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						byte[] bytes = adapter.getMessageHandler().pack(queryBean);
						if (bytes != null) {
							adapter.getSendQueue().put(bytes);
						} else {
							logger.error("打包失败:{}", new Object[] { JSON.toJSONString(queryBean) });
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
		
		// 初始化线程池
		int resPoolSize = 1;// 线程池
		executors = Executors.newFixedThreadPool(resPoolSize);
		for (int i = 0; i < resPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					while (adapter.getMessageConfigService().getBoolean("CAN_RUN")) {
						try {
							byte[] bytes = adapter.getReceiveQueue().take();
							Map<String, Object> dataContainer = adapter.getMessageHandler().unpack(bytes);
							if (dataContainer == null) {
								continue;
							}
							String respType = StringUtils.trimToNull((String) dataContainer.get("YHYDLX"));
							if ("FAIL".equalsIgnoreCase(respType)) {
								logger.error("解包失败:{}", new Object[] { dataContainer });
							}else{
								//具体业务处理代码
								
								 * 1.代付结果
								 * 2.代付查询结果
								 
								if(Constant.REALTIME_INSTEADPAY_QUERY.equals(dataContainer.get("messagecode").toString())){
									RealTimeQueryResultBean realTimePayResultBean = (RealTimeQueryResultBean) dataContainer.get("result");
									txnsCmbcInstPayLogDAO.updateInsteadPayResult(BeanCopyUtil.copyBean(CMBCRealTimeInsteadPayResultBean.class, realTimePayResultBean));
									
									break;
								}
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
		}
		executors.shutdown();*/
	}

}
