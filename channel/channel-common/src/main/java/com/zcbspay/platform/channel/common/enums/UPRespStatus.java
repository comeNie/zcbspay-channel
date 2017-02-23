package com.zcbspay.platform.channel.common.enums;

/**
 * 银联通道应答状态
 * @author AlanMa
 *
 */
public enum UPRespStatus {

	SUCESS("S", "成功"), 
	UNKNOWN("U", "未知"), 
	FAILURE("F", "失败"),
	;

	private String value;
	private final String displayName;

	UPRespStatus(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static UPRespStatus parseOf(String value) {

		for (UPRespStatus item : values())
			if (item.getValue().equals(value))
				return item;

		return null;
	}

}
