package org.openiam.idm.srvc.mngsys.ws;

import java.util.Collections;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("provisionConnectorWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService", targetNamespace = "urn:idm.openiam.org/srvc/mngsys/ws", portName = "ConnectorWebServicePort", serviceName = "ConnectorWebService")
public class ProvisionConnectorWebServiceImpl implements
        ProvisionConnectorWebService {

    @Autowired
    private ProvisionConnectorService connectorService;

    @Autowired
    private MetaDataTypeDozerConverter metaDataTypeDozerConverter;

    private static final Log log = LogFactory
            .getLog(ProvisionConnectorWebServiceImpl.class);
    @Override
    public List<ProvisionConnectorDto> getProvisionConnectors(
            @WebParam(name = "searchBean", targetNamespace = "") ProvisionConnectorSearchBean searchBean,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "from", targetNamespace = "") Integer from) {
        return connectorService
                .getProvisionConnectorsByExample(searchBean, size, from);
    }

    @Override
    public Integer getProvisionConnectorsCount(
            @WebParam(name = "searchBean", targetNamespace = "") ProvisionConnectorSearchBean searchBean) {
        return connectorService
                .getProvisionConnectorsCountByExample(searchBean);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<MetadataType> getProvisionConnectorsTypes() {
        List<MetadataTypeEntity> metadataTypes = connectorService
                .getProvisionConnectorsMetadataTypes();
        return metadataTypes != null ? metaDataTypeDozerConverter
                .convertToDTOList(metadataTypes, false)
                : Collections.EMPTY_LIST;
    }

    @Override
    public void addProvisionConnector(
            @WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
        connectorService.addProvisionConnector(con);
    }

    @Override
    public void updateProvisionConnector(
            @WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
        connectorService.updateProvisionConnector(con);
    }

    @Override
    public Response removeProvisionConnector(
            @WebParam(name = "conId", targetNamespace = "") String conId) {
        final org.openiam.base.ws.Response response = new Response(ResponseStatus.SUCCESS);
        try{
            connectorService.removeProvisionConnectorById(conId);

        } catch (Throwable e) {
            log.error("Cannot delete connector, please delete dependencies first.", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProvisionConnectorDto getProvisionConnector(
            @WebParam(name = "conId", targetNamespace = "") String conId) {
        return connectorService
                .getProvisionConnectorsById(conId);
    }
}
