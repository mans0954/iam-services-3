package org.openiam.idm.srvc.res.service;

import java.util.Collection;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;

public interface ResourceGroupDAO extends BaseDao<ResourceGroupEntity, String> {

	public ResourceGroupEntity getRecord(final String resourceId, final String groupId);
	public void deleteByResourceId(final String resourceId);
	public void deleteByGroupId(final String groupId);
	public void deleteByGroupId(final String groupId, final Collection<String> resourceIds);
}
