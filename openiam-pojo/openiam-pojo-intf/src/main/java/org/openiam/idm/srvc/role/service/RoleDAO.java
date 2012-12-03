package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import java.util.List;

public interface RoleDAO extends BaseDao<RoleEntity, String> {

    /**
     * Get the roles for a user
     *
     * @param userId
     * @return
     */
    public List<RoleEntity> findUserRoles(final String userId, final int from, final int size);

    /**
     * Find all the roles associated with a Group
     *
     * @return
     */
    public List<RoleEntity> getRolesForGroup(final String groupId, final int from, final int size);
    public int getNumOfRolesForGroup(final String groupId);
    
    public int getNumOfRolesForResource(final String resourceId);
    
    public List<RoleEntity> getRolesForResource(final String resourceId, final int from, final int size);
    
    public List<RoleEntity> getChildRoles(final String roleId, final int from, final int size);
    public int getNumOfChildRoles(final String roleId);
    
    public List<RoleEntity> getParentRoles(final String roleId, final int from, final int size);
    public int getNumOfParentRoles(final String roleId);
}