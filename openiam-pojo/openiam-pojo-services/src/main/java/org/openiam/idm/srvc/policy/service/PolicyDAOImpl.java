package org.openiam.idm.srvc.policy.service;

// Generated Mar 22, 2009 12:07:00 AM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.mule.util.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PolicyConstants;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for the Policy. @
 */
@Repository("policyDAO")
public class PolicyDAOImpl extends BaseDaoImpl<PolicyEntity, String> implements
        PolicyDAO {
    @SuppressWarnings("unchecked")
    @Override
    public List<PolicyEntity> findAllPolicies(String policyDefId, int startAt, int size) {
        log.debug("finding all PolicyEntities instances");
        try {

            Criteria cr = this.getCriteria()
                    .add(Restrictions.eq("policyDefId", policyDefId))
                    .addOrder(Order.asc("policyId"));
            if (startAt > -1) {
                cr.setFirstResult(startAt);
            }

            if (size > -1) {
                cr.setMaxResults(size);
            }
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
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        if (searchBean instanceof PolicySearchBean) {
            PolicySearchBean sb = (PolicySearchBean) searchBean;
            if (StringUtils.isNotBlank(sb.getPolicyDefId())) {
                criteria.add(Restrictions.eq("policyDefId", sb.getPolicyDefId()));
            }

            if (CollectionUtils.isNotEmpty(sb.getAttributes())) {
                criteria.createAlias("policyAttributes", "pa")
                        .createAlias("pa.defaultParametr", "dp");
                for (final Tuple<String, String> attribute : sb.getAttributes()) {
                    if (org.apache.commons.lang.StringUtils.isNotBlank(attribute.getKey()) && org.apache.commons.lang.StringUtils.isNotBlank(attribute.getValue())) {
                        criteria.add(Restrictions.eq("dp.name", attribute.getKey()))
                                .add(Restrictions.eq("pa.value1", attribute.getValue()));
                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(attribute.getKey())) {
                        criteria.add(Restrictions.eq("dp.name", attribute.getKey()));
                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(attribute.getValue())) {
                        criteria.add(Restrictions.eq("pa.value1", attribute.getValue()));
                    }
                }
            }

            if (StringUtils.isNotBlank(sb.getName())) {
                String name = sb.getName();
                MatchMode matchMode = null;
                if (org.apache.commons.lang.StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }
                criteria.add(Restrictions.ilike("name", name, matchMode));
            }
        }
        return criteria;
    }


    @Override
    protected String getPKfieldName() {
        return "policyId";
    }

}
