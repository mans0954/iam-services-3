package org.openiam.idm.srvc.org.service;

import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.org.dto.Organization;

import java.util.List;

public interface OrganizationService {

    public OrganizationEntity getOrganization(String orgId);
	public OrganizationEntity getOrganization(final String orgId, String requesterId);
    public OrganizationEntity getOrganizationByName(final String name, String requesterId);
    public int getNumOfOrganizationsForUser(final String userId, final String requesterId);
	public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size);
    public List<OrganizationEntity> getAllOrganizations(String requesterId);
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String parentClassification, String requesterId, final int from, final int size);
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String childClassification, String requesterId, final int from, final int size);
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size);
    public int count(final OrganizationSearchBean searchBean, String requesterId);
    public int getNumOfParentOrganizations(final String orgId, String parentClassification, String requesterId);
    public int getNumOfChildOrganizations(final String orgId, String childClassification, String requesterId);

	public void addUserToOrg(final String orgId, final String userId);
	public void removeUserFromOrg(String orgId, String userId);
	public void removeAttribute(final String attributeId);
	public void save(final OrganizationEntity organization);
	public void save(final OrganizationAttributeEntity attribute);
	public void removeChildOrganization(final String organizationId, final String childOrganizationId);
	public void addChildOrganization(final String organizationId, final String childOrganizationId);
	public void deleteOrganization(final String orgId);
	public UserAffiliationEntity getAffiliation(final String userId, final String organizationId);
}
