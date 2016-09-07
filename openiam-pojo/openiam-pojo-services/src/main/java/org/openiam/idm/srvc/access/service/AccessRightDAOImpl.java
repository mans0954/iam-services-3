package org.openiam.idm.srvc.access.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AccessRightDAOImpl extends BaseDaoImpl<AccessRightEntity, String> implements AccessRightDAO {

	private static final Log log = LogFactory.getLog(AccessRightDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof AccessRightSearchBean) {
			final AccessRightSearchBean sb = (AccessRightSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
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
