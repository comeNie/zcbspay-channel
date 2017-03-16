package com.zcbspay.platform.channel.simulation.withholding.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zcbspay.platform.channel.common.dao.impl.HibernateBaseDAOImpl;
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectDetaDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectDetaDO;

@Repository
public class OrderCollectDetaDAOImpl extends HibernateBaseDAOImpl<OrderCollectDetaDO> implements OrderCollectDetaDAO {
	private static final Logger log = LoggerFactory.getLogger(OrderCollectDetaDAOImpl.class);
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<OrderCollectDetaDO> getDetaListByBatchtid(Long batchId){
		Criteria criteria = getSession().createCriteria(OrderCollectDetaDO.class);
		criteria.add(Restrictions.eq("batchtid", batchId));
		return criteria.list();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateOrderToFail(String txnseqno,String rspCode,String rspMsg) {
		String hql = "update OrderCollectDetaDO set status = ? , respcode = ? , respmsg = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "03");
		query.setString(1, rspCode);
		query.setString(2, rspMsg);
		query.setString(3, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToFail() effect rows:"+rows);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public void updateOrderToSuccess(String txnseqno,String rspCode,String rspMsg) {
		String hql = "update OrderCollectDetaDO set status = ? , respcode = ? , respmsg = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, rspCode);
		query.setString(2, rspMsg);
		query.setString(3, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccess() effect rows:"+rows);
	}

}
