package org.openiam.idm.srvc.mngsys.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.*;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ManagedSystemServiceImpl implements ManagedSystemService {
    private static final Log log = LogFactory
            .getLog(ManagedSystemServiceImpl.class);
    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Autowired
    protected AttributeMapDAO attributeMapDAO;
    @Autowired
    protected ReconciliationResourceAttributeMapDAO reconciliationResourceAttributeMapDAO;
    @Autowired
    protected DefaultReconciliationAttributeMapDAO defaultReconciliationAttributeMapDAO;
//    @Autowired
//    protected ManagedSysRuleDAO managedSysRuleDAO;
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

    private static final String resourceTypeId="MANAGED_SYS";

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private KeyManagementService keyManagementService;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

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
    public String getManagedSysIdByResource(String id, String status) {
        return managedSysDAO.findIdByResource(id, status);
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
    public void removeManagedSysById(String id) {
        ManagedSysEntity sysEntity = managedSysDAO.findById(id);
        for (ManagedSystemObjectMatchEntity matchEntity : sysEntity
                .getMngSysObjectMatchs()) {
            matchDAO.delete(matchEntity);
        }
//        for (ManagedSysRuleEntity ruleEntity : sysEntity.getRules()) {
//            managedSysRuleDAO.delete(ruleEntity);
//        }
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
        List<AttributeMapEntity> attributeMapEntities = attributeMapDAO.findByManagedSysId(id);
        for(AttributeMapEntity mapEntity : attributeMapEntities) {
            attributeMapDAO.delete(mapEntity);
        }
        managedSysDAO.delete(sysEntity);
        resourceService.deleteResource(sysEntity.getResourceId());
    }
    
    @Override
    @CacheEvict(value = "decryptManagedSysPassword", allEntries=true)
    @Transactional
    public void addManagedSys(ManagedSysDto sys) {
        final ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys, true);

    	final ResourceEntity resource = new ResourceEntity();
    	resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
    	resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
    	resource.setIsPublic(false);
    	resource.setCoorelatedName(sys.getName());

    	resourceService.save(resource, null);
    	//resourceDAO.save(resource);
    	entity.setResourceId(resource.getId());

        managedSysDAO.save(entity);
        
        /*
        resource.setManagedSysId(entity.getManagedSysId());
        */
        sys.setId(entity.getId());
    }

    @Override
    @CacheEvict(value = "decryptManagedSysPassword", allEntries=true)
    @Transactional
    public void updateManagedSys(ManagedSysDto sys) {
        final ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys, true);
    	ResourceEntity resource = null;
    	if(StringUtils.isEmpty(entity.getResourceId())) {
    		resource = new ResourceEntity();
    		resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
    		resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
    		resource.setIsPublic(false);
    		resource.setCoorelatedName(sys.getName());
    		resourceService.save(resource, null);
    		entity.setResourceId(resource.getId());
            //resource.setManagedSysId(sys.getManagedSysId());
    	} else {
    		resource = resourceService.findResourceById(entity.getResourceId());
    		if(resource != null) {
    			resource.setCoorelatedName(entity.getName());
    			resourceService.save(resource, null);
    		}
    	}
        managedSysDAO.save(entity);

    }

    @Override
    @Transactional
    public String saveManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch) {
        ManagedSystemObjectMatchEntity entity = managedSystemObjectMatchDozerConverter.convertToEntity(objectMatch, false);
        matchDAO.save(entity);
        return entity.getObjectSearchId();
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
                    .getAttributePolicy().getPolicyId()));
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

        if (attrMap == null) {
            return null;
        }

        ManagedSysEntity mngSys = getManagedSysById(mSysId);
        Map<String, AttributeMapEntity> curAttrMapsMap = new HashMap<String, AttributeMapEntity>();
        Set<AttributeMapEntity> curAttrMaps = mngSys.getAttributeMaps();
        for (AttributeMapEntity ame : curAttrMaps) {
            curAttrMapsMap.put(ame.getAttributeMapId(), ame);
        }
        for (AttributeMapEntity ame : attrMap) {
            ReconciliationResourceAttributeMapEntity rram = ame.getReconResAttribute();
            PolicyEntity policy = policyDAO.findById(rram.getAttributePolicy().getPolicyId());
            rram.setAttributePolicy(policy);
            ame.setReconResAttribute(rram);
            ame.setManagedSystem(mngSys);
            ame.setResourceId(resId);
            ame.setSynchConfigId(synchConfigId);
            if (StringUtils.isNotBlank(ame.getAttributeMapId())) {
                AttributeMapEntity attrMapEntity = curAttrMapsMap.get(ame.getAttributeMapId());
                BeanUtils.copyProperties(ame, attrMapEntity, new String[]{"reconResAttribute"});
                attrMapEntity.getReconResAttribute().setAttributePolicy(ame.getReconResAttribute().getAttributePolicy());
            } else {
                curAttrMaps.add(ame);
            }
        }
        return new ArrayList<AttributeMapEntity>(attrMap);

    }

    @Override
    @Transactional
    public void deleteAttributesMapList(List<String> ids) throws Exception {
        attributeMapDAO.delete(ids);
    }

//    @Override
//    public List<ManagedSysRuleEntity> getRulesByManagedSysId(String managedSysId) {
//        return managedSysRuleDAO.findbyManagedSystemId(managedSysId);
//    }
//
//    @Override
//    @Transactional
//    public ManagedSysRuleEntity addRules(ManagedSysRuleEntity entity) {
//        if (entity.getManagedSysRuleId() != null) {
//            return entity;
//        }
//        entity.setManagedSysRuleId(managedSysRuleDAO.add(entity)
//                .getManagedSysRuleId());
//        return entity;
//    }
//
//    @Override
//    public void deleteRules(String ruleId) {
//        ManagedSysRuleEntity entity = managedSysRuleDAO.findById(ruleId);
//        if (entity == null)
//            return;
//        managedSysRuleDAO.delete(entity);
//    }

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


    @Cacheable(value="decryptManagedSysPassword", key="{ #managedSys.id}")
    public String getDecryptedPassword(ManagedSysDto managedSys) {
        String result = null;
        if (managedSys.getPswd() != null) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
            }
        }
        return result;
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
}
