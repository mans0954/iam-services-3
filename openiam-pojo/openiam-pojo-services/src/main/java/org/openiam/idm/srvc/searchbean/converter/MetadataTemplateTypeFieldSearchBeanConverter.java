package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.springframework.stereotype.Component;

@Component
public class MetadataTemplateTypeFieldSearchBeanConverter implements SearchBeanConverter<MetadataTemplateTypeFieldEntity, MetadataTemplateTypeFieldSearchBean> {

	@Override
	public MetadataTemplateTypeFieldEntity convert(
			MetadataTemplateTypeFieldSearchBean searchBean) {
		final MetadataTemplateTypeFieldEntity entity = new MetadataTemplateTypeFieldEntity();
		if(searchBean != null) {
			entity.setId(StringUtils.trimToNull(searchBean.getKey()));
			if(StringUtils.isNotBlank(searchBean.getTemplateId())) {
				final MetadataTemplateTypeEntity templateType = new MetadataTemplateTypeEntity();
				templateType.setId(StringUtils.trimToNull(searchBean.getTemplateId()));
				entity.setTemplateType(templateType);
			}
		}
		return entity;
	}

}
