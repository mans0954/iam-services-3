package org.openiam.idm.srvc.res.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
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
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				if(StringUtils.isNotBlank(sb.getName())) {
					criteria.add(Restrictions.eq("name", sb.getName()));
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
