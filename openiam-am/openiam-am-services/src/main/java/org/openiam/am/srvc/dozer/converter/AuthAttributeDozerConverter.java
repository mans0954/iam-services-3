package org.openiam.am.srvc.dozer.converter;

import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.dozer.converter.AbstractDozerEntityConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authAttributeDozerConverter")
public class AuthAttributeDozerConverter extends AbstractDozerEntityConverter<AuthAttribute, AuthAttributeEntity> {
    @Override
    public AuthAttributeEntity convertEntity(AuthAttributeEntity userEntity, boolean isDeep) {
            return convert(userEntity, isDeep, AuthAttributeEntity.class);
    }

    @Override
    public AuthAttribute convertDTO(AuthAttribute entity, boolean isDeep) {
            return convert(entity, isDeep, AuthAttribute.class);
    }

    @Override
    public AuthAttributeEntity convertToEntity(AuthAttribute entity, boolean isDeep) {
            return convertToCrossEntity(entity, isDeep, AuthAttributeEntity.class);
    }

    @Override
    public AuthAttribute convertToDTO(AuthAttributeEntity userEntity, boolean isDeep) {
            return convertToCrossEntity(userEntity, isDeep, AuthAttribute.class);
    }

    @Override
    public List<AuthAttributeEntity> convertToEntityList(List<AuthAttribute> list, boolean isDeep) {
            return convertListToCrossEntity(list, isDeep, AuthAttributeEntity.class);
    }

    @Override
    public List<AuthAttribute> convertToDTOList(List<AuthAttributeEntity> list, boolean isDeep) {
            return convertListToCrossEntity(list, isDeep, AuthAttribute.class);
    }
}
