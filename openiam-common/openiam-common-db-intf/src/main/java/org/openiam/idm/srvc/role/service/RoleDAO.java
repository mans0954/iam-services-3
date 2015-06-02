package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.base.TreeObjectId;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleDAO extends BaseDao<RoleEntity, String> {
    public List<RoleEntity> getRolesForUser(final String userId, final Set<String> filter, final int from, final int size);


    public List<TreeObjectId> findRolesWithSubRolesIds(List<String> initialRoleIds, final Set<String> filter);

    public List<String> findAllParentsIds();

    public void rolesHierarchyRebuild();
}