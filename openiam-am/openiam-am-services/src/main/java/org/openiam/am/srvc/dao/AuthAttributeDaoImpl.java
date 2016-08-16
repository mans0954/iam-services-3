package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.stereotype.Repository;

@Repository("authAttributeDao")
public class AuthAttributeDaoImpl extends BaseDaoImpl<AuthAttributeEntity, String> implements AuthAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }
    
    

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof AuthAttributeSearchBean) {
			final AuthAttributeSearchBean sb = (AuthAttributeSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				
				if (StringUtils.isNotEmpty(sb.getProviderType())) {
					criteria.add(Restrictions.eq("type.id", sb.getProviderType()));
				}
				
				if (StringUtils.isNotEmpty(sb.getAttributeName())) {
	                String attributeName = sb.getAttributeName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(attributeName, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    attributeName = attributeName.substring(1);
	                }
	                if (StringUtils.isNotEmpty(attributeName) && StringUtils.indexOf(attributeName, "*") == attributeName.length() - 1) {
	                    attributeName = attributeName.substring(0, attributeName.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotEmpty(attributeName)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("name", attributeName, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("name", attributeName));
	                    }
	                }
	            }
			}
		}
		return criteria;
	}
}