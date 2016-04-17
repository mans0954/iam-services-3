package org.openiam.idm.srvc.policy.service;

// Generated Mar 22, 2009 12:07:00 AM by Hibernate Tools 3.2.2.GA

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
public class PolicyDAOImpl extends BaseDaoImpl<PolicyEntity, String> implements PolicyDAO {

    @Override
    protected boolean cachable() {
        return true;
    }	

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        if (searchBean instanceof PolicySearchBean) {
            PolicySearchBean sb = (PolicySearchBean)searchBean;
            if(StringUtils.isNotBlank(sb.getPolicyDefId())) {
                criteria.add(Restrictions.eq("policyDef.id", sb.getPolicyDefId()));
            }
            if(StringUtils.isNotBlank(sb.getName())) {
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
                if (matchMode != null) {
					criteria.add(Restrictions.ilike("name", name, matchMode));
				} else {
					criteria.add(Restrictions.eq("name", name));
				}
                
                if(StringUtils.isNotBlank(sb.getPolicyDefId())) {
                	criteria.add(Restrictions.eq("policyDef.id", sb.getPolicyDefId()));
                }
            }
        }
        return criteria;
    }

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
