package org.openiam.idm.srvc.synch.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.synch.dto.*;

import java.util.List;

/**
 * Interface for  <code>IdmAuditLogDataService</code>. All audit logging activities 
 * persisted through this service.
 */
@WebService(targetNamespace = "http://www.openiam.org/service/synch", name = "IdentitySynchWebService")
public interface IdentitySynchWebService {
	
	@WebMethod
	SynchConfigListResponse getAllConfig();
	
	@WebMethod
	SynchConfigResponse findById(
			@WebParam(name = "id", targetNamespace = "")
			java.lang.String id);
	
	@WebMethod
	SynchConfigResponse addConfig(
			@WebParam(name = "synchConfig", targetNamespace = "")
			SynchConfig synchConfig);

    @WebMethod
    SynchConfigResponse mergeConfig(
            @WebParam(name = "synchConfig", targetNamespace = "")
            SynchConfig synchConfig);

    @WebMethod
    Response removeConfig(
            @WebParam(name = "config", targetNamespace = "")
            String configId);

    @WebMethod
    Response testConnection(
            @WebParam(name = "synchConfig", targetNamespace = "")
            SynchConfig synchConfig);

    /**
     * Moves a set of users from resource or role. Users are selected based on some search criteria defined in the
     * config object.
     * @param config
     * @return
     */
    @WebMethod
    Response bulkUserMigration(
            @WebParam(name = "config", targetNamespace = "")
            BulkMigrationConfig config);

    /**
     * When resources associated with a role have been modified, the role membership needs to be resynchronized
     * @param roleId
     * @return
     */
    @WebMethod
    Response resynchRole(
            @WebParam(name = "roleId", targetNamespace = "")
            final String roleId);

    ImportSyncResponse importAttrMapFromCSV(String syncId);

    @WebMethod
	SyncResponse startSynchronization(
			@WebParam(name = "config", targetNamespace = "")
			SynchConfig config);

    @WebMethod
    SyncResponse startCustomSynchronization(
            @WebParam(name = "config", targetNamespace = "")
            SynchConfig config,
            @WebParam(name = "additionalValues", targetNamespace = "") String additionalValues);

    @WebMethod
    SynchReviewResponse executeSynchReview(
            @WebParam(name = "synchReviewRequest", targetNamespace = "")
            SynchReviewRequest synchReviewRequest);

    @WebMethod
    Integer getSynchConfigCount(
            @WebParam(name = "searchBean", targetNamespace = "")
            SynchConfigSearchBean searchBean);

    @WebMethod
    List<SynchConfig> getSynchConfigs(
            @WebParam(name = "searchBean", targetNamespace = "") SynchConfigSearchBean searchBean,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "from", targetNamespace = "") Integer from);

    @WebMethod
    List<AttributeMap> getSynchConfigAttributeMaps(
            @WebParam(name = "synchConfigId", targetNamespace = "") String synchConfigId);

    @WebMethod
    List<AttributeMap> findSynchConfigAttributeMaps(
            @WebParam(name = "searchBean", targetNamespace = "") AttributeMapSearchBean searchBean);

}