package org.openiam.am.srvc.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
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
			if (CollectionUtils.isNotEmpty(sb.getKeySet())) {
	            criteria.add(Restrictions.eq(getPKfieldName(), sb.getKeySet()));
	        } else {
	            if (StringUtils.isNotEmpty(sb.getProviderId())) {
	                criteria.add(Restrictions.eq("provider.id", sb.getProviderId()));
	            }
	            
	            final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }

	            if (StringUtils.isNotEmpty(sb.getAmAttributeId())) {
	                criteria.add(Restrictions.eq("amAttribute.id", sb.getAmAttributeId()));
	            }

	        }
		}
		return criteria;
	}
}
