package org.openiam.srvc.idm;

import java.util.Collections;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.dozer.converter.ProvisionConnectorConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("provisionConnectorWebService")
@WebService(endpointInterface = "org.openiam.srvc.idm.ProvisionConnectorWebService", targetNamespace = "urn:idm.openiam.org/srvc/mngsys/ws", portName = "ConnectorWebServicePort", serviceName = "ConnectorWebService")
public class ProvisionConnectorWebServiceImpl extends AbstractBaseService implements ProvisionConnectorWebService {

    @Autowired
    private ProvisionConnectorService connectorService;

    @Autowired
    private MetaDataTypeDozerConverter metaDataTypeDozerConverter;
    
    @Autowired
    private ProvisionConnectorConverter provisionConnectorConverter;

    private static final Log log = LogFactory
            .getLog(ProvisionConnectorWebServiceImpl.class);
    @Override
    public List<ProvisionConnectorDto> getProvisionConnectors(
            @WebParam(name = "searchBean", targetNamespace = "") ProvisionConnectorSearchBean searchBean,
            @WebParam(name = "from", targetNamespace = "") int from,
            @WebParam(name = "size", targetNamespace = "") int size) {
        return connectorService
                .getProvisionConnectorsByExample(searchBean, from, size);
    }

    @Override
    public int getProvisionConnectorsCount(
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
    public Response addProvisionConnector(
            @WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
        return save(con);
    }

    @Override
    public Response updateProvisionConnector(
            @WebParam(name = "con", targetNamespace = "") ProvisionConnectorDto con) {
    	return save(con);
    }
    
    @Override
    public Response save(final ProvisionConnectorDto dto) {
    	final IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
    	idmAuditLog.setAction(AuditAction.SAVE_CONNECTOR.toString());
    	final ProvisionConnectorEntity entity = provisionConnectorConverter.convertToEntity(dto, true);
    	final Response response = new Response();
    	try {
    		if(StringUtils.isBlank(dto.getName())) {
    			throw new BasicDataServiceException(ResponseCode.NAME_MISSING);
    		}
    		
    		connectorService.save(entity);
    		response.setResponseValue(entity.getId());
    		response.succeed();
    	 } catch (BasicDataServiceException e) {
             response.setStatus(ResponseStatus.FAILURE);
             response.setErrorCode(e.getCode());
             idmAuditLog.fail();
             idmAuditLog.setFailureReason(e.getCode());
             idmAuditLog.setException(e);
         } catch (Throwable e) {
             log.error("Can't save or update resource property", e);
             response.setStatus(ResponseStatus.FAILURE);
             response.setErrorText(e.getMessage());
             idmAuditLog.fail();
             idmAuditLog.setException(e);
         } finally {
             auditLogService.enqueue(idmAuditLog);
         }
    	return response;
    }

    @Override
    public Response removeProvisionConnector(
            @WebParam(name = "conId", targetNamespace = "") String conId) {
        final org.openiam.base.ws.Response response = new Response(ResponseStatus.SUCCESS);
        try{
            connectorService.delete(conId);

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
        return connectorService.getDto(conId);
    }
}
