package org.openiam.idm.srvc.res.service;

import java.util.Collection;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;

public interface ResourceRoleDAO extends BaseDao<ResourceRoleEntity, ResourceRoleEmbeddableId>  {
	public void deleteByRoleId(final String roleId);
	public void deleteByResourceId(final String resourceId);
	public void deleteByResourceIds(final Collection<String> resourceIds);
	public void deleteByRoleId(final String roleId, final Collection<String> resourceIds);
}