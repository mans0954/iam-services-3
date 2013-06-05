package org.openiam.idm.srvc.mngsys.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.dto.*;

/**
 * Interface for <code>ManagedSystemWebService</code>
 * 
 * @author suneet shah
 * 
 */
@WebService
public interface ManagedSystemWebService {

    /**
     * 
     * @param searchBean
     * @return
     */
    @WebMethod
    public Integer getManagedSystemsCount(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean);

    /**
     * Return an list of the ManagedSys object in the system by SearchBean
     * 
     * @return the managed systems
     */
    @WebMethod
    List<ManagedSysDto> getManagedSystems(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "from", targetNamespace = "") Integer from);

    /**
     * Returns a ManagedSys object for the specified systemId.
     * 
     * @param sysId
     *            the sys id
     * @return the managed sys
     */
    @WebMethod
    ManagedSysDto getManagedSys(
            @WebParam(name = "sysId", targetNamespace = "") String sysId);

    /**
     * Returns a ManagedSys object for the specified name. The name is the value
     * in the name field in the ManagedSys object.
     * 
     * @param name
     *            the name
     * @return the managed sys by name
     */
    @WebMethod
    ManagedSysDto getManagedSysByName(
            @WebParam(name = "name", targetNamespace = "") String name);

    /**
     * Returns an array of ManagedSys object for a provider. The providerId is
     * the same as the connectorId.
     * 
     * @param providerId
     *            the provider id
     * @return the managed sys by provider
     */
    @WebMethod
    ManagedSysDto[] getManagedSysByProvider(
            @WebParam(name = "providerId", targetNamespace = "") String providerId);

    /**
     * Returns an array of ManagedSys object for a security domain.
     * 
     * @param domainId
     *            the domain id
     * @return the managed sys by domain
     */
    @WebMethod
    ManagedSysDto[] getManagedSysByDomain(
            @WebParam(name = "domainId", targetNamespace = "") String domainId);

    @WebMethod
    ManagedSysDto[] getAllManagedSys();

    /**
     * Gets the managed sys by resource.
     * 
     * @param resourceId
     *            the resource id
     * @return the managed sys by resource
     */
    @WebMethod
    ManagedSysDto getManagedSysByResource(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    /**
     * Creates a new managed system entry into the system. The ManagedSystemId
     * is auto-generated. Required fields include: <li>ConnectorId <li>DomainId
     * 
     * @param sys
     *            the sys
     */
    @WebMethod
    ManagedSysDto addManagedSystem(
            @WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys);

    /**
     * Updates an existing managed system entry.
     * 
     * @param sys
     *            the sys
     */
    @WebMethod
    void updateManagedSystem(
            @WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys);

    /**
     * Removes a managed system entry from the system.
     * 
     * @param sysId
     *            the sys id
     */
    @WebMethod
    void removeManagedSystem(
            @WebParam(name = "sysId", targetNamespace = "") String sysId);

    /**
     * Finds objects for an object type (like User, Group) for a ManagedSystem
     * definition.
     * 
     * @param managedSystemId
     *            the managed system id
     * @param objectType
     *            the object type
     * @return the managed system object match[]
     */
    @WebMethod
    public ManagedSystemObjectMatch[] managedSysObjectParam(
            @WebParam(name = "managedSystemId", targetNamespace = "") String managedSystemId,
            @WebParam(name = "objectType", targetNamespace = "") String objectType);

    @WebMethod
    public void addManagedSystemObjectMatch(
            @WebParam(name = "obj", targetNamespace = "") ManagedSystemObjectMatch obj);

    @WebMethod
    public void updateManagedSystemObjectMatch(
            @WebParam(name = "obj", targetNamespace = "") ManagedSystemObjectMatch obj);

    @WebMethod
    public void removeManagedSystemObjectMatch(
            @WebParam(name = "obj", targetNamespace = "") ManagedSystemObjectMatch obj);

    /**
     * Gets the approver association.
     * 
     * @param approverAssociationId
     *            the approver association id
     * @return the approver association
     */
    @WebMethod
    ApproverAssociation getApproverAssociation(
            @WebParam(name = "approverAssociationId", targetNamespace = "") String approverAssociationId);

    /**
     * Removes the approver association.
     * 
     * @param approverAssociationId
     *            the approver association id
     */
    @WebMethod
    Response removeApproverAssociation(
            @WebParam(name = "approverAssociationId", targetNamespace = "") String approverAssociationId);

    /**
     * Gets the attribute map.
     * 
     * @param attributeMapId
     *            the attribute map id
     * @return the attribute map
     */
    @WebMethod
    AttributeMap getAttributeMap(
            @WebParam(name = "attributeMapId", targetNamespace = "") String attributeMapId);

    /**
     * Adds the attribute map.
     * 
     * @param attributeMap
     *            the attribute map
     * @return the attribute map
     */
    @WebMethod
    AttributeMap addAttributeMap(
            @WebParam(name = "attributeMap", targetNamespace = "") AttributeMap attributeMap);

    /**
     * Updates attribute map.
     * 
     * @param attributeMap
     *            the attribute map
     * @return the attribute map
     */
    @WebMethod
    AttributeMap updateAttributeMap(
            @WebParam(name = "attributeMap", targetNamespace = "") AttributeMap attributeMap);

    /**
     * Removes the attribute map.
     * 
     * @param attributeMapId
     *            the attribute map id
     */
    @WebMethod
    void removeAttributeMap(
            @WebParam(name = "attributeMapId", targetNamespace = "") String attributeMapId);

    /**
     * Removes the resource attribute maps.
     * 
     * @param resourceId
     *            the resource id
     * @return the int
     */
    @WebMethod
    int removeResourceAttributeMaps(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    /**
     * Return the AttributeMap for the specified resourceId.
     * 
     * @param resourceId
     *            the resource id
     * @return the attribute map for resource
     */
    @WebMethod
    List<AttributeMap> getResourceAttributeMaps(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId);

    /**
     * Gets the all attribute maps.
     * 
     * @return the all attribute maps
     */
    @WebMethod
    List<AttributeMap> getAllAttributeMaps();

    @WebMethod
    List<DefaultReconciliationAttributeMap> getAllDefaultReconcileMap();

    @WebMethod
    public Response saveApproverAssociation(
            final @WebParam(name = "approverAssociation", targetNamespace = "") ApproverAssociation approverAssociation);
    
	@WebMethod
	public List<ApproverAssociation> getApproverAssociations(final @WebParam(name = "approverAssociation", targetNamespace = "") ApproverAssocationSearchBean searchBean,
															 final @WebParam(name="from", targetNamespace = "") int from,
															 final @WebParam(name="size", targetNamespace = "") int size);
}