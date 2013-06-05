package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;

import java.util.Collection;


public interface ResourceUserDAO extends BaseDao<ResourceUserEntity, String>  {

	public void deleteByResourceId(final String resourceId);
	public void deleteByUserId(final String userId, final Collection<String> resourceIds);
    public void deleteAllByUserId(String userId);
	public ResourceUserEntity getRecord(final String resourceId, final String userId);
}