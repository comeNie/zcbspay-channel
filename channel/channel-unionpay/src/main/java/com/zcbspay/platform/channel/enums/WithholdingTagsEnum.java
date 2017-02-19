/* 
 * WithholdingTagsEnum.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zcbspay.platform.channel.enums;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午10:19:01
 * @since
 */
public enum WithholdingTagsEnum {
	/**
	 * 代扣类交易
	 */
	WITHHOLDING("TAG_001"),
	/**
     * 银联代扣-查询
     */
    QUERY_TRADE("TAG_004"),
    /**
     * 申请下载对账文件
     */
    APPLY_ACC_CHECK("TAG_009"),
    
	;

	private String code;

	/**
	 * @param code
	 */
	private WithholdingTagsEnum(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	public static WithholdingTagsEnum fromValue(String code) {
		for (WithholdingTagsEnum tagsEnum : values()) {
			if (tagsEnum.getCode().equals(code)) {
				return tagsEnum;
			}
		}
		return null;
	}

}
