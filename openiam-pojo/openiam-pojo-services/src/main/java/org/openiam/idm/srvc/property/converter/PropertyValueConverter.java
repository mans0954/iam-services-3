package org.openiam.idm.srvc.property.converter;

import java.util.List;
import java.util.Set;

import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;
import org.springframework.stereotype.Component;

@Component
public class PropertyValueConverter extends AbstractDozerEntityConverter<PropertyValue, PropertyValueEntity> {

	@Override
	public PropertyValueEntity convertEntity(PropertyValueEntity entity, boolean isDeep) {
		return convert(entity, isDeep, PropertyValueEntity.class);
	}

	@Override
	public PropertyValue convertDTO(PropertyValue entity, boolean isDeep) {
		return convert(entity, isDeep, PropertyValue.class);
	}

	@Override
	public PropertyValueEntity convertToEntity(PropertyValue entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, PropertyValueEntity.class);
	}

	@Override
	public PropertyValue convertToDTO(PropertyValueEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, PropertyValue.class);
	}

	@Override
	public List<PropertyValueEntity> convertToEntityList(List<PropertyValue> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, PropertyValueEntity.class);
	}

	@Override
	public List<PropertyValue> convertToDTOList(List<PropertyValueEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, PropertyValue.class);
	}

    @Override
    public Set<PropertyValueEntity> convertToEntitySet(Set<PropertyValue> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, PropertyValueEntity.class);
    }

    @Override
    public Set<PropertyValue> convertToDTOSet(Set<PropertyValueEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, PropertyValue.class);
    }
}
