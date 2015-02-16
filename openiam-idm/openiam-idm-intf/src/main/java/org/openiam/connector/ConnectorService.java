package org.openiam.connector;

import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.type.ExtensibleObject;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


/**
 * Provides a consolidated interface that is to be used by all connectors..
 *
 * @author suneet
 */
@WebService(targetNamespace = "http://www.openiam.org/service/connector")
public interface ConnectorService {

    @WebMethod
    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config);

    /**
     * The add operation enables a requestor to create a new object on a target
     * Attributes used by the operation are: <br>
     * <li>PSOId: Unique identifier for the new object
     * <li>containerId: Object where this new object should be created in. In a directory, it can be a base DN such as: ou=eng, dc=openiam, dc=org
     * <li>data: Collection of data attributes that are to be stored in the target system
     * <li>targetId: An id that is unique for the provider and is the system where this new object
     * is to be created.
     * <li>returnData:
     *
     * @param reqType
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/add")
    ObjectResponse add( @WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/modify")
    ObjectResponse modify(@WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/testConnection")
    ResponseType testConnection( @WebParam(name = "reqType", targetNamespace = "") RequestType<? extends ExtensibleObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/delete")
    ObjectResponse delete(@WebParam(name = "reqType", targetNamespace = "") CrudRequest<? extends ExtensibleObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookup"  )
    SearchResponse lookup(@WebParam(name = "request", targetNamespace = "") LookupRequest<? extends ExtensibleObject> request);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookupAttributeNames")
    LookupAttributeResponse lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupRequest<? extends ExtensibleObject> reqType);
    
    @WebMethod(action="http://www.openiam.org/service/connector/ConnectorService/search")
    SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest<? extends ExtensibleObject> searchRequest);

    /**
     * @deprecated  Please use "resetPassword" mthod instead
     * The setPassword operation enables a requestor to specify a new password for an object
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/setPassword")
    @Deprecated

    ResponseType setPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request);

    /**
     * The expirePassword operation marks as invalid the current password for an object
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/expirePassword")
    ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request);

    /**
     * The resetPassword operation enables a requestor to change (to an unspecified value) the
     * password for an object and to obtain that newly generated password value.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resetPassword")
    ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request);

    /**
     * The validatePassword operation enables a requestor to determine whether a specified value would
     * be valid as the password for a specified object.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/validatePassword")
    ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request);


    /**
     * Suspend / disables a user
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/suspend")
    ResponseType suspend( @WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request);

    /**
     * Restores a user that was previously disabled.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resume")
    ResponseType resume(@WebParam(name = "request", targetNamespace = "") SuspendResumeRequest request);
}

