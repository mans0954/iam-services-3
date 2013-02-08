package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.am.srvc.dto.AuthLevel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authLevelDozerConverter")
public class AuthLevelDozerConverter extends AbstractDozerEntityConverter<AuthLevel, AuthLevelEntity> {
    @Override
    public AuthLevelEntity convertEntity(AuthLevelEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthLevelEntity.class);
    }

    @Override
    public AuthLevel convertDTO(AuthLevel entity, boolean isDeep) {
        return convert(entity, isDeep, AuthLevel.class);
    }

    @Override
    public AuthLevelEntity convertToEntity(AuthLevel entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthLevelEntity.class);
    }

    @Override
    public AuthLevel convertToDTO(AuthLevelEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthLevel.class);
    }

    @Override
    public List<AuthLevelEntity> convertToEntityList(List<AuthLevel> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthLevelEntity.class);
    }

    @Override
    public List<AuthLevel> convertToDTOList(List<AuthLevelEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthLevel.class);
    }
}
