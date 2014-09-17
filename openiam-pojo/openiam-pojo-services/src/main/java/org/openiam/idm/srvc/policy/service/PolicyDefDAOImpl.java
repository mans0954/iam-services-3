package org.openiam.idm.srvc.policy.service;

// Generated Mar 7, 2009 11:47:13 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyDefEntity;
import org.springframework.stereotype.Repository;

/**
 * Data access implementation for PolicyDefinitions
 * 
 */
@Repository("policyDefDAO")
public class PolicyDefDAOImpl extends BaseDaoImpl<PolicyDefEntity, String>
        implements PolicyDefDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
