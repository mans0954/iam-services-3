package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;

import java.util.List;

public interface ProvisionConnectorService {

    List<ProvisionConnectorEntity> getProvisionConnectorsByExample(ProvisionConnectorEntity example, Integer from, Integer size);

    Integer getProvisionConnectorsCountByExample(ProvisionConnectorEntity example);

    List<MetadataTypeEntity> getProvisionConnectorsMetadataTypes();

    void addProvisionConnector(ProvisionConnectorEntity connectorEntity);

    void updateProvisionConnector(ProvisionConnectorEntity connectorEntity);

    void removeProvisionConnectorById(String connectorId);

    ProvisionConnectorEntity getProvisionConnectorsById(String connectorId);
}
