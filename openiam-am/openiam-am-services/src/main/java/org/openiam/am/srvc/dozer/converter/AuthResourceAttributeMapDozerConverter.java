package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authResourceAttributeMapDozerConverter")
public class AuthResourceAttributeMapDozerConverter extends
        AbstractDozerEntityConverter<AuthResourceAttributeMap, AuthResourceAttributeMapEntity> {
    @Override
    public AuthResourceAttributeMapEntity convertEntity(AuthResourceAttributeMapEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, AuthResourceAttributeMapEntity.class);
    }

    @Override
    public AuthResourceAttributeMap convertDTO(AuthResourceAttributeMap entity, boolean isDeep) {
        return convert(entity, isDeep, AuthResourceAttributeMap.class);
    }

    @Override
    public AuthResourceAttributeMapEntity convertToEntity(AuthResourceAttributeMap entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, AuthResourceAttributeMapEntity.class);
    }

    @Override
    public AuthResourceAttributeMap convertToDTO(AuthResourceAttributeMapEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, AuthResourceAttributeMap.class);
    }

    @Override
    public List<AuthResourceAttributeMapEntity> convertToEntityList(List<AuthResourceAttributeMap> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthResourceAttributeMapEntity.class);
    }

    @Override
    public List<AuthResourceAttributeMap> convertToDTOList(List<AuthResourceAttributeMapEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, AuthResourceAttributeMap.class);
    }
}
