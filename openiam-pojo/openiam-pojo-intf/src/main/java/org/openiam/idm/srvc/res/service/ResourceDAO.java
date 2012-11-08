package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import java.util.List;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    ResourceEntity findByName(String resourceName);


    /**
     * Gets the resources by type.
     *
     * @param resourceTypeId the resource type id
     * @return the resources by type
     */
    List<ResourceEntity> getResourcesByType(String resourceTypeId);

    /**
     * Find resources for role.
     *
     * @param roleId   the role id
     * @return the list
     */
    List<ResourceEntity> findResourcesForRole(String roleId);

    /**
     * Find resources for roles.
     *
     * @param roleIdList the role id list
     * @return the list
     */
    List<ResourceEntity> findResourcesForRoles(List<String> roleIdList);

    List<ResourceEntity> findResourcesForUserRole(String userId);

    List<ResourceEntity> getRootResources(ResourceEntity resource, int startAt, int size);
}