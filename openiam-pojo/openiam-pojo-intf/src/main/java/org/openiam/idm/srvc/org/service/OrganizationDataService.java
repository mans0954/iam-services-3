package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
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
    @Deprecated
    public Organization getOrganization(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public Organization getOrganizationLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    public int getNumOfOrganizationsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                      	   @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    @Deprecated
    public List<Organization> getOrganizationsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                               final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                               final @WebParam(name = "from", targetNamespace = "") int from,
                                                               final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> getOrganizationsForUserLocalized(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                      final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                      final @WebParam(name = "from", targetNamespace = "") int from,
                                                      final @WebParam(name = "size", targetNamespace = "") int size,
                                                      final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    public List<Organization> getOrganizationsForUserByType(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                                     final @WebParam(name="organizationTypeId", targetNamespace = "") String organizationTypeId);
    @WebMethod
    public List<Organization> getOrganizationsForUserByTypeLocalized(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                      		final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                      		final @WebParam(name="organizationTypeId", targetNamespace = "") String organizationTypeId,
                                                      		final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    public List<Organization> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
                                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                 final @WebParam(name = "from", targetNamespace = "") int from,
                                                 final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> findBeansLocalized(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
    									final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                        final @WebParam(name = "from", targetNamespace = "") int from,
                                        final @WebParam(name = "size", targetNamespace = "") int size,
                                        final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    public List<Organization> getParentOrganizations(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                              final @WebParam(name = "from", targetNamespace = "") int from,
                                                              final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> getParentOrganizationsLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
    												 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                     final @WebParam(name = "from", targetNamespace = "") int from,
                                                     final @WebParam(name = "size", targetNamespace = "") int size,
                                                     final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    public List<Organization> getChildOrganizations(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                             final @WebParam(name = "from", targetNamespace = "") int from,
                                                             final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    public List<Organization> getChildOrganizationsLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
    												final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                    final @WebParam(name = "from", targetNamespace = "") int from,
                                                    final @WebParam(name = "size", targetNamespace = "") int size,
                                                    final @WebParam(name = "language", targetNamespace = "") Language language);

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
    @Deprecated
    public List<Organization> getAllowedParentOrganizationsForType(final @WebParam(name = "orgTypeId", targetNamespace = "") String orgTypeId,
                                                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<Organization> getAllowedParentOrganizationsForTypeLocalized(final @WebParam(name = "orgTypeId", targetNamespace = "") String orgTypeId,
                                                                   final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                                   final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    public List<Organization> findOrganizationsByAttributeValue(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                                                         final @WebParam(name = "attrValue", targetNamespace = "") String attrValue);
    @WebMethod
    public List<Organization> findOrganizationsByAttributeValueLocalized(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                                                final @WebParam(name = "attrValue", targetNamespace = "") String attrValue,
                                                                final @WebParam(name = "language", targetNamespace = "") Language language);
}
