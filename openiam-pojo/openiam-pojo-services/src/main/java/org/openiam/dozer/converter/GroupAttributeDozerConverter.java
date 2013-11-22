package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupAttribute;
import org.springframework.stereotype.Component;

@Component
public class GroupAttributeDozerConverter  extends AbstractDozerEntityConverter<GroupAttribute, GroupAttributeEntity> {

	@Override
	public GroupAttributeEntity convertEntity(GroupAttributeEntity entity, boolean isDeep) {
		return convert(entity, isDeep, GroupAttributeEntity.class);
	}

	@Override
	public GroupAttribute convertDTO(GroupAttribute entity, boolean isDeep) {
		return convert(entity, isDeep, GroupAttribute.class);
	}

	@Override
	public GroupAttributeEntity convertToEntity(GroupAttribute entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, GroupAttributeEntity.class);
	}

	@Override
	public GroupAttribute convertToDTO(GroupAttributeEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, GroupAttribute.class);
	}

	@Override
	public List<GroupAttributeEntity> convertToEntityList(List<GroupAttribute> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, GroupAttributeEntity.class);
	}

	@Override
	public List<GroupAttribute> convertToDTOList(List<GroupAttributeEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, GroupAttribute.class);
	}

    @Override
    public Set<GroupAttributeEntity> convertToEntitySet(Set<GroupAttribute> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, GroupAttributeEntity.class);
    }

    @Override
    public Set<GroupAttribute> convertToDTOSet(Set<GroupAttributeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, GroupAttribute.class);
    }
}
