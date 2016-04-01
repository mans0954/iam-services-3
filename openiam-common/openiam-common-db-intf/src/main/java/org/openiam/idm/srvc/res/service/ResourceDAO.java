package org.openiam.idm.srvc.res.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

public interface ResourceDAO extends BaseDao<ResourceEntity, String> {

    public ResourceEntity findByName(final String resourceName);
    public List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesByType(final String resourceTypeId);
    public List<ResourceEntity> getResourcesForRole(final String roleId,
                                                    final int from, final int size, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForUserByType(final String userId, String resourceTypeId, final ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForRoleNoLocalized(String roleId, int from, int size, ResourceSearchBean searchBean);
    public List<ResourceEntity> getResourcesForGroupNoLocalized(String groupId, int from, int size, ResourceSearchBean searchBean);
    }