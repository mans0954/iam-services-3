package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authProviderTypeDozerConverter")
public class AuthProviderTypeDozerConverter extends AbstractDozerEntityConverter<AuthProviderType, AuthProviderTypeEntity> {
    @Override
    public AuthProviderTypeEntity convertEntity(AuthProviderTypeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthProviderTypeEntity.class);
    }

    @Override
    public AuthProviderType convertDTO(AuthProviderType entity, boolean isDeep) {
        return convert(entity, isDeep, AuthProviderType.class);
    }

    @Override
    public AuthProviderTypeEntity convertToEntity(AuthProviderType entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthProviderTypeEntity.class);
    }

    @Override
    public AuthProviderType convertToDTO(AuthProviderTypeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthProviderType.class);
    }

    @Override
    public List<AuthProviderTypeEntity> convertToEntityList(List<AuthProviderType> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProviderTypeEntity.class);
    }

    @Override
    public List<AuthProviderType> convertToDTOList(List<AuthProviderTypeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProviderType.class);
    }
}
