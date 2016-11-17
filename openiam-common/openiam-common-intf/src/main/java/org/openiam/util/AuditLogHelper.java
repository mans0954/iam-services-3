package org.openiam.util;

import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.response.AuditLogResponse;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.api.AuditLogAPI;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
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
    @Autowired
    private AuditLogQueue queue;

    public IdmAuditLogEntity save(IdmAuditLogEntity event){
        IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
        wrapper.setLogEntity(event);
        AuditLogResponse response = (AuditLogResponse)requestServiceGateway.sendAndReceive(queue, AuditLogAPI.AuditLogSave, wrapper);
        return response.getEvent();
    }

    public void enqueue(final IdmAuditLogEntity event){
        if(event!=null){
            IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
            wrapper.setLogEntity(event);
            wrapper.setAsych(true);
            requestServiceGateway.send(queue, AuditLogAPI.AuditLogSave, wrapper);
        }
    }
}
