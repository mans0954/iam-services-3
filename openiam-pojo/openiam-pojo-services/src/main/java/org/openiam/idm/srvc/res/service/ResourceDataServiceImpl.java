package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.DozerUtils;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePrivilegeEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.searchbean.converter.ResourceSearchBeanConverter;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl implements ResourceDataService {

    private DozerUtils dozerUtils;
    private ResourceDAO resourceDao;
    private ResourceTypeDAO resourceTypeDao;
    private ResourcePropDAO resourcePropDao;
    private ResourceRoleDAO resourceRoleDao;
    private ResourceUserDAO resourceUserDao;
    private ResourcePrivilegeDAO resourcePrivilegeDao;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);

    @Autowired
    private ResourceSearchBeanConverter resourceSearchBeanConverter;

    @Required
    public void setResourceDao(ResourceDAO resourceDao) {
        this.resourceDao = resourceDao;
    }

    @Required
    public void setResourceTypeDao(ResourceTypeDAO resourceTypeDao) {
        this.resourceTypeDao = resourceTypeDao;
    }

    @Required
    public void setResourcePropDao(ResourcePropDAO resourcePropDao) {
        this.resourcePropDao = resourcePropDao;
    }

    @Required
    public void setResourceRoleDao(ResourceRoleDAO resourceRoleDao) {
        this.resourceRoleDao = resourceRoleDao;
    }

    @Required
    public void setResourcePrivilegeDao(ResourcePrivilegeDAO resourcePrivilegeDao) {
        this.resourcePrivilegeDao = resourcePrivilegeDao;
    }

    @Required
    public void setDozerUtils(final DozerUtils dozerUtils) {
        this.dozerUtils = dozerUtils;
    }

    @Required
    public void setResourceUserDao(ResourceUserDAO resourceUserDao) {
        this.resourceUserDao = resourceUserDao;
    }

    /**
     * Add a new resource from a transient resource object and sets resourceId
     * in the returned object.
     *
     * @param resource
     * @return
     */
    public Resource addResource(Resource resource) {
        if (resource == null)
            throw new IllegalArgumentException("Resource object is null");
        ResourceEntity resourceEntity = new ResourceEntity(resource, true);
        resourceDao.save(resourceEntity);
        resource.setResourceId(resourceEntity.getResourceId());
        return resource;
    }

    /**
     * Add a new resource from a transient resource object. Sets resourceId and
     * associates resource with a resource type. Sets category and branch from
     * parent.
     *
     * @param resource
     * @param resourceTypeId
     * @return
     */
    // public Resource addChildResource(Resource resource, Resource
    // resourceParent) {
    // resource.setResourceParent(resourceParent);
    // resource.setResourceType(resourceParent.getResourceType());
    // resource.setCategoryId(resourceParent.getCategoryId());
    // resource.setBranchId(resourceParent.getBranchId());
    // return addResource(resource);
    // }

    /**
     * Add a new resource from a transient resource object. Sets resourceId and
     * associates resource with a resource type.
     *
     * @param resource
     * @param resourceTypeId
     * @return
     */
    // public Resource addTypedResource(Resource resource, String
    // resourceTypeId) {
    // ResourceType resourceType = this.getResourceType(resourceTypeId);
    // resource.setResourceType(resourceType);
    // return addResource(resource);
    // }

    /**
     * Find a resource.
     *
     * @param resourceId
     * @return resource
     */
    public Resource getResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId is null");

        final ResourceEntity entity = resourceDao.findById(resourceId);
        if(entity != null) {
        	return new Resource(entity, true);
        } else {
        	return null;
        }
        //return dozerUtils.getDozerDeepMappedResource(new Resource(resourceDao.findById(resourceId)));
    }

    @WebMethod
    public int count(final ResourceSearchBean searchBean) {
    	final ResourceEntity entity = resourceSearchBeanConverter.convert(searchBean);
    	return resourceDao.count(entity);
    }

    @Override
    public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
        final ResourceEntity resource = resourceSearchBeanConverter.convert(searchBean);
        final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
        List<ResourceEntity> resultsEntities = null;
        if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
            resultsEntities = resourceDao.getRootResources(resource, from, size);
        } else {
            resultsEntities = resourceDao.getByExample(resource, from, size);
        }
        
        final List<Resource> retVal = new LinkedList<Resource>();
        if(CollectionUtils.isNotEmpty(resultsEntities)) {
        	for(final ResourceEntity entity : resultsEntities) {
        		retVal.add(new Resource(entity, (DozerMappingType.DEEP.equals(mappingType))));
        	}
        }
        return retVal;
    }

    /**
     * Update a resource.
     *
     * @param resource
     * @return
     */
    public Resource updateResource(Resource resource) {
        if (resource == null)
            throw new IllegalArgumentException("resource object is null");

        resourceDao.update(new ResourceEntity(resource, false));
        return resource;
    }

    /**
     * Remove a resource
     *
     * @param resourceId
     */
    public void removeResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceId is null");
        ResourceEntity obj = resourceDao.findById(resourceId);
        resourceDao.delete(obj);
    }

    // ResourceType -----------------------------------

    /**
     * Add a new resource type
     *
     * @param val
     * @return
     */
    public ResourceType addResourceType(ResourceType val) {
        if (val == null)
            throw new IllegalArgumentException("ResourcType is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.add(new ResourceTypeEntity(val));
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Find a resource type
     *
     * @param resourceTypeId
     * @return
     */
    public ResourceType getResourceType(String resourceTypeId) {
        if (resourceTypeId == null)
            throw new IllegalArgumentException("resourceTypeId is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.findById(resourceTypeId);
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Update a resource type
     *
     * @param resourceType
     * @return
     */
    public ResourceType updateResourceType(ResourceType resourceType) {
        if (resourceType == null)
            throw new IllegalArgumentException("resourceType object is null");

        ResourceTypeEntity resourceTypeEntity = resourceTypeDao.update(new ResourceTypeEntity(resourceType));
        return new ResourceType(resourceTypeEntity);
    }

    /**
     * Find all resource types
     *
     * @return
     */
    public List<ResourceType> getAllResourceTypes() {
        List<ResourceTypeEntity> resourceTypeEntities = resourceTypeDao.findAllResourceTypes();
        List<ResourceType> resourceTypes = null;
        if (resourceTypeEntities != null) {
            resourceTypes = new LinkedList<ResourceType>();
            for (ResourceTypeEntity resourceTypeEntity : resourceTypeEntities) {
                resourceTypes.add(new ResourceType(resourceTypeEntity));
            }
        }
        return resourceTypes;
    }

    /**
     * Remove a resource type
     *
     * @param resourceTypeId
     */
    public void removeResourceType(String resourceTypeId) {
        if (resourceTypeId == null)
            throw new IllegalArgumentException("resourceTypeId is null");
        ResourceTypeEntity obj = this.resourceTypeDao.findById(resourceTypeId);
        this.resourceTypeDao.remove(obj);
    }

    /**
     * Add a resource property.
     *
     * @param resourceProp
     * @return
     */
    public ResourceProp addResourceProp(ResourceProp resourceProp) {
        if (resourceProp == null)
            throw new IllegalArgumentException("ResourceProp object is null");

        ResourcePropEntity resourcePropEntity = resourcePropDao.add(new ResourcePropEntity(resourceProp));
        resourceProp.setResourcePropId(resourcePropEntity.getResourcePropId());
        return resourceProp;
    }

    // /**
    // * Add a new resource Property from a transient resource Property object.
    // * Sets resourcePropId and associates resourceProp with a resource.
    // *
    // * @param resourceProp
    // * @param resourceId
    // * @return
    // */
    // public ResourceProp addLinkedResourceProp(ResourceProp resourceProp,
    // String resourceId) {
    // if (resourceId == null)
    // throw new IllegalArgumentException("resourceId is null");
    // Resource resource = resourceDao.findById(resourceId);
    //
    // if (resource == null) {
    // log.error("Resource not found for resourceId =" + resourceId);
    // throw new ObjectNotFoundException();
    // }
    //
    // resourceProp.setResource(resource);
    // Set<ResourceProp> props = resource.getResourceProps();
    // if (props == null)
    // props = new HashSet<ResourceProp>();
    // props.add(resourceProp);
    // return resourcePropDao.add(resourceProp);
    // }

    /**
     * Find a resource property.
     *
     * @param resourcePropId
     * @return
     */
    public ResourceProp getResourceProp(String resourcePropId) {
        if (resourcePropId == null)
            throw new IllegalArgumentException("resourcePropId is null");

        ResourcePropEntity propEntity = resourcePropDao.findById(resourcePropId);
        return new ResourceProp(propEntity);
    }

    /**
     * Update a resource property
     *
     * @param resourceProp
     */
    public ResourceProp updateResourceProp(ResourceProp resourceProp) {
        if (resourceProp == null)
            throw new IllegalArgumentException("resourceProp object is null");

        ResourcePropEntity propEntity = resourcePropDao.update(new ResourcePropEntity(resourceProp));
        return new ResourceProp(propEntity);
    }

    /**
     * Remove a resource property
     *
     * @param resourcePropId
     */
    public void removeResourceProp(String resourcePropId) {
        if (resourcePropId == null)
            throw new IllegalArgumentException("resourcePropId is null");
        final ResourcePropEntity obj = this.resourcePropDao.findById(resourcePropId);
        resourcePropDao.remove(obj);
    }

    /**
     * Remove all resource properties
     *
     * @param
     */
    public int removeAllResourceProps() {
        return resourcePropDao.removeAllResourceProps();
    }

    /**
     * Recursive method to get resource descendants in a single non nested list.
     *
     * @param resourceId the resource id
     * @param visitedSet the descendents
     * @return the resource family helper
     */
    private void visitChildren(final String resourceId, final Set<ResourceEntity> visitedSet) {
        final ResourceEntity resource = resourceDao.findById(resourceId);
        if (!visitedSet.contains(resource)) {
            visitedSet.add(resource);
            final Set<ResourceEntity> children = resource.getChildResources();
            if (CollectionUtils.isNotEmpty(children)) {
                for (final Iterator<ResourceEntity> it = children.iterator(); it.hasNext(); ) {
                    final ResourceEntity r = it.next();
                    visitChildren(r.getResourceId(), visitedSet);
                }
            }
        }
    }

    /**
     * Add a resource role
     *
     * @param resourceRole
     * @return
     */
    public ResourceRole addResourceRole(ResourceRole resourceRole) {

        if (resourceRole == null)
            throw new IllegalArgumentException("ResourceRole object is null");

        ResourceRoleEntity propEntity = resourceRoleDao.add(new ResourceRoleEntity(resourceRole));
        return new ResourceRole(propEntity);
    }

    /**
     * Find resource role
     *
     * @param resourceRoleId
     * @return
     */
    public ResourceRole getResourceRole(ResourceRoleId resourceRoleId) {
        if (resourceRoleId == null)
            throw new IllegalArgumentException("resourceRoleId is null");

        ResourceRoleEntity roleEntity = resourceRoleDao.findById(new ResourceRoleEmbeddableId(resourceRoleId));
        return new ResourceRole(roleEntity);
    }

    public List<Role> getRolesForResource(String resourceId) {
        if (resourceId == null)
            throw new IllegalArgumentException("resourceRoleId is null");

        return dozerUtils.getDozerDeepMappedRoleList(resourceRoleDao.findRolesForResource(resourceId));
    }

    /**
     * Update resource role.
     *
     * @param resourceRole
     * @return
     */
    public ResourceRole updateResourceRole(ResourceRole resourceRole) {
        if (resourceRole == null)
            throw new IllegalArgumentException("resourceRole object is null");

        ResourceRoleEntity resourceRoleEntity = resourceRoleDao.update(new ResourceRoleEntity(resourceRole));
        return new ResourceRole(resourceRoleEntity);
    }

    /**
     * Find all resource roles
     *
     * @return
     */
    public List<ResourceRole> getAllResourceRoles() {
        final List<ResourceRoleEntity> resourceRoleEntityList = resourceRoleDao.findAllResourceRoles();
        List<ResourceRole> resourceRoleList = null;
        if (resourceRoleEntityList != null) {
            resourceRoleList = new LinkedList<ResourceRole>();
            for (ResourceRoleEntity resourceRoleEntity : resourceRoleEntityList) {
                resourceRoleList.add(new ResourceRole(resourceRoleEntity));
            }
        }
        return resourceRoleList;
    }

    /**
     * Remove resource role.
     *
     * @param resourceRoleId
     */
    public void removeResourceRole(ResourceRoleId resourceRoleId) {
        if (resourceRoleId == null)
            throw new IllegalArgumentException("resourceRoleId is null");
        final ResourceRoleEntity obj = this.resourceRoleDao.findById(new ResourceRoleEmbeddableId(resourceRoleId));
        resourceRoleDao.remove(obj);
    }

    /**
     * Remove all resource roles
     */
    public void removeAllResourceRoles() {
        this.resourceRoleDao.removeAllResourceRoles();
    }

    /**
     * Returns a list of Resource objects that are linked to a Role.
     *
     * @param roleId
     * @return
     */
    public List<Resource> getResourcesForRole(String roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId is null");
        }
        List<ResourceEntity> resourceEntities = resourceDao.findResourcesForRole(roleId);
        List<Resource> resources = new LinkedList<Resource>();
        if (resourceEntities != null) {
            for (ResourceEntity entity : resourceEntities) {
                resources.add(new Resource(entity, false));
            }
        }
        return resources;
    }

    /**
     * Returns a list of Resource objects that are linked to the list of Roles.
     *
     * @param roleIdList
     * @return
     */
    public List<Resource> getResourcesForRoles(List<String> roleIdList) {
        if (roleIdList == null) {
            throw new IllegalArgumentException("roleIdList is null");
        }
        List<ResourceEntity> resourceEntities = resourceDao.findResourcesForRoles(roleIdList);
        List<Resource> resources = new LinkedList<Resource>();
        if (resourceEntities != null) {
            for (ResourceEntity entity : resourceEntities) {
                resources.add(new Resource(entity, false));
            }
        }
        return resources;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.openiam.idm.srvc.res.service.ResourceDataService#addUserToResource
      * (org.openiam.idm.srvc.res.dto.ResourceUser)
      */
    public ResourceUser addUserToResource(ResourceUser resourceUser) {
        if (resourceUser == null) {
            throw new IllegalArgumentException("ResourceUser object is null");
        }
        ResourceUserEntity resourceUserEntity = resourceUserDao.add(new ResourceUserEntity(resourceUser));
        return new ResourceUser(resourceUserEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.openiam.idm.srvc.res.service.ResourceDataService#getUserResources
      * (java.lang.String)
      */
    public List<ResourceUser> getUserResources(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }
        List<ResourceUserEntity> resourceUserEntityList = resourceUserDao.findAllResourceForUsers(userId);
        List<ResourceUser> resourceUserList = null;
        if (resourceUserEntityList != null) {
            resourceUserList = new LinkedList<ResourceUser>();
            for (ResourceUserEntity resourceUserEntity : resourceUserEntityList) {
                resourceUserList.add(new ResourceUser(resourceUserEntity));
            }
        }
        return dozerUtils.getDozerDeepMappedResourceUserList(resourceUserList);
    }

    public List<Resource> getResourceObjForUser(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }

        List<ResourceEntity> entities = resourceDao.findResourcesForUserRole(userId);
        List<Resource> resources = new LinkedList<Resource>();
        if (entities != null) {
            for (ResourceEntity entity : entities) {
                resources.add(new Resource(entity, false));
            }
        }
        return resources;
    }


    /*
      * (non-Javadoc)
      *
      * @seeorg.openiam.idm.srvc.res.service.ResourceDataService#
      * removeUserFromAllResources(java.lang.String)
      */
    public void removeUserFromAllResources(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId object is null");
        }
        resourceUserDao.removeUserFromAllResources(userId);
    }

    public boolean isUserAuthorized(String userId, String resourceId) {
        log.info("isUserAuthorized called.");
        final List<ResourceUser> resList = getUserResources(userId);
        log.info("- resList= " + resList);
        if (resList == null) {
            log.info("resource list for user is null");
            return false;
        }
        for (final ResourceUser ru : resList) {
            log.info("resource id = " + ru.getId().getResourceId());
            if (ru.getId().getResourceId().equalsIgnoreCase(resourceId)) {
                return true;
            }
        }
        return false;

    }


    public boolean isUserAuthorizedByProperty(String userId, String propertyName, String propertyValue) {
        log.info("isUserAuthorized called.");

        if (propertyName == null || propertyName.length() == 0) {
            return false;
        }
        if (propertyValue == null || propertyValue.length() == 0) {
            return false;
        }

        List<Resource> resList = getResourceObjForUser(userId);

        log.info("- resList= " + resList);
        if (resList == null) {
            log.info("resource list for user is null");
            return false;
        }
        for (Resource res : resList) {

            ResourceProp prop = res.getResourceProperty(propertyName);
            if (prop != null) {
                String val = prop.getPropValue();
                if (val != null && val.length() > 0) {
                    val = val.toLowerCase();
                    propertyValue = propertyValue.toLowerCase();

                    if (propertyValue.contains(val)) {
                        return true;
                    }

                }
            }
        }

        return false;

    }

    @Override
    public ResourcePrivilege addResourcePrivilege(ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        ResourcePrivilegeEntity resourcePrivilegeEntity = resourcePrivilegeDao.add(new ResourcePrivilegeEntity(resourcePrivilege));

        return new ResourcePrivilege(resourcePrivilegeEntity);
    }

    @Override
    public void removeResourcePrivilege(String resourcePrivilegeId) {
        if (resourcePrivilegeId == null) {
            throw new IllegalArgumentException("resourcePrivilegeId object is null");
        }
        ResourcePrivilegeEntity privilegeEntity = resourcePrivilegeDao.findById(resourcePrivilegeId);
        resourcePrivilegeDao.remove(privilegeEntity);
    }

    @Override
    public ResourcePrivilege updateResourcePrivilege(ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        ResourcePrivilegeEntity privilegeEntity = resourcePrivilegeDao.update(new ResourcePrivilegeEntity(resourcePrivilege));
        return new ResourcePrivilege(privilegeEntity);
    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByResourceId(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        List<ResourcePrivilegeEntity> privilegeEntities = resourcePrivilegeDao.findPrivilegesByResourceId(resourceId);
        List<ResourcePrivilege> privilegeList = null;
        if (privilegeEntities != null) {
            privilegeList = new LinkedList<ResourcePrivilege>();
            for (ResourcePrivilegeEntity privilegeEntity : privilegeEntities) {
                privilegeList.add(new ResourcePrivilege(privilegeEntity));
            }
        }
        return privilegeList;
    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByEntitlementType(String resourceId, String type) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type object is null");
        }
        List<ResourcePrivilegeEntity> privilegeEntities = resourcePrivilegeDao.findPrivilegesByEntitlementType(resourceId, type);
        List<ResourcePrivilege> privilegeList = null;
        if (privilegeEntities != null) {
            privilegeList = new LinkedList<ResourcePrivilege>();
            for (ResourcePrivilegeEntity privilegeEntity : privilegeEntities) {
                privilegeList.add(new ResourcePrivilege(privilegeEntity));
            }
        }
        return privilegeList;
    }
}