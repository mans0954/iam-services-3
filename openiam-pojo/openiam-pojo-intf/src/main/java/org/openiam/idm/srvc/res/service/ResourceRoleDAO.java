package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;

public interface ResourceRoleDAO extends BaseDao<ResourceRoleEntity, ResourceRoleEmbeddableId>  {
	public void deleteByRoleId(final String roleId);
	public void deleteByResourceId(final String resourceId);
}