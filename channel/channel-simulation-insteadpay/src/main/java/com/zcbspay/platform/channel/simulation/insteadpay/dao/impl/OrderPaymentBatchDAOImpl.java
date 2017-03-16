package com.zcbspay.platform.channel.simulation.insteadpay.dao.impl;

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
import com.zcbspay.platform.channel.simulation.insteadpay.dao.OrderPaymentBatchDAO;
import com.zcbspay.platform.channel.simulation.insteadpay.pojo.OrderPaymentBatchDO;
import com.zcbspay.platform.channel.utils.DateUtil;
@Repository
public class OrderPaymentBatchDAOImpl extends HibernateBaseDAOImpl<OrderPaymentBatchDO> implements
		OrderPaymentBatchDAO {
	private static final Logger log = LoggerFactory.getLogger(OrderPaymentBatchDAOImpl.class);
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public OrderPaymentBatchDO getPaymentBatchOrderByTn(String tn) {
		Criteria criteria = getSession().createCriteria(OrderPaymentBatchDO.class);
		criteria.add(Restrictions.eq("tn", tn));
		OrderPaymentBatchDO uniqueResult = (OrderPaymentBatchDO) criteria.uniqueResult();
		return uniqueResult;
	}
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateOrderToSuccess(String tn) {
		// TODO Auto-generated method stub
		String hql = "update OrderPaymentBatchDO set status = ? , orderfinshtime = ? where tn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, DateUtil.getCurrentDateTime());
		query.setString(2, tn);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccess() effect rows:"+rows);
	}
	
}
