package com.zcbspay.platform.channel.simulation.withholding.dao.impl;

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
import com.zcbspay.platform.channel.simulation.withholding.dao.OrderCollectBatchDAO;
import com.zcbspay.platform.channel.simulation.withholding.pojo.OrderCollectBatchDO;
import com.zcbspay.platform.channel.utils.DateUtil;

@Repository
public class OrderCollectBatchDAOImpl extends HibernateBaseDAOImpl<OrderCollectBatchDO> implements OrderCollectBatchDAO {
	private static final Logger log = LoggerFactory.getLogger(OrderCollectBatchDAOImpl.class);
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public OrderCollectBatchDO getCollectBatchOrderByTn(String tn) {
		Criteria criteria = getSession().createCriteria(OrderCollectBatchDO.class);
		criteria.add(Restrictions.eq("tn", tn));
		OrderCollectBatchDO uniqueResult = (OrderCollectBatchDO) criteria.uniqueResult();
		return uniqueResult;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateOrderToSuccess(String tn) {
		// TODO Auto-generated method stub
		String hql = "update OrderCollectBatchDO set status = ? , orderfinshtime = ? where tn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, DateUtil.getCurrentDateTime());
		query.setString(2, tn);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccess() effect rows:"+rows);
	}

	

}
