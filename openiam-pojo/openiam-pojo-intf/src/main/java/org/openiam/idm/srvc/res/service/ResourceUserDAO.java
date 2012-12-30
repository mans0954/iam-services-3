package org.openiam.idm.srvc.res.service;

import java.util.Collection;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;


public interface ResourceUserDAO extends BaseDao<ResourceUserEntity, String>  {

	public void deleteByUserId(final String userId, final Collection<String> resourceIds);
	public ResourceUserEntity getRecord(final String resourceId, final String userId);
}