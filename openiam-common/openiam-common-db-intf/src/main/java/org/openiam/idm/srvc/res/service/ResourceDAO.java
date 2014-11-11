package org.openiam.idm.srvc.res.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

import java.util.List;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    public ResourceEntity findByName(final String resourceName);

    /**
     * Gets the resources by type.
     *
     * @param resourceTypeId the resource type id
     * @return the resources by type
     */
    public List<ResourceEntity> getResourcesByType(final String resourceTypeId);

    public List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);

    public List<ResourceEntity> getResourcesForRoleNoLocalized(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);

    public int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean);
    
    public List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);

    public List<ResourceEntity> getResourcesForGroupNoLocalized(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);

    public int getNumOfResourcesForGroup(final String groupId, final ResourceSearchBean searchBean);
    
    public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForUserByType(final String userId, String resourceTypeId, final ResourceSearchBean searchBean);
    public int getNumOfResourcesForUser(final String userId, final ResourceSearchBean searchBean);

}