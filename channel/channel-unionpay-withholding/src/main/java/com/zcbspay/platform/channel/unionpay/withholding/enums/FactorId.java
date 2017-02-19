package com.zcbspay.platform.channel.unionpay.withholding.enums;

public enum FactorId {

	CARDNUM_NAME("0101", "卡号+姓名"),
	CARDNUM_ID("0102", "卡号+身份证号"),
	CARDNUM_PHO("0103", "卡号+手机号"),
	CARDNUM_NAME_ID("0104", "卡号+姓名+身份证"),
	CARDNUM_NAME_PHO("0105", "卡号+姓名+手机号"),
	CARDNUM_ID_PHO("0106", "卡号+身份证号+手机号"),
	CARDNUM_NAME_ID_PHO("0107", "卡号+姓名+身份证号+手机号"),
	;

	private String value;
	private final String displayName;

	FactorId(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static FactorId parseOf(String value) {

		for (FactorId item : values())
			if (item.getValue().equals(value))
				return item;

		return null;
	}
}
