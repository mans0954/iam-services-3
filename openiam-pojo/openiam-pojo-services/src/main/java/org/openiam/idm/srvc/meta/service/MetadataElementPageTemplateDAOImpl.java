package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("metadataElementPageTemplateDAO")
public class MetadataElementPageTemplateDAOImpl extends BaseDaoImpl<MetadataElementPageTemplateEntity, String> implements MetadataElementPageTemplateDAO {

	
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof MetadataElementPageTemplateSearchBean) {
			final MetadataElementPageTemplateSearchBean sb = (MetadataElementPageTemplateSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
				
				if(CollectionUtils.isNotEmpty(sb.getPatternIds())) {
					criteria.createAlias("uriPatterns", "patterns").add( Restrictions.in("patterns.id", sb.getPatternIds()));
				}
				
				if(StringUtils.isNotEmpty(sb.getResourceId())) {
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

	@Override
	public List<MetadataElementPageTemplateEntity> getByResourceId(final String resourceId) {
		final MetadataElementPageTemplateSearchBean sb = new MetadataElementPageTemplateSearchBean();
		sb.setResourceId(resourceId);
		return getByExample(sb);
	}

}
