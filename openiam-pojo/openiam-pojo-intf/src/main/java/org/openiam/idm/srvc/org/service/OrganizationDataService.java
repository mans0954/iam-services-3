package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.MembershipOrganizationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


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
    public int getNumOfParentOrganizations(@WebParam(name = "searchBean", targetNamespace = "")
                                               MembershipOrganizationSearchBean searchBean);
    
    @WebMethod
    public int getNumOfChildOrganizations(@WebParam(name = "searchBean", targetNamespace = "") MembershipOrganizationSearchBean searchBean);
    
    @WebMethod
    public List<Organization> getParentOrganizations(@WebParam(name = "searchBean", targetNamespace = "") MembershipOrganizationSearchBean searchBean,
                             						 @WebParam(name = "from", targetNamespace = "") int from,
                             						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public List<Organization> getChildOrganizations(@WebParam(name = "searchBean", targetNamespace = "") MembershipOrganizationSearchBean searchBean,
                             						 @WebParam(name = "from", targetNamespace = "") int from,
                             						 @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public Response addChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
    									 @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);
    
    @WebMethod
    public Response removeChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
    									 	@WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);
}
