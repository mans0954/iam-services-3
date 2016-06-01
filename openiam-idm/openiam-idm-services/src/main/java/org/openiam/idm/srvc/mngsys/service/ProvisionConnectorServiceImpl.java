package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
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
    public List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorSearchBean searchBean, int from, int size) {
    	final List<ProvisionConnectorEntity> entities = provisionConnectorDao.getByExample(searchBean, from, size);
        return provisionConnectorConverter.convertToDTOList(entities, (searchBean != null) ? searchBean.isDeepCopy() : false);
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
    public void save(final ProvisionConnectorEntity entity) {
    	if(StringUtils.isBlank(entity.getId())) {
    		provisionConnectorDao.save(entity);
    	} else {
    		provisionConnectorDao.merge(entity);
    	}
    }

    @Override
    @Transactional
    public void delete(String id) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(id);
        provisionConnectorDao.delete(connectorEntity);
    }

    @Override
    @Transactional
    public ProvisionConnectorDto getDto(String id) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(id);
        return provisionConnectorConverter.convertToDTO(connectorEntity, true);
    }
}
