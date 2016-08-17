package org.openiam.idm.srvc.synch.service.generic;

import org.openiam.base.response.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;

/**
 * Interface for <code>GenericObjectSynchService</code> which is used to synchronize objects such as
 * Groups, Roles, Organizations and others. Synchronization for these activities is initiated from
 * startSynchronization()
 */
public interface GenericObjectSynchService {


    /**
     * Initiates the synchronization process
     * @param config
     * @return
     */
	SyncResponse startSynchronization(SynchConfig config);

}