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
    public List<OrgToOrgMembershipXrefEntity> getOrgToOrgXrefList();

    public List<OrganizationEntity> findAllByTypesAndIds(Set<String> allowedOrgTypes, Set<String> filterData);


}
