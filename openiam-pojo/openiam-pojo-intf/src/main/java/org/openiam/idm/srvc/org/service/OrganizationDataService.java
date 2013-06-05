package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.dto.OrganizationAttributeMapAdapter;
import org.openiam.idm.srvc.org.dto.UserAffiliation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.util.List;
import java.util.Map;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationDataService")
public interface OrganizationDataService {

    @WebMethod
    public List<Organization> getTopLevelOrganizations();

    @WebMethod
    public Response removeAttribute(final @WebParam(name = "attributeId", targetNamespace = "") String attributeId);

    @WebMethod
    public Organization getOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    public Response saveOrganization(@WebParam(name = "organization", targetNamespace = "") Organization organization);

    @WebMethod
    public Response saveAttribute(@WebParam(name = "organizationAttribute", targetNamespace = "") OrganizationAttribute organizationAttribute);

    @WebMethod
    public Response deleteOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    public List<Organization> getAllOrganizations();

    @WebMethod
    public List<Organization> getOrganizationsForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Response addUserToOrg(
            @WebParam(name = "orgId", targetNamespace = "")
            String orgId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    @WebMethod
    public Response removeUserFromOrg(
            @WebParam(name = "orgId", targetNamespace = "")
            String orgId,
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    @WebMethod
    List<Organization> findBeans(@WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
                             @WebParam(name = "from", targetNamespace = "") int from,
                             @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    int count(@WebParam(name="searchBean", targetNamespace="") OrganizationSearchBean searchBean);
    
    @WebMethod
    public int getNumOfParentOrganizations(@WebParam(name = "organizationId", targetNamespace = "") String organizationId);
    
    @WebMethod
    public int getNumOfChildOrganizations(@WebParam(name = "organizationId", targetNamespace = "") String organizationId);
    
    @WebMethod
    public List<Organization> getParentOrganizations(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                             						 @WebParam(name = "from", targetNamespace = "") int from,
                             						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public List<Organization> getChildOrganizations(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                             						 @WebParam(name = "from", targetNamespace = "") int from,
                             						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public Response addChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
    									 @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);
    
    @WebMethod
    public Response removeChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
    									 	@WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);
}
