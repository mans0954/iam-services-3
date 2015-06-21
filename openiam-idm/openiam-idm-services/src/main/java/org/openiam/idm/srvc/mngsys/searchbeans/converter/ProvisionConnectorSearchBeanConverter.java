package org.openiam.idm.srvc.mngsys.searchbeans.converter;

import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component("provisionConnectorSearchBeanConverter")
public class ProvisionConnectorSearchBeanConverter implements SearchBeanConverter<ProvisionConnectorEntity, ProvisionConnectorSearchBean> {

    @Override
    public ProvisionConnectorEntity convert(ProvisionConnectorSearchBean searchBean) {
        ProvisionConnectorEntity connectorEntity = new ProvisionConnectorEntity();
        connectorEntity.setName(searchBean.getConnectorName());
        connectorEntity.setId(searchBean.getKey());
        connectorEntity.setMetadataTypeId(searchBean.getConnectorTypeId());
        return connectorEntity;
    }

}
