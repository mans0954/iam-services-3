package org.openiam.idm.srvc.res.service;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ResourcePropSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.springframework.stereotype.Repository;

@Repository("resourcePropDAO")
public class ResourcePropDAOImpl extends BaseDaoImpl<ResourcePropEntity, String> implements ResourcePropDAO  {

	private static final Log log = LogFactory.getLog(ResourcePropDAOImpl.class);

	
	
	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		final Criteria criteria = super.getCriteria();
		if(searchBean != null && searchBean instanceof ResourcePropSearchBean) {
			final ResourcePropSearchBean sb = (ResourcePropSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
				if(StringUtils.isNotBlank(sb.getResourceId())) {
					criteria.add(Restrictions.eq("resource.id", sb.getResourceId()));
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
