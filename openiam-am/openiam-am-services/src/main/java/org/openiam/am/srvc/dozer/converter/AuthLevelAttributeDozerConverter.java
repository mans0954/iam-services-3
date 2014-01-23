package org.openiam.am.srvc.dozer.converter;

import java.util.List;

import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.springframework.stereotype.Component;

@Component
public class AuthLevelAttributeDozerConverter extends AbstractDozerEntityConverter<AuthLevelAttribute, AuthLevelAttributeEntity> {

	@Override
	public AuthLevelAttributeEntity convertEntity(final AuthLevelAttributeEntity entity, final boolean isDeep) {
		return convert(entity, isDeep, AuthLevelAttributeEntity.class);
	}

	@Override
	public AuthLevelAttribute convertDTO(final AuthLevelAttribute entity, final boolean isDeep) {
		return convert(entity, isDeep, AuthLevelAttribute.class);
	}

	@Override
	public AuthLevelAttributeEntity convertToEntity(final AuthLevelAttribute entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuthLevelAttributeEntity.class);
	}

	@Override
	public AuthLevelAttribute convertToDTO(final AuthLevelAttributeEntity entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuthLevelAttribute.class);
	}

	@Override
	public List<AuthLevelAttributeEntity> convertToEntityList(final List<AuthLevelAttribute> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuthLevelAttributeEntity.class);
	}

	@Override
	public List<AuthLevelAttribute> convertToDTOList(final List<AuthLevelAttributeEntity> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuthLevelAttribute.class);
	}

}
