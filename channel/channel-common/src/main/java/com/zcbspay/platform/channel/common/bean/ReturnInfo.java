package com.zcbspay.platform.channel.common.bean;

import java.io.Serializable;

public class ReturnInfo implements Serializable {

    private static final long serialVersionUID = 8900216098379299380L;

    private String repCode;
    private String repMsg;

    public ReturnInfo() {
        super();
    }

    public ReturnInfo(String repCode, String repMsg) {
        super();
        this.repCode = repCode;
        this.repMsg = repMsg;
    }

    public String getRepCode() {
        return repCode;
    }

    public void setRepCode(String repCode) {
        this.repCode = repCode;
    }

    public String getRepMsg() {
        return repMsg;
    }

    public void setRepMsg(String repMsg) {
        this.repMsg = repMsg;
    }

    @Override
    public String toString() {
        return "ReturnInfo [repCode=" + repCode + ", repMsg=" + repMsg + "]";
    }

}
