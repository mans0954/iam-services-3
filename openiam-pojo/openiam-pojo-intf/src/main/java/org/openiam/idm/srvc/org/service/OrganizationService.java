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
    Organization getOrganizationDTO(final String orgId);
    @Deprecated
    OrganizationEntity getOrganization(String orgId);
    @Deprecated
    OrganizationEntity getOrganization(final String orgId, String requesterId);
    @Deprecated
    OrganizationEntity getOrganizationByName(final String name, String requesterId);
    @Deprecated
    List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size);
    @Deprecated
    List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size);
    @Deprecated
    List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size);
    @Deprecated
    List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size);
    @Deprecated
    List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId);
    @Deprecated
    List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue);

    void fireUpdateOrgMap();

    Organization getOrganizationDTO(final String orgId, final LanguageEntity langauge);
    OrganizationEntity getOrganizationLocalized(String orgId, final LanguageEntity langauge);
    OrganizationEntity getOrganizationLocalized(final String orgId, String requesterId, final LanguageEntity langauge);
    OrganizationEntity getOrganizationByName(final String name, String requesterId, final LanguageEntity langauge);
    List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size, final LanguageEntity langauge);
    List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size, final LanguageEntity langauge);
    List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId, final LanguageEntity langauge);
    List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue, final LanguageEntity langauge);

    int getNumOfOrganizationsForUser(final String userId, final String requesterId);
	int count(final OrganizationSearchBean searchBean, String requesterId);
    int getNumOfParentOrganizations(final String orgId, String requesterId);
    int getNumOfChildOrganizations(final String orgId, String requesterId);
	void addUserToOrg(final String orgId, final String userId);
	void removeUserFromOrg(String orgId, String userId);
	void removeAttribute(final String attributeId);
    Organization save(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	Organization save(final Organization organization, final String requestorId) throws BasicDataServiceException;
    void addRequiredAttributes(OrganizationEntity organization);
	void save(final OrganizationAttributeEntity attribute);
	void removeChildOrganization(final String organizationId, final String childOrganizationId);
	void addChildOrganization(final String organizationId, final String childOrganizationId);
	void deleteOrganization(final String orgId) throws BasicDataServiceException;
    void deleteOrganization(final String orgId, final boolean skipPrePostProcessors) throws BasicDataServiceException;
	void validateOrg2OrgAddition(String parentId, String memberId) throws BasicDataServiceException;
    void validate(final Organization organization) throws BasicDataServiceException;

    Set<String> getDelegationFilter(String requesterId, String organizationTypeId);
    Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, String organizationTypeId);

    void addLocation(LocationEntity val);
    void updateLocation(LocationEntity val);
    void removeLocation(String locationId);
    void removeAllLocations(String organizationId);
    LocationEntity getLocationById(String locationId);
    List<LocationEntity> getLocationList(String organizationId);

    List<Location> getLocationDtoList(String organizationId, boolean isDeep);

    List<LocationEntity> getLocationList(String organizationId, Integer from, Integer size);

    List<LocationEntity> getLocationList(LocationSearchBean searchBean, Integer from, Integer size);
    int getNumOfLocations(LocationSearchBean searchBean);

    int getNumOfLocationsForUser(String userId);
    int getNumOfLocationsForOrganization(String organizationId);

    List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId, Integer from, Integer size);
    List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId);
}
