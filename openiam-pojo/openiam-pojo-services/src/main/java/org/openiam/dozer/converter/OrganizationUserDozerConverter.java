package org.openiam.dozer.converter;

import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("organizationUserDozerConverter")
public class OrganizationUserDozerConverter extends AbstractDozerEntityConverter<OrganizationUserDTO, OrganizationUserEntity> {

	@Override
	public OrganizationUserEntity convertEntity(final OrganizationUserEntity entity, final boolean isDeep) {
		return convert(entity, isDeep, OrganizationUserEntity.class);
	}

	@Override
	public OrganizationUserDTO convertDTO(final OrganizationUserDTO entity, final boolean isDeep) {
		return convert(entity, isDeep, OrganizationUserDTO.class);
	}

	@Override
	public OrganizationUserEntity convertToEntity(final OrganizationUserDTO entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, OrganizationUserEntity.class);
	}

	@Override
	public OrganizationUserDTO convertToDTO(final OrganizationUserEntity entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, OrganizationUserDTO.class);
	}

	@Override
	public List<OrganizationUserEntity> convertToEntityList(final List<OrganizationUserDTO> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, OrganizationUserEntity.class);
	}

	@Override
	public List<OrganizationUserDTO> convertToDTOList(final List<OrganizationUserEntity> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, OrganizationUserDTO.class);
	}

    @Override
    public Set<OrganizationUserEntity> convertToEntitySet(Set<OrganizationUserDTO> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, OrganizationUserEntity.class);
    }

    @Override
    public Set<OrganizationUserDTO> convertToDTOSet(Set<OrganizationUserEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, OrganizationUserDTO.class);
    }
}