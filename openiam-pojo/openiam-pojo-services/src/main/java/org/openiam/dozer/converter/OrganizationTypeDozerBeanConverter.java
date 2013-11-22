package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.springframework.stereotype.Component;

@Component
public class OrganizationTypeDozerBeanConverter extends AbstractDozerEntityConverter<OrganizationType, OrganizationTypeEntity> {

	@Override
	public OrganizationTypeEntity convertEntity(OrganizationTypeEntity entity, boolean isDeep) {
		return convert(entity, isDeep, OrganizationTypeEntity.class);
	}

	@Override
	public OrganizationType convertDTO(OrganizationType entity, boolean isDeep) {
		return convert(entity, isDeep, OrganizationType.class);
	}

	@Override
	public OrganizationTypeEntity convertToEntity(OrganizationType entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, OrganizationTypeEntity.class);
	}

	@Override
	public OrganizationType convertToDTO(OrganizationTypeEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, OrganizationType.class);
	}

	@Override
	public List<OrganizationTypeEntity> convertToEntityList(List<OrganizationType> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, OrganizationTypeEntity.class);
	}

	@Override
	public List<OrganizationType> convertToDTOList(List<OrganizationTypeEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, OrganizationType.class);
	}

    @Override
    public Set<OrganizationTypeEntity> convertToEntitySet(Set<OrganizationType> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, OrganizationTypeEntity.class);
    }

    @Override
    public Set<OrganizationType> convertToDTOSet(Set<OrganizationTypeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, OrganizationType.class);
    }

}
