package org.openiam.idm.srvc.mngsys.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
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
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
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

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getManagedSysByDomain(String domainId) {
        return managedSysDAO.findbyDomain(domainId);
    }

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
        for (ManagedSysRuleEntity ruleEntity : sysEntity.getRules()) {
            managedSysRuleDAO.delete(ruleEntity);
        }
        if(CollectionUtils.isNotEmpty(sysEntity.getGroups())) {
        	for(final GroupEntity group : sysEntity.getGroups()) {
        		group.setManagedSystem(null);
        		groupDAO.update(group);
        	}
        }
        managedSysDAO.delete(sysEntity);
    }
    
    @Override
    @Transactional
    public void addManagedSys(ManagedSysDto sys) {
        final ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys, true);

    	final ResourceEntity resource = new ResourceEntity();
    	resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
    	resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
    	resource.setIsPublic(false);

    	resourceDAO.save(resource);
    	entity.setResourceId(resource.getResourceId());

        managedSysDAO.save(entity);
        resource.setManagedSysId(entity.getManagedSysId());
        sys.setManagedSysId(entity.getManagedSysId());
    }

    @Override
    @Transactional
    public void updateManagedSys(ManagedSysDto sys) {
        final ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys, true);
    	ResourceEntity resource = null;
    	if(org.apache.commons.lang.StringUtils.isEmpty(entity.getResourceId())) {
    		resource = new ResourceEntity();
    		resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
    		resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
    		resource.setIsPublic(false);
    		resourceDAO.save(resource);
    		entity.setResourceId(resource.getResourceId());
            resource.setManagedSysId(sys.getManagedSysId());
    	}
        managedSysDAO.save(entity);

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
}
