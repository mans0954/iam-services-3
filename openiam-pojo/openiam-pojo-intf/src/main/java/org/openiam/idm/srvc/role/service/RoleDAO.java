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
    public List<RoleEntity> findUserRoles(String userId);

    /**
     * Find all the roles associated with a Group
     *
     * @return
     */
    public List<RoleEntity> findRolesInGroup(String groupId);
}