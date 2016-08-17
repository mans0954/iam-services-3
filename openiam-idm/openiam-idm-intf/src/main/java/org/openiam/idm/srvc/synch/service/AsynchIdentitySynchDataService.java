package org.openiam.idm.srvc.synch.service;

import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReviewRequest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interface for <code>AsynchIdentitySynchService</code>. This interface is used in an asynchronous manner.
 */
public interface AsynchIdentitySynchDataService {

	void startSynchronization(SynchConfig config);

    void startCustomSynchronization(SynchConfig config, final String additionalValues);

    void executeSynchReview(SynchReviewRequest synchReviewRequest);

    /**
     * Moves a set of users from resource or role. Users are selected based on some search criteria defined in the
     * config object.
     * @param config
     * @return
     */
    void bulkUserMigration(BulkMigrationConfig config);

    /**
     * Asynchronous interface to allow for role re-synchroniation.
     * When resources associated with a role have been modified, the role membership needs to be resynchronized
     * @param roleId
     * @return
     */
    void resynchRole(final String roleId);
}