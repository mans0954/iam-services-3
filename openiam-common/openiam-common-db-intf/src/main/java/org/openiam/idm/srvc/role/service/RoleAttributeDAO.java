package org.openiam.idm.srvc.role.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.dto.RoleAttribute;

import java.util.List;

public interface RoleAttributeDAO extends BaseDao<RoleAttributeEntity, String> {

     public List<RoleAttributeEntity> findByRoleId(String roleID);

	//public void deleteByRoleId(final String roleId);
}