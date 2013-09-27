package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.springframework.stereotype.Component;

@Component
public class IdentityQuestionDozerConverter extends AbstractDozerEntityConverter<IdentityQuestion,  IdentityQuestionEntity> {

	@Override
	public IdentityQuestionEntity convertEntity(IdentityQuestionEntity entity,
			boolean isDeep) {
		return convert(entity, isDeep, IdentityQuestionEntity.class);
	}

	@Override
	public IdentityQuestion convertDTO(IdentityQuestion entity, boolean isDeep) {
		 return convert(entity, isDeep, IdentityQuestion.class);
	}

	@Override
	public IdentityQuestionEntity convertToEntity(IdentityQuestion entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, IdentityQuestionEntity.class);
	}

	@Override
	public IdentityQuestion convertToDTO(IdentityQuestionEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, IdentityQuestion.class);
	}

	@Override
	public List<IdentityQuestionEntity> convertToEntityList(
			List<IdentityQuestion> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, IdentityQuestionEntity.class);
	}

	@Override
	public List<IdentityQuestion> convertToDTOList(
			List<IdentityQuestionEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, IdentityQuestion.class);
	}

    @Override
    public Set<IdentityQuestionEntity> convertToEntitySet(
            Set<IdentityQuestion> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityQuestionEntity.class);
    }

    @Override
    public Set<IdentityQuestion> convertToDTOSet(
            Set<IdentityQuestionEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityQuestion.class);
    }

}
