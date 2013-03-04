package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import java.util.List;

public interface ManagedSystemService {

    List<ManagedSysEntity> getManagedSystemsByExample(ManagedSysEntity example, Integer from, Integer size);

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

}
