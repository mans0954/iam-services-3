package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.springframework.stereotype.Component;

@Component
public class AccessRightDozerConverter extends AbstractDozerEntityConverter<AccessRight, AccessRightEntity> {

	@Override
	public AccessRightEntity convertEntity(AccessRightEntity entity,
			boolean isDeep) {
		 return convert(entity, isDeep, AccessRightEntity.class);
	}

	@Override
	public AccessRight convertDTO(AccessRight entity, boolean isDeep) {
		return convert(entity, isDeep, AccessRight.class);
	}

	@Override
	public AccessRightEntity convertToEntity(AccessRight entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AccessRightEntity.class);
	}

	@Override
	public AccessRight convertToDTO(AccessRightEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, AccessRight.class);
	}

	@Override
	public List<AccessRightEntity> convertToEntityList(List<AccessRight> list,
			boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AccessRightEntity.class);
	}

	@Override
	public List<AccessRight> convertToDTOList(List<AccessRightEntity> list,
			boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, AccessRight.class);
	}

	@Override
	public Set<AccessRightEntity> convertToEntitySet(Set<AccessRight> set,
			boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, AccessRightEntity.class);
	}

	@Override
	public Set<AccessRight> convertToDTOSet(Set<AccessRightEntity> set,
			boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, AccessRight.class);
	}

}
