package org.openiam.idm.srvc.mngsys.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;

import java.util.List;

public interface ManagedSystemObjectMatchDAO extends BaseDao<ManagedSystemObjectMatchEntity, String> {


	/**
	 * Finds objects for an object type (like User, Group) for a ManagedSystem definition
	 * @param managedSystemId
	 * @param objectType
	 * @return
	 */
	public List<ManagedSystemObjectMatchEntity> findBySystemId(String managedSystemId, String objectType);
	
}