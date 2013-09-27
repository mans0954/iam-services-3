package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.springframework.stereotype.Component;

@Component
public class GroupDozerConverter extends AbstractDozerEntityConverter<Group, GroupEntity> {

	@Override
	public GroupEntity convertEntity(GroupEntity entity, boolean isDeep) {
		return convert(entity, isDeep, GroupEntity.class);
	}

	@Override
	public Group convertDTO(Group entity, boolean isDeep) {
		return convert(entity, isDeep, Group.class);
	}

	@Override
	public GroupEntity convertToEntity(Group entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, GroupEntity.class);
	}

	@Override
	public Group convertToDTO(GroupEntity entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, Group.class);
	}

	@Override
	public List<GroupEntity> convertToEntityList(List<Group> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, GroupEntity.class);
	}

	@Override
	public List<Group> convertToDTOList(List<GroupEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, Group.class);
	}

    @Override
    public Set<GroupEntity> convertToEntitySet(Set<Group> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, GroupEntity.class);
    }

    @Override
    public Set<Group> convertToDTOSet(Set<GroupEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, Group.class);
    }

}
