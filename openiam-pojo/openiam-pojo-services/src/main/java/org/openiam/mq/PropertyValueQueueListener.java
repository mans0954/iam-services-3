package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.PropertyValueListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.property.service.PropertyValueService;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.mq.constants.api.common.PropertyValueAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.common.PropertyValueQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 06/12/16.
 */
@RabbitListener(id="propertyValueQueueListener",
        queues = "#{PropertyValueQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
@Component
public class PropertyValueQueueListener extends AbstractListener<PropertyValueAPI> {
    @Autowired
    private PropertyValueService propertyValueService;
    @Autowired
    protected PropertyValueSweeper propertyValueSweeper;

    @Autowired
    public PropertyValueQueueListener(PropertyValueQueue queue) {
        super(queue);
    }


    protected RequestProcessor<PropertyValueAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<PropertyValueAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(PropertyValueAPI propertyValueAPI, EmptyServiceRequest request) throws BasicDataServiceException {
                PropertyValueListResponse response = new PropertyValueListResponse();
                response.setList(propertyValueService.getAll());
                return response;
            }
        };
    }
    protected RequestProcessor<PropertyValueAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<PropertyValueAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(PropertyValueAPI propertyValueAPI, IdServiceRequest request) throws BasicDataServiceException {
                StringResponse response = new StringResponse();
                response.setValue(propertyValueSweeper.getValue(request.getId(), request.getLanguage()));
                return response;
            }
        };

    }
    protected RequestProcessor<PropertyValueAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<PropertyValueAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(PropertyValueAPI propertyValueAPI, BaseCrudServiceRequest request) throws BasicDataServiceException {
                if(request instanceof PropertyValueCrudRequest) {
                    IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
                    idmAuditLog.setRequestorUserId(request.getRequesterId());
                    idmAuditLog.setAction(AuditAction.MODIFY_PROPERTIES.value());

                    PropertyValueCrudRequest crudRequest = (PropertyValueCrudRequest) request;
                    propertyValueService.save(crudRequest.getPropertyValueList());
                    return new Response();
                } else {
                    throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown requestType: " + request.getClass().getName());
                }
            }
        };
    }
}
