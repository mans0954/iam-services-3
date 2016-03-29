package org.openiam.idm.srvc.mngsys.service;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.domain.*;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;

import java.util.List;

public interface ManagedSystemService {

    List<ManagedSysEntity> getManagedSystemsByExample(ManagedSysSearchBean searchBean,
            Integer from, Integer size);

    Integer getManagedSystemsCountByExample(ManagedSysSearchBean searchBean);

    void addManagedSys(ManagedSysDto entity);

    ManagedSysEntity getManagedSysById(String id);

    List<ManagedSysEntity> getManagedSysByConnectorId(String connectorId);

    String getDecryptedPassword(ManagedSysDto managedSys);

//    List<ManagedSysEntity> getManagedSysByDomain(String domainId);

    List<ManagedSysEntity> getAllManagedSys();

    void removeManagedSysById(String id);

    void updateManagedSys(ManagedSysDto entity);

    ManagedSysEntity getManagedSysByResource(String id, String status);

    String getManagedSysIdByResource(String id, String status);

    ManagedSysEntity getManagedSysByName(String name);

    AttributeMapEntity getAttributeMap(String attributeMapId);

    AttributeMapEntity addAttributeMap(AttributeMapEntity attributeMap);

    void updateAttributeMap(AttributeMapEntity attributeMap);

    void removeAttributeMap(String attributeMapId);

    void removeResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getAttributeMapsByManagedSysId(String managedSysId);

    List<AttributeMapEntity> getResourceAttributeMaps(AttributeMapSearchBean searchBean);

    List<AttributeMapEntity> getAllAttributeMaps();

//    List<ManagedSysRuleEntity> getRulesByManagedSysId(String managedSysId);

    List<DefaultReconciliationAttributeMapEntity> getAllDefaultReconAttributeMap();

//    ManagedSysRuleEntity addRules(ManagedSysRuleEntity entity);

//    void deleteRules(String ruleId);

    List<ManagedSystemObjectMatchEntity> managedSysObjectParam(
            String managedSystemId, String objectType);

    List<AttributeMapEntity> saveAttributesMap(
            List<AttributeMapEntity> attrMap, String mSysId, String resId,
            String synchConfigId) throws Exception;

    void deleteAttributesMapList(List<String> ids) throws Exception;

    String saveManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch);

    void updateManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch);

    void deleteManagedSystemObjectMatch(String objectMatchId);

    List<AuthProviderEntity> findAuthProvidersByManagedSysId(String managedSysId);
    
    public void saveApproverAssociations(final List<ApproverAssociationEntity> entityList, final AssociationType type, final String id);

    List<ManagedSysEntity> getAllManagedSysNames();

    public String getResourceIdByManagedSysId(final String managedSysId);
}
