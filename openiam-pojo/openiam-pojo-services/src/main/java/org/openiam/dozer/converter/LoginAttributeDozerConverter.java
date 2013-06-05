package org.openiam.dozer.converter;

import org.openiam.idm.srvc.auth.domain.LoginAttributeEntity;
import org.openiam.idm.srvc.auth.dto.LoginAttribute;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("loginAttributeDozerConverter")
public class LoginAttributeDozerConverter extends AbstractDozerEntityConverter<LoginAttribute, LoginAttributeEntity> {
    @Override
    public LoginAttributeEntity convertEntity(LoginAttributeEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, LoginAttributeEntity.class);
    }

    @Override
    public LoginAttribute convertDTO(LoginAttribute entity, boolean isDeep) {
        return convert(entity, isDeep, LoginAttribute.class);
    }

    @Override
    public LoginAttributeEntity convertToEntity(LoginAttribute entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LoginAttributeEntity.class);
    }

    @Override
    public LoginAttribute convertToDTO(LoginAttributeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, LoginAttribute.class);
    }

    @Override
    public List<LoginAttributeEntity> convertToEntityList(List<LoginAttribute> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LoginAttributeEntity.class);
    }

    @Override
    public List<LoginAttribute> convertToDTOList(List<LoginAttributeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LoginAttribute.class);
    }
}
