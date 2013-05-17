package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.springframework.stereotype.Component;

@Component("attributeMapDozerConverter")
public class AttributeMapDozerConverter extends
		AbstractDozerEntityConverter<AttributeMap, AttributeMapEntity> {
	@Override
	public AttributeMapEntity convertEntity(AttributeMapEntity userEntity,
			boolean isDeep) {
		return convert(userEntity, isDeep, AttributeMapEntity.class);
	}

	@Override
	public AttributeMap convertDTO(AttributeMap entity, boolean isDeep) {
		return convert(entity, isDeep, AttributeMap.class);
	}

	@Override
	public AttributeMapEntity convertToEntity(AttributeMap entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AttributeMapEntity.class);
	}

	@Override
	public AttributeMap convertToDTO(AttributeMapEntity userEntity,
			boolean isDeep) {
		return convertToCrossEntity(userEntity, isDeep, AttributeMap.class);
	}

	@Override
	public List<AttributeMapEntity> convertToEntityList(
			List<AttributeMap> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AttributeMapEntity.class);
	}

	@Override
	public List<AttributeMap> convertToDTOList(List<AttributeMapEntity> list,
			boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AttributeMap.class);
	}
}
