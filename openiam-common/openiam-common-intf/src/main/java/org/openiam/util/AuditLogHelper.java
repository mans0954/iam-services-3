package org.openiam.util;

import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.response.AuditLogResponse;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.api.AuditLogAPI;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/08/16.
 */
@Component
public class AuditLogHelper {

    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private AuditLogQueue queue;
    
    public IdmAuditLogEntity newInstance() {
    	final IdmAuditLogEntity log = new IdmAuditLogEntity();
    	log.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
    	return log;
    }

    public IdmAuditLogEntity save(IdmAuditLogEntity event){
        IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
        wrapper.setLogEntity(event);
        AuditLogResponse response = rabbitMQSender.sendAndReceive(queue, AuditLogAPI.AuditLogSave, wrapper, AuditLogResponse.class);
        return response.getEvent();
    }

    public void enqueue(final IdmAuditLogEntity event){
        if(event!=null){
            IdmAuditLogRequest wrapper = new IdmAuditLogRequest();
            wrapper.setLogEntity(event);
            rabbitMQSender.send(queue, AuditLogAPI.AuditLogSave, wrapper);
        }
    }
}
