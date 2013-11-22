package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityAnswerDozerConverter extends AbstractDozerEntityConverter<UserIdentityAnswer,  UserIdentityAnswerEntity> {

	@Override
	public UserIdentityAnswerEntity convertEntity(
			UserIdentityAnswerEntity entity, boolean isDeep) {
		return convert(entity, isDeep, UserIdentityAnswerEntity.class);
	}

	@Override
	public UserIdentityAnswer convertDTO(UserIdentityAnswer entity,
			boolean isDeep) {
		 return convert(entity, isDeep, UserIdentityAnswer.class);
	}

	@Override
	public UserIdentityAnswerEntity convertToEntity(UserIdentityAnswer entity,
			boolean isDeep) {
		 return convertToCrossEntity(entity, isDeep, UserIdentityAnswerEntity.class);
	}

	@Override
	public UserIdentityAnswer convertToDTO(UserIdentityAnswerEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, UserIdentityAnswer.class);
	}

	@Override
	public List<UserIdentityAnswerEntity> convertToEntityList(
			List<UserIdentityAnswer> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, UserIdentityAnswerEntity.class);
	}

	@Override
	public List<UserIdentityAnswer> convertToDTOList(
			List<UserIdentityAnswerEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, UserIdentityAnswer.class);
	}

    @Override
    public Set<UserIdentityAnswerEntity> convertToEntitySet(
            Set<UserIdentityAnswer> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UserIdentityAnswerEntity.class);
    }

    @Override
    public Set<UserIdentityAnswer> convertToDTOSet(
            Set<UserIdentityAnswerEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UserIdentityAnswer.class);
    }

}
