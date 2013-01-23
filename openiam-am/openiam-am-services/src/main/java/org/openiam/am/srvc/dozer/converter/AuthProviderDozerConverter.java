package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dto.AuthProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authProviderDozerConverter")
public class AuthProviderDozerConverter extends AbstractDozerEntityConverter<AuthProvider, AuthProviderEntity> {
    @Override
    public AuthProviderEntity convertEntity(AuthProviderEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthProviderEntity.class);
    }

    @Override
    public AuthProvider convertDTO(AuthProvider entity, boolean isDeep) {
        return convert(entity, isDeep, AuthProvider.class);
    }

    @Override
    public AuthProviderEntity convertToEntity(AuthProvider entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthProviderEntity.class);
    }

    @Override
    public AuthProvider convertToDTO(AuthProviderEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthProvider.class);
    }

    @Override
    public List<AuthProviderEntity> convertToEntityList(List<AuthProvider> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProviderEntity.class);
    }

    @Override
    public List<AuthProvider> convertToDTOList(List<AuthProviderEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthProvider.class);
    }
}
