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

    List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size);

    //List<ResourceEntity> getRootResources(ResourceEntity resource, int startAt, int size);
    
    int getNumOfResourcesForRole(String roleId);
    
    List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size);
    
    int getNumOfResourcesForGroup(final String groupId);
    
    public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size);
    public int getNumOfResourcesForUser(final String userId);

}