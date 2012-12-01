package org.openiam.idm.srvc.org.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.OrgClassificationEnum;
import org.openiam.idm.srvc.org.dto.Organization;


import java.util.List;


/**
 * Data access object interface for Organization.
 *
 * @author Suneet Shah
 */

public interface OrganizationDAO extends BaseDao<OrganizationEntity, String> {
    
    /**
     * Returns an Organization object that is the parent of the orgId specified.
     * Return null is no parent organizations are found.
     *
     * @param orgId
     * @return
     */
    OrganizationEntity findParent(String orgId);

    /**
     * Returns a list of Organization objects that are root level entities; ie. they
     * don't have a parent.
     *
     * @return
     */
    List<OrganizationEntity> findRootOrganizations();

    /**
     * Returns a List of Organization objects that are sub-organizations of the specified
     * orgId. Returns null if no children are found.
     *
     * @param orgId
     * @return
     */
    List<OrganizationEntity> findChildOrganization(String orgId);

    List<OrganizationEntity> findAllOrganization();

    List<OrganizationEntity> findOrganizationByStatus(String parentId, String status);

    List<OrganizationEntity> findOrganizationByType(String type, String parentId);

    List<OrganizationEntity> findOrganizationByClassification(String parentId, OrgClassificationEnum classification);


}
