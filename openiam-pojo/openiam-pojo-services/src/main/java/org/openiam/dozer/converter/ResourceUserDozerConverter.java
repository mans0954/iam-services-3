package org.openiam.dozer.converter;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceUser;
import org.springframework.stereotype.Component;

@Component("resourceUserDozerConverter")
public class ResourceUserDozerConverter extends AbstractDozerEntityConverter<ResourceUser, ResourceUserEntity> {

	@Override
	public ResourceUserEntity convertEntity(final ResourceUserEntity entity, final boolean isDeep) {
		final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
		return mapper.map(entity, entity.getClass());
	}

	@Override
	public ResourceUser convertDTO(ResourceUser entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
		return mapper.map(entity, entity.getClass());
	}

	@Override
	public ResourceUserEntity convertToEntity(ResourceUser entity,
			boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		return mapper.map(entity, ResourceUserEntity.class);
	}

	@Override
	public ResourceUser convertToDTO(ResourceUserEntity entity, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		return mapper.map(entity, ResourceUser.class);
	}

	@Override
	public List<ResourceUserEntity> convertToEntityList(
			List<ResourceUser> list, boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		final List<ResourceUserEntity> retVal = new LinkedList<ResourceUserEntity>();
		if(CollectionUtils.isNotEmpty(list)) {
			for(final ResourceUser resource : list) {
				retVal.add(mapper.map(resource, ResourceUserEntity.class));
			}
		}
		return retVal;
	}

	@Override
	public List<ResourceUser> convertToDTOList(List<ResourceUserEntity> list,
			boolean isDeep) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		final List<ResourceUser> retVal = new LinkedList<ResourceUser>();
		if(CollectionUtils.isNotEmpty(list)) {
			for(final ResourceUserEntity resource : list) {
				retVal.add(mapper.map(resource, ResourceUser.class));
			}
		}
		return retVal;
	}

}
