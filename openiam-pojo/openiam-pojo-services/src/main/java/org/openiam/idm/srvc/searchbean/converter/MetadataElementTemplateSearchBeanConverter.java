package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.springframework.stereotype.Component;

@Component("metadataElementPageTemplateSearchBeanConverter")
public class MetadataElementTemplateSearchBeanConverter implements SearchBeanConverter<MetadataElementPageTemplateEntity, MetadataElementPageTemplateSearchBean> {

	@Override
	public MetadataElementPageTemplateEntity convert(final MetadataElementPageTemplateSearchBean searchBean) {
		final MetadataElementPageTemplateEntity entity = new MetadataElementPageTemplateEntity();
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		entity.setName(StringUtils.trimToNull(searchBean.getName()));
		if(CollectionUtils.isNotEmpty(searchBean.getPatternIds())) {
			for(final String patternId : searchBean.getPatternIds()) {
				if(StringUtils.isNotBlank(patternId)) {
					final URIPatternEntity pattern = new URIPatternEntity();
					pattern.setId(patternId);
					entity.addURIPattern(pattern);
				}
			}
		}
		return entity;
	}

}
