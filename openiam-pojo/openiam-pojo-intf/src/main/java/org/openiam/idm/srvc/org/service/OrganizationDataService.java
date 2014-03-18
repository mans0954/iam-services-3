package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationDataService")
public interface OrganizationDataService {

	@WebMethod
	public Response validateEdit(final @WebParam(name = "organization", targetNamespace = "") Organization organization);
	
	@WebMethod
	public Response validateDelete(final @WebParam(name = "orgId", targetNamespace = "") String id);
	
    @WebMethod
    public Organization getOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public int getNumOfOrganizationsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                      	   @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    
    @WebMethod
    public List<Organization> getOrganizationsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                      @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                      @WebParam(name = "from", targetNamespace = "") int from,
                                                      @WebParam(name = "size", targetNamespace = "") int size);
    
    @WebMethod
    public List<Organization> getOrganizationsForUserByType(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                      		@WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                      		@WebParam(name="organizationTypeId", targetNamespace = "") String organizationTypeId);

    @WebMethod
    public List<Organization> getAllOrganizations(@WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<Organization> findBeans(@WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        @WebParam(name = "from", targetNamespace = "") int from,
                                        @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> getParentOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                     @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                     @WebParam(name = "from", targetNamespace = "") int from,
                                                     @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> getChildOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                    @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                    @WebParam(name = "from", targetNamespace = "") int from,
                                                    @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    int count(@WebParam(name="searchBean", targetNamespace="") OrganizationSearchBean searchBean,
              @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public int getNumOfParentOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                           @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public int getNumOfChildOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                          @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    @WebMethod
    public Response saveOrganization(final @WebParam(name = "organization", targetNamespace = "") Organization organization, 
    								 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public Response addUserToOrg(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                 @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Response addChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                         @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);


    @WebMethod
    public Response deleteOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    public Response removeUserFromOrg(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                      @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Response removeChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                            @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);

    @WebMethod
    public Response canAddUserToOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                            @WebParam(name = "userId", targetNamespace = "") String userId);
    
    @WebMethod
    public Response canRemoveUserToOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
            									@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<Organization> getAllowedParentOrganizationsForType(@WebParam(name = "orgTypeId", targetNamespace = "") final String orgTypeId,
                                                                         @WebParam(name = "requesterId", targetNamespace = "") String requesterId);
    @WebMethod
    public List<Organization> findOrganizationsByAttributeValue(@WebParam(name = "attrName", targetNamespace = "") final String attrName,
                                                                @WebParam(name = "attrValue", targetNamespace = "") final String attrValue);
}
