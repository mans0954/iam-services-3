package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.DozerBeanMapper;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.openiam.dozer.DozerUtils;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
//import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
//import org.openiam.idm.srvc.mngsys.service.AttributeMapDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.res.dto.*;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.org.dto.Organization;
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

	private LoginDataService loginManager;
	private UserDataService userManager;
	private RoleDataService roleDataService;
    private OrganizationDataService orgManager;

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
    public void setOrgManager(OrganizationDataService orgManager) {
        this.orgManager = orgManager;
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
	
    @Required
    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    @Required
    public void setUserManager(UserDataService userManager) {
        this.userManager = userManager;
    }

    @Required
    public void setRoleDataService(RoleDataService roleDataService) {
        this.roleDataService = roleDataService;
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

		resourceDao.save(resource);
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
	 * 
	 * @return resource
	 */
	public Resource getResource(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");

		return dozerUtils.getDozerDeepMappedResource(resourceDao.findById(resourceId));
	}


 	public Resource getResourceByName(String resourceName) {
		if (resourceName == null)
			throw new IllegalArgumentException("resourceName is null");

		return dozerUtils.getDozerDeepMappedResource(resourceDao.findByName(resourceName));

	}

 	@WebMethod
    public int count(final ResourceSearchBean searchBean) {
		final Resource resource = new Resource();
		resource.setResourceId(searchBean.getKey());
		resource.setName(searchBean.getName());
		
		if(StringUtils.isNotBlank(searchBean.getResourceTypeId())) {
			final ResourceType type = new ResourceType();
			type.setResourceTypeId(searchBean.getResourceTypeId());
			resource.setResourceType(type);
		}
		
		return resourceDao.count(resourceSearchBeanConverter.convert(searchBean));
 	}

    @Override
	public List<Resource> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
		final List<Resource> results = resourceDao.getByExample(resourceSearchBeanConverter.convert(searchBean), from, size);
		final DozerMappingType mappingType = (searchBean.isDeepCopy()) ? DozerMappingType.DEEP : DozerMappingType.SHALLOW;
		return dozerUtils.getDozerDeepMappedResourceList(results, mappingType);
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

		resourceDao.update(resource);
		return resource;
	}

	/**
	 * Find all resources
	 * 
	 * @return list of resources
	 */
	public List<Resource> getAllResources() {
		return dozerUtils.getDozerDeepMappedResourceList(resourceDao.findAll());
	}

	/**
	 * Remove a resource
	 * 
	 * @param resourceId
	 */
	public void removeResource(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		Resource obj = resourceDao.findById(resourceId);
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

		return resourceTypeDao.add(val);
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

		return resourceTypeDao.findById(resourceTypeId);
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

		return resourceTypeDao.update(resourceType);
	}

	/**
	 * Find all resource types
	 * 
	 * @return
	 */
	public List<ResourceType> getAllResourceTypes() {
		return resourceTypeDao.findAllResourceTypes();
	}

	/**
	 * Remove a resource type
	 * 
	 * @param resourceTypeId
	 */
	public void removeResourceType(String resourceTypeId) {
		if (resourceTypeId == null)
			throw new IllegalArgumentException("resourceTypeId is null");
		ResourceType obj = this.resourceTypeDao.findById(resourceTypeId);
		this.resourceTypeDao.remove(obj);
	}

	/**
	 * Remove all resource types
	 * 
	 * @return
	 */
	public int removeAllResourceTypes() {
		return this.resourceTypeDao.removeAllResourceTypes();
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

		return resourcePropDao.add(resourceProp);

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

		return resourcePropDao.findById(resourcePropId);
	}

	/**
	 * Update a resource property
	 * 
	 * @param resourceProp
	 */
	public ResourceProp updateResourceProp(ResourceProp resourceProp) {
		if (resourceProp == null)
			throw new IllegalArgumentException("resourceProp object is null");

		return resourcePropDao.update(resourceProp);
	}

	/**
	 * Find all resource properties
	 * 
	 * @return
	 */
	public List<ResourceProp> getAllResourceProps() {
		List<ResourceProp> resourcePropList = resourcePropDao
				.findAllResourceProps();

		return resourcePropList;
	}

	/**
	 * Remove a resource property
	 * 
	 * @param resourcePropId
	 */
	public void removeResourceProp(String resourcePropId) {
		if (resourcePropId == null)
			throw new IllegalArgumentException("resourcePropId is null");
		final ResourceProp obj = this.resourcePropDao.findById(resourcePropId);
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
	 * Find resource children
	 * 
	 * @param resourceId
	 * @return
	 */
	public List<Resource> getChildResources(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		final Resource resource = resourceDao.findById(resourceId);
		if(resource == null) {
			throw new IllegalArgumentException(String.format("Resource with id: %s does not exist", resourceId));
		}

		return dozerUtils.getDozerDeepMappedResourceList(resource.getChildResources());
	}

	private String getResourceType(Resource r) {
		String rt = "";
		ResourceType resType = r.getResourceType();
		if (resType != null)
			rt = resType.getResourceTypeId() + ":";
		return rt;
	}

	/**
	 * Find a resource and all its descendants and put them in a list.
	 * 
	 * @param resourceId
	 *            the resource id
	 * 
	 * @return resource list
	 */

	public List<Resource> getResourceFamily(String resourceId) {
		final Resource resource = resourceDao.findById(resourceId);
		final Set<Resource> visitedSet = new LinkedHashSet<Resource>();
		visitedSet.add(resource);
		if(CollectionUtils.isNotEmpty(resource.getChildResources())) {
			for (final Iterator<Resource> it = resource.getChildResources().iterator(); it.hasNext();) {
				final Resource r = it.next();
				visitChildren(r.getResourceId(), visitedSet);
			}
		}
		return dozerUtils.getDozerDeepMappedResourceList(visitedSet);
	}

	/**
	 * Recursive method to get resource descendants in a single non nested list.
	 * 
	 * @param resourceId
	 *            the resource id
	 * @param descendents
	 *            the descendents
	 * 
	 * @return the resource family helper
	 */
	private void visitChildren(final String resourceId, final Set<Resource> visitedSet) {
		final Resource resource = resourceDao.findById(resourceId);
		if(!visitedSet.contains(resource)) {
			visitedSet.add(resource);
			final Set<Resource> children = resource.getChildResources();
			if(CollectionUtils.isNotEmpty(children)) {
				for (final Iterator<Resource> it = children.iterator(); it.hasNext();) {
					final Resource r = it.next();
					visitChildren(r.getResourceId(), visitedSet);
				}
			}
		}
	}

	// Resource get methods
	// =====================================================

	/**
	 * Find resources having a specified metadata type
	 * 
	 * @param resourceTypeId
	 * @return
	 */
	public List<Resource> getResourcesByType(String resourceTypeId) {
		if (resourceTypeId == null)
			throw new IllegalArgumentException("resourceTypeId is null");
		return dozerUtils.getDozerDeepMappedResourceList(resourceDao.getResourcesByType(resourceTypeId));
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

		return resourceRoleDao.add(resourceRole);

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

		return resourceRoleDao.findById(resourceRoleId);
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

		return resourceRoleDao.update(resourceRole);
	}

	/**
	 * Find all resource roles
	 * 
	 * @return
	 */
	public List<ResourceRole> getAllResourceRoles() {
		final List<ResourceRole> resourceRoleList = resourceRoleDao.findAllResourceRoles();

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
		final ResourceRole obj = this.resourceRoleDao.findById(resourceRoleId);
		resourceRoleDao.remove(obj);
	}

	/**
	 * Remove all resource roles
	 * 
	 */
	public void removeAllResourceRoles() {
		this.resourceRoleDao.removeAllResourceRoles();
	}

	/**
	 * Returns a list of Resource objects that are linked to a Role.
	 * 
	 * @param domainId
	 * @param roleId
	 * @return
	 */
	public List<Resource> getResourcesForRole(String roleId) {
		if (roleId == null) {
			throw new IllegalArgumentException("roleId is null");
		}
		return dozerUtils.getDozerDeepMappedResourceList(resourceDao.findResourcesForRole(roleId));
	}

	/**
	 * Returns a list of Resource objects that are linked to the list of Roles.
	 * 
	 * @param domainId
	 * @param roleIdList
	 * @return
	 */
	public List<Resource> getResourcesForRoles(List<String> roleIdList) {
		if (roleIdList == null) {
			throw new IllegalArgumentException("roleIdList is null");
		}
		return dozerUtils.getDozerDeepMappedResourceList(resourceDao.findResourcesForRoles(roleIdList));
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
		return resourceUserDao.add(resourceUser);
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
		return dozerUtils.getDozerDeepMappedResourceUserList( resourceUserDao.findAllResourceForUsers(userId));
	}

     public List<Resource> getResourceObjForUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId object is null");
		}

        return dozerUtils.getDozerDeepMappedResourceList( resourceDao.findResourcesForUserRole(userId) );
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

            ResourceProp prop =  res.getResourceProperty(propertyName);
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

	public boolean isRoleAuthorized(String roleId, String resourceId) {
		log.info("isUserAuthorized called.");

		List<Resource> resList = getResourcesForRole(roleId);
		log.info("- resList= " + resList);
		if (resList == null) {
			log.info("resource list for user is null");
			return false;
		}
		for (Resource r : resList) {
			log.info("resource id = " + r.getResourceId());
			if (r.getResourceId().equalsIgnoreCase(resourceId)) {
				return true;
			}
		}
		return false;
	}

    @Override
    public ResourcePrivilege addResourcePrivilege(ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        return resourcePrivilegeDao.add(resourcePrivilege);

    }

    @Override
    public void removeResourcePrivilege(String resourcePrivilegeId) {
        if (resourcePrivilegeId == null) {
            throw new IllegalArgumentException("resourcePrivilegeId object is null");
        }
        resourcePrivilegeDao.remove(new ResourcePrivilege(resourcePrivilegeId));
    }

    @Override
    public ResourcePrivilege updateResourcePrivilege( ResourcePrivilege resourcePrivilege) {
        if (resourcePrivilege == null) {
            throw new IllegalArgumentException("ResourcePrivilege object is null");
        }
        return resourcePrivilegeDao.update(resourcePrivilege);
    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByResourceId(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        return resourcePrivilegeDao.findPrivilegesByResourceId(resourceId);

    }

    @Override
    public List<ResourcePrivilege> getPrivilegesByEntitlementType( String resourceId,  String type) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId object is null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type object is null");
        }
        return resourcePrivilegeDao.findPrivilegesByEntitlementType(resourceId, type);
    }
}