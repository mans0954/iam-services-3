package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTemplateTypeEntityDAOImpl extends BaseDaoImpl<MetadataTemplateTypeEntity, String> implements MetadataTemplateTypeEntityDAO {

	
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof MetadataTemplateTypeSearchBean) {
			final MetadataTemplateTypeSearchBean sb = (MetadataTemplateTypeSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			} else {
				if(StringUtils.isNotBlank(sb.getName())) {
					criteria.add(Restrictions.eq("name", sb.getName()));
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
