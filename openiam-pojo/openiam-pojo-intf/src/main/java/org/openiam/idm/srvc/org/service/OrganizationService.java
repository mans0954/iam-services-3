package org.openiam.idm.srvc.org.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.user.dto.UserAttribute;

public interface OrganizationService {
	public Organization getOrganizationDTO(final String orgId);

    public OrganizationEntity getOrganization(final String orgId);

    public OrganizationEntity getOrganizationByName(final String name);

    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId);

    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue);

    public void fireUpdateOrgMap();

   // public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity lang);

    //public List<Location> getLocationListByPageForUser(String userId, Integer from, Integer size);

    public List<Organization> getOrganizationsDtoForUser(String userId, final int from, final int size);

    public List<OrganizationEntity> getParentOrganizations(final String orgId, final int from, final int size);

    public List<Organization> getParentOrganizationsDto(String orgId, int from, int size);

    //public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size, final LanguageEntity lang);

   // public List<Organization> getChildOrganizationsDto(String orgId, String requesterId, int from, int size, final LanguageEntity lang);

    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, final int from, final int size);
    public List<OrganizationEntity> getOrganizationsForUser(String userId, final int from, final int size);


    public List<Organization> findBeansDto(final OrganizationSearchBean searchBean, int from, int size);

    public List<Organization> getAllowedParentOrganizationsDtoForType(final String orgTypeId);

    /*public List<Organization> findOrganizationsDtoByAttributeValue(final String attrName, String attrValue, final LanguageEntity lang);

    public int getNumOfOrganizationsForUser(final String userId, final String requesterId);

    public int getNumOfParentOrganizations(final String orgId, String requesterId);

    public int getNumOfChildOrganizations(final String orgId, String requesterId);

    public void addUserToOrg(final String orgId, final String userId);

    public void addUserToOrg(String orgId, String userId, String metadataTypeId);*/


	public int count(final OrganizationSearchBean searchBean);
    public Organization save(final Organization organization, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	public Organization save(final Organization organization) throws BasicDataServiceException;
    public void addRequiredAttributes(OrganizationEntity organization);
	public void removeChildOrganization(final String organizationId, final String childOrganizationId);
	public void addChildOrganization(final String organizationId, final String childOrganizationId, final Set<String> rightIds, final Date startDate, final Date endDate);
	public Response deleteOrganization(final String orgId) throws BasicDataServiceException;
    public void deleteOrganization(final String orgId, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	public void validateOrg2OrgAddition(String parentId, String memberId, final Set<String> rightIds) throws BasicDataServiceException;
    public void validate(final Organization organization) throws BasicDataServiceException;

    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap);
    public Set<String> getDelegationFilter();

        public void addLocation(LocationEntity val);

    public void updateLocation(LocationEntity val);

    public void removeLocation(String locationId);

    public void removeAllLocations(String organizationId);

    public LocationEntity getLocationById(String locationId);

    public Location getLocationDtoById(String locationId);

    public List<LocationEntity> getLocationList(String organizationId);

    public List<Location> getLocationDtoList(String organizationId, boolean isDeep);

    public List<LocationEntity> getLocationList(String organizationId, int from, int size);

    public List<Location> getLocationDtoList(String organizationId, int from, int size);

    public List<LocationEntity> getLocationList(LocationSearchBean searchBean, int from, int size);

    public List<Location> getLocationDtoList(LocationSearchBean searchBean, int from, int size);

    public int getNumOfLocations(LocationSearchBean searchBean);

    public int getNumOfLocationsForUser(String userId);

    public int getNumOfLocationsForOrganization(String organizationId);

    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId, int from, int size);

    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId);
    
    public void addGroupToOrganization(final String organizationId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate);
    public void removeGroupFromOrganization(final String organizationId, final String groupId);
    
    public void addResourceToOrganization(final String organizationId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate);
    public void removeResourceFromOrganization(final String organizationId, final String groupId);
    
    public void addRoleToOrganization(final String organizationId, final String roleId, final Set<String> rightIds, final Date startDate, final Date endDate);
    public void removeRoleFromOrganization(final String organizationId, final String roleId);

    public List<Organization> getUserAffiliationsByType(String userId, String typeId, final int from, final int size);

    public List<OrganizationAttribute> getOrgAttributesDtoList(String orgId);
    public void saveAttribute(final OrganizationAttributeEntity attribute);


    public Response saveOrganization(final Organization organization);
    public Response saveOrganizationWithSkipPrePostProcessors(final Organization organization, final boolean skipPrePostProcessors);
    public Response deleteOrganizationWithSkipPrePostProcessors(final String orgId, final boolean skipPrePostProcessors) throws BasicDataServiceException;

    public Response addUserToOrg(final String orgId,
                                 final String userId,
                                 final Set<String> rightIds,
                                 final Date startDate,
                                 final Date endDate);

    public Response removeUserFromOrg(String orgId, String userId);
    
    public boolean isIndexed(String id);
}
