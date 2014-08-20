package org.openiam.idm.srvc.synch.ws;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.synch.dto.*;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interface for <code>AsynchIdentitySynchService</code>. This interface is used in an asynchronous manner.
 */
@WebService(targetNamespace = "http://www.openiam.org/service/synch", name = "AsynchIdentitySynchService")
public interface AsynchIdentitySynchService {

	@WebMethod
	void startSynchronization(
			@WebParam(name = "config", targetNamespace = "")
			SynchConfig config);

    @WebMethod
    void executeSynchReview(
            @WebParam(name = "synchReviewRequest", targetNamespace = "")
            SynchReviewRequest synchReviewRequest);

    /**
     * Moves a set of users from resource or role. Users are selected based on some search criteria defined in the
     * config object.
     * @param config
     * @return
     */
    @WebMethod
    void bulkUserMigration(
            @WebParam(name = "config", targetNamespace = "")
            BulkMigrationConfig config);

    /**
     * Asynchronous interface to allow for role re-synchroniation.
     * When resources associated with a role have been modified, the role membership needs to be resynchronized
     * @param roleId
     * @return
     */
    @WebMethod
    void resynchRole(
            @WebParam(name = "roleId", targetNamespace = "")
            final String roleId);
}