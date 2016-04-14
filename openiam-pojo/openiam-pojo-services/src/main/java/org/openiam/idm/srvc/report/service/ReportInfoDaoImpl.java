package org.openiam.idm.srvc.report.service;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

/**
 * DAO service for ReportInfoEntity implementation
 *
 * @author vitaly.yakunin
 */
@Repository
public class ReportInfoDaoImpl extends BaseDaoImpl<ReportInfoEntity, String> implements ReportInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(ReportInfoDaoImpl.class);

    @Override
    public ReportInfoEntity findByName(String name) {
        Criteria criteria = getSession().createCriteria(ReportInfoEntity.class).add(Restrictions.eq("reportName", name));
        return (ReportInfoEntity) criteria.uniqueResult();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportInfoEntity> findAllReports( int startAt, int size) {
		if(log.isDebugEnabled()) {
			log.debug("finding all Report instances");
		}
		try {

			Criteria cr = this.getCriteria()
					.addOrder(Order.asc("reportId"));
			if (startAt > -1) {
	            cr.setFirstResult(startAt);
	        }

	        if (size > -1) {
	            cr.setMaxResults(size);
	        }
			return (List<ReportInfoEntity>) cr.list();
		} catch (HibernateException re) {
			log.error("find all Reports failed", re);
			throw re;
		}
	}

}
