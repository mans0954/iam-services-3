package org.openiam.idm.srvc.org.service;

import org.hibernate.Criteria;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import java.util.List;
import java.util.Set;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationDAO extends BaseDao<OrganizationEntity, String> {

    int getNumOfChildOrganizations(String orgId, Set<String> filter);

    List<OrganizationEntity> getChildOrganizations(String orgId, Set<String> filter, final int from, final int size);

    int getNumOfParentOrganizations(String orgId, Set<String> filter);

    List<OrganizationEntity> getParentOrganizations(String orgId, Set<String> filter, final int from, final int size);

    public List<OrganizationEntity> getOrganizationsForUser(final String userId, final Set<String> filter, final int from, final int size);

    public int getNumOfOrganizationsForUser(final String userId, final Set<String> filter);

    //public List<Org2OrgXrefEntity> getOrgToOrgXrefList();

    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData);

    //public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, final String attrValue);

    public List<OrganizationEntity> getUserAffiliationsByType(final String userId, final String typeId, final Set<String> filter, final int from,
                                                              final int size);

    public void deleteOrganizationUserDependency(final String orgId) ;

    public List<OrgToOrgMembershipXrefEntity> getOrg2OrgXrefs();
}
