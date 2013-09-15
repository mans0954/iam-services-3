package org.openiam.idm.srvc.role.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RolePolicyEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("roleDataService")
public class RoleDataServiceImpl implements RoleDataService {

	@Autowired
	private RoleDAO roleDao;
	
	@Autowired
	private RoleAttributeDAO roleAttributeDAO;
	
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
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
	

	private static final Log log = LogFactory.getLog(RoleDataServiceImpl.class);

    @Override
    public RoleEntity getRole(String roleId){
        return  getRole(roleId, null);
    }
	@Override
    @Transactional(readOnly = true)
	public RoleEntity getRole(String roleId, final String requesterId) {
        if(DelegationFilterHelper.isAllowed(roleId, getDelegationFilter(requesterId))){
            return roleDao.findById(roleId);
        }
        return null;
	}
	
	@Override
	@Transactional
	public void removeRole(String roleId) {
		if(roleId != null) {
			final RoleEntity roleEntity = roleDao.findById(roleId);
			if(roleEntity != null) {
				//roleAttributeDAO.deleteByRoleId(roleId);
				roleDao.delete(roleEntity);
			}
		}
	}
	
	@Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public void assocUserToRole(String userId, String roleId) {
        if (roleId == null)
            throw new IllegalArgumentException("role is null");
        if (userId == null)
            throw new IllegalArgumentException("user object is null");
        UserEntity userEntity = userDAO.findById(userId);
        RoleEntity roleEntity = roleDao.findById(roleId);
        userEntity.getRoles().add(roleEntity);
    }

    @Override
    @Transactional
	public void addUserToRole(String roleId, String userId) {
          if (roleId == null)
              throw new IllegalArgumentException("role is null");
          if (userId == null)
              throw new IllegalArgumentException("user object is null");

            UserEntity userEntity = userDAO.findById(userId);
            RoleEntity roleEntity = roleDao.findById(roleId);
            userEntity.getRoles().add(roleEntity);
	}
	
	@Override
    @Transactional
	public void removeUserFromRole(String roleId, String userId) {
        if (roleId == null)
            throw new IllegalArgumentException("role is null");
        if (userId == null)
            throw new IllegalArgumentException("user object is null");
        UserEntity userEntity = userDAO.findById(userId);
        RoleEntity roleEntity = roleDao.findById(roleId);
        userEntity.getRoles().remove(roleEntity);
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
    @Transactional(readOnly = true)
	public RolePolicyEntity getRolePolicy(String rolePolicyId) {
		return rolePolicyDao.findById(rolePolicyId);
	}	

	@Override
    @Transactional
	public void saveRole(final RoleEntity role) throws BasicDataServiceException {
		if(role != null && entityValidator.isValid(role)) {
			if(StringUtils.isBlank(role.getRoleId())) {
				roleDao.save(role);
			} else {
				final RoleEntity dbRole = roleDao.findById(role.getRoleId());
				if(dbRole != null) {
					role.setChildRoles(dbRole.getChildRoles());
					role.setGroups(dbRole.getGroups());
					role.setParentRoles(dbRole.getParentRoles());
					role.setResources(dbRole.getResources());
					role.setRolePolicy(dbRole.getRolePolicy());
					role.setUsers(dbRole.getUsers());
					
					mergeAttributes(role, dbRole);
					roleDao.merge(role);
				}
			}
		}
	}
	
	private void mergeAttributes(final RoleEntity bean, final RoleEntity dbObject) {
		
		final Set<RoleAttributeEntity> renewedSet = new HashSet<RoleAttributeEntity>();
		
		final Set<RoleAttributeEntity> beanProps = (bean.getRoleAttributes() != null) ? bean.getRoleAttributes() : new HashSet<RoleAttributeEntity>();
		final Set<RoleAttributeEntity> dbProps = (dbObject.getRoleAttributes() != null) ? dbObject.getRoleAttributes() : new HashSet<RoleAttributeEntity>();
		
		/* delete */
		/*
		for(final Iterator<RoleAttributeEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
			final RoleAttributeEntity dbProp = dbIt.next();
			
			boolean contains = false;
			for(final Iterator<RoleAttributeEntity> it = beanProps.iterator(); it.hasNext();) {
			final RoleAttributeEntity beanProp = it.next();
				if(StringUtils.equals(dbProp.getRoleAttrId(), beanProp.getRoleAttrId())) {
					contains = true;
					break;
				}
			}
			
			if(!contains) {
				dbIt.remove();
			}
		}
		*/
			
		/* update */
		for(final Iterator<RoleAttributeEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
			final RoleAttributeEntity dbProp = dbIt.next();
			for(final Iterator<RoleAttributeEntity> it = beanProps.iterator(); it.hasNext();) {
				final RoleAttributeEntity beanProp = it.next();
				if(StringUtils.equals(dbProp.getRoleAttrId(), beanProp.getRoleAttrId())) {
					dbProp.setAttrGroup(beanProp.getAttrGroup());
					dbProp.setMetadataElementId(beanProp.getMetadataElementId());
					dbProp.setName(beanProp.getName());
					dbProp.setValue(beanProp.getValue());
					renewedSet.add(dbProp);
					break;
				}
			}
		}
		
		/* add */
		for(final Iterator<RoleAttributeEntity> it = beanProps.iterator(); it.hasNext();) {
			boolean contains = false;
			final RoleAttributeEntity beanProp = it.next();
			for(final Iterator<RoleAttributeEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
				final RoleAttributeEntity dbProp = dbIt.next();
				if(StringUtils.equals(dbProp.getRoleAttrId(), beanProp.getRoleAttrId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				beanProp.setRole(bean);
				//dbProps.add(beanProp);
				renewedSet.add(beanProp);
			}
		}
		
		bean.setRoleAttributes(renewedSet);
	}

	@Override
    @Transactional
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
    @Transactional
	public void removeRolePolicy(String rolePolicyId) {
		if(rolePolicyId != null) {
			final RolePolicyEntity entity = rolePolicyDao.findById(rolePolicyId);
			if(entity != null) {
				rolePolicyDao.delete(entity);
			}
		}
	}

	/*
	@Override
    @Transactional
	public void saveAttribute(RoleAttributeEntity attribute) {
		if(attribute != null) {
			if(StringUtils.isBlank(attribute.getRoleAttrId())) {
				roleAttributeDAO.save(attribute);
			} else {
				roleAttributeDAO.update(attribute);
			}
		}
	}
	*/

	/*
	@Override
    @Transactional
	public void removeAttribute(final String roleAttributeId) {
		if(roleAttributeId != null) {
			final RoleAttributeEntity entity = roleAttributeDAO.findById(roleAttributeId);
			if(entity != null) {
				roleAttributeDAO.delete(entity);
			}
		}
	}
	*/

	@Override
    @Transactional(readOnly = true)
	public List<RoleEntity> getRolesInGroup(final String groupId, final String requesterId, int from, int size) {
		return roleDao.getRolesForGroup(groupId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public List<RoleEntity> getUserRoles(String userId, final String requesterId, int from, int size) {
		return roleDao.getRolesForUser(userId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public List<Role> getUserRolesAsFlatList(String userId) {
		UserEntity userEntity = userDAO.findById(userId);
		Set<RoleEntity> userRoles = userEntity.getRoles();
		
		final Set<RoleEntity> visitedSet = new HashSet<RoleEntity>();

		if(CollectionUtils.isNotEmpty(userRoles)) {
			for(final RoleEntity entity : userRoles) {
				visitChildRoles(entity.getRoleId(), visitedSet);
			}
		}
		
		final List<RoleEntity> resultList = new ArrayList<RoleEntity>(visitedSet);
		return roleDozerConverter.convertToDTOList(resultList, true);
	}

	@Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
	public List<RoleEntity> getRolesForResource(final String resourceId, final String requesterId, final int from, final int size) {
		return roleDao.getRolesForResource(resourceId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfRolesForResource(final String resourceId, final String requesterId) {
		return roleDao.getNumOfRolesForResource(resourceId, getDelegationFilter(requesterId));
	}

	@Override
    @Transactional(readOnly = true)
	public List<RoleEntity> getChildRoles(final String roleId, final String requesterId, int from, int size) {
		return roleDao.getChildRoles(roleId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfChildRoles(final String roleId, final String requesterId) {
		return roleDao.getNumOfChildRoles(roleId, getDelegationFilter(requesterId));
	}

	@Override
    @Transactional(readOnly = true)
	public List<RoleEntity> getParentRoles(final String roleId, final String requesterId, int from, int size) {
		return roleDao.getParentRoles(roleId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfParentRoles(final String roleId, final String requesterId) {
		return roleDao.getNumOfParentRoles(roleId, getDelegationFilter(requesterId));
	}

	@Override
    @Transactional
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
    @Transactional
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
    @Transactional(readOnly = true)
	public int getNumOfRolesForGroup(String groupId, final String requesterId) {
		return roleDao.getNumOfRolesForGroup(groupId, getDelegationFilter(requesterId));
	}

	@Override
    @Transactional(readOnly = true)
	public List<RoleEntity> getRolesForUser(final String userId, final String requesterId, final int from, final int size) {
		return roleDao.getRolesForUser(userId, getDelegationFilter(requesterId), from, size);
	}

	@Override
    @Transactional(readOnly = true)
	public int getNumOfRolesForUser(final String userId, final String requesterId) {
		return roleDao.getNumOfRolesForUser(userId, getDelegationFilter(requesterId));
	}

	@Override
    @Transactional(readOnly = true)
	public RoleEntity getRoleByName(String roleName, final String requesterId) {
        final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.setName(roleName);
        final List<RoleEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
		return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
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
