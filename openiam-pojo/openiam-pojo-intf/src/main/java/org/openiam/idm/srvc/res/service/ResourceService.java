package org.openiam.idm.srvc.res.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourcePropSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceType;

public interface ResourceService {

	public void deleteResource(final String resourceId);
	public void save(final ResourceEntity resource, final String requestorId);
    public void addRequiredAttributes(ResourceEntity resource);
    public List<ResourceProp> findBeansDTO(final ResourcePropSearchBean sb, final int from, final int size);
    public List<ResourcePropEntity> findBeans(final ResourcePropSearchBean sb, final int from, final int size);

    //For system use only
    //public ResourceEntity findResourceByIdNoLocalized(String resourceId);

	public ResourceEntity findResourceById(final String resourceId);
	public Resource findResourceDtoById(String resourceId, Language language);
	public List<ResourceEntity> findResourcesByIds(final Collection<String> resourceIdCollection);
	public List<Resource> findResourcesDtoByIds(Collection<String> resourceIdCollection, Language language);
	public ResourceEntity findResourceByName(final String name);
	public int count(final ResourceSearchBean searchBean);
	//public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	//public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size);
	public List<ResourceEntity> findBeansLocalized(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	public List<Resource> findBeansLocalizedDto(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language);
	/*	public int getNumOfChildResources(final String resourceId);
	public List<ResourceEntity> getParentResources(final  String resourceId, final int from, final int size);
	public List<Resource> getParentResourcesDto(String resourceId, int from, int size, Language language);
	public int getNumOfParentResources(final String resourceId);
	public List<ResourceEntity> getResourcesForRole(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);*/
	public List<Resource> getResourcesDtoForRole(String roleId, int from, int size, final ResourceSearchBean searchBean, Language language);

    /**
     * For internal system use only, without @LocalizedServiceGet
     * @param roleId
     * @param from
     * @param size
     * @param searchBean
     * @return
     */
    public List<Resource> getResourcesForRoleNoLocalized(final String roleId, final int from, final int size, final ResourceSearchBean searchBean);

	//public int getNumOfResourceForGroup(final String groupId, final ResourceSearchBean searchBean);

    /**
     * For internal use only, without @LocalizedServiceGet
     * @param groupId
     * @param from
     * @param size
     * @param searchBean
     * @return
     */
    public List<Resource> getResourcesForGroupNoLocalized(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);

    //public List<ResourceEntity> getResourcesForGroup(final String groupId, final int from, final int size, final ResourceSearchBean searchBean);
	public List<Resource> getResourcesDtoForGroup(String groupId, int from, int size, final ResourceSearchBean searchBean, Language language);
	//public int getNumOfResourceForUser(final String userId, final ResourceSearchBean searchBean);
	//public List<ResourceEntity> getResourcesForUser(final String userId, final int from, final int size, final ResourceSearchBean searchBean);
	public List<Resource> getResourcesDtoForUser(String userId, int from, int size, final ResourceSearchBean searchBean, Language language);
    //public List<ResourceEntity> getResourcesForUserByType(String userId, String resourceTypeId, final ResourceSearchBean searchBean);
	public List<Resource> getResourcesDtoForUserByType(String userId, String resourceTypeId, final ResourceSearchBean searchBean, Language language);

	public void save(final ResourceTypeEntity entity);
	public ResourceTypeEntity findResourceTypeById(final String id);
	public List<ResourceTypeEntity> getAllResourceTypes();
	public List<ResourceType> getAllResourceTypesDto(final Language language);

    public ResourcePropEntity findResourcePropById(final String id);

	//public List<ResourceEntity> getChildResources(final String resourceId, final int from, final int size);

	//public List<Resource> getChildResourcesDto(String resourceId, int from, int size, Language language);
	public void addChildResource(final String parentResourceId, final String childResourceId, final Set<String> rights, final Date startDate, final Date endDate);
	public void deleteChildResource(final String resourceId, final String childResourceId);

	public void addResourceGroup(final String resourceId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate);
	public void deleteResourceGroup(final String resourceId, final String groupId);

	public void deleteResourceRole(final String resourceId, final String roleId);
		//public int getNumOfResourcesForRole(final String roleId, final ResourceSearchBean searchBean);
    public void addResourceToRole(final String resourceId, final String roleId, final Set<String> rightIds, final Date startDate, final Date endDate);
    
    public void validateResource2ResourceAddition(final String parentId, final String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException;
    
    public boolean isMemberOfAnyEntity(final String resourceId);
        public void validateResource2ResourceAddition(final String parentId, final String memberId) throws BasicDataServiceException;
    public Resource getResourceDTO(final String resourceId);

	public Resource getResourceDTO(String resourceId, boolean isDeepCopy);
    
    public void validateResourceDeletion(final String resourceId) throws BasicDataServiceException;
    
    public List<ResourceTypeEntity> findResourceTypes(ResourceTypeSearchBean searchBean, int from, int size);

	public List<ResourceType> findResourceTypesDto(final ResourceTypeSearchBean searchBean, int from, int size, Language language);

    int countResourceTypes(ResourceTypeSearchBean searchBean);
    void deleteResourceType(String resourceTypeId);

	public void validate(final Resource resource) throws BasicDataServiceException;

	public ResourceEntity saveResource(Resource resource, final String requesterId) throws BasicDataServiceException;

	//public void removeUserFromResource(String resourceId, final String userId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void addUserToResource(String resourceId, String userId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void deleteResourceWeb(String resourceId, String requesterId) throws BasicDataServiceException;

	//public void addChildResourceWeb(String resourceId, String childResourceId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void deleteChildResourceWeb(String resourceId, String memberResourceId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void addGroupToResourceWeb(String resourceId, String groupId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void removeGroupToResource(String resourceId, String groupId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	//public void addRoleToResourceWeb(String resourceId, String roleId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	public void removeRoleToResource(String resourceId, String roleId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException;

	public void saveAttribute(final ResourcePropEntity attribute);
}
