package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.base.TreeObjectId;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleDAO extends BaseDao<RoleEntity, String> {

    /**
     * Find all the roles associated with a Group
     *
     * @return
     */
	@Deprecated
    List<RoleEntity> getRolesForGroup(final String groupId, final Set<String> filter, final int from, final int size);
	
	@Deprecated
    int getNumOfRolesForGroup(final String groupId, final Set<String> filter);

    @Deprecated
    int getNumOfRolesForResource(final String resourceId, final Set<String> filter);
    @Deprecated
    List<RoleEntity> getRolesForResource(final String resourceId, final Set<String> filter, final int from, final int size);
    
    @Deprecated
    List<RoleEntity> getChildRoles(final String roleId, final Set<String> filter, final int from, final int size);
    
    @Deprecated
    int getNumOfChildRoles(final String roleId, final Set<String> filter);
    
    @Deprecated
    List<RoleEntity> getParentRoles(final String roleId, final Set<String> filter, final int from, final int size);
    
    @Deprecated
    int getNumOfParentRoles(final String roleId, final Set<String> filter);

    List<RoleEntity> getRolesForUser(final String userId, final Set<String> filter, final int from, final int size);
    
    @Deprecated
    int getNumOfRolesForUser(final String userId, final Set<String> filter);


    List<TreeObjectId> findRolesWithSubRolesIds(List<String> initialRoleIds, final Set<String> filter);

    List<String> findAllParentsIds();

    void rolesHierarchyRebuild();
}