package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import java.util.List;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    ResourceEntity findByName(final String resourceName);

    /**
     * Gets the resources by type.
     *
     * @param resourceTypeId the resource type id
     * @return the resources by type
     */
    List<ResourceEntity> getResourcesByType(final String resourceTypeId);

    List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);

    @Deprecated
    int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean);
    
    @Deprecated
    List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);
    
    @Deprecated
    int getNumOfResourcesForGroup(final String groupId, final ResourceSearchBean searchBean);
    
    @Deprecated
    List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
    @Deprecated
    List<ResourceEntity> getResourcesForUserByType(final String userId, String resourceTypeId, final ResourceSearchBean searchBean);
    @Deprecated
    int getNumOfResourcesForUser(final String userId, final ResourceSearchBean searchBean);

}