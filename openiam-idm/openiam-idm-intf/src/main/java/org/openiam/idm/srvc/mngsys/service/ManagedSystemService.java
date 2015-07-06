package org.openiam.idm.srvc.mngsys.service;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.MngSysPolicySearchBean;
import org.openiam.idm.srvc.mngsys.bean.MngSysPolicyBean;
import org.openiam.idm.srvc.mngsys.domain.*;
import org.openiam.idm.srvc.mngsys.dto.*;

import java.util.List;

public interface ManagedSystemService {

    List<ManagedSysDto> getManagedSystemsByExample(ManagedSysSearchBean example,
                                                      Integer from, Integer size);

    Integer getManagedSystemsCountByExample(ManagedSysEntity example);

    ManagedSysEntity getManagedSysById(String id);

    MngSysPolicyEntity getManagedSysPolicyById(String id);

    List<MngSysPolicyDto> getManagedSysPolicyByMngSysId(String mngSysId);

    MngSysPolicyDto getManagedSysPolicyByMngSysIdAndMetadataType(String mngSysId, String metadataTypeId);

    List<MngSysPolicyDto> findMngSysPolicies(MngSysPolicySearchBean searchBean, Integer from, Integer size);

    List<MngSysPolicyBean> findMngSysPolicyBeans(MngSysPolicySearchBean searchBean, Integer from, Integer size);

    String saveMngSysPolicyBean(MngSysPolicyBean mngSysPolicy) throws BasicDataServiceException;

    int getMngSysPoliciesCount(MngSysPolicySearchBean searchBean);

    List<ManagedSysEntity> getManagedSysByConnectorId(String connectorId);

    String getDecryptedPassword(ManagedSysDto managedSys);

//    List<ManagedSysEntity> getManagedSysByDomain(String domainId);

    List<ManagedSysEntity> getAllManagedSys();

    void removeManagedSysById(String id) throws BasicDataServiceException;

    ManagedSysEntity getManagedSysByResource(String id, String status);

    ManagedSysEntity getManagedSysByName(String name);

    AttributeMapEntity getAttributeMap(String attributeMapId);

    AttributeMapEntity addAttributeMap(AttributeMapEntity attributeMap);

    void updateAttributeMap(AttributeMapEntity attributeMap);

    void removeAttributeMap(String attributeMapId);

    void removeResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getResourceAttributeMaps(String resourceId);

    void removeMngSysPolicy(String mngSysPolicyId) throws BasicDataServiceException ;

    List<AttributeMap> getAttributeMapsByMngSysPolicyId(String mngSysPolicyId);

    List<AttributeMapEntity> getResourceAttributeMaps(AttributeMapSearchBean searchBean);

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

    String saveManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch);

    void updateManagedSystemObjectMatch(ManagedSystemObjectMatch objectMatch);

    void deleteManagedSystemObjectMatch(String objectMatchId);

    List<AuthProviderEntity> findAuthProvidersByManagedSysId(String managedSysId);

    public void saveApproverAssociations(final List<ApproverAssociationEntity> entityList, final AssociationType type, final String id);

    public void save(final ManagedSysEntity entity) throws BasicDataServiceException;
}
