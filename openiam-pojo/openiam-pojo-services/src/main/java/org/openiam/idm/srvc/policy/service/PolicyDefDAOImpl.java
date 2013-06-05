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

    public List<String> findAllPolicyTypes() {
        try {

            Criteria cr = this.getCriteria()
                    .setProjection(Projections.projectionList()

                    .add(Projections.property("name")))
                    .addOrder(Order.asc("name"));
            return (List<String>) cr.list();
        } catch (HibernateException re) {
            log.error("findAllPolicyTypes failed", re);
            throw re;
        }
    }

    public List<PolicyDefEntity> findAllPolicyDef() {
        try {
            Criteria cr = this.getCriteria().addOrder(Order.asc("policyDefId"));
            List<PolicyDefEntity> result = (List<PolicyDefEntity>) cr.list();
            for (PolicyDefEntity p : result) {
                Hibernate.initialize(p.getPolicies());
                Hibernate.initialize(p.getPolicyDefParams());
            }
            return result;
        } catch (HibernateException re) {
            log.error("findAllPolicyDef failed", re);
            throw re;
        }
    }

    @Override
    protected String getPKfieldName() {
        return "policyDefId";
    }

}
