package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authProviderAttributeDozerConverter")
public class AuthProviderAttributeDozerConverter extends
        AbstractDozerEntityConverter<AuthProviderAttribute, AuthProviderAttributeEntity> {

    @Override
    public AuthProviderAttributeEntity convertEntity(AuthProviderAttributeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthProviderAttributeEntity.class);
    }

    @Override
    public AuthProviderAttribute convertDTO(AuthProviderAttribute entity, boolean isDeep) {
        return convert(entity, isDeep, AuthProviderAttribute.class);
    }

    @Override
    public AuthProviderAttributeEntity convertToEntity(AuthProviderAttribute entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthProviderAttributeEntity.class);
    }

    @Override
    public AuthProviderAttribute convertToDTO(AuthProviderAttributeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthProviderAttribute.class);
    }

    @Override
    public List<AuthProviderAttributeEntity> convertToEntityList(List<AuthProviderAttribute> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProviderAttributeEntity.class);
    }

    @Override
    public List<AuthProviderAttribute> convertToDTOList(List<AuthProviderAttributeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProviderAttribute.class);
    }
}
