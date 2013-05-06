package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

public interface OrganizationService {

	public List<OrganizationEntity> getTopLevelOrganizations();
	public OrganizationEntity getOrganization(final String orgId);
	public List<OrganizationEntity> getOrganizationsForUser(String userId);
	public void addUserToOrg(final String orgId, final String userId);
	public void removeUserFromOrg(String orgId, String userId);
	public List<OrganizationEntity> getAllOrganizations();
	public List<OrganizationEntity> findBeans(final OrganizationEntity searchBean, final int from, final int size);
	public int count(final OrganizationEntity searchBean);
	public void removeAttribute(final String attributeId);
	public void save(final OrganizationEntity organization);
	public void save(final OrganizationAttributeEntity attribute);
	public void removeChildOrganization(final String organizationId, final String childOrganizationId);
	public void addChildOrganization(final String organizationId, final String childOrganizationId);
	public void deleteOrganization(final String orgId);
	public List<OrganizationEntity> getParentOrganizations(final String organizationId, final int from, final int size);
	public List<OrganizationEntity> getChildOrganizations(final String organizationId, final int from, final int size);
	public int getNumOfParentOrganizations(final String organizationId);
	public int getNumOfChildOrganizations(final String organizationId);
	
	public UserAffiliationEntity getAffiliation(final String userId, final String organizationId);
}
