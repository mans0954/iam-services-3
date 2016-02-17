package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.openiam.dozer.converter.ProvisionConnectorConverter;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisionConnectorServiceImpl implements ProvisionConnectorService {
    @Autowired
    private ProvisionConnectorDao provisionConnectorDao;

    @Autowired
    private ProvisionConnectorConverter provisionConnectorConverter;

    @Override
    @Transactional(readOnly = true)
    public List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorSearchBean searchBean, Integer from, Integer size) {
    	final List<ProvisionConnectorEntity> entities = provisionConnectorDao.getByExample(searchBean, from, size);
        return provisionConnectorConverter.convertToDTOList(entities, searchBean.isDeepCopy());
    }

    @Override
    @Transactional(readOnly = true)
    public int getProvisionConnectorsCountByExample(ProvisionConnectorSearchBean searchBean) {
        return provisionConnectorDao.count(searchBean);
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
