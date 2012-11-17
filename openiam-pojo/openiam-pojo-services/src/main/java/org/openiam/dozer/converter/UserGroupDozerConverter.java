package org.openiam.dozer.converter;

import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.UserGroup;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("userGroupDozerConverter")
public class UserGroupDozerConverter extends AbstractDozerEntityConverter<UserGroup, UserGroupEntity> {
    @Override
    public UserGroupEntity convertEntity(UserGroupEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserGroupEntity.class);
    }

    @Override
    public UserGroup convertDTO(UserGroup entity, boolean isDeep) {
        return convert(entity, isDeep, UserGroup.class);
    }

    @Override
    public UserGroupEntity convertToEntity(UserGroup entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, UserGroupEntity.class);
    }

    @Override
    public UserGroup convertToDTO(UserGroupEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, UserGroup.class);
    }

    @Override
    public List<UserGroupEntity> convertToEntityList(List<UserGroup> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserGroupEntity.class);
    }

    @Override
    public List<UserGroup> convertToDTOList(List<UserGroupEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserGroup.class);
    }
}
