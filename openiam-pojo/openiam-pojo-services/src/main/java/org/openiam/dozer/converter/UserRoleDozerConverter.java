package org.openiam.dozer.converter;

import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("userRoleDozerConverter")
public class UserRoleDozerConverter extends AbstractDozerEntityConverter<UserRole, UserRoleEntity> {
    @Override
    public UserRoleEntity convertEntity(UserRoleEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserRoleEntity.class);
    }

    @Override
    public UserRole convertDTO(UserRole entity, boolean isDeep) {
        return convert(entity, isDeep, UserRole.class);
    }

    @Override
    public UserRoleEntity convertToEntity(UserRole entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, UserRoleEntity.class);
    }

    @Override
    public UserRole convertToDTO(UserRoleEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, UserRole.class);
    }

    @Override
    public List<UserRoleEntity> convertToEntityList(List<UserRole> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserRoleEntity.class);
    }

    @Override
    public List<UserRole> convertToDTOList(List<UserRoleEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserRole.class);
    }
}
