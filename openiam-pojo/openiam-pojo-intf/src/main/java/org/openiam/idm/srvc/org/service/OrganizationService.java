package org.openiam.idm.srvc.org.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.dto.UserAttribute;

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
	public void save(final OrganizationEntity organization, final String requestorId);
    public void addRequiredAttributes(OrganizationEntity organization);
	public void save(final OrganizationAttributeEntity attribute);
	public void removeChildOrganization(final String organizationId, final String childOrganizationId);
	public void addChildOrganization(final String organizationId, final String childOrganizationId);
	public void deleteOrganization(final String orgId);
	
	public void validateOrg2OrgAddition(String parentId, String memberId) throws BasicDataServiceException;

    public Set<String> getDelegationFilter(String requesterId, String organizationTypeId);
    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, String organizationTypeId);
}
