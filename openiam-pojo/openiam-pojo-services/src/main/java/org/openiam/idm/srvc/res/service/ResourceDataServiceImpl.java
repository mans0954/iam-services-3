package org.openiam.idm.srvc.res.service;

import java.util.*;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.DozerBeanMapper;
import org.openiam.exception.data.ObjectNotFoundException;
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
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Required;

@WebService(endpointInterface = "org.openiam.idm.srvc.res.service.ResourceDataService", targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "ResourceDataWebServicePort", serviceName = "ResourceDataWebService")
public class ResourceDataServiceImpl implements ResourceDataService {

	private Map<DozerMappingType, DozerBeanMapper> dozerMap;
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
	public void setDozerMap(final Map<DozerMappingType, DozerBeanMapper> dozerMap) {
		this.dozerMap = dozerMap;
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

		return resourceDao.add(resource);
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

		return getDozerMappedResource(resourceDao.findById(resourceId));
	}


 	public Resource getResourceByName(String resourceName) {
		if (resourceName == null)
			throw new IllegalArgumentException("resourceName is null");

		return getDozerMappedResource(resourceDao.findResourceByName(resourceName));

	}
	
    /**
     * Find resources by name
     *
     * @return list of resources
     */
    public List<Resource> getResourcesByName(String resourceName) {
        if (resourceName == null)
            throw new IllegalArgumentException("resourceName is null");

        return getDozerMappedResourceList(resourceDao.findResourcesByName(resourceName));
    }


    /**
      * Find resources by example
      *
      * @return list of resources
      */
    public List<Resource> getResourcesByExample(Resource resource) {
    	return getDozerMappedResourceList(resourceDao.findByExample(resource));
    }


    /**
     * Find resources which have a specified property
     *
     * @param propName
     * @param propValue
     * @return
     */
    public List<Resource> getResourcesByProperty (String propName, String propValue) {
        if (propName == null)
            throw new IllegalArgumentException("propName is null");
        if (propValue == null)
            throw new IllegalArgumentException("propValue is null");

        return getDozerMappedResourceList(resourceDao.findResourcesByProperty(propName, propValue));

    }

    /**
     * Find resource which has a specified set of unique properties
     *
     * @param propList
     * @return
     */
    public Resource getResourceByProperties(List<ResourceProp> propList) {
        if (propList == null)
            throw new IllegalArgumentException("propList is null");

        return getDozerMappedResource(resourceDao.findResourceByProperties(propList));

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

		return resourceDao.update(resource);
	}

	/**
	 * Find all resources
	 * 
	 * @return list of resources
	 */
	public List<Resource> getAllResources() {
		return getDozerMappedResourceList(resourceDao.findAllResources());
	}

	/**
	 * Remove a resource
	 * 
	 * @param resourceId
	 */
	public void removeResource(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		Resource obj = this.resourceDao.findById(resourceId);
		this.resourceDao.remove(obj);
	}

	/**
	 * Remove all resources
	 */
	public int removeAllResources() {
		return this.resourceDao.removeAllResources();
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
	 * Find type of a resource
	 * 
	 * @param resourceId
	 * @return
	 */
	public ResourceType findTypeOfResource(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");

		return resourceDao.findTypeOfResource(resourceId);
	}

	// /**
	// * Link metadata type to a resource
	// *
	// * @param resourceTypeId
	// * @param resourceId
	// */
	// public void linkTypeToResource(String resourceId, String resourceTypeId)
	// {
	// if (resourceTypeId == null)
	// throw new IllegalArgumentException("resourceTypeId is null");
	// if (resourceId == null)
	// throw new IllegalArgumentException("resourceId is null");
	// this.resourceDao.linkTypeToResource(resourceId, resourceTypeId);
	// }
	//
	// /**
	// * Unlink type from resource
	// *
	// * @param resourceId
	// */
	// public void unlinkTypeFromResource(String resourceId) {
	// if (resourceId == null)
	// throw new IllegalArgumentException("resourceId is null");
	//
	// this.resourceDao.unlinkTypeFromResource(resourceId);
	// }

	// ResourceProp ---------------------------------------

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
	 * Remove properties with a specified resourceId
	 * 
	 * @param resourceId
	 * @return
	 */
	public int removePropertiesByResource(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");

		return resourceDao.removePropertiesByResource(resourceId);
	}

	/**
	 * Find resource properties
	 * 
	 * @param resourceId
	 * @return
	 */
	public List<ResourceProp> findResourceProperties(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");

		return resourceDao.findResourceProperties(resourceId);
	}

	// /**
	// * Add a property to a resource
	// *
	// * @param resourcePropId
	// * @param resourceId
	// */
	// public void linkPropertyToResource(String resourcePropId, String
	// resourceId) {
	// if (resourcePropId == null)
	// throw new IllegalArgumentException("resourcePropId is null");
	// if (resourceId == null)
	// throw new IllegalArgumentException("resourceId is null");
	//
	// this.resourceDao.linkPropertyToResource(resourcePropId, resourceId);
	// }
	//
	// /**
	// * Remove a property from a resource
	// *
	// * @param resourcePropId
	// * @param resourceId
	// */
	// public void unlinkPropertyFromResource(String resourcePropId,
	// String resourceId) {
	// if (resourcePropId == null)
	// throw new IllegalArgumentException("resourcePropId is null");
	// if (resourceId == null)
	// throw new IllegalArgumentException("resourceId is null");
	//
	// this.resourceDao.unlinkPropertyFromResource(resourcePropId, resourceId);
	// }
	//
	// /**
	// * Remove all properties from a resource
	// *
	// * @param resourceId
	// */
	// public void unlinkAllPropertiesFromResource(String resourceId) {
	// if (resourceId == null)
	// throw new IllegalArgumentException("resourceId is null");
	//
	// this.resourceDao.unlinkAllPropertiesFromResource(resourceId);
	//
	// }

	// ResourceParent ------------------------------------------------

	/**
	 * Find resource children
	 * 
	 * @param resourceId
	 * @return
	 */
	public List<Resource> getChildResources(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		return getDozerMappedResourceList(resourceDao.getChildResources(resourceId));
	}

	// /**
	// * Set a parent resource
	// *
	// * @param parentResourceId
	// * @param childResourceId
	// */
	// public void linkResourceToParent(String childResourceId,
	// String parentResourceId) {
	// if (parentResourceId == null)
	// throw new IllegalArgumentException("parentResourceId is null");
	// if (childResourceId == null)
	// throw new IllegalArgumentException("childResourceId is null");
	// this.resourceDao
	// .linkResourceToParent(childResourceId, parentResourceId);
	// }
	//
	// /**
	// * Remove a resource parent relationship
	// *
	// * @param childResourceId
	// */
	// public void unlinkResourceFromParent(String childResourceId) {
	// if (childResourceId == null)
	// throw new IllegalArgumentException("childResourceId is null");
	// this.resourceDao.unlinkResourceFromParent(childResourceId);
	// }
	//
	// resource hierarchy methods
	// -------------------------------------------------

	/**
	 * Find a resource and its descendants and return as nested List.
	 * 
	 * @param resourceId
	 *            the resource id
	 * 
	 * @return list of nested lists of resource objects
	 */

	public List<Resource> getResourceTree(String resourceId) {
		final List<Resource> resourceTree = new ArrayList<Resource>();
		resourceTree.add(resourceDao.findById(resourceId));

		final List<Resource> resourceChildren = resourceDao.getChildResources(resourceId);
		resourceTree.addAll(resourceChildren);

		for (final Iterator<Resource> it = resourceChildren.iterator(); it.hasNext();) {
			final Resource r = (Resource) it.next();
			final List<Resource> descendents = getResourceTreeHelper(r.getResourceId());
			if (CollectionUtils.isNotEmpty(descendents)) {
				r.setChildResources(new HashSet<Resource>(descendents));
			}
		}
		return resourceTree;
	}

	/**
	 * Recursive helper method to get nested resource descendants.
	 * 
	 * @param resourceId
	 *            the resource id
	 * 
	 * @return the resource tree helper
	 */
	private List<Resource> getResourceTreeHelper(String resourceId) {

		final List<Resource> descendents = resourceDao.getChildResources(resourceId);

		for (final Iterator<Resource> it = descendents.iterator(); it.hasNext();) {
			final Resource r = (Resource) it.next();
			final List<Resource> nextChildren = resourceDao.getChildResources(r.getResourceId());
			if (CollectionUtils.isNotEmpty(nextChildren)) {
				r.setChildResources(new HashSet<Resource>(nextChildren));
				getResourceTreeHelper(r.getResourceId());
			}
		}
		return descendents;
	}

	private String getResourceType(Resource r) {
		String rt = "";
		ResourceType resType = r.getResourceType();
		if (resType != null)
			rt = resType.getResourceTypeId() + ":";
		return rt;
	}

	/**
	 * Find a resource and its descendants and return as an xml tree.
	 * 
	 * @param resourceId
	 *            the resource id
	 * 
	 * @return xml string
	 */
	public String getResourceTreeXML(String resourceId) {
		StringBuffer xml = new StringBuffer();

		Resource mainResource = resourceDao.findById(resourceId);

		xml.append("<Resource label='" + getResourceType(mainResource)
				+ mainResource.getName() + "' resourceId='"
				+ mainResource.getResourceId() + "'>");
		// xml.append("<ResourceId label='id'>" + mainResource.getResourceId() +
		// "</ResourceId>");
		// xml.append("<ResourceDescription label='desc'>" +
		// mainResource.getDescription()+ "</ResourceDescription>");
		// xml.append("<Resources label='child'>");

		// descendants:
		List<Resource> resourceTree = resourceDao.getChildResources(resourceId);
		for (Iterator<Resource> it = resourceTree.iterator(); it.hasNext();) {
			Resource r = (Resource) it.next();

			xml.append("<Resource label='" + getResourceType(r) + r.getName()
					+ "' resourceId='" + r.getResourceId() + "'>");
			// xml.append("<ResourceId label='id'>" + r.getResourceId() +
			// "</ResourceId>");
			// xml.append("<ResourceDescription label='desc'>" +
			// r.getDescription()+ "</ResourceDescription>");

			// xml.append(getResourceTreeXmlHelper(r.getResourceId(), xml));
			getResourceTreeXmlHelper(r.getResourceId(), xml);

			xml.append("</Resource>");
		}

		// xml.append("</Resources>");
		xml.append("</Resource>");

		return xml.toString();
	}

	/**
	 * Recursive helper method to get nested resource descendants as xml.
	 * 
	 * @param resourceId
	 *            the resource id
	 * @param xml
	 *            the xml
	 * 
	 * @return the resource tree xml helper
	 */
	private StringBuffer getResourceTreeXmlHelper(String resourceId,
			StringBuffer xml) {

		List<Resource> descendents = resourceDao.getChildResources(resourceId);

		// xml.append("<Resources label='child'>");
		for (Iterator<Resource> it = descendents.iterator(); it.hasNext();) {
			Resource r = (Resource) it.next();

			xml.append("<Resource label='" + getResourceType(r) + r.getName()
					+ "' resourceId='" + r.getResourceId() + "'>");
			// xml.append("<ResourceId label='id'>" + r.getResourceId() +
			// "</ResourceId>");
			// xml.append("<ResourceDescription label='desc'>" +
			// r.getDescription()+ "</ResourceDescription>");
			getResourceTreeXmlHelper(r.getResourceId(), xml);
			xml.append("</Resource>");
		}
		// xml.append("</Resources>");

		return xml;

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

		final List<Resource> resourceTree = new ArrayList<Resource>();
		resourceTree.add(resourceDao.findById(resourceId));

		final List<Resource> resourceChildren = resourceDao.getChildResources(resourceId);
		resourceTree.addAll(resourceChildren);

		for (final Iterator<Resource> it = resourceChildren.iterator(); it.hasNext();) {
			final Resource r = (Resource) it.next();
			final List<Resource> descendents = new ArrayList<Resource>();
			resourceTree.addAll(getResourceFamilyHelper(r.getResourceId(), descendents));
		}
		return resourceTree;
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
	private List<Resource> getResourceFamilyHelper(String resourceId,
			List<Resource> descendents) {

		final List<Resource> children = resourceDao.getChildResources(resourceId);
		descendents.addAll(children);
		for (final Iterator<Resource> it = children.iterator(); it.hasNext();) {
			final Resource r = (Resource) it.next();
			getResourceFamilyHelper(r.getResourceId(), descendents);
		}
		return descendents;
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
		return getDozerMappedResourceList(resourceDao.getResourcesByType(resourceTypeId));
	}

	/**
	 * Find root resources i.e. resources with null or blank value for parent
	 * 
	 * @return
	 */
	public List<Resource> getRootResources() {
		return getDozerMappedResourceList(resourceDao.getRootResources());
	}

	/**
	 * Find all resources for a specified category.
	 * 
	 * @param categoryId
	 * @return
	 */
	public List<Resource> getResourcesByCategory(String categoryId) {
		if (categoryId == null)
			throw new IllegalArgumentException("categoryId is null");
		return getDozerMappedResourceList(resourceDao.getResourcesByCategory(categoryId));
	}

	/**
	 * Find all resources with a specified branch
	 * 
	 * @param branchId
	 * @return
	 */
	public List<Resource> getResourcesByBranch(String branchId) {
		if (branchId == null)
			throw new IllegalArgumentException("branchId is null");
		return getDozerMappedResourceList(resourceDao.getResourcesByBranch(branchId));

	}

	/**
	 * Remove resources having a specified metadata type
	 * 
	 * @param resourceTypeId
	 * @return rows affected
	 */
	public int removeResourcesByType(String resourceTypeId) {
		if (resourceTypeId == null)
			throw new IllegalArgumentException("resourceTypeId is null");
		return resourceDao.removeResourcesByType(resourceTypeId);
	}

	/**
	 * Remove all resources for a specified category.
	 * 
	 * @param categoryId
	 * @return rows affected
	 */
	public int removeResourcesByCategory(String categoryId) {
		if (categoryId == null)
			throw new IllegalArgumentException("categoryId is null");
		return resourceDao.removeResourcesByCategory(categoryId);
	}

	/**
	 * Remove all resources with a specified branch
	 * 
	 * @param branchId
	 * @return rows affected
	 */
	public int removeResourcesByBranch(String branchId) {
		if (branchId == null)
			throw new IllegalArgumentException("branchId is null");
		return resourceDao.removeResourcesByBranch(branchId);

	}

	// public List<Resource> getResourceNodeTree (String resourceId) {
	//
	// List<Resource> resourceList = new ArrayList();
	//
	// List<Resource> rootChildren =
	// resourceDao.getResourcesByParent(resourceId);
	//
	// if ( (rootChildren == null)||(rootChildren.isEmpty()) ) {
	// return resourceList;
	// }
	//
	// for (Iterator<Resource> it = rootChildren.iterator(); it.hasNext(); ) {
	// Resource r = (Resource) it.next();
	// resourceList.add(r);
	// getChildResourcesNodeLevel(r.getResourceId(), resourceList);
	// }
	//
	// return resourceList;
	//
	// }

	// public List<Resource> getChildResourcesNodeLevel(String resourceId,
	// List<Resource> resourceList) {
	//
	// Resource res = this.resourceDao.findById(resourceId);
	// int nodeLevel = res.getNodeLevel();
	// ++nodeLevel;
	//
	// List<Resource> childResources =
	// resourceDao.getResourcesByParent(resourceId);
	// if ( (childResources == null)||(childResources.isEmpty()) ) {
	// return resourceList;
	// }
	//
	// for (Iterator<Resource> it = childResources.iterator(); it.hasNext(); ) {
	// Resource r = (Resource)it.next();
	// r.setNodeLevel(nodeLevel);
	// resourceList.add(r);
	// getChildResourcesNodeLevel(r.getResourceId(), resourceList);
	// }
	//
	// return resourceList;
	//
	// }

	// ResourceRole ---------------------------------------

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

        return getDozerMappedRoleList(resourceRoleDao.findRolesForResource(resourceId));
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

	public List<ResourceRole> getResourceRolesByResource(String resourceId) {
		if (resourceId == null) {
			throw new IllegalArgumentException("resourceId is null");
		}
		return resourceDao.findResourceRolesByResource(resourceId);
	}

	/**
	 * Returns a list of Resource objects that are linked to a Role.
	 * 
	 * @param domainId
	 * @param roleId
	 * @return
	 */
	public List<Resource> getResourcesForRole(String domainId, String roleId) {
		if (domainId == null) {
			throw new IllegalArgumentException("domainId is null");
		}
		if (roleId == null) {
			throw new IllegalArgumentException("roleId is null");
		}
		return getDozerMappedResourceList(resourceDao.findResourcesForRole(domainId, roleId));
	}

	/**
	 * Returns a list of Resource objects that are linked to the list of Roles.
	 * 
	 * @param domainId
	 * @param roleIdList
	 * @return
	 */
	public List<Resource> getResourcesForRoles(String domainId,
			List<String> roleIdList) {
		if (domainId == null) {
			throw new IllegalArgumentException("domainId is null");
		}
		if (roleIdList == null) {
			throw new IllegalArgumentException("roleIdList is null");
		}
		return getDozerMappedResourceList(resourceDao.findResourcesForRoles(domainId, roleIdList));
	}

	/**
	 * Add a resource role privilege
	 * 
	 * @param resourceId
	 *            the resource id
	 * @param roleId
	 *            the role id
	 * @param privilegeId
	 *            the privilege id
	 */
	public void addResourceRolePrivilege(String resourceId, String roleId,
			String privilegeId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (privilegeId == null)
			throw new IllegalArgumentException("privilegeId is null");

		resourceDao.addResourceRolePrivilege(resourceId, roleId, privilegeId);
	}

	/**
	 * Removes the resource role privilege.
	 * 
	 * @param resourceId
	 *            the resource id
	 * @param roleId
	 *            the role id
	 * @param privilegeId
	 *            the privilege id
	 */
	void removeResourceRolePrivilege(String resourceId, String roleId,
			String privilegeId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (privilegeId == null)
			throw new IllegalArgumentException("privilegeId is null");

		resourceDao.removeResourceRolePrivilege(resourceId, roleId, privilegeId);
	}

	/**
	 * Removes the all role privileges from resource.
	 * 
	 * @param resourceId
	 *            the resource id
	 */
	void removeResourceRolePrivileges(String resourceId) {
		if (resourceId == null)
			throw new IllegalArgumentException("resourceId is null");

		resourceDao.removeResourceRolePrivileges(resourceId);
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
		return getDozerMappedResourceUserList( resourceUserDao.findAllResourceForUsers(userId));
	}

     public List<Resource> getResourceObjForUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId object is null");
		}

        return getDozerMappedResourceList( resourceDao.findResourcesForUserRole(userId) );
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

	public boolean isRoleAuthorized(String domainId, String roleId,
			String resourceId) {
		log.info("isUserAuthorized called.");

		List<Resource> resList = this.getResourcesForRole(domainId, roleId);
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



    /* Temp hack ---------------------  -------------------------*/
    
    public String attributeString(String domainId, String principal) {

        List<String> oidList = new ArrayList<String>();
        String permitOverRide;
        String orgName = null;
        Organization org = null;

        Login principalLg =  loginManager.getLoginByManagedSys(domainId, principal, "0");

        if (principalLg == null) {
            return null;
        }

        User usr = userManager.getUserWithDependent(principalLg.getUserId(), true);
        List<Role> roleList =  roleDataService.getUserRoles(principalLg.getUserId());




        if (usr.getCompanyId() != null && usr.getCompanyId().length() > 0) {

            org = orgManager.getOrganization(usr.getCompanyId());
            if (org != null && org.getOrganizationName() != null) {
                orgName = org.getOrganizationName();
                // oid = org.getAlias();
              //  addOid(oidList, org.getAlias());
            }else {
                orgName = "NA";
            }
        }

        UserAttribute attrPermitOverride =  usr.getAttribute("permit-override");
        if (attrPermitOverride == null  ) {
            permitOverRide = "N";
        }else {
            if ( attrPermitOverride.getValue() == null || !attrPermitOverride.getValue().equalsIgnoreCase("Y")) {
                permitOverRide = "N";
            }else {
                permitOverRide = "Y";
            }

        }
        List<Organization> affiliationList = null;

        if (roleContains("EMERGENCY_ROLE", roleList)) {

            log.info("Emergency role found");


            affiliationList = orgManager.getAllOrganizations();
        } else {

            affiliationList =  orgManager.getOrganizationsForUser(principalLg.getUserId());
        }

        if (affiliationList != null && affiliationList.size() > 0) {
            Set<Organization> orgSet = new TreeSet<Organization>(affiliationList);
            orgSet.add(org);

            for (Organization o : orgSet) {
            //for (Organization o : affiliationList) {
                if ( o.getAlias() != null && !o.getAlias().isEmpty() ) {

                    addOid(oidList, o.getAlias());
                }

            }
        }else {
            if (org != null && org.getAlias() != null) {
                addOid(oidList, org.getAlias());
            }

        }

       // sort objects
       // role
        Set<Role> roleSet = new TreeSet<Role>(roleList);

       // oidList





        String roleStr = null;

        if (roleList != null && !roleList.isEmpty()) {

            for ( Role r : roleSet) {
            //for ( Role r : roleList) {
                if (roleStr == null) {
                    roleStr = r.getId().getRoleId();
                }else {
                    roleStr = roleStr + "," + r.getId().getRoleId();
                }

            }
        }



        StringBuffer headerString = new StringBuffer();

        if (usr.getFirstName() != null && usr.getFirstName().length() > 0) {
            headerString.append("firstname=" + usr.getFirstName());
        }else {
            headerString.append("&firstname=NA");
        }
        if (usr.getLastName() != null && usr.getLastName().length() > 0) {
            headerString.append("&secondname=" + usr.getLastName());
        }else {
            headerString.append("&secondname=NA");
        }
        headerString.append("&fullname=" + usr.getFirstName() + " " + usr.getLastName());
        if (roleStr != null && roleStr.length() > 0) {
            headerString.append("&role=" + roleStr );
        }else {
            headerString.append("&role=NO_ROLE");
        }
        headerString.append("&organization=" + orgName );
        headerString.append("&organizationoid=" + getOidString(oidList) );

        headerString.append("&permit=" + permitOverRide );

        return headerString.toString();
    }

    private void addOid(List<String> oidList, String newOid) {
        for (String oid : oidList) {
            if (oid.equalsIgnoreCase(newOid)) {
                // found - its already in the list
                return;
            }

        }
        oidList.add(newOid);
    }

    private String getOidString(List<String> oidList) {
        StringBuffer oid = new StringBuffer();

        int ctr = 0;
        for ( String o : oidList) {

            if (ctr == 0) {
                oid.append( o );
            } else {
                if (o != null && !o.isEmpty())
                oid.append("," + o);
            }
            ctr++;


        }
        if (oidList.isEmpty()) {
            return "NA";
        }
        return oid.toString();

    }


    private boolean roleContains(String roleId, List<Role> roleList) {

        if (roleList == null || roleList.isEmpty()) {
            return false;
        }



        for (Role r : roleList) {
            if (r != null) {
                log.info("Checking Role name " + r);

                if (r.getId().getRoleId().equalsIgnoreCase(roleId)) {
                    return true;
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
    
    private Resource getDozerMappedResource(final Resource resource) {
    	Resource retVal = null;
    	if(resource != null) {
    		retVal = dozerMap.get(DozerMappingType.DEEP).map(resource, Resource.class);
    	}
    	return retVal;
    }
    
    private List<Resource> getDozerMappedResourceList(final List<Resource> resourceList) {
    	final List<Resource> convertedList = new LinkedList<Resource>();
    	if(CollectionUtils.isNotEmpty(resourceList)) {
    		for(final Resource resource : resourceList) {
    			convertedList.add(dozerMap.get(DozerMappingType.DEEP).map(resource, Resource.class));
    		}
    	}
    	return convertedList;
    }
    
	private List<Role> getDozerMappedRoleList(final List<Role> roleList) {
		final List<Role> convertedList = new LinkedList<Role>();
		if(CollectionUtils.isNotEmpty(roleList)) {
			for(final Role role : roleList) {
				convertedList.add(dozerMap.get(DozerMappingType.DEEP).map(role, Role.class));
			}
		}
		return convertedList;
	}
	
	private ResourceUser getDozerMappedResourceUser(final ResourceUser resourceUser) {
		ResourceUser retVal = null;
		if(resourceUser != null) {
			retVal = dozerMap.get(DozerMappingType.DEEP).map(resourceUser, ResourceUser.class);
		}
		return retVal;
	}
	
	private List<ResourceUser> getDozerMappedResourceUserList(final List<ResourceUser> resourceUserList) {
		final List<ResourceUser> convertedList = new LinkedList<ResourceUser>();
		if(CollectionUtils.isNotEmpty(resourceUserList)) {
			for(final ResourceUser resourceUser : resourceUserList) {
				convertedList.add(dozerMap.get(DozerMappingType.DEEP).map(resourceUser, ResourceUser.class));
			}
		}
		return convertedList;
	}
}




