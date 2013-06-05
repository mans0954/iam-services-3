package org.openiam.idm.srvc.mngsys.service;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProvisionConnectorServiceImpl implements ProvisionConnectorService {

    @Autowired
    private ProvisionConnectorDao provisionConnectorDao;

    @Override
    @Transactional(readOnly = true)
    public List<ProvisionConnectorEntity> getProvisionConnectorsByExample(ProvisionConnectorEntity example, Integer from, Integer size) {
        return provisionConnectorDao.getByExample(example, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getProvisionConnectorsCountByExample(ProvisionConnectorEntity example) {
        return provisionConnectorDao.count(example);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetadataTypeEntity> getProvisionConnectorsMetadataTypes() {
        return provisionConnectorDao.getMetadataTypes();
    }

    @Override
    @Transactional
    public void addProvisionConnector(ProvisionConnectorEntity connectorEntity) {
        provisionConnectorDao.add(connectorEntity);
    }

    @Override
    @Transactional
    public void updateProvisionConnector(ProvisionConnectorEntity connectorEntity) {
        provisionConnectorDao.update(connectorEntity);
    }

    @Override
    @Transactional
    public void removeProvisionConnectorById(String connectorId) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(connectorId);
        provisionConnectorDao.delete(connectorEntity);
    }

    @Override
    @Transactional
    public ProvisionConnectorEntity getProvisionConnectorsById(String connectorId) {
        return provisionConnectorDao.findById(connectorId);
    }
}
