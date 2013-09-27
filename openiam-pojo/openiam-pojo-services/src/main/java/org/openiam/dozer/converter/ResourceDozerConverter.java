package org.openiam.dozer.converter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.springframework.stereotype.Component;

@Component("resourceDozerMapper")
public class ResourceDozerConverter extends AbstractDozerEntityConverter<Resource, ResourceEntity> {

	@Override
	public ResourceEntity convertEntity(ResourceEntity entity, boolean isDeep) {
		return convert(entity, isDeep, ResourceEntity.class);
	}

	@Override
	public Resource convertDTO(Resource entity, boolean isDeep) {
		return convert(entity, isDeep, Resource.class);
	}

	@Override
	public ResourceEntity convertToEntity(Resource entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ResourceEntity.class);
	}

	@Override
	public Resource convertToDTO(ResourceEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, Resource.class);
	}

	@Override
	public List<ResourceEntity> convertToEntityList(List<Resource> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ResourceEntity.class);
	}

	@Override
	public List<Resource> convertToDTOList(List<ResourceEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, Resource.class);
	}

    @Override
    public Set<ResourceEntity> convertToEntitySet(Set<Resource> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, ResourceEntity.class);
    }

    @Override
    public Set<Resource> convertToDTOSet(Set<ResourceEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Resource.class);
    }
}
