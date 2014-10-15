package org.openiam.idm.srvc.res.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.collection.spi.PersistentCollection;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
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
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class ResourceServiceImpl implements ResourceService {

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
    private ResourceDozerConverter dozerConverter;

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

    @Value("${org.openiam.resource.admin.resource.type.id}")
    private String adminResourceTypeId;

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

        /* admin resource can't have an admin resource - do this check here */
        boolean isAdminResource = StringUtils.equals(entity.getResourceType().getId(), adminResourceTypeId);
        
        if(entity.getType() != null && StringUtils.isNotBlank(entity.getType().getId())) {
        	entity.setType(typeDAO.findById(entity.getType().getId()));
        } else {
        	entity.setType(null);
        }

        if (StringUtils.isNotBlank(entity.getId())) {
            final ResourceEntity dbObject = resourceDao.findById(entity.getId());
            entity.setAdminResource(dbObject.getAdminResource());
            entity.setApproverAssociations(dbObject.getApproverAssociations());

            if (isAdminResource) {
                entity.setAdminResource(null);
            } else if (entity.getAdminResource() == null) {
                final ResourceEntity adminResource = getNewAdminResource(entity, requestorId);
                entity.setAdminResource(adminResource);
                if (CollectionUtils.isEmpty(dbObject.getApproverAssociations())) {
                    entity.addApproverAssociation(createDefaultApproverAssociations(entity, requestorId));
                }
            }
            entity.setChildResources(dbObject.getChildResources());
            entity.setParentResources(dbObject.getParentResources());
            entity.setUsers(dbObject.getUsers());
            entity.setGroups(dbObject.getGroups());
            entity.setRoles(dbObject.getRoles());

            //elementDAO.flush();
            mergeAttributes(entity, dbObject);

        } else {
            boolean addApproverAssociation = false;
            if (isAdminResource) {
                entity.setAdminResource(null);
            } else {
                entity.setAdminResource(getNewAdminResource(entity, requestorId));
                addApproverAssociation = true;
            }
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

    private ResourceEntity getNewAdminResource(final ResourceEntity entity, final String requestorId) {
        final ResourceEntity adminResource = new ResourceEntity();
        adminResource.setName(String.format("RES_ADMIN_%s_%s", entity.getName(),
                RandomStringUtils.randomAlphanumeric(2)));
        adminResource.setResourceType(resourceTypeDao.findById(adminResourceTypeId));
        adminResource.addUser(userDAO.findById(requestorId));
        return adminResource;
    }

    public void mergeAttributes(final ResourceEntity bean, final ResourceEntity dbObject) {
    	
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
    @Transactional(readOnly = true)
    public int count(ResourceSearchBean searchBean) {
        // final ResourceEntity entity =
        // resourceSearchBeanConverter.convert(searchBean);
        return resourceDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<ResourceEntity> findBeans(final ResourceSearchBean searchBean, final int from, final int size, final LanguageEntity language) {
        // final ResourceEntity resource =
        // resourceSearchBeanConverter.convert(searchBean);
        List<ResourceEntity> resultsEntities = null;
        // if (Boolean.TRUE.equals(searchBean.getRootsOnly())) {
        // resultsEntities = resourceDao.getRootResources(resource, from, size);
        // } else {
        resultsEntities = resourceDao.getByExample(searchBean, from, size);
        // }
        return resultsEntities;

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
    @Transactional(readOnly = true)
    public List<ResourceEntity> getChildResources(String resourceId, int from, int size) {
        final ResourceEntity example = new ResourceEntity();
        final ResourceEntity parent = new ResourceEntity();
        parent.setId(resourceId);
        example.addParentResource(parent);
        final List<ResourceEntity> resultList = resourceDao.getByExample(example, from, size);
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildResources(String resourceId) {
        final ResourceEntity example = new ResourceEntity();
        final ResourceEntity parent = new ResourceEntity();
        parent.setId(resourceId);
        example.addParentResource(parent);
        return resourceDao.count(example);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceEntity> getParentResources(String resourceId, int from, int size) {
        final ResourceEntity example = new ResourceEntity();
        final ResourceEntity child = new ResourceEntity();
        child.setId(resourceId);
        example.addChildResource(child);
        return resourceDao.getByExample(example, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentResources(String resourceId) {
        final ResourceEntity example = new ResourceEntity();
        final ResourceEntity child = new ResourceEntity();
        child.setId(resourceId);
        example.addChildResource(child);
        return resourceDao.count(example);
    }

    @Override
    @Transactional
    public void addChildResource(String parentResourceId, String childResourceId) {
        final ResourceEntity parent = resourceDao.findById(parentResourceId);
        final ResourceEntity child = resourceDao.findById(childResourceId);
        parent.addChildResource(child);
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
    public void addResourceGroup(String resourceId, String groupId) {
        final ResourceEntity entity = resourceDao.findById(resourceId);
        final GroupEntity groupEntity = groupDao.findById(groupId);
        entity.addGroup(groupEntity);
        resourceDao.save(entity);
    }

    @Override
    @Transactional
    public void deleteResourceGroup(String resourceId, String groupId) {
        final ResourceEntity entity = resourceDao.findById(resourceId);
        final GroupEntity groupEntity = groupDao.findById(groupId);
        entity.remove(groupEntity);
        resourceDao.save(entity);
    }

    @Override
    @Transactional
    public void addResourceToRole(String resourceId, String roleId) {
        final ResourceEntity entity = resourceDao.findById(resourceId);
        final RoleEntity roleEntity = roleDao.findById(roleId);
        entity.addRole(roleEntity);
        resourceDao.save(entity);
    }

    @Override
    @Transactional
    public void deleteResourceRole(String resourceId, String roleId) {
        final ResourceEntity entity = resourceDao.findById(resourceId);
        final RoleEntity roleEntity = roleDao.findById(roleId);
        entity.remove(roleEntity);
        resourceDao.save(entity);
    }

    @Override
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

            final ResourceEntity searchBean = new ResourceEntity();
            searchBean.setAdminResource(new ResourceEntity(resourceId));
            final List<ResourceEntity> adminOfResources = resourceDao.getByExample(searchBean);
            if (CollectionUtils.isNotEmpty(adminOfResources)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_RESOURCE, adminOfResources
                        .get(0).getName());
            }

            final RoleEntity roleSearchBean = new RoleEntity();
            roleSearchBean.setAdminResource(new ResourceEntity(resourceId));
            final List<RoleEntity> adminOfRoles = roleDao.getByExample(roleSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfRoles)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_ROLE, adminOfRoles.get(0)
                        .getName());
            }

            final GroupEntity groupSearchBean = new GroupEntity();
            groupSearchBean.setAdminResource(new ResourceEntity(resourceId));
            final List<GroupEntity> adminOfGroups = groupDao.getByExample(groupSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfGroups)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_GROUP, adminOfGroups.get(0)
                        .getName());
            }

            final OrganizationEntity orgSearchBean = new OrganizationEntity();
            orgSearchBean.setAdminResource(new ResourceEntity(resourceId));
            final List<OrganizationEntity> adminOfOrgs = orgDAO.getByExample(orgSearchBean);
            if (CollectionUtils.isNotEmpty(adminOfOrgs)) {
                throw new BasicDataServiceException(ResponseCode.RESOURCE_IS_AN_ADMIN_OF_ORG, adminOfOrgs.get(0)
                        .getName());
            }
        }
    }

    private boolean causesCircularDependency(final ResourceEntity parent, final ResourceEntity child,
                                             final Set<ResourceEntity> visitedSet) {
        boolean retval = false;
        if (parent != null && child != null) {
            if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentResources())) {
                    for (final ResourceEntity entity : parent.getParentResources()) {
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
    public Resource getResourceDTO(String resourceId) {
        return dozerConverter.convertToDTO(resourceDao.findById(resourceId), true);
    }

    @Override
    public List<ResourceTypeEntity> findResourceTypes(final ResourceTypeSearchBean searchBean, int from, int size) {
        return resourceTypeDao.getByExample(searchBean, from, size);
    }

    @Override
    public int countResourceTypes(final ResourceTypeSearchBean searchBean) {
        return resourceTypeDao.count(searchBean);
    }
}
