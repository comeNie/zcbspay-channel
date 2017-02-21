package com.zcbspay.platform.channel.common.enums;

public enum UPRespInfo {

	TRADE_SUCESS("1000", "处理完成或接收成功"),
	QUERY_SUCESS("0000", "查询登记受理成功"),
	UNKNOWN_DATAP("2000", "初始，系统正在对数据处理"),
	UNKNOWN_TIMEOUT("2001", "超时未知，提交银行处理"),
	UNKNOWN("2002", "未知，交易待查询"),
	;

	private String value;
	private final String displayName;

	UPRespInfo(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static UPRespInfo parseOf(String value) {

		for (UPRespInfo item : values())
			if (item.getValue().equals(value))
				return item;

		return null;
	}

}
