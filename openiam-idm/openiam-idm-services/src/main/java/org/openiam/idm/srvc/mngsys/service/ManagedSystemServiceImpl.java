package org.openiam.idm.srvc.mngsys.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagedSystemServiceImpl implements ManagedSystemService {

    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Autowired
    protected AttributeMapDAO attributeMapDAO;
    @Autowired
    protected ReconciliationResourceAttributeMapDAO reconciliationResourceAttributeMapDAO;
    @Autowired
    protected DefaultReconciliationAttributeMapDAO defaultReconciliationAttributeMapDAO;
    @Autowired
    protected ManagedSysRuleDAO managedSysRuleDAO;
    @Autowired
    protected PolicyDAO policyDAO;

    @Autowired
    private ManagedSystemObjectMatchDAO matchDAO;
    
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    
    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    
    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    private AuthProviderDao authProviderDao;
    
    @Autowired
    private RoleDAO roleDAO;
    
    @Autowired
    private ApproverAssociationDAO approverAssociationDao;
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private MetadataElementDAO elementDAO;

    private static final String resourceTypeId="MANAGED_SYS";

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getManagedSystemsByExample(
            ManagedSysEntity example, Integer from, Integer size) {
        return managedSysDAO.getByExample(example, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getManagedSystemsCountByExample(ManagedSysEntity example) {
        return managedSysDAO.count(example);
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysById(String id) {
        return managedSysDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getManagedSysByConnectorId(String connectorId) {
        return managedSysDAO.findbyConnectorId(connectorId);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ManagedSysEntity> getManagedSysByDomain(String domainId) {
//        return managedSysDAO.findbyDomain(domainId);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getAllManagedSys() {
        return managedSysDAO.findAllManagedSys();
    }

    @Override
    @Transactional
    public void removeManagedSysById(String id) throws BasicDataServiceException {
        final ManagedSysEntity sysEntity = managedSysDAO.findById(id);
        if(sysEntity != null) {
        	if(CollectionUtils.isNotEmpty(sysEntity.getAuthProviders())) {
        		throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER, sysEntity.getAuthProviders().iterator().next().getName());
        	}
        	
        	/*
        	if(CollectionUtils.isNotEmpty(sysEntity.getContentProviders())) {
        		throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_CONTENT_PROVIDERS);
        	}
        	*/
        	
	        for (final ManagedSystemObjectMatchEntity matchEntity : sysEntity.getMngSysObjectMatchs()) {
	            matchDAO.delete(matchEntity);
	        }
	        for (final ManagedSysRuleEntity ruleEntity : sysEntity.getRules()) {
	            managedSysRuleDAO.delete(ruleEntity);
	        }
	        if(CollectionUtils.isNotEmpty(sysEntity.getGroups())) {
	        	for(final GroupEntity group : sysEntity.getGroups()) {
	        		group.setManagedSystem(null);
	        		groupDAO.update(group);
	        	}
	        }
	        
	        if(CollectionUtils.isNotEmpty(sysEntity.getRoles())) {
	        	for(final RoleEntity role : sysEntity.getRoles()) {
	        		role.setManagedSystem(null);
	        		roleDAO.update(role);
	        	}
	        }
	        
	        managedSysDAO.delete(sysEntity);
	        if(sysEntity.getResource() != null) {
	        	resourceService.deleteResource(sysEntity.getResource().getId());
	        }
        }
    }
    
    @Override
    @Transactional
    public void saveManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch) {
        ManagedSystemObjectMatchEntity entity = managedSystemObjectMatchDozerConverter.convertToEntity(objectMatch, false);
        matchDAO.save(entity);
    }

    @Override
    @Transactional
    public void updateManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch) {
        ManagedSystemObjectMatchEntity entity = managedSystemObjectMatchDozerConverter.convertToEntity(objectMatch, false);
        matchDAO.update(entity);
    }

    @Override
    @Transactional
    public void deleteManagedSystemObjectMatch(String objectMatchId) {
        ManagedSystemObjectMatchEntity entity = matchDAO.findById(objectMatchId);
        matchDAO.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysByResource(String id, String status) {
        return managedSysDAO.findByResource(id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysByName(String name) {
        return managedSysDAO.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeMapEntity getAttributeMap(String attributeMapId) {
        return attributeMapDAO.findById(attributeMapId);

    }

    @Override
    public AttributeMapEntity addAttributeMap(AttributeMapEntity attributeMap) {

        attributeMap.setReconResAttribute(this
                .saveReconResAttributeMap(attributeMap.getReconResAttribute()));
        return attributeMapDAO.add(attributeMap);
    }

    @Override
    public void updateAttributeMap(AttributeMapEntity attributeMap) {
        attributeMap.setReconResAttribute(this
                .saveReconResAttributeMap(attributeMap.getReconResAttribute()));
        attributeMapDAO.update(attributeMap);
    }

    @Override
    @Transactional
    public void removeAttributeMap(String attributeMapId) {
        AttributeMapEntity amE = attributeMapDAO.findById(attributeMapId);
        if (amE.getReconResAttribute() != null) {
            reconciliationResourceAttributeMapDAO.delete(amE
                    .getReconResAttribute());
            amE.setReconResAttribute(null);
        }
        if (amE != null)
            attributeMapDAO.delete(amE);
    }

    @Override
    @Transactional
    public void removeResourceAttributeMaps(String resourceId) {
        attributeMapDAO.removeResourceAttributeMaps(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getResourceAttributeMaps(String resourceId) {
        return attributeMapDAO.findByResourceId(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getAttributeMapsByManagedSysId(String managedSysId) {
        return attributeMapDAO.findByManagedSysId(managedSysId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getResourceAttributeMaps(AttributeMapSearchBean searchBean) {
        return attributeMapDAO.getByExample(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getAllAttributeMaps() {
        return attributeMapDAO.findAllAttributeMaps();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefaultReconciliationAttributeMapEntity> getAllDefaultReconAttributeMap() {
        return defaultReconciliationAttributeMapDAO.getAll();
    }

    private ReconciliationResourceAttributeMapEntity saveReconResAttributeMap(
            ReconciliationResourceAttributeMapEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getAttributePolicy() != null) {
            entity.setAttributePolicy(policyDAO.findById(entity
                    .getAttributePolicy().getId()));
            entity.setDefaultAttributePolicy(null);
        } else if (entity.getDefaultAttributePolicy() != null) {
            entity.setDefaultAttributePolicy(defaultReconciliationAttributeMapDAO
                    .findById(entity.getDefaultAttributePolicy()
                            .getDefaultAttributeMapId()));
            entity.setAttributePolicy(null);
        }
        if (entity.getReconciliationResourceAttributeMapId() == null) {
            return reconciliationResourceAttributeMapDAO.add(entity);
        } else {
            reconciliationResourceAttributeMapDAO.update(entity);
        }

        return entity;
    }

    @Override
    @Transactional
    public List<AttributeMapEntity> saveAttributesMap(
            List<AttributeMapEntity> attrMap, String mSysId, String resId,
            String synchConfigId) throws Exception {
        if (attrMap == null)
            return null;
        for (AttributeMapEntity a : attrMap) {
            a.setManagedSysId(mSysId);
            a.setResourceId(resId);
            a.setSynchConfigId(synchConfigId);
            if (a.getAttributeMapId() == null
                    || a.getAttributeMapId().equalsIgnoreCase("NEW")) {
                // new
                a.setAttributeMapId(null);
                a.setAttributeMapId(this.addAttributeMap(a).getAttributeMapId());
            } else {
                // update
                this.updateAttributeMap(a);
            }
        }
        return new ArrayList<AttributeMapEntity>(attrMap);

    }

    @Override
    @Transactional
    public void deleteAttributesMapList(List<String> ids) throws Exception {
        attributeMapDAO.delete(ids);
    }

    @Override
    public List<ManagedSysRuleEntity> getRulesByManagedSysId(String managedSysId) {
        return managedSysRuleDAO.findbyManagedSystemId(managedSysId);
    }

    @Override
    @Transactional
    public ManagedSysRuleEntity addRules(ManagedSysRuleEntity entity) {
        if (entity.getManagedSysRuleId() != null) {
            return entity;
        }
        entity.setManagedSysRuleId(managedSysRuleDAO.add(entity)
                .getManagedSysRuleId());
        return entity;
    }

    @Override
    public void deleteRules(String ruleId) {
        ManagedSysRuleEntity entity = managedSysRuleDAO.findById(ruleId);
        if (entity == null)
            return;
        managedSysRuleDAO.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSystemObjectMatchEntity> managedSysObjectParam(
            String managedSystemId, String objectType) {
        return matchDAO.findBySystemId(managedSystemId, objectType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderEntity> findAuthProvidersByManagedSysId(String managedSysId) {
        return authProviderDao.getByManagedSysId(managedSysId);
    }

	@Override
	@Transactional
	public void saveApproverAssociations(List<ApproverAssociationEntity> entityList, final AssociationType type, final String entityId) {
		//final List<ApproverAssociationEntity> newList = new LinkedList<ApproverAssociationEntity>();
		//final List<ApproverAssociationEntity> updateList = new LinkedList<ApproverAssociationEntity>();
		final List<ApproverAssociationEntity> deleteList = new LinkedList<ApproverAssociationEntity>();
		
		if(type != null && StringUtils.isNotBlank(entityId)) {
			final List<ApproverAssociationEntity> existingList = approverAssociationDao.getByAssociation(entityId, type);
			if(CollectionUtils.isNotEmpty(existingList)) {
				for(final ApproverAssociationEntity existingEntity : existingList) {
					boolean contains = false;
					if(CollectionUtils.isNotEmpty(entityList)) {
						for(final ApproverAssociationEntity incomingEntity : entityList) {
							if(StringUtils.equals(existingEntity.getId(), incomingEntity.getId())) {
								contains = true;
								break;
							}
						}
					}
					
					if(!contains) {
						deleteList.add(existingEntity);
					}
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(entityList)) {
			for(final ApproverAssociationEntity entity : entityList) {
				if(StringUtils.isNotBlank(entity.getId())) {
					approverAssociationDao.merge(entity);
				} else {
					approverAssociationDao.save(entity);
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(deleteList)) {
			for(final ApproverAssociationEntity entity : deleteList) {
				approverAssociationDao.delete(entity);
			}
		}
	}
	
	private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
    	if(bean != null && StringUtils.isNotBlank(bean.getId())) {
    		return elementDAO.findById(bean.getId());
    	} else {
    		return null;
    	}
    }

	@Override
	@Transactional
	public void save(final ManagedSysEntity entity) throws BasicDataServiceException {
		if(StringUtils.isBlank(entity.getResource().getId())) {
			final ResourceEntity resource = new ResourceEntity();
			resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
			resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
			resource.setIsPublic(false);
			resource.setCoorelatedName(entity.getName());
			resource.setResourceProps(entity.getResource().getResourceProps());
			resourceService.save(resource, null);
			entity.setResource(resource);
		} else {
			final ResourceEntity resource = resourceService.findResourceById(entity.getResource().getId());
			if(CollectionUtils.isEmpty(entity.getResource().getResourceProps())) {
				resource.getResourceProps().clear();
			} else {
				//update existing
				for(final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
					for(final ResourcePropEntity persistentAttribute : resource.getResourceProps()) {
						if(StringUtils.equals(transientAttribute.getId(), persistentAttribute.getId())) {
							persistentAttribute.setElement(getEntity(transientAttribute.getElement()));
							persistentAttribute.setIsMultivalued(transientAttribute.getIsMultivalued());
							persistentAttribute.setName(transientAttribute.getName());
							persistentAttribute.setValue(transientAttribute.getValue());
							break;
						}
					}
				}
				
				//add  new
				for(final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
					if(StringUtils.isBlank(transientAttribute.getId())) {
						transientAttribute.setElement(getEntity(transientAttribute.getElement()));
						transientAttribute.setResource(resource);
						resource.getResourceProps().add(transientAttribute);
					}
				}
				
				//delete
				for(final Iterator<ResourcePropEntity> persistentIterator = resource.getResourceProps().iterator(); persistentIterator.hasNext();) {
					boolean contains = false;
					final ResourcePropEntity persistentAttribute = persistentIterator.next();
					for(final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
						if(StringUtils.equals(transientAttribute.getId(), persistentAttribute.getId())) {
							contains = true;
						}
					}
					
					if(!contains) {
						persistentIterator.remove();
					}
				}
			}
			//resource.setResourceProps(entity.getResource().getResourceProps());
			resource.setCoorelatedName(entity.getName());
			//resourceService.save(resource, null);
			entity.setResource(resource);
			/*
			resourceService.save(resource, null);
			entity.setResource(resource);
			*/
		}
		
		if(StringUtils.isBlank(entity.getId())) {
			managedSysDAO.save(entity);
		} else {
			managedSysDAO.merge(entity);
		}
	}
}
