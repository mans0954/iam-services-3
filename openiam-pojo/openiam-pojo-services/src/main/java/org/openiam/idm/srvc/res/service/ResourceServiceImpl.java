package org.openiam.idm.srvc.res.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.dozer.converter.ResourceTypeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementPageTemplateDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceType;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.openiam.util.UserUtils;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("resourceService")
public class ResourceServiceImpl implements ResourceService, ApplicationContextAware {

    @Autowired
    private ResourceDAO resourceDao;
    @Autowired
    private RoleDAO roleDao;
    @Autowired
    private GroupDAO groupDao;
    @Autowired
    private ResourceTypeDAO resourceTypeDao;

    @Autowired
    private ResourcePropDAO resourcePropDao;

    @Autowired
    private ResourceDozerConverter resourceConverter;

    @Autowired
    private AuthProviderDao authProviderDAO;

    @Autowired
    private ContentProviderDao contentProviderDAO;

    @Autowired
    private URIPatternDao uriPatternDAO;

    @Autowired
    private MetadataElementDAO elementDAO;

    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Autowired
    private OrganizationDAO orgDAO;

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private MetadataTypeDAO typeDAO;

    @Autowired
    private MetadataElementPageTemplateDAO templateDAO;

    @Autowired
    private ResourceTypeDozerConverter resourceTypeConverter;

    @Autowired
    @Qualifier("entityValidator")
    protected EntityValidator entityValidator;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private ResourcePropDozerConverter resourcePropConverter;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private GroupDataService groupDataService;

    @Autowired
    private RoleDataService roleService;

    @Autowired
    private AccessRightDAO accessRightDAO;
    @Value("${org.openiam.ui.admin.right.id}")
    private String adminRightId;

    private static final Log log = LogFactory.getLog(ResourceDataServiceImpl.class);

    private ApplicationContext ac;


    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @Value("${org.openiam.resource.admin.resource.type.id}")
    private String adminResourceTypeId;

    @Override
    @Transactional(readOnly = true)
    public String getResourcePropValueByName(final String resourceId, final String propName) {
        return resourcePropDao.findValueByName(resourceId, propName);
    }

    @Override
    @Cacheable(value = "resourcePropCache", key = "{ #resourceId, #propName}")
    public String getResourcePropValueByNameWeb(final String resourceId, final String propName) {
        return this.getResourcePropValueByName(resourceId, propName);
    }

    @Override
    @Transactional
    public void deleteResource(String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            final ResourceEntity entity = resourceDao.findById(resourceId);
            if (entity != null) {
                resourceDao.delete(entity);
            }
        }
    }

    @Override
    @Transactional
    public void deleteResourceType(String resourceTypeId) {
        if (StringUtils.isNotBlank(resourceTypeId)) {
            final ResourceTypeEntity entity = resourceTypeDao.findById(resourceTypeId);
            if (entity != null) {
                resourceTypeDao.delete(entity);
            }
        }
    }

    @Override
    @Transactional
    public void save(ResourceEntity entity, final String requestorId) {
        if (entity.getResourceType() != null) {
            entity.setResourceType(resourceTypeDao.findById(entity.getResourceType().getId()));
        }
        
        if(entity.getType() != null && StringUtils.isNotBlank(entity.getType().getId())) {
        	entity.setType(typeDAO.findById(entity.getType().getId()));
        } else {
        	entity.setType(null);
        }

        if (StringUtils.isNotBlank(entity.getId())) {
            final ResourceEntity dbObject = resourceDao.findById(entity.getId());
            entity.setApproverAssociations(dbObject.getApproverAssociations());


            entity.setChildResources(dbObject.getChildResources());
            entity.setParentResources(dbObject.getParentResources());
            entity.setUsers(dbObject.getUsers());
            entity.setGroups(dbObject.getGroups());
            entity.setRoles(dbObject.getRoles());

            //elementDAO.flush();
            mergeAttribute(entity, dbObject);

        } else {
            boolean addApproverAssociation = false;
            resourceDao.save(entity);

            if (addApproverAssociation) {
                entity.addApproverAssociation(createDefaultApproverAssociations(entity, requestorId));
            }

            addRequiredAttributes(entity);
        }
        
        

        resourceDao.merge(entity);
    }
    @Override
    @Transactional
    public void addRequiredAttributes(ResourceEntity resource) {
        if(resource!=null && resource.getType()!=null && StringUtils.isNotBlank(resource.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(resource.getType().getId());
            List<MetadataElementEntity> elementList = elementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        resourcePropDao.save(AttributeUtil.buildResAttribute(resource, element));
                    }
                }
            }
        }
    }

    private ApproverAssociationEntity createDefaultApproverAssociations(final ResourceEntity entity,
                                                                        final String requestorId) {
        final ApproverAssociationEntity association = new ApproverAssociationEntity();
        association.setAssociationEntityId(entity.getId());
        association.setAssociationType(AssociationType.RESOURCE);
        association.setApproverLevel(Integer.valueOf(0));
        association.setApproverEntityId(requestorId);
        association.setApproverEntityType(AssociationType.USER);
        return association;
    }

/*    private ResourceEntity getNewAdminResource(final ResourceEntity entity, final String requestorId) {
        final ResourceEntity adminResource = new ResourceEntity();
        adminResource.setName(String.format("RES_ADMIN_%s_%s", entity.getName(),
                RandomStringUtils.randomAlphanumeric(2)));
        adminResource.setResourceType(resourceTypeDao.findById(adminResourceTypeId));
        adminResource.addUser(userDAO.findById(requestorId));
        return adminResource;
    }*/

    private void mergeAttribute(final ResourceEntity bean, final ResourceEntity dbObject) {
    	
    	/* 
    	 * if the incoming bean is from the database, there is no reason to do any merging 
    	 * This was written to avoid merging  of attributes when you call findById on the resourceService,
    	 * and then save the same object (see ManagedSystemServiceImpl.updateMangagedSys)
    	 */
    	if(bean.getResourceProps() != null && bean.getResourceProps() instanceof PersistentCollection) {
    		return;
    	}
        Set<ResourcePropEntity> beanProps = (bean.getResourceProps() != null) ? bean.getResourceProps() : new HashSet<ResourcePropEntity>();
        Set<ResourcePropEntity> dbProps = (dbObject.getResourceProps() != null) ? new HashSet<ResourcePropEntity>(dbObject.getResourceProps()) : new HashSet<ResourcePropEntity>();

        /* update */
        Iterator<ResourcePropEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final ResourcePropEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final ResourcePropEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setElement(getEntity(beanProp.getElement()));
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    //renewedProperties.add(dbProp);
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<ResourcePropEntity> toAdd = new HashSet<>();
        for (final ResourcePropEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final ResourcePropEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setResource(bean);
                beanProp.setElement(getEntity(beanProp.getElement()));
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setResourceProps(dbProps);
    }
    
    private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
    	if(bean != null && StringUtils.isNotBlank(bean.getId())) {
    		return elementDAO.findById(bean.getId());
    	} else {
    		return null;
    	}
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceEntity findResourceById(String resourceId) {
        return resourceDao.findById(resourceId);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Cacheable(value = "resources", key = "{ #resourceId,#language}")
    public Resource findResourceDtoById(String resourceId, Language language) {
        Resource resource = null;
        try {
            if (resourceId != null) {
                //ResourceEntity resourceEntity = resourceDao.findById(resourceId);
                ResourceEntity resourceEntity = this.getProxyService().findResourceById(resourceId);
                Resource resourceDto = resourceConverter.convertToDTO(resourceEntity, true);
                if (resourceDto != null) {
                    resource = resourceDto;
                }
            }
        } catch (Throwable e) {
            log.error("Exception", e);
        }

        return resource;
    }

/*    @Override
    @Transactional(readOnly = true)
    public ResourceEntity findResourceByIdNoLocalized(String resourceId) {
        return resourceDao.findByIdNoLocalized(resourceId);
    }*/

    @Override
    @Transactional(readOnly = true)
    public int count(ResourceSearchBean searchBean) {
        // final ResourceEntity entity =
        // resourceSearchBeanConverter.convert(searchBean);
        return resourceDao.count(searchBean);
    }

 /*   @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size) {
        return resourceDao.getByExampleNoLocalize(searchBean, from, size);
    }*/

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<ResourceEntity> findBeansLocalized(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language) {
        return resourceDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "resources", key = "{ #searchBean.cacheUniqueBeanKey, #from, #size, #language}")
    public List<Resource> findBeansLocalizedDto(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language) {
        //List<ResourceEntity> resourceEntityList = this.findBeansLocalized(searchBean, from, size, language);

        //ResourceService bean = (ResourceService)ac.getBean("resourceService");
        List<ResourceEntity> resourceEntityList = this.getProxyService().findBeansLocalized(searchBean, from, size, language);

        List<Resource> resourceList = resourceConverter.convertToDTOList(resourceEntityList, searchBean.isDeepCopy());
        return resourceList;
    }

    @Override
    @Transactional(readOnly = true)
    public ResourcePropEntity findResourcePropById(String id) {
        return resourcePropDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceEntity findResourceByName(String name) {
        return resourceDao.findByName(name);
    }

    @Override
    @Transactional
    public void save(ResourceTypeEntity entity) {
        if (StringUtils.isBlank(entity.getId())) {
            resourceTypeDao.save(entity);
        } else {
            resourceTypeDao.merge(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceTypeEntity findResourceTypeById(String id) {
        return resourceTypeDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceTypeEntity> getAllResourceTypes() {
        return resourceTypeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<ResourceType> getAllResourceTypesDto(final Language language) {
        //List<ResourceTypeEntity> resourceTypeEntityList = resourceTypeDao.findAll();
        List<ResourceTypeEntity> resourceTypeEntityList = this.getProxyService().getAllResourceTypes();
        return resourceTypeConverter.convertToDTOList(resourceTypeEntityList, false);
    }

    @Override
    @Transactional
    public void save(ResourcePropEntity entity) {
        if (StringUtils.isBlank(entity.getId())) {
            resourcePropDao.save(entity);
        } else {
            resourcePropDao.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteResourceProp(String id) {
        final ResourcePropEntity entity = resourcePropDao.findById(id);
        if (entity != null) {
            resourcePropDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public void addChildResource(final String parentResourceId,
                                 final String childResourceId,
                                 final Set<String> rights,
                                 final Date startDate,
                                 final Date endDate) {
        final ResourceEntity parent = resourceDao.findById(parentResourceId);
        final ResourceEntity child = resourceDao.findById(childResourceId);
        parent.addChildResource(child, accessRightDAO.findByIds(rights), startDate, endDate);
        resourceDao.save(parent);
    }

    @Override
    @Transactional
    public void deleteChildResource(String resourceId, String childResourceId) {
        final ResourceEntity parent = resourceDao.findById(resourceId);
        final ResourceEntity child = resourceDao.findById(childResourceId);
        parent.removeChildResource(child);
        resourceDao.save(parent);
    }

    @Override
    @Transactional
    public void addResourceGroup(final String resourceId,
                                 final String groupId,
                                 final Set<String> rightIds,
                                 final Date startDate,
                                 final Date endDate) {
        final ResourceEntity resource = resourceDao.findById(resourceId);
        final GroupEntity group = groupDao.findById(groupId);
        if(resource != null && group != null) {
            group.addResource(resource, accessRightDAO.findByIds(rightIds), startDate, endDate);
        }
    }

    @Override
    @Transactional
    public void deleteResourceGroup(String resourceId, String groupId) {
        final ResourceEntity resource = resourceDao.findById(resourceId);
        final GroupEntity group = groupDao.findById(groupId);
        if(resource != null && group != null) {
            group.removeResource(resource);
        }
     resourceDao.evictCache();
    }

    @Override
    @Transactional
    public void addResourceToRole(final String resourceId,
                                  final String roleId,
                                  final Set<String> rightIds,
                                  final Date startDate,
                                  final Date endDate) {
        final ResourceEntity resource = resourceDao.findById(resourceId);
        final RoleEntity role = roleDao.findById(roleId);
        if(resource != null & role != null) {
            role.addResource(resource, accessRightDAO.findByIds(rightIds), startDate, endDate);
            roleDao.save(role);
        }
    }

    @Override
    @Transactional
    public void deleteResourceRole(String resourceId, String roleId) {
        final ResourceEntity resource = resourceDao.findById(resourceId);
        final RoleEntity role = roleDao.findById(roleId);
        if(resource != null && role != null) {
            role.removeResource(resource);
            roleDao.update(role);
        }
    }

/*    @Override
    @Transactional(readOnly = true)
    public int getNumOfResourcesForRole(String roleId, final ResourceSearchBean searchBean) {
        return resourceDao.getNumOfResourcesForRole(roleId, searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getResourcesForRole(String roleId, int from, int size,
                                                    final ResourceSearchBean searchBean) {
        return resourceDao.getResourcesForRole(roleId, from, size, searchBean);
    }



    @Override
    @Transactional(readOnly = true)
    public int getNumOfResourceForGroup(String groupId, final ResourceSearchBean searchBean) {
        return resourceDao.getNumOfResourcesForGroup(groupId, searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getResourcesForGroup(String groupId, int from, int size,
                                                     final ResourceSearchBean searchBean) {
        return resourceDao.getResourcesForGroup(groupId, from, size, searchBean);
    }



    @Override
    @Transactional(readOnly = true)
    public int getNumOfResourceForUser(String userId, final ResourceSearchBean searchBean) {
        return resourceDao.getNumOfResourcesForUser(userId, searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getResourcesForUser(String userId, int from, int size,
                                                    final ResourceSearchBean searchBean) {
        return resourceDao.getResourcesForUser(userId, from, size, searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getResourcesForUserByType(String userId, String resourceTypeId,
                                                          final ResourceSearchBean searchBean) {
        return resourceDao.getResourcesForUserByType(userId, resourceTypeId, searchBean);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResourcesForRoleNoLocalized(String roleId, int from, int size, ResourceSearchBean searchBean) {
        List<ResourceEntity> resourceEntities = resourceDao.getResourcesForRoleNoLocalized(roleId, from, size, searchBean);
        return resourceConverter.convertToDTOList(resourceEntities, searchBean.isDeepCopy());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResourcesForGroupNoLocalized(String groupId, int from, int size, ResourceSearchBean searchBean) {
        List<ResourceEntity> resourceEntities = resourceDao.getResourcesForGroupNoLocalized(groupId, from, size, searchBean);
        return resourceConverter.convertToDTOList(resourceEntities, searchBean.isDeepCopy());
    }

    @Override
    @Transactional
    public void validateResource2ResourceAddition(final String parentId, final String memberId, final Set<String> rights, final Date startDate, final Date endDate)
            throws BasicDataServiceException {
        if (StringUtils.isBlank(parentId) || StringUtils.isBlank(memberId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                    "Parent ResourceId or Child ResourceId is null");
        }

        final ResourceEntity parent = resourceDao.findById(parentId);
        final ResourceEntity child = resourceDao.findById(memberId);

        if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if(startDate != null && endDate != null && startDate.after(endDate)) {
            throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
        }

        if (causesCircularDependency(parent, child, new HashSet<ResourceEntity>())) {
            throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
        }

        /*
         * This no longer makes sense within the context of Access Rights
        if (parent.hasChildResoruce(child)) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }
        */

        if (StringUtils.equals(parentId, memberId)) {
            throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
        }

        if (parent.getResourceType() != null && !parent.getResourceType().isSupportsHierarchy()) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, parent
                    .getResourceType().getDescription());
        }

        if (child.getResourceType() != null && !child.getResourceType().isSupportsHierarchy()) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, child
                    .getResourceType().getDescription());
        }
    }

    @Override
    @Transactional(readOnly=true)
    public boolean isMemberOfAnyEntity(String resourceId) {
        final ResourceEntity resource = findResourceById(resourceId);
        if(resource != null) {
            return CollectionUtils.isNotEmpty(resource.getChildResources()) ||
                    CollectionUtils.isNotEmpty(resource.getGroups()) ||
                    CollectionUtils.isNotEmpty(resource.getOrganizations()) ||
                    CollectionUtils.isNotEmpty(resource.getRoles());
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> findResourcesByIds(Collection<String> resourceIdCollection) {
        return resourceDao.findByIds(resourceIdCollection);
    }

    @Override
    @Transactional
    public void validateResource2ResourceAddition(final String parentId, final String memberId)
            throws BasicDataServiceException {
        if (StringUtils.isBlank(parentId) || StringUtils.isBlank(memberId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                    "Parent ResourceId or Child ResourceId is null");
        }

        final ResourceEntity parent = resourceDao.findById(parentId);
        final ResourceEntity child = resourceDao.findById(memberId);

        if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (causesCircularDependency(parent, child, new HashSet<ResourceEntity>())) {
            throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
        }

        if (parent.hasChildResoruce(child)) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }

        if (StringUtils.equals(parentId, memberId)) {
            throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
        }

        if (parent.getResourceType() != null && !parent.getResourceType().isSupportsHierarchy()) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, parent
                    .getResourceType().getDescription());
        }

        if (child.getResourceType() != null && !child.getResourceType().isSupportsHierarchy()) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_TYPE_NOT_SUPPORTS_HIERARCHY, child
                    .getResourceType().getDescription());
        }
    }

    @Override
    @Transactional
    public void validateResourceDeletion(final String resourceId) throws BasicDataServiceException {
        final ResourceEntity entity = resourceDao.findById(resourceId);
        if (entity != null) {

            final List<ManagedSysEntity> managedSystems = managedSysDAO.findByResource(resourceId);
            if (CollectionUtils.isNotEmpty(managedSystems)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_MANAGED_SYSTEM, managedSystems.get(0)
                        .getName());
            }

            final List<ContentProviderEntity> contentProviders = contentProviderDAO.getByResourceId(resourceId);
            if (CollectionUtils.isNotEmpty(contentProviders)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_CONTENT_PROVIDER, contentProviders.get(0)
                        .getName());
            }

            final List<URIPatternEntity> uriPatterns = uriPatternDAO.getByResourceId(resourceId);
            if (CollectionUtils.isNotEmpty(uriPatterns)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_URI_PATTERN, uriPatterns.get(0).getPattern());
            }

            final List<AuthProviderEntity> authProviders = authProviderDAO.getByResourceId(resourceId);
            if (CollectionUtils.isNotEmpty(authProviders)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER, authProviders
                        .get(0).getName());
            }

            final List<MetadataElementEntity> metadataElements = elementDAO.getByResourceId(resourceId);
            if (CollectionUtils.isNotEmpty(metadataElements)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_METADATA_ELEMENT, metadataElements.get(0)
                        .getAttributeName());
            }

            final List<MetadataElementPageTemplateEntity> pageTemplates = templateDAO.getByResourceId(resourceId);
            if (CollectionUtils.isNotEmpty(pageTemplates)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_PAGE_TEMPLATE, pageTemplates.get(0)
                        .getName());
            }

            /*final ResourceEntity searchBean = new ResourceEntity();
            final List<ResourceEntity> adminOfResources = resourceDao.getByExample(searchBean);
            if (CollectionUtils.isNotEmpty(adminOfResources)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_RESOURCE, adminOfResources
                        .get(0).getName());
            }*/

/*            final RoleEntity roleSearchBean = new RoleEntity();
            final List<RoleEntity> adminOfRoles = roleDao.getByExample(roleSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfRoles)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_ROLE, adminOfRoles.get(0)
                        .getName());
            }

            final GroupEntity groupSearchBean = new GroupEntity();
            final List<GroupEntity> adminOfGroups = groupDao.getByExample(groupSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfGroups)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_GROUP, adminOfGroups.get(0)
                        .getName());
            }

            final OrganizationEntity orgSearchBean = new OrganizationEntity();
            final List<OrganizationEntity> adminOfOrgs = orgDAO.getByExample(orgSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfOrgs)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_ORG, adminOfOrgs.get(0)
                        .getName());
            }*/
        }
    }

    private boolean causesCircularDependency(final ResourceEntity parent, final ResourceEntity child,
                                             final Set<ResourceEntity> visitedSet) {
        boolean retval = false;
        if (parent != null && child != null) {
            if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentResources())) {
                    for (final ResourceToResourceMembershipXrefEntity xref : parent.getParentResources()) {
                        final ResourceEntity entity = xref.getEntity();
                        retval = entity.getId().equals(child.getId());
                        if (retval) {
                            break;
                        }
                        causesCircularDependency(parent, entity, visitedSet);
                    }
                }
            }
        }
        return retval;
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getResourceDTO(String resourceId) {
        return getResourceDTO(resourceId, true);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "resources", key = "{ #resourceId, #isDeepCopy}")
    public Resource getResourceDTO(String resourceId, boolean isDeepCopy) {
        return resourceConverter.convertToDTO(resourceDao.findById(resourceId), isDeepCopy);
    }

    @Override
    public List<ResourceTypeEntity> findResourceTypes(final ResourceTypeSearchBean searchBean, int from, int size) {
        return resourceTypeDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<ResourceType> findResourceTypesDto(final ResourceTypeSearchBean searchBean, int from, int size, Language language) {
        //List<ResourceTypeEntity> resourceTypeEntityList = resourceTypeDao.getByExample(searchBean, from, size);
        List<ResourceTypeEntity> resourceTypeEntityList = this.getProxyService().findResourceTypes(searchBean, from, size);
        return resourceTypeConverter.convertToDTOList(resourceTypeEntityList, searchBean.isDeepCopy());
    }

    @Override
    public int countResourceTypes(final ResourceTypeSearchBean searchBean) {
        return resourceTypeDao.count(searchBean);
    }

    @Override
    public void validate(final Resource resource) throws BasicDataServiceException {
        if (resource == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Role object is null");
        }

        ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
        if (StringUtils.isEmpty(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Name is null or empty");
        }

	/* duplicate name check */
        final ResourceEntity nameCheck = this.findResourceByName(entity.getName());
        if (nameCheck != null) {
            if (StringUtils.isBlank(entity.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
            } else if (!nameCheck.getId().equals(entity.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Resource Name is already in use");
            }
        }

        if (entity.getResourceType() == null || StringUtils.isBlank(entity.getResourceType().getId())) {
            throw new BasicDataServiceException(ResponseCode.INVALID_RESOURCE_TYPE, "Resource Type is not set");
        }

        entityValidator.isValid(entity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "resources", allEntries = true),
            @CacheEvict(value = "resourcePropCache", allEntries = true)
    })
    @Transactional
    public ResourceEntity saveResource(Resource resource, final String requesterId) throws BasicDataServiceException {
        //final Response response = new Response(ResponseStatus.SUCCESS);
        this.validate(resource);
        final ResourceEntity entity = resourceConverter.convertToEntity(resource, true);
        this.save(entity, requesterId);
        //response.setResponseValue(entity.getId());

        return entity;
    }

    @Override
    @CacheEvict(value = "resourcePropCache", allEntries = true)
    @Transactional
    public ResourcePropEntity saveOrUpdateResourceProperty(ResourceProp prop, IdmAuditLogEntity idmAuditLog) throws BasicDataServiceException {
        if (prop == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Resource Property object is null");
        }

        final ResourcePropEntity entity = resourcePropConverter.convertToEntity(prop, false);
        if (StringUtils.isNotBlank(prop.getId())) {
            final ResourcePropEntity dbObject = this.findResourcePropById(prop.getId());
            if (dbObject == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND,
                        "No Resource Property object is found");
            }
        }

        if (StringUtils.isBlank(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME, "Resource Property name is not set");
        }

        if (StringUtils.isBlank(entity.getValue())) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_VALUE_MISSING,
                    "Resource Property value is not set");
        }

        if (entity == null || StringUtils.isBlank(entity.getResource().getId())) {
            throw new BasicDataServiceException(ResponseCode.RESOURCE_PROP_RESOURCE_ID_MISSING,
                    "Resource ID is not set for Resource Property object");
        }
        this.save(entity);
        ResourcePropEntity resourcePropEntity = entity;
        //response.setResponseValue(entity.getId());
        //idmAuditLog.succeed();

        return resourcePropEntity;
    }

    @Override
    @CacheEvict(value = "resourcePropCache", allEntries = true)
    @Transactional
    public void removeResourceProp(String resourcePropId, String requesterId) throws BasicDataServiceException {
        if (StringUtils.isBlank(resourcePropId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                    "Resource property ID is not specified");
        }
        this.deleteResourceProp(resourcePropId);
    }

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void removeUserFromResource(String resourceId, String userId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_USER_FROM_RESOURCE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

        idmAuditLog.setAuditDescription(String.format("Remove user %s from resource: %s", userId, resourceId));

        if (resourceId == null || userId == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
        }
        userDataService.removeUserFromResource(userId, resourceId);
    }

/*
    @Override
    @Transactional
    @CacheEvict(value = "resources", allEntries = true)
    public void addUserToResource(String resourceId, String userId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_USER_TO_RESOURCE.value());
        UserEntity userEntity = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

        idmAuditLog.setAuditDescription(String.format("Add user %s to resource: %s", userId, resourceId));

        if (resourceId == null || userId == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ResourceId or UserId is not set");
        }

        userDataService.addUserToResource(userId, resourceId);
    }
*/

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void deleteResourceWeb(String resourceId, String requesterId) throws BasicDataServiceException {
        if (resourceId == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "Resource ID is not specified");
        }

        this.validateResourceDeletion(resourceId);
        this.deleteResource(resourceId);
    }

/*    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void addChildResourceWeb(String resourceId, String childResourceId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_CHILD_RESOURCE.value());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        ResourceEntity resourceEntityChild = this.findResourceById(childResourceId);
        idmAuditLog.setTargetResource(childResourceId, resourceEntityChild.getName());

        idmAuditLog.setAuditDescription(
                String.format("Add child resource: %s to resource: %s", childResourceId, resourceId));

        this.validateResource2ResourceAddition(resourceId, childResourceId);
        this.addChildResource(resourceId, childResourceId);
    }

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void deleteChildResourceWeb(String resourceId, String memberResourceId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_CHILD_RESOURCE.value());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        ResourceEntity resourceEntityChild = this.findResourceById(memberResourceId);
        idmAuditLog.setTargetResource(memberResourceId, resourceEntityChild.getName());

        idmAuditLog.setAuditDescription(
                String.format("Remove child resource: %s from resource: %s", memberResourceId, resourceId));

        if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(memberResourceId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS,
                    "Parent ResourceId or Child ResourceId is null");
        }

        this.deleteChildResource(resourceId, memberResourceId);
    }

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void addGroupToResourceWeb(String resourceId, String groupId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_GROUP_TO_RESOURCE.value());
        Group group = groupDataService.getGroupDTO(groupId);
        idmAuditLog.setTargetGroup(groupId, group.getName());
        Resource resource = this.findResourceDtoById(resourceId, null);
        idmAuditLog.setTargetResource(resourceId, resource.getName());

        idmAuditLog.setAuditDescription(String.format("Add group: %s to resource: %s", groupId, resourceId));
        if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
        }

        this.addResourceGroup(resourceId, groupId);
    }

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void removeGroupToResource(String resourceId, String groupId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_GROUP_FROM_RESOURCE.value());
        GroupEntity groupEntity = groupDataService.getGroup(groupId);
        idmAuditLog.setTargetGroup(groupId, groupEntity.getName());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Remove group: %s from resource: %s", groupId, resourceId));

        if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(groupId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or ResourceId is null");
        }

        this.deleteResourceGroup(resourceId, groupId);
    }*/

/*    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void addRoleToResourceWeb(String resourceId, String roleId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.ADD_ROLE_TO_RESOURCE.value());
        RoleEntity roleEntity = roleService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());

        idmAuditLog.setAuditDescription(String.format("Add role: %s to resource: %s", roleId, resourceId));

        if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
        }

        this.addResourceToRole(resourceId, roleId);
    }*/

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    @Transactional
    public void removeRoleToResource(String resourceId, String roleId, String requesterId, IdmAuditLog idmAuditLog) throws BasicDataServiceException {
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.REMOVE_ROLE_FROM_RESOURCE.value());
        RoleEntity roleEntity = roleService.getRole(roleId);
        idmAuditLog.setTargetRole(roleId, roleEntity.getName());
        ResourceEntity resourceEntity = this.findResourceById(resourceId);
        idmAuditLog.setTargetResource(resourceId, resourceEntity.getName());
        idmAuditLog.setAuditDescription(String.format("Remove role: %s from resource: %s", roleId, resourceId));

        if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(roleId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "RoleId or ResourceId is null");
        }
        this.deleteResourceRole(resourceId, roleId);
    }

    private ResourceService getProxyService() {
        ResourceService service = (ResourceService) ac.getBean("resourceService");
        return service;
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Resource> getResourcesDtoForGroup(String groupId, int from, int size,
                                                  final ResourceSearchBean searchBean, Language language) {
        List<ResourceEntity> resourceEntityList = resourceDao.getResourcesForGroup(groupId, from, size, searchBean);
        return resourceConverter.convertToDTOList(resourceEntityList, false);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Resource> findResourcesDtoByIds(Collection<String> resourceIdCollection, final Language language) {
        List<Resource> resourceList = null;
        try {
            if (CollectionUtils.isNotEmpty(resourceIdCollection)) {
                //List<ResourceEntity> resourceEntityList = resourceDao.findByIds(resourceIdCollection);
                List<ResourceEntity> resourceEntityList = this.getProxyService().findResourcesByIds(resourceIdCollection);
                List<Resource> resourceListDto = resourceConverter.convertToDTOList(resourceEntityList, true);
                if (CollectionUtils.isNotEmpty(resourceListDto)) {
                    resourceList = resourceListDto;
                }
            }
        } catch (Throwable e) {
            log.error("Exception", e);
        }
        return resourceList;
    }


    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Resource> getResourcesDtoForRole(String roleId, int from, int size,
                                                 final ResourceSearchBean searchBean, Language language) {
        List<ResourceEntity> resourceEntityList = resourceDao.getResourcesForRole(roleId, from, size, searchBean);
        return resourceConverter.convertToDTOList(resourceEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Resource> getResourcesDtoForUser(String userId, int from, int size,
                                                 final ResourceSearchBean searchBean, Language language) {
        List<ResourceEntity> resourceEntityList = resourceDao.getResourcesForUser(userId, from, size, searchBean);
        return resourceConverter.convertToDTOList(resourceEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language) {
        return resourceDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Resource> getResourcesDtoForUserByType(String userId, String resourceTypeId,
                                                       final ResourceSearchBean searchBean, Language language) {
        List<ResourceEntity> resourceEntityList = resourceDao.getResourcesForUserByType(userId, resourceTypeId, searchBean);
        return resourceConverter.convertToDTOList(resourceEntityList, true);
    }
}
