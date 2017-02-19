package com.zcbspay.platform.channel.unionpay.withholding.enums;

public enum ErrorCodeUP {

	WITHHOLD_REPEAT("TUP001", "代扣请求重复"),
	SIGN_FAIL("TUP002", "调用银联接口签名失败"),
	XML2JB_FAIL("TUP003", "XML转JavaBean失败"),
	INTERRUPT_EXP("TUP004", "线程异常中断"),
	;

	private String value;
	private final String displayName;

	ErrorCodeUP(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static ErrorCodeUP parseOf(String value) {

		for (ErrorCodeUP item : values())
			if (item.getValue().equals(value))
				return item;

		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(ErrorCodeUP.WITHHOLD_REPEAT.getDisplayName());
		System.out.println(ErrorCodeUP.WITHHOLD_REPEAT.getValue());
		System.out.println(parseOf(ErrorCodeUP.WITHHOLD_REPEAT.getValue()).getDisplayName());
	}
}
