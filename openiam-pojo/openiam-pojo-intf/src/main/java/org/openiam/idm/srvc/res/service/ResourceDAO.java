package org.openiam.idm.srvc.res.service;

import org.hibernate.SessionFactory;
import org.openiam.base.BaseDAO;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.res.dto.ResourceType;

import java.util.List;

public interface ResourceDAO extends BaseDao<Resource, String> {

    Resource findByName(String resourceName);


    /**
     * Gets the resources by type.
     *
     * @param resourceTypeId the resource type id
     * @return the resources by type
     */
    List<Resource> getResourcesByType(String resourceTypeId);

    /**
     * Find resources for role.
     *
     * @param roleId   the role id
     * @return the list
     */
    List<Resource> findResourcesForRole(String roleId);

    /**
     * Find resources for roles.
     *
     * @param roleIdList the role id list
     * @return the list
     */
    List<Resource> findResourcesForRoles(List<String> roleIdList);

    List<Resource> findResourcesForUserRole(String userId);

    List<Resource> getRootResources(Resource resource, int startAt, int size);
}