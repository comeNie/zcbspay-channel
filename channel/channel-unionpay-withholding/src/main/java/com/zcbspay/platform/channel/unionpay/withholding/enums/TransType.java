package com.zcbspay.platform.channel.unionpay.withholding.enums;

public enum TransType {

	WITHDRAW("11", "代扣"),
	QUERY("00", "代扣查询"),
	ALYACCCHK("99", "申请下载对账文件"),
	;

	private String value;
	private final String displayName;

	TransType(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static TransType parseOf(String value) {

		for (TransType item : values())
			if (item.getValue().equals(value))
				return item;

		return null;
	}
}
