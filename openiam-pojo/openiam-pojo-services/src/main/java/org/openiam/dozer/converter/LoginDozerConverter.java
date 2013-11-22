package org.openiam.dozer.converter;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("loginDozerConverter")
public class LoginDozerConverter extends AbstractDozerEntityConverter<Login, LoginEntity> {
    @Override
    public LoginEntity convertEntity(LoginEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, LoginEntity.class);
    }

    @Override
    public Login convertDTO(Login entity, boolean isDeep) {
        return convert(entity, isDeep, Login.class);
    }

    @Override
    public LoginEntity convertToEntity(Login entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LoginEntity.class);
    }

    @Override
    public Login convertToDTO(LoginEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, Login.class);
    }

    @Override
    public List<LoginEntity> convertToEntityList(List<Login> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LoginEntity.class);
    }

    @Override
    public List<Login> convertToDTOList(List<LoginEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Login.class);
    }

    @Override
    public Set<LoginEntity> convertToEntitySet(Set<Login> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, LoginEntity.class);
    }

    @Override
    public Set<Login> convertToDTOSet(Set<LoginEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Login.class);
    }
}
