package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.springframework.stereotype.Component;

@Component("organizationDozerConverter")
public class OrganizationDozerConverter extends AbstractDozerEntityConverter<Organization, OrganizationEntity> {

	@Override
	public OrganizationEntity convertEntity(final OrganizationEntity entity, final boolean isDeep) {
		return convert(entity, isDeep, OrganizationEntity.class);
	}

	@Override
	public Organization convertDTO(final Organization entity, final boolean isDeep) {
		return convert(entity, isDeep, Organization.class);
	}

	@Override
	public OrganizationEntity convertToEntity(final Organization entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, OrganizationEntity.class);
	}

	@Override
	public Organization convertToDTO(final OrganizationEntity entity, final boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, Organization.class);
	}

	@Override
	public List<OrganizationEntity> convertToEntityList(final List<Organization> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, OrganizationEntity.class);
	}

	@Override
	public List<Organization> convertToDTOList(final List<OrganizationEntity> list, final boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, Organization.class);
	}

}