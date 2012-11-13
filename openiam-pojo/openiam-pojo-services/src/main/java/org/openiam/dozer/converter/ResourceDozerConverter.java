package org.openiam.dozer.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.springframework.stereotype.Component;

@Component("resourceDozerMapper")
public class ResourceDozerConverter extends AbstractDozerEntityConverter<Resource, ResourceEntity> {

	@Override
	public ResourceEntity convertEntity(ResourceEntity entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
		return mapper.map(entity, entity.getClass());
	}

	@Override
	public Resource convertDTO(Resource entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
		return mapper.map(entity, entity.getClass());
	}

	@Override
	public ResourceEntity convertToEntity(Resource entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		return mapper.map(entity, ResourceEntity.class);
	}

	@Override
	public Resource convertToDTO(ResourceEntity entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		return mapper.map(entity, Resource.class);
	}

	@Override
	public List<ResourceEntity> convertToEntityList(List<Resource> list, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		final List<ResourceEntity> retVal = new LinkedList<ResourceEntity>();
		if(CollectionUtils.isNotEmpty(list)) {
			for(final Resource resource : list) {
				retVal.add(mapper.map(resource, ResourceEntity.class));
			}
		}
		return retVal;
	}

	@Override
	public List<Resource> convertToDTOList(List<ResourceEntity> list, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		final List<Resource> retVal = new LinkedList<Resource>();
		if(CollectionUtils.isNotEmpty(list)) {
			for(final ResourceEntity resource : list) {
				retVal.add(mapper.map(resource, Resource.class));
			}
		}
		return retVal;
	}

}
