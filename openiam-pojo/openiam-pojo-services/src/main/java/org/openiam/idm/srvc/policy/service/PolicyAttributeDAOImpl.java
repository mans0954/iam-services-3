package org.openiam.idm.srvc.policy.service;

// Generated Mar 22, 2009 12:07:00 AM by Hibernate Tools 3.2.2.GA

import java.util.Collection;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for the Policy. @
 */
@Repository("policyAttributeDAO")
public class PolicyAttributeDAOImpl extends BaseDaoImpl<PolicyAttributeEntity, String> implements
		PolicyAttributeDAO {

	@Override
	protected String getPKfieldName() {
		// TODO Auto-generated method stub
		return "id";
	}

	
	
}
