package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.searchbeans.MetadataTemplateTypeSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class MetadataTemplateTypeSearchBeanConverter implements SearchBeanConverter<MetadataTemplateTypeEntity, MetadataTemplateTypeSearchBean> {

	@Override
	public MetadataTemplateTypeEntity convert(final MetadataTemplateTypeSearchBean searchBean) {
		final MetadataTemplateTypeEntity entity = new MetadataTemplateTypeEntity();
		if(searchBean != null) {
			entity.setId(StringUtils.trimToNull(searchBean.getKey()));
			entity.setName(StringUtils.trimToNull(searchBean.getName()));
		}
		return entity;
	}

}
