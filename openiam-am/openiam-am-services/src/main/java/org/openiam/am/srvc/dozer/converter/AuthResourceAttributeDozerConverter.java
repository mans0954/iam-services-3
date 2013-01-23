package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.am.srvc.dto.AttributeMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authResourceAttributeDozerConverter")
public class AuthResourceAttributeDozerConverter extends
        AbstractDozerEntityConverter<AttributeMap, AuthResourceAttributeEntity> {
    @Override
    public AuthResourceAttributeEntity convertEntity(AuthResourceAttributeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthResourceAttributeEntity.class);
    }

    @Override
    public AttributeMap convertDTO(AttributeMap entity, boolean isDeep) {
        return convert(entity, isDeep, AttributeMap.class);
    }

    @Override
    public AuthResourceAttributeEntity convertToEntity(AttributeMap entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthResourceAttributeEntity.class);
    }

    @Override
    public AttributeMap convertToDTO(AuthResourceAttributeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AttributeMap.class);
    }

    @Override
    public List<AuthResourceAttributeEntity> convertToEntityList(List<AttributeMap> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthResourceAttributeEntity.class);
    }

    @Override
    public List<AttributeMap> convertToDTOList(List<AuthResourceAttributeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AttributeMap.class);
    }
}
