package org.openiam.util;

import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.response.AuditLogResponse;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.AuditLogAPI;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/08/16.
 */
@Component
public class AuditLogHelper {

    @Autowired
    private RequestServiceGateway requestServiceGateway;

    public IdmAuditLogEntity save(IdmAuditLogEntity event){
        IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
        wrapper.setLogEntity(event);

//        MQRequest<IdmAuditLogRequest, AuditLogAPI> request = new MQRequest<>();
//        request.setRequestBody(wrapper);
//        request.setRequestApi(AuditLogAPI.AuditLogSave);
        AuditLogResponse response = (AuditLogResponse)requestServiceGateway.sendAndReceive(OpenIAMQueue.AuditLog, AuditLogAPI.AuditLogSave, wrapper);

        return response.getEvent();
    }

    public void enqueue(final IdmAuditLogEntity event){
        if(event!=null){
            IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
            wrapper.setLogEntity(event);

//            MQRequest<IdmAuditLogRequest, AuditLogAPI> request = new MQRequest<>();
//            request.setRequestBody(wrapper);
//            request.setRequestApi(AuditLogAPI.AuditLogSave);
            requestServiceGateway.send(OpenIAMQueue.AuditLog, AuditLogAPI.AuditLogSave, wrapper);
        }
    }
}
