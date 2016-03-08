package org.openiam.idm.srvc.mngsys.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.MngSysPolicySearchBean;
import org.openiam.idm.srvc.mngsys.bean.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.bean.MngSysPolicyBean;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.msg.dto.ManagedSysSearchBean;

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
    public int getManagedSystemsCount(
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

    
    @WebMethod
    List<ManagedSysDto> getAllManagedSys();

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
    Response saveManagedSystem(
            @WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys);

    /**
     * Send request for SSL Certificate and install it
     *
     * @param sys
     * @return
     */
    @WebMethod
    Response requestSSLCert(
            @WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Removes a managed system entry from the system.
     * 
     * @param sysId
     *            the sys id
     */
    @WebMethod
    Response removeManagedSystem(
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
    public Response saveManagedSystemObjectMatch(
            @WebParam(name = "obj", targetNamespace = "") ManagedSystemObjectMatch obj);

    @WebMethod
    public void removeManagedSystemObjectMatch(
            @WebParam(name = "obj", targetNamespace = "") ManagedSystemObjectMatch obj);

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
    void removeResourceAttributeMaps(
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

    @WebMethod
    List<AttributeMap> getAttributeMapsByMngSysPolicyId(@WebParam(name = "mngSysPolicyId", targetNamespace = "") String mngSysPolicyId);

    @WebMethod
    public List<AttributeMap> findResourceAttributeMaps(
            @WebParam(name = "searchBean", targetNamespace = "") AttributeMapSearchBean searchBean);

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
    MngSysPolicyDto getMngSysPolicyById(@WebParam(name = "mngSysPolicyId", targetNamespace = "") String mngSysPolicyId);

    @WebMethod
    MngSysPolicyBean getMngSysPolicyBeanById(@WebParam(name = "mngSysPolicyId", targetNamespace = "") String mngSysPolicyId);

    @WebMethod
    List<MngSysPolicyDto> getMngSysPoliciesByMngSysId(@WebParam(name = "mngSysId", targetNamespace = "") String mngSysId);

    @WebMethod
    List<MngSysPolicyDto> findMngSysPolicies(@WebParam(name = "searchBean", targetNamespace = "") MngSysPolicySearchBean searchBean,
                                             @WebParam(name = "from", targetNamespace = "") Integer from,
                                             @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    List<MngSysPolicyBean> findMngSysPolicyBeans(@WebParam(name = "searchBean", targetNamespace = "") MngSysPolicySearchBean searchBean,
                                                 @WebParam(name = "from", targetNamespace = "") Integer from,
                                                 @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    int getMngSysPoliciesCount(@WebParam(name = "searchBean", targetNamespace = "") MngSysPolicySearchBean searchBean);

    @WebMethod
    public Response saveApproverAssociations(final List<ApproverAssociation> approverAssociationList, final AssociationType type, final String entityId);
    
    @WebMethod
    public Response saveApproverAssociation(
            final @WebParam(name = "approverAssociation", targetNamespace = "") ApproverAssociation approverAssociation);

    @WebMethod
    public List<ApproverAssociation> getApproverAssociations(
            final @WebParam(name = "approverAssociation", targetNamespace = "") ApproverAssocationSearchBean searchBean,
            final @WebParam(name = "from", targetNamespace = "") int from,
            final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    List<ManagedSysRuleDto> getRulesByManagedSysId(
            final @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId);

    @WebMethod
    ManagedSysRuleDto addRules(
            final @WebParam(name = "entity", targetNamespace = "") ManagedSysRuleDto entity);

    @WebMethod
    void removeMngSysPolicy(
            final @WebParam(name = "mngSysPolicyId", targetNamespace = "") String mngSysPolicyId) throws Exception;

    @WebMethod
    void deleteRules(
            final @WebParam(name = "ruleId", targetNamespace = "") String ruleId);

    @WebMethod
    List<AttributeMap> saveAttributesMap(
            final @WebParam(name = "attrMap", targetNamespace = "") List<AttributeMap> attrMap,
            final @WebParam(name = "mSysId", targetNamespace = "") String mSysId,
            final @WebParam(name = "resId", targetNamespace = "") String resId,
            final @WebParam(name = "synchConfigId", targetNamespace = "") String synchConfigId)
            throws Exception;

    void deleteAttributesMapList(List<String> ids) throws Exception;

    @WebMethod
    Response saveMngSysPolicyBean(
            final @WebParam(name = "mngSysPolicy", targetNamespace = "") MngSysPolicyBean mngSysPolicy);

}