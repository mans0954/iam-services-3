package org.openiam.idm.srvc.role.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.res.service.ResourceRoleDAO;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("roleDataService")
@Transactional
public class RoleDataServiceImpl implements RoleDataService {

	@Autowired
	private RoleDAO roleDao;
	
	@Autowired
	private RoleAttributeDAO roleAttributeDAO;
	
	@Autowired
	private UserRoleDAO userRoleDao;
	
	@Autowired
	private RolePolicyDAO rolePolicyDao;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private UserDAO userDAO;

    @Autowired
    private UserDataService userDataService;
	
	@Autowired
	private RoleDozerConverter roleDozerConverter;

	@Autowired
	private ResourceRoleDAO resourceRoleDAO;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	

	private static final Log log = LogFactory.getLog(RoleDataServiceImpl.class);

    @Override
    public RoleEntity getRole(String roleId){
        return  getRole(roleId, null);
    }
	@Override
	public RoleEntity getRole(String roleId, final String requesterId) {
        if(DelegationFilterHelper.isAllowed(roleId, getDelegationFilter(requesterId))){
            return roleDao.findById(roleId);
        }
        return null;
	}
	
	@Override
	//@Transactional
	public void removeRole(String roleId) {
		if(roleId != null) {
			final RoleEntity roleEntity = roleDao.findById(roleId);
			if(roleEntity != null) {
				resourceRoleDAO.deleteByRoleId(roleId);
				userRoleDAO.deleteByRoleId(roleId);
				roleAttributeDAO.deleteByRoleId(roleId);
				roleDao.delete(roleEntity);
			}
		}
	}
	
	@Override
	public void addGroupToRole(String roleId, String groupId) {
		if(roleId != null && groupId != null) {
			final RoleEntity role = roleDao.findById(roleId);
			final GroupEntity group = groupDAO.findById(groupId);
			if(role != null && group != null) {
				role.addGroup(group);
				roleDao.save(role);
			}
		}
	}
	
	@Override
	public void removeGroupFromRole(String roleId, String groupId) {
		if(roleId != null && groupId != null) {
			final RoleEntity role = roleDao.findById(roleId);
			final GroupEntity group = groupDAO.findById(groupId);
			if(role != null && group != null) {
				role.removeGroup(group.getGrpId());
				roleDao.save(role);
			}
		}

	}
    /**
     * Adds a user to a role using the UserRole object. Similar to addUserToRole, but allows you to update attributes likes start and end date.
     */
    @Override
    public void assocUserToRole(UserRoleEntity ur) {
        if (ur.getRoleId() == null)
            throw new IllegalArgumentException("roleId is null");
        if (ur.getUserId() == null)
            throw new IllegalArgumentException("userId object is null");

        ur.setUserRoleId(null);
        userRoleDao.add(ur);
    }

    /**
     * Updates the attributes in the user role object.
     *
     * @param ur
     */
    @Override
    public void updateUserRoleAssoc(UserRoleEntity ur) {
        if (ur.getRoleId() == null)
            throw new IllegalArgumentException("roleId is null");
        if (ur.getUserId() == null)
            throw new IllegalArgumentException("userId object is null");
        userRoleDao.update(ur);
    }

	@Override
	public void addUserToRole(String roleId, String userId) {
		if(roleId != null && userId != null) {
			if(userRoleDao.getRecord(userId, roleId) == null) {
				userRoleDao.save(new UserRoleEntity(userId, roleId));
			}
		}
	}
	
	@Override
	public void removeUserFromRole(String roleId, String userId) {
		if(roleId != null && userId != null) {
			final UserRoleEntity record = userRoleDao.getRecord(userId, roleId);
			if(record != null) {
				userRoleDao.delete(record);
			}
		}
	}

	private void visitChildRoles(final String roleId, final Set<RoleEntity> visitedSet) {
		if(roleId != null) {
			if(visitedSet != null) {
				final RoleEntity role = roleDao.findById(roleId);
				if(role != null) {
					if(!visitedSet.contains(role)) {
						visitedSet.add(role);
						if(CollectionUtils.isNotEmpty(role.getChildRoles())) {
							for(final RoleEntity child : role.getChildRoles()) {
								visitChildRoles(child.getRoleId(), visitedSet);
							}
						}
					}
				}
			}
		}
	}
	
	private void visitParentRoles(final String roleId, final Set<RoleEntity> visitedSet) {
		if(roleId != null) {
			if(visitedSet != null) {
				final RoleEntity role = roleDao.findById(roleId);
				if(role != null) {
					if(!visitedSet.contains(role)) {
						visitedSet.add(role);
						if(CollectionUtils.isNotEmpty(role.getParentRoles())) {
							for(final RoleEntity child : role.getParentRoles()) {
								visitParentRoles(child.getRoleId(), visitedSet);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public RolePolicyEntity getRolePolicy(String rolePolicyId) {
		return rolePolicyDao.findById(rolePolicyId);
	}	

	@Override
	public void saveRole(final RoleEntity role) {
		if(role != null) {
			if(StringUtils.isBlank(role.getRoleId())) {
				roleDao.save(role);
			} else {
				roleDao.update(role);
			}
		}
	}

	@Override
	public void savePolicy(final RolePolicyEntity policy) {
		if(policy != null) {
			if(StringUtils.isBlank(policy.getRolePolicyId())) {
				rolePolicyDao.save(policy);
			} else {
				rolePolicyDao.update(policy);
			}
		}
	}

	@Override
	public void removeRolePolicy(String rolePolicyId) {
		if(rolePolicyId != null) {
			final RolePolicyEntity entity = rolePolicyDao.findById(rolePolicyId);
			if(entity != null) {
				rolePolicyDao.delete(entity);
			}
		}
	}

	@Override
	public void saveAttribute(RoleAttributeEntity attribute) {
		if(attribute != null) {
			if(StringUtils.isBlank(attribute.getRoleAttrId())) {
				roleAttributeDAO.save(attribute);
			} else {
				roleAttributeDAO.update(attribute);
			}
		}
	}

	@Override
	public void removeAttribute(final String roleAttributeId) {
		if(roleAttributeId != null) {
			final RoleAttributeEntity entity = roleAttributeDAO.findById(roleAttributeId);
			if(entity != null) {
				roleAttributeDAO.delete(entity);
			}
		}
	}

	@Override
	public List<RoleEntity> getRolesInGroup(final String groupId, final String requesterId, int from, int size) {
		return roleDao.getRolesForGroup(groupId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public List<UserRoleEntity> getUserRolesForUser(String userId,  int from, int size) {
		final UserRoleEntity example = new UserRoleEntity();
		example.setUserId(userId);
		return userRoleDao.getByExample(example, from, size);
	}

//	@Override
//	public List<UserEntity> getUsersInRole(final String roleId, final String requesterId, int from, int size) {
//		final UserRoleEntity example = new UserRoleEntity();
//		example.setRoleId(roleId);
//		final List<UserRoleEntity> userRoleEntityList = userRoleDao.getByExample(example, from, size);
//		final Set<String> roleIds = new LinkedHashSet<String>();
//		if(CollectionUtils.isNotEmpty(userRoleEntityList)) {
//			for(final UserRoleEntity entity : userRoleEntityList) {
//				roleIds.add(entity.getRoleId());
//			}
//		}
//		return userDAO.findByIds(roleIds, from, size);
//	}

	@Override
	public List<RoleEntity> getUserRoles(String userId, final String requesterId, int from, int size) {
		return roleDao.findUserRoles(userId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public List<Role> getUserRolesAsFlatList(String userId) {
		final UserRoleEntity example = new UserRoleEntity();
		example.setUserId(userId);
		final List<UserRoleEntity> userRoleEntityList = userRoleDao.getByExample(example);
		final Set<String> roleIds = new LinkedHashSet<String>();
		if(CollectionUtils.isNotEmpty(userRoleEntityList)) {
			for(final UserRoleEntity entity : userRoleEntityList) {
				roleIds.add(entity.getRoleId());
			}
		}
		
		final Set<RoleEntity> visitedSet = new HashSet<RoleEntity>();
		final List<RoleEntity> entityList = roleDao.findByIds(roleIds);
		if(CollectionUtils.isNotEmpty(entityList)) {
			for(final RoleEntity entity : entityList) {
				visitChildRoles(entity.getRoleId(), visitedSet);
			}
		}
		
		final List<RoleEntity> resultList = new ArrayList<RoleEntity>(visitedSet);
		return roleDozerConverter.convertToDTOList(resultList, true);
	}

	@Override
	public List<RoleEntity> findBeans(RoleSearchBean searchBean, final String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return new ArrayList<RoleEntity>(0);
        }
        return roleDao.getByExample(searchBean, from, size);
	}

	@Override
	public int countBeans(RoleSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return 0;
        }
        return roleDao.count(searchBean);
	}

	@Override
	public List<RoleEntity> getRolesForResource(final String resourceId, final String requesterId, final int from, final int size) {
		return roleDao.getRolesForResource(resourceId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId, final String requesterId) {
		return roleDao.getNumOfRolesForResource(resourceId, getDelegationFilter(requesterId));
	}

	@Override
	public List<RoleEntity> getChildRoles(final String roleId, final String requesterId, int from, int size) {
		return roleDao.getChildRoles(roleId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public int getNumOfChildRoles(final String roleId, final String requesterId) {
		return roleDao.getNumOfChildRoles(roleId, getDelegationFilter(requesterId));
	}

	@Override
	public List<RoleEntity> getParentRoles(final String roleId, final String requesterId, int from, int size) {
		return roleDao.getParentRoles(roleId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public int getNumOfParentRoles(final String roleId, final String requesterId) {
		return roleDao.getNumOfParentRoles(roleId, getDelegationFilter(requesterId));
	}

	@Override
	public void addChildRole(final String roleId, final String childRoleId) {
		if(roleId != null && childRoleId != null && !roleId.equals(childRoleId)) {
			final RoleEntity child = roleDao.findById(childRoleId);
			final RoleEntity parent = roleDao.findById(roleId);
			if(parent != null && child != null && !parent.hasChildRole(child.getRoleId())) {
				parent.addChildRole(child);
			}
			roleDao.update(parent);
		}
	}

	@Override
	public void removeChildRole(final String roleId, final String childRoleId) {
		if(roleId != null && childRoleId != null) {
			final RoleEntity child = roleDao.findById(childRoleId);
			final RoleEntity parent = roleDao.findById(roleId);
			if(parent != null && child != null) {
				parent.removeChildRole(child.getRoleId());
			}
			roleDao.update(parent);
		}
	}

	@Override
	public int getNumOfRolesForGroup(String groupId, final String requesterId) {
		return roleDao.getNumOfRolesForGroup(groupId, getDelegationFilter(requesterId));
	}

	@Override
	public List<RoleEntity> getRolesForUser(final String userId, final String requesterId, final int from, final int size) {
		return roleDao.getRolesForUser(userId, getDelegationFilter(requesterId), from, size);
	}

	@Override
	public int getNumOfRolesForUser(final String userId, final String requesterId) {
		return roleDao.getNumOfRolesForUser(userId, getDelegationFilter(requesterId));
	}

	@Override
	public RoleEntity getRoleByName(String roleName, final String requesterId) {
        final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.setName(roleName);
        final List<RoleEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
		return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
	}

	@Override
	public UserRoleEntity getUserRole(String userId, String roleId, final String requesterId) {
		return userRoleDAO.getRecord(userId, roleId);
	}

    private Set<String> getDelegationFilter(String requesterId){
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            filterData = new HashSet<String>(
                    DelegationFilterHelper.getRoleFilterFromString( userDataService.getUserAttributesDto(requesterId)));
        }
        return filterData;
    }
}
