package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;

public interface ManagedSystemService {

    List<ManagedSysEntity> getManagedSystemsByExample(ManagedSysEntity example,
            Integer from, Integer size);

    Integer getManagedSystemsCountByExample(ManagedSysEntity example);

    void addManagedSys(ManagedSysEntity entity);

    ManagedSysEntity getManagedSysById(String id);

    List<ManagedSysEntity> getManagedSysByConnectorId(String connectorId);

    List<ManagedSysEntity> getManagedSysByDomain(String domainId);

    List<ManagedSysEntity> getAllManagedSys();

    void removeManagedSysById(String id);

    void updateManagedSys(ManagedSysEntity entity);

    ManagedSysEntity getManagedSysByResource(String id, String status);

    ManagedSysEntity getManagedSysByName(String name);

    AttributeMapEntity getAttributeMap(String attributeMapId);

    AttributeMapEntity addAttributeMap(AttributeMapEntity attributeMap);

    void updateAttributeMap(AttributeMapEntity attributeMap);

    void removeAttributeMap(String attributeMapId);

    void removeResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getAllAttributeMaps();

    List<ManagedSysRuleEntity> getRulesByManagedSysId(String managedSysId);

    List<DefaultReconciliationAttributeMapEntity> getAllDefaultReconAttributeMap();

    ManagedSysRuleEntity addRules(ManagedSysRuleEntity entity);

    void deleteRules(String ruleId);

    List<ManagedSystemObjectMatchEntity> managedSysObjectParam(
            String managedSystemId, String objectType);

    List<AttributeMapEntity> saveAttributesMap(
            List<AttributeMapEntity> attrMap, String mSysId, String resId,
            String synchConfigId) throws Exception;

    void deleteAttributesMapList(List<String> ids) throws Exception;
}
