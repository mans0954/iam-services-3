package org.openiam.provision.service;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionGroup;
import org.openiam.provision.resp.ProvisionGroupResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * <code>GroupProvisionService</code> Interface for the Provisioning service which is
 * used for provisioning group.
 *
 * @author suneet
 *
 */
@WebService(targetNamespace = "http://www.openiam.org/service/provision", name = "GroupProvisionControllerServicePort")
public interface GroupProvisionService {
    /**
     *  The addGroup operation enables a requester to create a new group on the
     *  target systems.
     *  Also this operation can do modify if this group has existed in one of the target systems.
     *
     * @param group - new provisioning group
     * @return ProvisionGroupResponse
     * @throws Exception
     */
    @WebMethod
    public ProvisionGroupResponse addGroup(
            @WebParam(name = "group", targetNamespace = "") ProvisionGroup group)
            throws Exception;

    /**
     * The modifyGroup operation enables the requester to modify an existing group
     * in appropriate target systems
     *
     * @param group - provision group for modify
     * @return ProvisionGroupResponse
     */
    @WebMethod
    public ProvisionGroupResponse modifyGroup (
            @WebParam(name = "group", targetNamespace = "") ProvisionGroup group);

    /**
     * The deleteGroup operation enables the requester to delete an existing group
     * from the appropriate target systems
     *
     * @param managedSystemId - target system
     * @param groupId - group ID
     * @param status - status od delete operation
     * @param requesterId - requester
     * @return
     */
    @WebMethod
    public ProvisionGroupResponse deleteGroup(
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "groupId", targetNamespace = "") String groupId,
            @WebParam(name = "status", targetNamespace = "") UserStatusEnum status,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * The removeGroup operation enables the requester to delete an existing group
     * from all target systems
     *
     * @param groupId - group ID
     * @param requesterId - requester
     * @return
     */
    @WebMethod
    public ProvisionGroupResponse removeGroup(
            @WebParam(name = "groupId", targetNamespace = "") String groupId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    /**
     * De-provisioning Group only from selected resources
     *
     * @param groupId - goup id
     * @param requesterId - requestor
     * @param resourceList - selected resources
     * @return
     */
    @WebMethod
    public ProvisionGroupResponse deprovisionSelectedResources(
            @WebParam(name = "groupId", targetNamespace = "") String groupId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
            @WebParam(name = "resourceList", targetNamespace = "") List<String> resourceList);
}
