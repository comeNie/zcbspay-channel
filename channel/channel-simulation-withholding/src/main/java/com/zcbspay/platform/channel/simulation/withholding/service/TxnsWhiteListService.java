package com.zcbspay.platform.channel.simulation.withholding.service;

import com.zcbspay.platform.channel.simulation.withholding.pojo.PojoTxnsWhiteList;

public interface TxnsWhiteListService{

    /**
     * 保存白名单数据
     * @param whiteList
     */
    public void saveWhiteList(PojoTxnsWhiteList whiteList);
    
    /**
     * 通过卡信息获取白名单数据
     * @param whiteList
     * @return
     */
    public PojoTxnsWhiteList getByCardInfo(PojoTxnsWhiteList whiteList);
}
