package com.zcbspay.platform.channel.unionpay.withholding.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zcbspay.platform.channel.utils.DateUtil;

/**
 * 定时加载properties文件参数
 * 
 * @author AlanMa
 *
 */
public class ParamsUtil {

	private static final Logger log = LoggerFactory.getLogger(ParamsUtil.class);

	/**
	 * 商户号
	 */
	private String merchantId;
	/**
	 * 证书ID
	 */
	private String certId;
	/**
	 * 证书路径
	 */
	private String certPath;
	/**
	 * 证书密码
	 */
	private String certPasswd;
	/**
	 * wsdl地址
	 */
	private String address;
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 报文字符集
	 */
	private String encoding;
	/**
	 * 签名方式
	 */
	private String signMethod;
	/**
	 * 接收结果通知地址
	 */
	private String backUrl;
	/**
	 * 扣款类型
	 */
	private String dkType;
	/**
	 * 币种
	 */
	private String atType;

	/*
	 * 工具运行参数
	 */
	private boolean canRun;
	private String refresh_interval;
	private static ParamsUtil constant;
	// private String path = "/home/web/trade/unionpay/";
	private String fileName = "unionpay_params.properties";

	public static synchronized ParamsUtil getInstance() {
		if (constant == null) {
			constant = new ParamsUtil();
		}
		return constant;
	}

	private ParamsUtil() {
		refresh();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (canRun) {
					try {
						refresh();
						int interval = NumberUtils.toInt(refresh_interval, 60) * 1000;// 刷新间隔，单位：秒
						log.info("refresh Constant datetime:" + DateUtil.getCurrentDateTime());
						Thread.sleep(interval);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void refresh() {
		try {
			// File file = new File(path+fileName);
			// if (!file.exists()) {
			String path = getClass().getResource("/").getPath();
			// file = null;
			// }
			Properties prop = new Properties();
			InputStream stream = null;
			stream = new BufferedInputStream(new FileInputStream(new File(path + fileName)));
			prop.load(stream);

			merchantId = prop.getProperty("merchantId");
			certId = prop.getProperty("certId");
			certPath = prop.getProperty("certPath");
			certPasswd = prop.getProperty("certPasswd");
			address = prop.getProperty("address");
			version = prop.getProperty("version");
			encoding = prop.getProperty("encoding");
			signMethod = prop.getProperty("signMethod");
			backUrl = prop.getProperty("backUrl");
			dkType = prop.getProperty("dkType");
			atType = prop.getProperty("atType");

			canRun = true;
			refresh_interval = prop.getProperty("refresh_interval");
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public String getCertPasswd() {
		return certPasswd;
	}

	public void setCertPasswd(String certPasswd) {
		this.certPasswd = certPasswd;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

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

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getDkType() {
		return dkType;
	}

	public void setDkType(String dkType) {
		this.dkType = dkType;
	}

	public String getAtType() {
		return atType;
	}

	public void setAtType(String atType) {
		this.atType = atType;
	}

}
