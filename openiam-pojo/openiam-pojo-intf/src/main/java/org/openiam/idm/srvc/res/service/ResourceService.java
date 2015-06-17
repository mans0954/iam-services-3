package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceType;

public interface ResourceService {

	void deleteResource(final String resourceId);
	void save(final ResourceEntity resource, final String requestorId);
    void addRequiredAttributes(ResourceEntity resource);
    String getResourcePropValueByName(final String resourceId, final String propName);
	ResourceEntity findResourceById(final String resourceId);
	List<ResourceEntity> findResourcesByIds(final Collection<String> resourceIdCollection);
	ResourceEntity findResourceByName(final String name);
	int count(final ResourceSearchBean searchBean);
	List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	
	@Deprecated
	int getNumOfChildResources(final String resourceId);
	@Deprecated
	List<ResourceEntity> getParentResources(final String resourceId, final int from, final int size);
	int getNumOfParentResources(final String resourceId);
	List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);
	
	@Deprecated
	int getNumOfResourceForGroup(final String groupId, final ResourceSearchBean searchBean);
	
	@Deprecated
	List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);
	
	@Deprecated
	int getNumOfResourceForUser(final String userId, final ResourceSearchBean searchBean);
	
	@Deprecated
	List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
	
	@Deprecated
	List<ResourceEntity> getResourcesForUserByType(String userId, String resourceTypeId, final ResourceSearchBean searchBean);

	void save(final ResourceTypeEntity entity);
	ResourceTypeEntity findResourceTypeById(final String id);
	List<ResourceTypeEntity> getAllResourceTypes();

    void save(final ResourcePropEntity entity);
    ResourcePropEntity findResourcePropById(final String id);
	void deleteResourceProp(final String id);

	@Deprecated
	List<ResourceEntity> getChildResources(final String resourceId, final int from, final int size);
	
	void addChildResource(final String parentResourceId, final String childResourceId, final Set<String> rights);
	void deleteChildResource(final String resourceId, final String childResourceId);

	void addResourceGroup(final String resourceId, final String groupId);
	void deleteResourceGroup(final String resourceId, final String groupId);

	void deleteResourceRole(final String resourceId, final String roleId);
	
	@Deprecated
	int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean);
    void addResourceToRole(final String resourceId, final String roleId);
    
    void validateResource2ResourceAddition(final String parentId, final String memberId, final Set<String> rights) throws BasicDataServiceException;
    
    boolean isMemberOfAnyEntity(final String resourceId);
    
    Resource getResourceDTO(final String resourceId);
    
    void validateResourceDeletion(final String resourceId) throws BasicDataServiceException;
    
    List<ResourceTypeEntity> findResourceTypes(ResourceTypeSearchBean searchBean, int from, int size);

    int countResourceTypes(ResourceTypeSearchBean searchBean);
    void deleteResourceType(String resourceTypeId);
}
