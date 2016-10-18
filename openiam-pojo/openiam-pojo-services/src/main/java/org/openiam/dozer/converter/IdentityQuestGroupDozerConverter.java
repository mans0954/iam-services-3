package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.springframework.stereotype.Component;

@Component
public class IdentityQuestGroupDozerConverter extends AbstractDozerEntityConverter<IdentityQuestGroup, IdentityQuestGroupEntity> {

	@Override
	public IdentityQuestGroupEntity convertEntity(IdentityQuestGroupEntity entity,
			boolean isDeep) {
		return convert(entity, isDeep, IdentityQuestGroupEntity.class);
	}

	@Override
	public IdentityQuestGroup convertDTO(IdentityQuestGroup entity, boolean isDeep) {
		 return convert(entity, isDeep, IdentityQuestGroup.class);
	}

	@Override
	public IdentityQuestGroupEntity convertToEntity(IdentityQuestGroup entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, IdentityQuestGroupEntity.class);
	}

	@Override
	public IdentityQuestGroup convertToDTO(IdentityQuestGroupEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, IdentityQuestGroup.class);
	}

	@Override
	public List<IdentityQuestGroupEntity> convertToEntityList(
			List<IdentityQuestGroup> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, IdentityQuestGroupEntity.class);
	}

	@Override
	public List<IdentityQuestGroup> convertToDTOList(
			List<IdentityQuestGroupEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, IdentityQuestGroup.class);
	}

    @Override
    public Set<IdentityQuestGroupEntity> convertToEntitySet(
            Set<IdentityQuestGroup> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityQuestGroupEntity.class);
    }

    @Override
    public Set<IdentityQuestGroup> convertToDTOSet(
            Set<IdentityQuestGroupEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, IdentityQuestGroup.class);
    }

}
