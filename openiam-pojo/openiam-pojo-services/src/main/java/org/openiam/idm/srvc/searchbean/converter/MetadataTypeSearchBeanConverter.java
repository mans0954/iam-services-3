package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.MetadataElementPageTemplateSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.springframework.stereotype.Component;

@Component("metadataTypeSearchBeanConverter")
public class MetadataTypeSearchBeanConverter implements SearchBeanConverter<MetadataTypeEntity, MetadataTypeSearchBean> {

	@Override
	public MetadataTypeEntity convert(final MetadataTypeSearchBean searchBean) {
		final MetadataTypeEntity entity = new MetadataTypeEntity();
		entity.setActive(searchBean.isActive());
		entity.setMetadataTypeId(StringUtils.trimToNull(searchBean.getKey()));
		entity.setSyncManagedSys(searchBean.isSyncManagedSys());
		return entity;
	}

}
