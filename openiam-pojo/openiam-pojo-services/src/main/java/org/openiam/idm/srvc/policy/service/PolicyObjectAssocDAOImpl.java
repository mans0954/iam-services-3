package org.openiam.idm.srvc.policy.service;

// Generated Dec 1, 2009 12:48:52 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class PolicyObjectAssoc.
 * @see org.openiam.idm.srvc.pswd.service.PolicyObjectAssoc
 * @author Hibernate Tools
 */
@Repository("policyObjectAssocDAO")
public class PolicyObjectAssocDAOImpl extends
        BaseDaoImpl<PolicyObjectAssocEntity, String> implements
        PolicyObjectAssocDAO {

    @SuppressWarnings("unchecked")
    @Override
    public PolicyObjectAssocEntity findAssociationByLevel(String level,
            String value) {
        try {
            Criteria cr = this.getCriteria().add(
                    Restrictions.and(
                            Restrictions.eq("associationLevel", level),
                            Restrictions.eq("associationValue", value)));
            List<PolicyObjectAssocEntity> results = (List<PolicyObjectAssocEntity>) cr
                    .list();

            if (results == null || results.isEmpty()) {
                log.info("No policyAssociation objects found.");
                return null;
            } else {
                log.info("PolicAssoc found. Count=" + results.size());
            }
            return results.get(0);
        } catch (HibernateException re) {
            log.error("findAssociationByLevel failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PolicyObjectAssocEntity> findByPolicy(String policyId) {
        try {

            Criteria cr = this.getCriteria().add(
                    Restrictions.eq("policyId", policyId));

            List<PolicyObjectAssocEntity> results = (List<PolicyObjectAssocEntity>) cr
                    .list();
            if (results == null || results.isEmpty()) {
                log.info("No policyAssociation objects found.");
                return null;
            }
            return results;

        } catch (HibernateException re) {
            log.error("findAssociationByLevel failed", re);
            throw re;
        }
    }

    @Override
    protected String getPKfieldName() {
        return "policyObjectId";
    }

}
