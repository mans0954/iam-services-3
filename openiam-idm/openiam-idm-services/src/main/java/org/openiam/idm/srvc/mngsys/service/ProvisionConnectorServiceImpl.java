package org.openiam.idm.srvc.mngsys.service;

import org.openiam.dozer.converter.ProvisionConnectorConverter;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ProvisionConnectorSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProvisionConnectorServiceImpl implements ProvisionConnectorService {
    @Autowired
    private ProvisionConnectorSearchBeanConverter provisionConnectorSearchBeanConverter;
    @Autowired
    private ProvisionConnectorDao provisionConnectorDao;

    @Autowired
    private ProvisionConnectorConverter provisionConnectorConverter;

    @Override
    @Transactional(readOnly = true)
    public List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorEntity example, Integer from, Integer size) {
        List<ProvisionConnectorEntity> connectorEntities = provisionConnectorDao.getByExample(example, from, size);
        List<ProvisionConnectorDto> provisionConnectors = null;
        if (connectorEntities != null) {
            provisionConnectors = provisionConnectorConverter.convertToDTOList(
                    connectorEntities, false);
        }
        return provisionConnectors;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorSearchBean searchBean, Integer from, Integer size) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorSearchBeanConverter
                .convert(searchBean);
        return getProvisionConnectorsByExample(connectorEntity, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getProvisionConnectorsCountByExample(ProvisionConnectorEntity example) {
        return provisionConnectorDao.count(example);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getProvisionConnectorsCountByExample(ProvisionConnectorSearchBean searchBean) {
        ProvisionConnectorEntity exampleEntity = provisionConnectorSearchBeanConverter
                .convert(searchBean);
        return getProvisionConnectorsCountByExample(exampleEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetadataTypeEntity> getProvisionConnectorsMetadataTypes() {
        return provisionConnectorDao.getMetadataTypes();
    }

    @Override
    @Transactional
    public void addProvisionConnector(ProvisionConnectorDto connectorDto) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorConverter
                .convertToEntity(connectorDto, true);
        provisionConnectorDao.save(connectorEntity);
    }

    @Override
    @Transactional
    public void updateProvisionConnector(ProvisionConnectorDto connectorDto) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorConverter
                .convertToEntity(connectorDto, true);
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
    public ProvisionConnectorDto getProvisionConnectorsById(String connectorId) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(connectorId);
        return provisionConnectorConverter.convertToDTO(connectorEntity, true);
    }
}
