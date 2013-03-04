package org.openiam.idm.srvc.role.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.dozer.converter.UserRoleDozerConverter;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.res.service.ResourceRoleDAO;
import org.openiam.idm.srvc.res.service.ResourceUserDAO;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.dto.RoleConstant;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
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
    private UserDozerConverter userDozerConverter;
    
	@Autowired
    private UserRoleDozerConverter userRoleDozerConverter;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private RoleDozerConverter roleDozerConverter;
	
	@Autowired
	private ResourceRoleDAO resourceRoleDAO;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	

	private static final Log log = LogFactory.getLog(RoleDataServiceImpl.class);

	@Override
	public RoleEntity getRole(String roleId) {
		return roleDao.findById(roleId);
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
	public List<RoleEntity> getRolesInGroup(String groupId, int from, int size) {
		return roleDao.getRolesForGroup(groupId, from, size);
	}

	@Override
	public List<UserRoleEntity> getUserRolesForUser(String userId, int from, int size) {
		final UserRoleEntity example = new UserRoleEntity();
		example.setUserId(userId);
		return userRoleDao.getByExample(example, from, size);
	}

	@Override
	public List<UserEntity> getUsersInRole(final String roleId, int from, int size) {
		final UserRoleEntity example = new UserRoleEntity();
		example.setRoleId(roleId);
		final List<UserRoleEntity> userRoleEntityList = userRoleDao.getByExample(example, from, size);
		final Set<String> roleIds = new LinkedHashSet<String>();
		if(CollectionUtils.isNotEmpty(userRoleEntityList)) {
			for(final UserRoleEntity entity : userRoleEntityList) {
				roleIds.add(entity.getRoleId());
			}
		}
		return userDAO.findByIds(roleIds, from, size);
	}

	@Override
	public List<RoleEntity> getUserRoles(String userId, int from, int size) {
		return roleDao.findUserRoles(userId, from, size);
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
	public List<RoleEntity> findBeans(RoleEntity example, int from, int size) {
		return roleDao.getByExample(example, from, size);
	}

	@Override
	public int countBeans(RoleEntity example) {
		return roleDao.count(example);
	}

	@Override
	public List<RoleEntity> getRolesForResource(final String resourceId, final int from, final int size) {
		return roleDao.getRolesForResource(resourceId, from, size);
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId) {
		return roleDao.getNumOfRolesForResource(resourceId);
	}

	@Override
	public List<RoleEntity> getChildRoles(String roleId, int from, int size) {
		return roleDao.getChildRoles(roleId, from, size);
	}

	@Override
	public int getNumOfChildRoles(String roleId) {
		return roleDao.getNumOfChildRoles(roleId);
	}

	@Override
	public List<RoleEntity> getParentRoles(String roleId, int from, int size) {
		return roleDao.getParentRoles(roleId, from, size);
	}

	@Override
	public int getNumOfParentRoles(String roleId) {
		return roleDao.getNumOfParentRoles(roleId);
	}

	@Override
	public void addChildRole(final String roleId, final String childRoleId) {
		if(roleId != null && childRoleId != null && !roleId.equals(childRoleId)) {
			final RoleEntity child = roleDao.findById(childRoleId);
			final RoleEntity parent = roleDao.findById(roleId);
			if(parent != null && child != null && !parent.hasChildRole(child.getRoleId())) {
				parent.addChildRole(child);
			}
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
		}
	}

	@Override
	public int getNumOfRolesForGroup(String groupId) {
		return roleDao.getNumOfRolesForGroup(groupId);
	}

	@Override
	public List<RoleEntity> getRolesForUser(final String userId, final int from, final int size) {
		return roleDao.getRolesForUser(userId, from, size);
	}

	@Override
	public int getNumOfRolesForUser(final String userId) {
		return roleDao.getNumOfRolesForUser(userId);
	}
}
