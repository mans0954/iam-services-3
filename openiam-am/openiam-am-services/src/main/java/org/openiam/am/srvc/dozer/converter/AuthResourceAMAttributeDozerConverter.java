package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authResourceAMAttributeDozerConverter")
public class AuthResourceAMAttributeDozerConverter extends
        AbstractDozerEntityConverter<AuthResourceAMAttribute, AuthResourceAMAttributeEntity> {
    @Override
    public AuthResourceAMAttributeEntity convertEntity(AuthResourceAMAttributeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthResourceAMAttributeEntity.class);
    }

    @Override
    public AuthResourceAMAttribute convertDTO(AuthResourceAMAttribute entity, boolean isDeep) {
        return convert(entity, isDeep, AuthResourceAMAttribute.class);
    }

    @Override
    public AuthResourceAMAttributeEntity convertToEntity(AuthResourceAMAttribute entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthResourceAMAttributeEntity.class);
    }

    @Override
    public AuthResourceAMAttribute convertToDTO(AuthResourceAMAttributeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthResourceAMAttribute.class);
    }

    @Override
    public List<AuthResourceAMAttributeEntity> convertToEntityList(List<AuthResourceAMAttribute> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthResourceAMAttributeEntity.class);
    }

    @Override
    public List<AuthResourceAMAttribute> convertToDTOList(List<AuthResourceAMAttributeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthResourceAMAttribute.class);
    }
}
