package org.openiam.idm.srvc.policy.service;

// Generated Mar 22, 2009 12:07:00 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for the Policy. @
 */
@Repository("policyDAO")
public class PolicyDAOImpl extends BaseDaoImpl<PolicyEntity, String> implements
		PolicyDAO {
	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyEntity> findAllPolicies(String policyDefId) {
		log.debug("finding all PolicyEntities instances");
		try {

			Criteria cr = this.getCriteria()
					.add(Restrictions.eq("policyDefId", policyDefId))
					.addOrder(Order.asc("policyId"));

			return (List<PolicyEntity>) cr.list();
		} catch (HibernateException re) {
			log.error("find all Policies failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyEntity> findPolicyByName(String policyDefId,
			String policyName) {
		log.debug("finding Policy instance by name");
		try {
			Criteria cr = this.getCriteria().add(
					Restrictions.and(
							Restrictions.eq("policyDefId", policyDefId),
							Restrictions.eq("name", policyName)));

			return (List<PolicyEntity>) cr.list();
		} catch (HibernateException re) {
			log.error("find by example failed", re);
			throw re;
		}

	}

	@Override
	protected String getPKfieldName() {
		return "policyId";
	}

}
