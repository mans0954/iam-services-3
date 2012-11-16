package org.openiam.dozer.converter;

import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("userDozerConverter")
public class UserDozerConverter  extends AbstractDozerEntityConverter<User, UserEntity> {
    @Override
    public UserEntity convertEntity(UserEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserEntity.class);
    }

    @Override
    public User convertDTO(User entity, boolean isDeep) {
        return convert(entity, isDeep, User.class);
    }

    @Override
    public UserEntity convertToEntity(User entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, UserEntity.class);
    }

    @Override
    public User convertToDTO(UserEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, User.class);
    }

    @Override
    public List<UserEntity> convertToEntityList(List<User> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserEntity.class);
    }

    @Override
    public List<User> convertToDTOList(List<UserEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, User.class);
    }
}
