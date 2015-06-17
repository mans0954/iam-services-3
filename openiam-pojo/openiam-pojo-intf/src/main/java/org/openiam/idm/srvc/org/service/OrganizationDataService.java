package org.openiam.idm.srvc.org.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationDataService")
public interface OrganizationDataService {

	@WebMethod
    Response validateEdit(final @WebParam(name = "organization", targetNamespace = "") Organization organization);
	
	@WebMethod
    Response validateDelete(final @WebParam(name = "orgId", targetNamespace = "") String id);

    @WebMethod
    @Deprecated
    Organization getOrganization(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                 final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    Organization getOrganizationLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                          final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    int getNumOfOrganizationsForUser(@WebParam(name = "userId", targetNamespace = "") String userId,
                                     @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    @Deprecated
    List<Organization> getOrganizationsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                               final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                               final @WebParam(name = "from", targetNamespace = "") int from,
                                               final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    List<Organization> getOrganizationsForUserLocalized(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                        final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                        final @WebParam(name = "from", targetNamespace = "") int from,
                                                        final @WebParam(name = "size", targetNamespace = "") int size,
                                                        final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    List<Organization> getOrganizationsForUserByType(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                     final @WebParam(name = "organizationTypeId", targetNamespace = "") String organizationTypeId);
    @WebMethod
    List<Organization> getOrganizationsForUserByTypeLocalized(final @WebParam(name = "userId", targetNamespace = "") String userId,
                                                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                              final @WebParam(name = "organizationTypeId", targetNamespace = "") String organizationTypeId,
                                                              final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    /**
     * Without localization for internal se only
     * Performance optimized method
     */ List<Organization> findBeans(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                     final @WebParam(name = "from", targetNamespace = "") int from,
                                     final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    List<Organization> findBeansLocalized(final @WebParam(name = "searchBean", targetNamespace = "") OrganizationSearchBean searchBean,
                                          final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                          final @WebParam(name = "from", targetNamespace = "") int from,
                                          final @WebParam(name = "size", targetNamespace = "") int size,
                                          final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    List<Organization> getParentOrganizations(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                              final @WebParam(name = "from", targetNamespace = "") int from,
                                              final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    List<Organization> getParentOrganizationsLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                       final @WebParam(name = "from", targetNamespace = "") int from,
                                                       final @WebParam(name = "size", targetNamespace = "") int size,
                                                       final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    List<Organization> getChildOrganizations(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                             final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                             final @WebParam(name = "from", targetNamespace = "") int from,
                                             final @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    List<Organization> getChildOrganizationsLocalized(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                      final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                      final @WebParam(name = "from", targetNamespace = "") int from,
                                                      final @WebParam(name = "size", targetNamespace = "") int size,
                                                      final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    int count(@WebParam(name="searchBean", targetNamespace="") OrganizationSearchBean searchBean,
              @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    int getNumOfParentOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                    @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    int getNumOfChildOrganizations(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                                   @WebParam(name = "requesterId", targetNamespace = "") String requesterId);


    @WebMethod
    Response saveOrganization(final @WebParam(name = "organization", targetNamespace = "") Organization organization,
                              final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    Response saveOrganizationWithSkipPrePostProcessors(final @WebParam(name = "organization", targetNamespace = "") Organization organization,
                                                       final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                       final @WebParam(name = "skipPrePostProcessors", targetNamespace = "") boolean skipPrePostProcessors);

    @WebMethod
    Response addUserToOrg(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                          @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    Response addChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                  @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);


    @WebMethod
    Response deleteOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    Response deleteOrganizationWithSkipPrePostProcessors(final @WebParam(name = "orgId", targetNamespace = "") String orgId,
                                                         final @WebParam(name = "skipPrePostProcessors", targetNamespace = "") boolean skipPrePostProcessors);

    @WebMethod
    Response removeUserFromOrg(@WebParam(name = "orgId", targetNamespace = "") String orgId,
                               @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    Response removeChildOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                     @WebParam(name = "childOrganizationId", targetNamespace = "") String childOrganizationId);

    @WebMethod
    Response canAddUserToOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                      @WebParam(name = "userId", targetNamespace = "") String userId);
    
    @WebMethod
    Response canRemoveUserToOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                         @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    @Deprecated
    List<Organization> getAllowedParentOrganizationsForType(final @WebParam(name = "orgTypeId", targetNamespace = "") String orgTypeId,
                                                            final @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    List<Organization> getAllowedParentOrganizationsForTypeLocalized(final @WebParam(name = "orgTypeId", targetNamespace = "") String orgTypeId,
                                                                     final @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                                                     final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    @Deprecated
    List<Organization> findOrganizationsByAttributeValue(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                                         final @WebParam(name = "attrValue", targetNamespace = "") String attrValue);
    @WebMethod
    List<Organization> findOrganizationsByAttributeValueLocalized(final @WebParam(name = "attrName", targetNamespace = "") String attrName,
                                                                  final @WebParam(name = "attrValue", targetNamespace = "") String attrValue,
                                                                  final @WebParam(name = "language", targetNamespace = "") Language language);


    @WebMethod
    Response addLocation(@WebParam(name = "location", targetNamespace = "") Location location);


    @WebMethod
    Response updateLocation(@WebParam(name = "location", targetNamespace = "") Location location);


    @WebMethod
    Response removeLocation(@WebParam(name = "location", targetNamespace = "") String locationId);


    @WebMethod
    Location getLocationById(@WebParam(name = "locationId", targetNamespace = "") String locationId);


    @WebMethod
    List<Location> getLocationList(@WebParam(name = "organizationId", targetNamespace = "") String organizationId);


    @WebMethod
    List<Location> getLocationListByPage(@WebParam(name = "organizationId", targetNamespace = "") String organizationId,
                                         @WebParam(name = "from", targetNamespace = "") Integer from,
                                         @WebParam(name = "size", targetNamespace = "") Integer size);
    @WebMethod
    List<Location> findLocationBeans(@WebParam(name = "searchBean", targetNamespace = "") LocationSearchBean searchBean,
                                     @WebParam(name = "from", targetNamespace = "") int from,
                                     @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
    int getNumOfLocations(@WebParam(name = "searchBean", targetNamespace = "") LocationSearchBean searchBean);

    @WebMethod
    int getNumOfLocationsForOrganization(@WebParam(name = "organizationId", targetNamespace = "") String organizationId);

    @WebMethod
    int getNumOfLocationsForUser(@WebParam(name = "userId", targetNamespace = "") String organizationId);

    @WebMethod
    List<Location> getLocationListByPageForUser(@WebParam(name = "userId", targetNamespace = "") String organizationId,
                                                @WebParam(name = "from", targetNamespace = "") Integer from,
                                                @WebParam(name = "size", targetNamespace = "") Integer size);
}
