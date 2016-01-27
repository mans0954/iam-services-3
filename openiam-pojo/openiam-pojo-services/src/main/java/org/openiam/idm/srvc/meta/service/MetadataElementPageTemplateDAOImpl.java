package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
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
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				if (StringUtils.isNotEmpty(sb.getName())) {
	                String name = sb.getName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(name, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    name = name.substring(1);
	                }
	                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
	                    name = name.substring(0, name.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotEmpty(name)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("name", name, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("name", name));
	                    }
	                }
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
