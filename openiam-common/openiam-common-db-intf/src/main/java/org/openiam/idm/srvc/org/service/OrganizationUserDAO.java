package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserIdEntity;

import java.util.List;
import java.util.Set;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationUserDAO extends BaseDao<OrganizationUserEntity, OrganizationUserIdEntity> {
    public List<OrganizationUserEntity> findByOrganizationId(String organizationId);

    public List<OrganizationUserEntity> findByUserId(String userId);

    public OrganizationUserEntity find(String userId, String orgId);
}
