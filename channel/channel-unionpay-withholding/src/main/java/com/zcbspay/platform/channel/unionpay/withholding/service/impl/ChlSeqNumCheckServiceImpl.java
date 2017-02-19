package com.zcbspay.platform.channel.unionpay.withholding.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zcbspay.platform.channel.common.bean.TradeBeanUP;
import com.zcbspay.platform.channel.common.enums.UPRespStatus;
import com.zcbspay.platform.channel.unionpay.withholding.dao.TxnsUnionPayDao;
import com.zcbspay.platform.channel.unionpay.withholding.enums.ErrorCodeUP;
import com.zcbspay.platform.channel.unionpay.withholding.exception.UnionPayException;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;
import com.zcbspay.platform.channel.unionpay.withholding.service.ChlSeqNumCheckService;

@Service("chlSeqNumCheckService")
public class ChlSeqNumCheckServiceImpl implements ChlSeqNumCheckService {

    private static final Logger log = LoggerFactory.getLogger(ChlSeqNumCheckServiceImpl.class);

    @Autowired
    private TxnsUnionPayDao txnsUnionPayDao;

    @Override
    public void isRepeatRequest(String txnseqno) throws UnionPayException {
        PojoTxnsLogUp result = txnsUnionPayDao.findByTxnseqnoAndStatus(txnseqno, UPRespStatus.FAILURE.getValue(), false);
        if (result != null) {
            log.error("repeat withholding request !!!");
            throw new UnionPayException(ErrorCodeUP.WITHHOLD_REPEAT.getValue());
        }
        log.info("pass repeat withholding request check");
    }

    @Override
    public void isOverlimit(TradeBeanUP tradeBean) throws UnionPayException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isSysthronizeStatus(String orderId) {
        PojoTxnsLogUp result = txnsUnionPayDao.findByOrderIdAndStatus(orderId, UPRespStatus.UNKNOWN.getValue(), true);
        return result == null ? false : true;
    }
}
