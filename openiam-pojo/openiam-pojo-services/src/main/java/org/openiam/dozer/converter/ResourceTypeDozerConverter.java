package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.springframework.stereotype.Component;

@Component("resourceTypeDozerConverter")
public class ResourceTypeDozerConverter extends AbstractDozerEntityConverter<ResourceType, ResourceTypeEntity> {

	@Override
	public ResourceTypeEntity convertEntity(ResourceTypeEntity entity, boolean isDeep) {
		return convert(entity, isDeep, ResourceTypeEntity.class);
	}

	@Override
	public ResourceType convertDTO(ResourceType entity, boolean isDeep) {
		return convert(entity, isDeep, ResourceType.class);
	}

	@Override
	public ResourceTypeEntity convertToEntity(ResourceType entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ResourceTypeEntity.class);
	}

	@Override
	public ResourceType convertToDTO(ResourceTypeEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ResourceType.class);
	}

	@Override
	public List<ResourceTypeEntity> convertToEntityList(List<ResourceType> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ResourceTypeEntity.class);
	}

	@Override
	public List<ResourceType> convertToDTOList(List<ResourceTypeEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ResourceType.class);
	}

    @Override
    public Set<ResourceTypeEntity> convertToEntitySet(Set<ResourceType> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ResourceTypeEntity.class);
    }

    @Override
    public Set<ResourceType> convertToDTOSet(Set<ResourceTypeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ResourceType.class);
    }
}
