package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.Date;
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

	public void deleteResource(final String resourceId);
	public void save(final ResourceEntity resource, final String requestorId);
    public void addRequiredAttributes(ResourceEntity resource);
    public String getResourcePropValueByName(final String resourceId, final String propName);
	public ResourceEntity findResourceById(final String resourceId);
	public List<ResourceEntity> findResourcesByIds(final Collection<String> resourceIdCollection);
	public ResourceEntity findResourceByName(final String name);
	public int count(final ResourceSearchBean searchBean);
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	
	public void save(final ResourceTypeEntity entity);
	public ResourceTypeEntity findResourceTypeById(final String id);
	public List<ResourceTypeEntity> getAllResourceTypes();

    public void save(final ResourcePropEntity entity);
    public ResourcePropEntity findResourcePropById(final String id);
	public void deleteResourceProp(final String id);

	public void addChildResource(final String parentResourceId, final String childResourceId, final Set<String> rights, final Date startDate, final Date endDate);
	public void deleteChildResource(final String resourceId, final String childResourceId);

	public void addResourceGroup(final String resourceId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate);
	public void deleteResourceGroup(final String resourceId, final String groupId);

	public void deleteResourceRole(final String resourceId, final String roleId);
	
    public void addResourceToRole(final String resourceId, final String roleId, final Set<String> rightIds, final Date startDate, final Date endDate);
    
    public void validateResource2ResourceAddition(final String parentId, final String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException;
    
    public boolean isMemberOfAnyEntity(final String resourceId);
    
    public Resource getResourceDTO(final String resourceId);
    
    public void validateResourceDeletion(final String resourceId) throws BasicDataServiceException;
    
    public List<ResourceTypeEntity> findResourceTypes(ResourceTypeSearchBean searchBean, int from, int size);

    int countResourceTypes(ResourceTypeSearchBean searchBean);
    void deleteResourceType(String resourceTypeId);
}
