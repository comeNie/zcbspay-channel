package com.zcbspay.platform.channel.unionpay.withholding.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zcbspay.platform.channel.common.dao.impl.HibernateBaseDAOImpl;
import com.zcbspay.platform.channel.unionpay.withholding.dao.TxnsUnionPayDao;
import com.zcbspay.platform.channel.unionpay.withholding.pojo.PojoTxnsLogUp;

@Repository("txnsUnionPayDao")
public class TxnsUnionPayDaoImpl extends HibernateBaseDAOImpl<PojoTxnsLogUp> implements TxnsUnionPayDao {

    private static final Logger log = LoggerFactory.getLogger(TxnsUnionPayDaoImpl.class);

    @Override
    @Transactional(readOnly = true)
    public PojoTxnsLogUp findByTxnseqnoAndStatus(String txnseqno, String status, boolean isEqual) {
        String hql = null;
        if (isEqual) {
            hql = "from PojoTxnsLogUp where txnseqno = ? and tradeStatus =?";
        }
        else {
            hql = "from PojoTxnsLogUp where txnseqno = ? and tradeStatus !=?";
        }
        Query query = getSession().createQuery(hql);
        query.setString(0, txnseqno);
        query.setString(1, status);
        return (PojoTxnsLogUp) query.uniqueResult();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void updateSeqRecStatus(String orderId, String status, String repCode, String repMsg) {
        String hql = "update PojoTxnsLogUp set tradeStatus = ?,respcod=?, respmsg=? where orderId = ? ";
        Session session = getSession();
        Query query = session.createQuery(hql);
        query.setString(0, status);
        query.setString(1, repCode);
        query.setString(2, repMsg);
        query.setString(3, orderId);
        int rows = query.executeUpdate();
        log.info("updateSeqRecStatus() effect rows:" + rows);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void createSeqRecord(PojoTxnsLogUp pojoTxnsLogUp) {
        saveEntity(pojoTxnsLogUp);
    }

    @Override
    public PojoTxnsLogUp findByOrderIdAndStatus(String orderId, String status, boolean isEqual) {
        String hql = null;
        if (isEqual) {
            hql = "from PojoTxnsLogUp where orderId = ? and tradeStatus =?";
        }
        else {
            hql = "from PojoTxnsLogUp where orderId = ? and tradeStatus !=?";
        }
        Query query = getSession().createQuery(hql);
        query.setString(0, orderId);
        query.setString(1, status);
        return (PojoTxnsLogUp) query.uniqueResult();
    }

    @Override
    public PojoTxnsLogUp getCheckRecord(String transType, String status, String queryTmBegin, String queryTmEnd) {
        String hql = "from PojoTxnsLogUp where transType = ? and status =?  and queryTm>=? and queryTm<=?";
        Query query = getSession().createQuery(hql);
        query.setString(0, transType);
        query.setString(1, status);
        query.setString(2, queryTmBegin);
        query.setString(3, queryTmEnd);
        return (PojoTxnsLogUp) query.uniqueResult();
    }

}
