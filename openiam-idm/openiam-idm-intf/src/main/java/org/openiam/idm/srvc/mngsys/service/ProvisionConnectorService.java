package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;

import java.util.List;

public interface ProvisionConnectorService {

    List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorSearchBean searchBean, Integer from, Integer size);

    int getProvisionConnectorsCountByExample(ProvisionConnectorSearchBean searchBean);

    List<MetadataTypeEntity> getProvisionConnectorsMetadataTypes();

    void addProvisionConnector(ProvisionConnectorDto connectorDto);

    void updateProvisionConnector(ProvisionConnectorDto connectorDto);

    void removeProvisionConnectorById(String connectorId);

    ProvisionConnectorDto getProvisionConnectorsById(String connectorId);
}