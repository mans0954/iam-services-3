package org.openiam.am.srvc.dozer.converter;

import java.util.List;

import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.springframework.stereotype.Component;

@Component
public class AuthLevelGroupingDozerConverter extends AbstractDozerEntityConverter<AuthLevelGrouping, AuthLevelGroupingEntity> {

	@Override
	public AuthLevelGroupingEntity convertEntity(final AuthLevelGroupingEntity entity, final boolean isDeep) {
		return convert(entity, isDeep, AuthLevelGroupingEntity.class);
	}

	@Override
	public AuthLevelGrouping convertDTO(final AuthLevelGrouping entity, final boolean isDeep) {
		return convert(entity, isDeep, AuthLevelGrouping.class);
	}

	@Override
	public AuthLevelGroupingEntity convertToEntity(final AuthLevelGrouping entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuthLevelGroupingEntity.class);
	}

	@Override
	public AuthLevelGrouping convertToDTO(final AuthLevelGroupingEntity entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AuthLevelGrouping.class);
	}

	@Override
	public List<AuthLevelGroupingEntity> convertToEntityList(final List<AuthLevelGrouping> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuthLevelGroupingEntity.class);
	}

	@Override
	public List<AuthLevelGrouping> convertToDTOList(final List<AuthLevelGroupingEntity> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AuthLevelGrouping.class);
	}

}
