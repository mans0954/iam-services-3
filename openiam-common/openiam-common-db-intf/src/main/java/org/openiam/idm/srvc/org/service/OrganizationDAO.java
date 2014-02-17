package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Org2OrgXref;

import java.util.List;
import java.util.Set;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationDAO extends BaseDao<OrganizationEntity, String> {

    /**
     * Returns a list of Organization objects that are root level entities; ie. they
     * don't have a parent.
     *
     * @return
     */
    List<OrganizationEntity> findRootOrganizations();

    List<OrganizationEntity> findAllOrganization();

    int getNumOfChildOrganizations(String orgId, Set<String> filter);
    List<OrganizationEntity> getChildOrganizations(String orgId, Set<String> filter, final int from, final int size);
    
    int getNumOfParentOrganizations(String orgId, Set<String> filter);
    List<OrganizationEntity> getParentOrganizations(String orgId, Set<String> filter, final int from, final int size);
    
    public List<OrganizationEntity> getOrganizationsForUser(final String userId, final Set<String> filter, final int from, final int size);
    public int getNumOfOrganizationsForUser(final String userId, final Set<String> filter);

    public List<Org2OrgXref> getOrgToOrgXrefList();

    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData);

}
