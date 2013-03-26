package org.openiam.idm.srvc.searchbean.converter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.springframework.stereotype.Component;

@Component("metadataElementSearchBeanConverter")
public class MetadataElementSearchBeanConverter implements SearchBeanConverter<MetadataElementEntity, MetadataElementSearchBean> {

	@Override
	public MetadataElementEntity convert(final MetadataElementSearchBean searchBean) {
		final MetadataElementEntity entity = new MetadataElementEntity();
		entity.setAttributeName(StringUtils.trimToNull(searchBean.getAttributeName()));
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		if(StringUtils.isNotBlank(searchBean.getMetadataTypeId())) {
			final MetadataTypeEntity typeEntity = new MetadataTypeEntity();
			typeEntity.setMetadataTypeId(StringUtils.trimToNull(searchBean.getMetadataTypeId()));
			entity.setMetadataType(typeEntity);
		}
		if(StringUtils.isNotBlank(searchBean.getTemplateId())) {
			final MetadataElementPageTemplateEntity template = new MetadataElementPageTemplateEntity();
			template.setId(StringUtils.trimToNull(searchBean.getTemplateId()));
			final Set<MetadataElementPageTemplateXrefEntity> xrefSet = new HashSet<MetadataElementPageTemplateXrefEntity>();
			final MetadataElementPageTemplateXrefEntity xref = new MetadataElementPageTemplateXrefEntity();
			xref.setTemplate(template);
			xrefSet.add(xref);
			entity.setTemplateSet(xrefSet);
		}
		return entity;
	}

}
