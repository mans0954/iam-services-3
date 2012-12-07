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

    @SuppressWarnings("unchecked")
    public List<PolicyDefParamEntity> findPolicyDefParamByGroup(String defId,
            String group) {
        try {

            Criteria cr = this.getCriteria().add(
                    Restrictions.and(Restrictions.eq("paramGroup", group),
                            Restrictions.eq("policyDefId", defId)));
            List<PolicyDefParamEntity> result = (List<PolicyDefParamEntity>) cr
                    .list();
            if (result == null || result.size() == 0)
                return null;
            return result;
        } catch (HibernateException re) {
            log.error("findPolicyDefParamByGroup failed", re);
            throw re;
        }
    }

    @Override
    protected String getPKfieldName() {
        return "defParamId";
    }

}
