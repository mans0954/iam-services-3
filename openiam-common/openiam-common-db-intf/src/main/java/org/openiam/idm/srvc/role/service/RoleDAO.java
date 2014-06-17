package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import java.util.List;
import java.util.Set;

public interface RoleDAO extends BaseDao<RoleEntity, String> {

    /**
     * Find all the roles associated with a Group
     *
     * @return
     */
    public List<RoleEntity> getRolesForGroup(final String groupId, final Set<String> filter, final int from, final int size);
    public int getNumOfRolesForGroup(final String groupId, final Set<String> filter);

    public int getNumOfRolesForResource(final String resourceId, final Set<String> filter);
    public List<RoleEntity> getRolesForResource(final String resourceId, final Set<String> filter, final int from, final int size);
    
    public List<RoleEntity> getChildRoles(final  String roleId, final Set<String> filter, final int from, final int size);
    public int getNumOfChildRoles(final  String roleId, final Set<String> filter);
    
    public List<RoleEntity> getParentRoles(final  String roleId, final Set<String> filter, final int from, final int size);
    public int getNumOfParentRoles(final String roleId, final Set<String> filter);

    public List<RoleEntity> getRolesForUser(final String userId, final Set<String> filter, final int from, final int size);
    public int getNumOfRolesForUser(final String userId, final Set<String> filter);


}