package org.openiam.idm.srvc.grp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.service.ResourceGroupDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private UserDataService userDataService;
    
    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    private ResourceGroupDAO resoruceGroupDAO;

    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
	
	private static final Log log = LogFactory.getLog(GroupDataServiceImpl.class);

	public GroupDataServiceImpl() {

	}

    public GroupEntity getGroup(final String grpId) {
        return getGroup(grpId, null);
    }

	public GroupEntity getGroup(final String grpId, final String requesterId) {
        if(DelegationFilterHelper.isAllowed(grpId, getDelegationFilter(requesterId))){
            return groupDao.findById(grpId);
        }
        return null;
	}

    @Override
    public GroupEntity getGroupByName(final String groupName, final String requesterId) {
        final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setName(groupName);
        final List<GroupEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

    @Override
    public UserGroupEntity getRecord(final String userId, final String groupId, final String requesterId) {
        if(DelegationFilterHelper.isAllowed(groupId, getDelegationFilter(requesterId))){
            return userGroupDao.getRecord(groupId, userId);
        }
        return null;
    }

    @Override
    public List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size) {
        return groupDao.getChildGroups(groupId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    public List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size) {
        return groupDao.getParentGroups(groupId, getDelegationFilter(requesterId), from, size);
    }


    @Override
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final  String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return new ArrayList<GroupEntity>(0);
        }
        return groupDao.getByExample(searchBean, from, size);
    }

    @Override
    public List<GroupEntity> getGroupsForUser(final String userId, final String requesterId, int from, int size) {
        return groupDao.getGroupsForUser(userId, getDelegationFilter(requesterId), from, size);
    }
    @Override
    public List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size) {
        return groupDao.getGroupsForResource(resourceId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    public List<GroupEntity> getGroupsForRole(final String roleId, final String requesterId, int from, int size) {
        return groupDao.getGroupsForRole(roleId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        return groupDao.getNumOfGroupsForRole(roleId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfGroupsForResource(final String resourceId, final String requesterId) {
        return groupDao.getNumOfGroupsForResource(resourceId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        return groupDao.getNumOfGroupsForUser(userId, getDelegationFilter(requesterId));
    }

    @Override
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return 0;
        }
        return groupDao.count(searchBean);
    }

    @Override
    public int getNumOfChildGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfChildGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfParentGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfParentGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    public List<Group> getCompiledGroupsForUser(final String userId) {
        final List<GroupEntity> groupList = this.getGroupsForUser(userId, null, 0, Integer.MAX_VALUE);
        final Set<GroupEntity> visitedSet = new HashSet<GroupEntity>();
        if(CollectionUtils.isNotEmpty(groupList)) {
            for(final GroupEntity group : groupList) {
                visitGroups(group, visitedSet);
            }
        }
        return groupDozerConverter.convertToDTOList(new ArrayList<GroupEntity>(visitedSet), true);
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
	public void saveGroup(final GroupEntity group) throws BasicDataServiceException {
		if(group != null && entityValidator.isValid(group)) {

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


    private Set<String> getDelegationFilter(String requesterId){
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString( userDataService.getUserAttributesDto(requesterId)));
        }
        return filterData;
    }
}
