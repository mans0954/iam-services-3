package org.openiam.idm.srvc.org.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OrganizationService {
    @Deprecated
	public Organization getOrganizationDTO(final String orgId);
    @Deprecated
    public OrganizationEntity getOrganization(String orgId);
    @Deprecated
    public OrganizationEntity getOrganization(final String orgId, String requesterId);
    @Deprecated
    public OrganizationEntity getOrganizationByName(final String name, String requesterId);
    @Deprecated
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size);
    @Deprecated
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size);
    @Deprecated
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size);
    @Deprecated
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size);
    @Deprecated
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId);
    @Deprecated
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue);

    public void fireUpdateOrgMap();

    public Organization getOrganizationDTO(final String orgId, final LanguageEntity langauge);
    public OrganizationEntity getOrganizationLocalized(String orgId, final LanguageEntity langauge);
    public OrganizationEntity getOrganizationLocalized(final String orgId, String requesterId, final LanguageEntity langauge);
    public OrganizationEntity getOrganizationByName(final String name, String requesterId, final LanguageEntity langauge);
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size, final LanguageEntity langauge);
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId, final LanguageEntity langauge);
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue, final LanguageEntity langauge);

    public int getNumOfOrganizationsForUser(final String userId, final String requesterId);
	public int count(final OrganizationSearchBean searchBean, String requesterId);
    public int getNumOfParentOrganizations(final String orgId, String requesterId);
    public int getNumOfChildOrganizations(final String orgId, String requesterId);
	public void addUserToOrg(final String orgId, final String userId);
	public void removeUserFromOrg(String orgId, String userId);
	public void removeAttribute(final String attributeId);
    public Organization save(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	public Organization save(final Organization organization, final String requestorId) throws BasicDataServiceException;
    public void addRequiredAttributes(OrganizationEntity organization);
	public void save(final OrganizationAttributeEntity attribute);
	public void removeChildOrganization(final String organizationId, final String childOrganizationId);
	public void addChildOrganization(final String organizationId, final String childOrganizationId);
	public void deleteOrganization(final String orgId) throws BasicDataServiceException;
    public void deleteOrganization(final String orgId, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	public void validateOrg2OrgAddition(String parentId, String memberId) throws BasicDataServiceException;
    public void validate(final Organization organization) throws BasicDataServiceException;

    public Set<String> getDelegationFilter(String requesterId, String organizationTypeId);
    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, String organizationTypeId);

    public void addLocation(LocationEntity val);
    public void updateLocation(LocationEntity val);
    public void removeLocation(String locationId);
    public void removeAllLocations(String organizationId);
    public LocationEntity getLocationById(String locationId);
    public List<LocationEntity> getLocationList(String organizationId);

    public List<Location> getLocationDtoList(String organizationId, boolean isDeep);

    public List<LocationEntity> getLocationList(String organizationId, Integer from, Integer size);

    public List<LocationEntity> getLocationList(LocationSearchBean searchBean, Integer from, Integer size);
    public int getNumOfLocationsForUser(String userId);
    public int getNumOfLocationsForOrganization(String organizationId);

    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId, Integer from, Integer size);
    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId);
}
