package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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
    @Transactional
    public void addManagedSys(ManagedSysEntity entity) {
        managedSysDAO.add(entity);
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
        managedSysDAO.delete(sysEntity);
    }

    @Override
    @Transactional
    public void updateManagedSys(ManagedSysEntity entity) {
        managedSysDAO.update(entity);
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
    public int removeResourceAttributeMaps(String resourceId) {
        return attributeMapDAO.removeResourceAttributeMaps(resourceId);
    }

    @Override
    public List<AttributeMapEntity> getResourceAttributeMaps(String resourceId) {
        return attributeMapDAO.findByResourceId(resourceId);
    }

    @Override
    public List<AttributeMapEntity> getAllAttributeMaps() {
        return attributeMapDAO.findAllAttributeMaps();
    }

    @Override
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
    public List<ManagedSysRuleEntity> getRulesByManagedSysId(String managedSysId) {
        return managedSysRuleDAO.findbyManagedSystemId(managedSysId);
    }

    @Override
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
        if (!StringUtils.hasText(ruleId))
            return;
        ManagedSysRuleEntity entity = managedSysRuleDAO.findById(ruleId);
        if (entity == null)
            return;
        managedSysRuleDAO.delete(entity);
    }

	@Override
	public List<ManagedSystemObjectMatchEntity> managedSysObjectParam(
			String managedSystemId, String objectType) {
		return matchDAO.findBySystemId(managedSystemId, objectType);
	}
}
