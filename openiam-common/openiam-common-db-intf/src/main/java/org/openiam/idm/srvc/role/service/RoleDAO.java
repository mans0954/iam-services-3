package org.openiam.idm.srvc.role.service;

import java.util.List;
import java.util.Set;

import org.openiam.base.TreeObjectId;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.role.domain.RoleEntity;

public interface RoleDAO extends BaseDao<RoleEntity, String> {
    public List<TreeObjectId> findRolesWithSubRolesIds(List<String> initialRoleIds, final Set<String> filter);

    public List<String> findAllParentsIds();

    public void rolesHierarchyRebuild();
    
}