package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.resp.LookupObjectResponse;
import org.openiam.provision.type.ExtensibleAttribute;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/**
 * <code>GroupProvisionService</code> Interface for the Provisioning service which is
 * used for provisioning group.
 *
 * @author suneet
 *
 */
@WebService(targetNamespace = "http://www.openiam.org/service/provision")
@XmlSeeAlso(ProvisionGroup.class)
public interface ObjectProvisionService<T> {
    /**
     *  The add operation enables a requester to create a new object on the
     *  target systems.
     *  Also this operation can do modify if this object has existed in one of the target systems.
     *
     * @param object - new provisioning object
     * @return Response
     * @throws Exception
     */
    @WebMethod
    Response add(
            @WebParam(name = "object", targetNamespace = "") T object)
            throws Exception;

    /**
     * The modify operation enables the requester to modify an existing group
     * in appropriate target systems
     *
     * @param object - provision object for modify
     * @return Response
     */
    @WebMethod
    Response modify(
            @WebParam(name = "object", targetNamespace = "") T object);

    /**
     * The delete operation enables the requester to delete an existing object
     * from the appropriate target systems
     *
     * @param managedSystemId - target system
     * @param objectId - object ID
     * @param status - status od delete operation
     * @param requesterId - requester
     * @return
     */
    @WebMethod
    Response delete(
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "objectId", targetNamespace = "") String objectId,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * The remove operation enables the requester to delete an existing object
     * from all target systems
     *
     * @param objectId - object ID
     * @param requesterId - requester
     * @return
     */
    @WebMethod
    Response remove(
            @WebParam(name = "objectId", targetNamespace = "") String objectId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    /**
     * De-provisioning Object only from selected resources
     *
     * @param objectId - object id
     * @param requesterId - requestor
     * @param resourceList - selected resources
     * @return
     */
    @WebMethod
    Response deprovisionSelectedResources(
            @WebParam(name = "objectId", targetNamespace = "") String objectId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
            @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList);

    /**
     * The modifyIdentity operation modifies an existing identity
     * in appropriate target systems
     *
     * @param identity - identity object for modify
     * @return Response
     */
    @WebMethod
    Response modifyIdentity(
            @WebParam(name = "identity", targetNamespace = "") IdentityDto identity);


    @WebMethod
    LookupObjectResponse getTargetSystemObject(
            @WebParam(name = "principalName", targetNamespace = "") String principalName,
            @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
            @WebParam(name = "attributes", targetNamespace = "") List<ExtensibleAttribute> attributes);
}
