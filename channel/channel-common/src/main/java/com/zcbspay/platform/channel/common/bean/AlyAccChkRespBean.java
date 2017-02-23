package com.zcbspay.platform.channel.common.bean;

import java.io.Serializable;



/**
 * 银联5.6　申请下载对账文件应答字段
 * @author AlanMa
 *
 */
public class AlyAccChkRespBean implements Serializable {
	
    private static final long serialVersionUID = 2860976464150161234L;
    /**
	 * 交易流水号
	 */
	private String queryId;
	/**
	 *  发送日期
	 */
	private String queryDt;
	/**
     *  发送时间
     */
    private String sendTm;
	/**
	 * 交易日期
	 */
	private String transDt;
	/**
	 * 文件下载地址
	 */
	private String downloadUrl;
    
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
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    
    public String getSendTm() {
        return sendTm;
    }

    
    public void setSendTm(String sendTm) {
        this.sendTm = sendTm;
    }

    @Override
    public String toString() {
        return "AlyAccChkRespBean [queryId=" + queryId + ", queryDt=" + queryDt + ", sendTm=" + sendTm + ", transDt=" + transDt + ", downloadUrl=" + downloadUrl + "]";
    }
	
}
