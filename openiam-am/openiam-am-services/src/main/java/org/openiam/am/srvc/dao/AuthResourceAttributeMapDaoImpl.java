package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.searchbean.AuthResourceAttributeMapSearchBean;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.stereotype.Repository;

@Repository("authResourceAttributeMapDao")
public class AuthResourceAttributeMapDaoImpl extends BaseDaoImpl<AuthResourceAttributeMapEntity, String> implements AuthResourceAttributeMapDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }
    
    

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof AuthResourceAttributeMapSearchBean) {
			final AuthResourceAttributeMapSearchBean sb = (AuthResourceAttributeMapSearchBean)searchBean;
			if (StringUtils.isNotBlank(sb.getKey())) {
	            criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
	        } else {
	            if (StringUtils.isNotEmpty(sb.getProviderId())) {
	                criteria.add(Restrictions.eq("provider.id", sb.getProviderId()));
	            }

	            if (StringUtils.isNotEmpty(sb.getName())) {
	                String targetAttributeName = sb.getName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(targetAttributeName, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    targetAttributeName = targetAttributeName.substring(1);
	                }
	                if (StringUtils.isNotEmpty(targetAttributeName) && StringUtils.indexOf(targetAttributeName, "*") == targetAttributeName.length() - 1) {
	                    targetAttributeName = targetAttributeName.substring(0, targetAttributeName.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotEmpty(targetAttributeName)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("name", targetAttributeName, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("name", targetAttributeName));
	                    }
	                }
	            }

	            if (StringUtils.isNotEmpty(sb.getAmAttributeId())) {
	                criteria.add(Restrictions.eq("amAttribute.id", sb.getAmAttributeId()));
	            }

	        }
		}
		return criteria;
	}
}
