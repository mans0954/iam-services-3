package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.springframework.stereotype.Component;

@Component
public class MetadataTemplateTypeDozerConverter extends AbstractDozerEntityConverter<MetadataTemplateType, MetadataTemplateTypeEntity> {

	@Override
	public MetadataTemplateTypeEntity convertEntity(
			MetadataTemplateTypeEntity entity, boolean isDeep) {
		  return convert(entity, isDeep, MetadataTemplateTypeEntity.class);
	}

	@Override
	public MetadataTemplateType convertDTO(MetadataTemplateType entity,
			boolean isDeep) {
		return convert(entity, isDeep, MetadataTemplateType.class);
	}

	@Override
	public MetadataTemplateTypeEntity convertToEntity(
			MetadataTemplateType entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataTemplateTypeEntity.class);
	}

	@Override
	public MetadataTemplateType convertToDTO(MetadataTemplateTypeEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataTemplateType.class);
	}

	@Override
	public List<MetadataTemplateTypeEntity> convertToEntityList(
			List<MetadataTemplateType> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, MetadataTemplateTypeEntity.class);
	}

	@Override
	public List<MetadataTemplateType> convertToDTOList(
			List<MetadataTemplateTypeEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, MetadataTemplateType.class);
	}

}
