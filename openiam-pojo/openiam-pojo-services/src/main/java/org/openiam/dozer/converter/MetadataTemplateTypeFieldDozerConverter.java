package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.springframework.stereotype.Component;

@Component
public class MetadataTemplateTypeFieldDozerConverter extends AbstractDozerEntityConverter<MetadataTemplateTypeField, MetadataTemplateTypeFieldEntity> {

	@Override
	public MetadataTemplateTypeFieldEntity convertEntity(MetadataTemplateTypeFieldEntity entity, boolean isDeep) {
		return convert(entity, isDeep, MetadataTemplateTypeFieldEntity.class);
	}

	@Override
	public MetadataTemplateTypeField convertDTO(
			MetadataTemplateTypeField entity, boolean isDeep) {
		return convert(entity, isDeep, MetadataTemplateTypeField.class);
	}

	@Override
	public MetadataTemplateTypeFieldEntity convertToEntity(
			MetadataTemplateTypeField entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataTemplateTypeFieldEntity.class);
	}

	@Override
	public MetadataTemplateTypeField convertToDTO(MetadataTemplateTypeFieldEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, MetadataTemplateTypeField.class);
	}

	@Override
	public List<MetadataTemplateTypeFieldEntity> convertToEntityList(
			List<MetadataTemplateTypeField> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, MetadataTemplateTypeFieldEntity.class);
	}

	@Override
	public List<MetadataTemplateTypeField> convertToDTOList(
			List<MetadataTemplateTypeFieldEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, MetadataTemplateTypeField.class);
	}

    @Override
    public Set<MetadataTemplateTypeFieldEntity> convertToEntitySet(Set<MetadataTemplateTypeField> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, MetadataTemplateTypeFieldEntity.class);
    }

    @Override
    public Set<MetadataTemplateTypeField> convertToDTOSet(Set<MetadataTemplateTypeFieldEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, MetadataTemplateTypeField.class);
    }
}
