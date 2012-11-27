package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.res.dto.ResourceRoleId;
import org.openiam.idm.srvc.role.dto.Role;

import java.util.List;

public interface ResourceRoleDAO extends BaseDao<ResourceRoleEntity, ResourceRoleEmbeddableId>  {
	public void deleteByRoleId(final String roleId);
	public void deleteByResourceId(final String resourceId);
}