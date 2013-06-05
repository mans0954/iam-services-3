package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

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

    int removeResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getResourceAttributeMaps(String resourceId);

    List<AttributeMapEntity> getAllAttributeMaps();

    List<DefaultReconciliationAttributeMapEntity> getAllDefaultReconAttributeMap();
}
