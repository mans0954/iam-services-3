package org.openiam.idm.srvc.mngsys.ws;

import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.dozer.converter.ProvisionConnectorConverter;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.searchbeans.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ProvisionConnectorSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service("provisionConnectorWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/mngsys/ws",
        portName = "ConnectorWebServicePort",
        serviceName = "ConnectorWebService")
public class ProvisionConnectorWebServiceImpl implements ProvisionConnectorWebService {

    @Autowired
    private ProvisionConnectorService connectorService;
    @Autowired
    private ProvisionConnectorConverter provisionConnectorConverter;
    @Autowired
    private MetaDataTypeDozerConverter metaDataTypeDozerConverter;
    @Autowired
    private ProvisionConnectorSearchBeanConverter provisionConnectorSearchBeanConverter;

    @Override
    public List<ProvisionConnectorDto> getProvisionConnectors(@WebParam(name = "searchBean", targetNamespace = "") ProvisionConnectorSearchBean searchBean, @WebParam(name = "size", targetNamespace = "") Integer size, @WebParam(name = "from", targetNamespace = "") Integer from) {
        List<ProvisionConnectorDto> provisionConnectors = new LinkedList<ProvisionConnectorDto>();
        ProvisionConnectorEntity connectorEntity =  provisionConnectorSearchBeanConverter.convert(searchBean);
        List<ProvisionConnectorEntity> connectors = connectorService.getProvisionConnectorsByExample(connectorEntity, size, from);
        if(connectors != null) {
            provisionConnectors = provisionConnectorConverter.convertToDTOList(connectors,false);
        }
        return provisionConnectors;
    }

    @Override
    public Integer getProvisionConnectorsCount(@WebParam(name = "searchBean", targetNamespace = "") ProvisionConnectorSearchBean searchBean) {
        ProvisionConnectorEntity connectorEntity =  provisionConnectorSearchBeanConverter.convert(searchBean);
        return connectorService.getProvisionConnectorsByExampleCount(connectorEntity);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<MetadataType> getProvisionConnectorsTypes() {
        List<MetadataTypeEntity> metadataTypes = connectorService.getProvisionConnectorsMetadataTypes();
        return metadataTypes != null ? metaDataTypeDozerConverter.convertToDTOList(metadataTypes, false) : Collections.EMPTY_LIST;
    }
    @Override
    public void addProvisionConnector(@WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorConverter.convertToEntity(con,true);
        connectorService.addProvisionConnector(connectorEntity);
    }

    @Override
    public void updateProvisionConnector(@WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
        ProvisionConnectorEntity connectorEntity = provisionConnectorConverter.convertToEntity(con,true);
        connectorService.updateProvisionConnector(connectorEntity);
    }

    @Override
    public void removeProvisionConnector(@WebParam(name = "conId", targetNamespace = "") String conId) {
        connectorService.removeProvisionConnectorById(conId);
    }

    @Override
    public ProvisionConnectorDto getProvisionConnector(@WebParam(name = "conId", targetNamespace = "") String conId) {
        ProvisionConnectorEntity connectorEntity = connectorService.getProvisionConnectorsById(conId);
        return provisionConnectorConverter.convertToDTO(connectorEntity,true);
    }
}
