package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;

import java.util.List;
import java.util.Set;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationDAO extends BaseDao<OrganizationEntity, String> {
    public List<OrganizationEntity> getOrganizationsForUser(final String userId, final Set<String> filter, final int from, final int size);
    public int getNumOfOrganizationsForUser(final String userId, final Set<String> filter);

    public List<OrgToOrgMembershipXrefEntity> getOrgToOrgXrefList();

    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData);


}
