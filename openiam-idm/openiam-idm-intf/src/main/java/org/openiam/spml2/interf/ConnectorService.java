package org.openiam.spml2.interf;

import org.openiam.connector.type.SearchRequest;
import org.openiam.connector.type.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;

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
    public ResponseType testConnection(@WebParam(name = "requestType", targetNamespace = "") TestRequestType<? extends GenericProvisionObject> requestType);


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
    AddResponseType add(@WebParam(name = "reqType", targetNamespace = "")  AddRequestType<? extends GenericProvisionObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/modify")
    ModifyResponseType modify(
            @WebParam(name = "reqType", targetNamespace = "")
            ModifyRequestType<? extends GenericProvisionObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/delete")
    ResponseType delete(
            @WebParam(name = "reqType", targetNamespace = "")
            DeleteRequestType<? extends GenericProvisionObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookup")
    LookupResponseType lookup(
            @WebParam(name = "reqType", targetNamespace = "")
            LookupRequestType<? extends GenericProvisionObject> reqType);

    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/lookupAttributeNames")
    LookupAttributeResponseType lookupAttributeNames(@WebParam(name = "reqType", targetNamespace = "") LookupAttributeRequestType<? extends GenericProvisionObject> reqType);
    
    @WebMethod(action="http://www.openiam.org/service/connector/ConnectorService/search")
    SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "")
            SearchRequest searchRequest);

    /**
     * The setPassword operation enables a requestor to specify a new password for an object
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/setPassword")
    ResponseType setPassword(
            @WebParam(name = "request", targetNamespace = "")
            SetPasswordRequestType request);

    /**
     * The expirePassword operation marks as invalid the current password for an object
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/expirePassword")
    ResponseType expirePassword(
            @WebParam(name = "request", targetNamespace = "")
            ExpirePasswordRequestType request);

    /**
     * The resetPassword operation enables a requestor to change (to an unspecified value) the
     * password for an object and to obtain that newly generated password value.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resetPassword")
    ResetPasswordResponseType resetPassword(
            @WebParam(name = "request", targetNamespace = "")
            ResetPasswordRequestType request);

    /**
     * The validatePassword operation enables a requestor to determine whether a specified value would
     * be valid as the password for a specified object.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/validatePassword")
    ValidatePasswordResponseType validatePassword(
            @WebParam(name = "request", targetNamespace = "")
            ValidatePasswordRequestType request);


    /**
     * Suspend / disables a user
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/suspend")
    ResponseType suspend(
            @WebParam(name = "request", targetNamespace = "")
            SuspendRequestType request);

    /**
     * Restores a user that was previously disabled.
     *
     * @param request
     * @return
     */
    @WebMethod(action = "http://www.openiam.org/service/connector/ConnectorService/resume")
    ResponseType resume(
            @WebParam(name = "request", targetNamespace = "")
            ResumeRequestType request);

}

