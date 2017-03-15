package com.zcbspay.platform.channel.simulation.withholding.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zcbspay.platform.channel.common.dao.impl.HibernateBaseDAOImpl;
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectSingleDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectSingleDO;

@Repository
public class OrderCollectSingleDAOImpl extends HibernateBaseDAOImpl<OrderCollectSingleDO> implements OrderCollectSingleDAO {
	private static final Logger log = LoggerFactory.getLogger(OrderCollectSingleDAOImpl.class);
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateOrderToFail(String txnseqno) {
		String hql = "update OrderCollectSingleDO set status = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "03");
		query.setString(1, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToFail() effect rows:"+rows);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public void updateOrderToSuccess(String txnseqno) {
		String hql = "update OrderCollectSingleDO set status = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccess() effect rows:"+rows);
	}

	/**
	 *
	 * @param tn
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateOrderToSuccessByTN(String tn) {
		// TODO Auto-generated method stub
		String hql = "update OrderCollectSingleDO set status = ? where tn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, tn);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccessByTN() effect rows:"+rows);
	}

	

}
