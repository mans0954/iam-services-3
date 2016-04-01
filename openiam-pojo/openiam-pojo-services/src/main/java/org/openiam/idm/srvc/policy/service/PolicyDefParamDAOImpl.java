package org.openiam.idm.srvc.policy.service;

// Generated Mar 7, 2009 11:47:13 AM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.springframework.stereotype.Repository;

/**
 * Data access implementation for PolicyDefinitions
 * 
 */
@Repository("policyDefParamDAO")
public class PolicyDefParamDAOImpl extends
		BaseDaoImpl<PolicyDefParamEntity, String> implements PolicyDefParamDAO {

	@Override
	protected boolean cachable() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openiam.idm.srvc.policy.service.PolicyDefParamDAO#
	 * findPolicyDefParamByGroup(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyDefParamEntity> findPolicyDefParamByGroup(String defId,
			String group) {
		Criteria cr = this.getCriteria();
		if (group != null) {
			cr.add(Restrictions.and(Restrictions.eq("paramGroup", group), Restrictions.eq("policyDef.id", defId)));
		} else {
			cr.add(Restrictions.eq("policyDef.id", defId));
		}
		List<PolicyDefParamEntity> result = (List<PolicyDefParamEntity>) cr.list();
		if (result == null || result.size() == 0)
			return null;
		return result;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
