package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.springframework.stereotype.Component;

@Component("organizationAttributeDozerConverter")
public class OrganizationAttributeDozerConverter
        extends
        AbstractDozerEntityConverter<OrganizationAttribute, OrganizationAttributeEntity> {

    @Override
    public OrganizationAttributeEntity convertEntity(
            final OrganizationAttributeEntity entity, final boolean isDeep) {
        return convert(entity, isDeep, OrganizationAttributeEntity.class);
    }

    @Override
    public OrganizationAttribute convertDTO(final OrganizationAttribute entity,
            final boolean isDeep) {
        return convert(entity, isDeep, OrganizationAttribute.class);
    }

    @Override
    public OrganizationAttributeEntity convertToEntity(
            final OrganizationAttribute entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                OrganizationAttributeEntity.class);
    }

    @Override
    public OrganizationAttribute convertToDTO(
            final OrganizationAttributeEntity entity, final boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, OrganizationAttribute.class);
    }

    @Override
    public List<OrganizationAttributeEntity> convertToEntityList(
            final List<OrganizationAttribute> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                OrganizationAttributeEntity.class);
    }

    @Override
    public List<OrganizationAttribute> convertToDTOList(
            final List<OrganizationAttributeEntity> list, final boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                OrganizationAttribute.class);
    }

    @Override
    public Set<OrganizationAttributeEntity> convertToEntitySet(
            final Set<OrganizationAttribute> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                OrganizationAttributeEntity.class);
    }

    @Override
    public Set<OrganizationAttribute> convertToDTOSet(
            final Set<OrganizationAttributeEntity> set, final boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep,
                OrganizationAttribute.class);
    }

}
