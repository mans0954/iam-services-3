package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.springframework.stereotype.Component;

@Component("metadataElementPageDozerConverter")
public class MetadataElementTemplateDozerConverter extends AbstractDozerEntityConverter<MetadataElementPageTemplate, MetadataElementPageTemplateEntity> {

	@Override
	public MetadataElementPageTemplateEntity convertEntity(
			MetadataElementPageTemplateEntity entity, boolean isDeep) {
		 return convert(entity, isDeep, MetadataElementPageTemplateEntity.class);
	}

	@Override
	public MetadataElementPageTemplate convertDTO(
			MetadataElementPageTemplate entity, boolean isDeep) {
		return convert(entity, isDeep, MetadataElementPageTemplate.class);
	}

	@Override
	public MetadataElementPageTemplateEntity convertToEntity(
			MetadataElementPageTemplate entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataElementPageTemplateEntity.class);
	}

	@Override
	public MetadataElementPageTemplate convertToDTO(
			MetadataElementPageTemplateEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataElementPageTemplate.class);
	}

	@Override
	public List<MetadataElementPageTemplateEntity> convertToEntityList(
			List<MetadataElementPageTemplate> list, boolean isDeep) {
		 return convertListToCrossEntity(list, isDeep, MetadataElementPageTemplateEntity.class);
	}

	@Override
	public List<MetadataElementPageTemplate> convertToDTOList(
			List<MetadataElementPageTemplateEntity> list, boolean isDeep) {
		 return convertListToCrossEntity(list, isDeep, MetadataElementPageTemplate.class);
	}

}
