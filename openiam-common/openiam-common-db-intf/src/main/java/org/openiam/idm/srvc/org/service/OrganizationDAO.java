package org.openiam.idm.srvc.org.service;

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
    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData);

    public List<OrgToOrgMembershipXrefEntity> getOrg2OrgXrefs();
}
