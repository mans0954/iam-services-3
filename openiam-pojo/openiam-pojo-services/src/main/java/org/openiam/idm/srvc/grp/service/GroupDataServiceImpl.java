package org.openiam.idm.srvc.grp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.service.ResourceGroupDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <code>GroupDataServiceImpl</code> provides a service to manage groups as
 * well as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 * 
 * @author Suneet Shah
 * @version 2.0
 */

@Service("groupManager")
@Transactional
public class GroupDataServiceImpl implements GroupDataService {
	
	@Autowired
	private GroupDAO groupDao;
	
	@Autowired
	private GroupAttributeDAO groupAttrDao;
	
	@Autowired
	private UserGroupDAO userGroupDao;
    
    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    private ResourceGroupDAO resoruceGroupDAO;
	
	private static final Log log = LogFactory.getLog(GroupDataServiceImpl.class);

	public GroupDataServiceImpl() {

	}
	
	public GroupEntity getGroup(final String grpId) {
		return groupDao.findById(grpId);
	}

	public boolean isUserInCompiledGroupList(String groupId, String userId) {
		if(groupId != null) {
			final List<Group> userGroupList =  getCompiledGroupsForUser(userId);
			if(CollectionUtils.isNotEmpty(userGroupList)) {
				for (Group grp : userGroupList) {
					if (grp.getGrpId().equalsIgnoreCase(groupId)) {
						return true;
					}
				}
			}
		}
		return false;
		
	}

	@Override
	public void addUserToGroup(final String groupId, final String userId) {
		if(groupId != null && userId != null) {
			final UserGroupEntity entity = userGroupDao.getRecord(groupId, userId);
			if(entity == null) {
				final UserGroupEntity toSave = new UserGroupEntity(groupId, userId);
				userGroupDao.save(toSave);
			}
		}
	}
	
	@Override
	public void removeUserFromGroup(String groupId, String userId) {
		if(groupId != null && userId != null) {
			final UserGroupEntity entity = userGroupDao.getRecord(groupId, userId);
			if(entity != null) {
				userGroupDao.delete(entity);
			}
		}
	}
	
	@Override
	public void saveGroup(final GroupEntity group) {
		if(group != null) {
			if(StringUtils.isNotBlank(group.getGrpId())) {
				final GroupEntity dbGroup = groupDao.findById(group.getGrpId());
				if(dbGroup != null) {
					group.setAttributes(dbGroup.getAttributes());
					group.setChildGroups(dbGroup.getChildGroups());
					group.setParentGroups(dbGroup.getParentGroups());
					group.setResourceGroups(dbGroup.getResourceGroups());
					group.setRoles(dbGroup.getRoles());
					group.setUserGroups(dbGroup.getUserGroups());
					groupDao.merge(group);
				}
			} else {
				groupDao.save(group);
			}
		}
	}

	@Override
	//@Transactional
	public void deleteGroup(String groupId) {
		final GroupEntity entity = groupDao.findById(groupId);
		if(entity != null) {
			userGroupDao.deleteByGroupId(groupId);
			resoruceGroupDAO.deleteByGroupId(groupId);
			groupAttrDao.deleteByGroupId(groupId);
			groupDao.delete(entity);
		}
	}

	@Override
	public List<GroupEntity> getChildGroups(final String groupId, final int from, final int size) {
		return groupDao.getChildGroups(groupId, from, size);
	}

	@Override
	public List<GroupEntity> getParentGroups(final String groupId, final int from, final int size) {
		return groupDao.getParentGroups(groupId, from, size);
	}
	
	@Override
	public List<Group> getCompiledGroupsForUser(final String userId) {
		final List<GroupEntity> groupList = groupDao.getGroupsForUser(userId, 0, Integer.MAX_VALUE);
		final Set<GroupEntity> visitedSet = new HashSet<GroupEntity>();
		if(CollectionUtils.isNotEmpty(groupList)) {
			for(final GroupEntity group : groupList) {
				visitGroups(group, visitedSet);
			}
		}
		return groupDozerConverter.convertToDTOList(new ArrayList<GroupEntity>(visitedSet), true);
	}
	
	private void visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		if(entity != null) {
			if(!visitedSet.contains(entity)) {
				visitedSet.add(entity);
				final Set<GroupEntity> children = entity.getChildGroups();
				if(CollectionUtils.isNotEmpty(children)) {
					for(final GroupEntity child : children) {
						visitGroups(child, visitedSet);
					}
				}
			}
		}
	}

	@Override
	public List<GroupEntity> getGroupsForUser(String userId, int from, int size) {
		return groupDao.getGroupsForUser(userId, from, size);
	}
	
	@Override
	public int getNumOfGroupsForUser(String userId) {
		return groupDao.getNumOfGroupsForUser(userId);
	}

	@Override
	public void saveAttribute(final GroupAttributeEntity attribute) {
		if(StringUtils.isNotBlank(attribute.getId())) {
			groupAttrDao.update(attribute);
		} else {
			groupAttrDao.save(attribute);
		}
	}

	@Override
	public void removeAttribute(String attributeId) {
		final GroupAttributeEntity entity = groupAttrDao.findById(attributeId);
		if(entity != null) {
			groupAttrDao.delete(entity);
		}
	}

	@Override
	public List<GroupEntity> findBeans(GroupSearchBean searchBean, int from, int size) {
        List<GroupEntity> retVal = groupDao.getByExample(searchBean, from, size);
        return retVal;
	}

	@Override
	public int countBeans(GroupSearchBean searchBean) {
        int count = groupDao.count(searchBean);
        return count;
	}

	@Override
	public List<GroupEntity> getGroupsForResource(final String resourceId, final int from, final int size) {
		final GroupEntity entity = new GroupEntity();
		
		final ResourceGroupEntity resourceGroupEntity = new ResourceGroupEntity();
		resourceGroupEntity.setResourceId(resourceId);
		entity.addResourceGroup(resourceGroupEntity);
		return groupDao.getByExample(entity, from, size);
	}
	
	@Override
	public int getNumOfGroupsForResource(final String resourceId) {
		final GroupEntity entity = new GroupEntity();
		final ResourceGroupEntity resourceGroupEntity = new ResourceGroupEntity();
		resourceGroupEntity.setResourceId(resourceId);
		entity.addResourceGroup(resourceGroupEntity);
		return groupDao.count(entity);
	}

	@Override
	public List<GroupEntity> getGroupsForRole(String roleId, int from, int size) {
		return groupDao.getGroupsForRole(roleId, from, size);
	}

	@Override
	public int getNumOfGroupsForRole(String roleId) {
		return groupDao.getNumOfGroupsForRole(roleId);
	}

	@Override
	public int getNumOfChildGroups(String groupId) {
		return groupDao.getNumOfChildGroups(groupId);
	}

	@Override
	public int getNumOfParentGroups(String groupId) {
		return groupDao.getNumOfParentGroups(groupId);
	}

	@Override
	public void addChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			final GroupEntity child = groupDao.findById(childGroupId);
			if(group != null && child != null) {
				if(!group.hasChildGroup(childGroupId)) {
					group.addChildGroup(child);
					groupDao.update(group);
				}
			}
		}
	}

	@Override
	public void removeChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			if(group != null) {
				group.removeChildGroup(childGroupId);
			}
		}
	}

	@Override
	public GroupEntity getGroupByName(String groupName) {
		final GroupEntity example = new GroupEntity();
		example.setGrpName(groupName);
		final List<GroupEntity> foundList = groupDao.getByExample(example);
		return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
	}

	@Override
	public UserGroupEntity getRecord(String userId, String groupId) {
		return userGroupDao.getRecord(groupId, userId);
	}
}
