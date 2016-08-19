package org.openiam.provision.service;

import org.openiam.base.response.LookupObjectResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
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
public interface ObjectProvisionDataService<T> {
    /**
     *  The add operation enables a requester to create a new object on the
     *  target systems.
     *  Also this operation can do modify if this object has existed in one of the target systems.
     *
     * @param object - new provisioning object
     * @return Response
     * @throws Exception
     */
    Response add(T object) throws Exception;
    
    /**
     * This is a temporary method - not meant to be used externally.
     * It is a temporary solution that was created in order to remove the 'resources' collection
     * from the Group object
     *
     * @param object - provision object for modify
     * @return Response
     */
    public Response addResourceToGroup(T pGroup, String resourceId);

    /**
     * The modify operation enables the requester to modify an existing group
     * in appropriate target systems
     *
     * @param object - provision object for modify
     * @return Response
     */
    Response modify(T object);

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
    Response delete(String managedSystemId, String objectId, UserStatusEnum status, String requesterId);

    /**
     * The remove operation enables the requester to delete an existing object
     * from all target systems
     *
     * @param objectId - object ID
     * @param requesterId - requester
     * @return
     */
    Response remove(String objectId, String requesterId);
    /**
     * De-provisioning Object only from selected resources
     *
     * @param objectId - object id
     * @param requesterId - requestor
     * @param resourceList - selected resources
     * @return
     */
    Response deprovisionSelectedResources(String objectId, String requesterId, List<String> resourceList);

    /**
     * The modifyIdentity operation modifies an existing identity
     * in appropriate target systems
     *
     * @param identity - identity object for modify
     * @return Response
     */
    Response modifyIdentity(IdentityDto identity);


    LookupObjectResponse getTargetSystemObject(String principalName, String managedSysId, List<ExtensibleAttribute> attributes);
}
