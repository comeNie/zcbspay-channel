package com.zcbspay.platform.channel.unionpay.withholding.withholding.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zcbspay.platform.channel.common.bean.ApplyAccCheckUP;
import com.zcbspay.platform.channel.common.bean.QueryTradeBeanUP;
import com.zcbspay.platform.channel.common.bean.ResultBean;
import com.zcbspay.platform.channel.common.bean.TradeBean;
import com.zcbspay.platform.channel.unionpay.withholding.withholding.service.UnionPayWithholdService;
import com.zcbspay.platform.channel.unionpay.withholding.withholding.service.ZcbsToUnionPayWithholdingService;

@Service("zcbsToUnionPayWithholdingService")
public class ZcbsToUnionPayWithholdingServiceImpl implements ZcbsToUnionPayWithholdingService {

    @Autowired
    private UnionPayWithholdService unionPayWithholdService;

    @Override
    public ResultBean withholding(TradeBean tradeBean) {
        // 代扣
        ResultBean resultBean = unionPayWithholdService.withholding(tradeBean);
        return resultBean;
    }

    @Override
    public ResultBean queryTrade(QueryTradeBeanUP queryTradeBean) {
        // 代扣-查询交易状态
        ResultBean resultBean = unionPayWithholdService.queryTrade(queryTradeBean);
        return resultBean;
    }

    @Override
    public ResultBean applyAccChecking(ApplyAccCheckUP applyAccCheckUP) {
        // 申请下载对账文件
        ResultBean resultBean = unionPayWithholdService.applyAccChecking(applyAccCheckUP);
        return resultBean;
    }

}
