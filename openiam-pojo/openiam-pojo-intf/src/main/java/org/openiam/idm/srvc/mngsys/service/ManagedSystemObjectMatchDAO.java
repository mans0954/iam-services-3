package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;

import java.util.List;

public interface ManagedSystemObjectMatchDAO {

	public abstract void add(ManagedSystemObjectMatch transientInstance);

	public abstract void remove(ManagedSystemObjectMatch persistentInstance);

	public ManagedSystemObjectMatch update(ManagedSystemObjectMatch detachedInstance);

	/**
	 * Finds objects for an object type (like User, Group) for a ManagedSystem definition
	 * @param managedSystemId
	 * @param objectType
	 * @return
	 */
	public List<ManagedSystemObjectMatch> findBySystemId(String managedSystemId, String objectType);
	
	public ManagedSystemObjectMatch findById(String id);

	public abstract List<ManagedSystemObjectMatch> findByExample(ManagedSystemObjectMatch instance);

}