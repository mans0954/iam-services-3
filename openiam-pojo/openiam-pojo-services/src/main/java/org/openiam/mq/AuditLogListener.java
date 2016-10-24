package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.audit.service.dispatcher.*;
import org.openiam.mq.constants.AuditLogAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
public class AuditLogListener extends AbstractRabbitMQListener<AuditLogAPI> {
    @Autowired
    private AuditLogDispatcher auditLogDispatcher;
    @Autowired
    private FindAuditLogDispatcher findAuditLogDispatcher;
    @Autowired
    private GetAuditLogIDsDispatcher getAuditLogIDsDispatcher;
    @Autowired
    private CountAuditLogDispatcher countAuditLogDispatcher;
    @Autowired
    private GetAuditLogDispatcher getAuditLogDispatcher;

    public AuditLogListener() {
        super(OpenIAMQueue.AuditLog);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AuditLogAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AuditLogAPI apiName = message.getRequestApi();
        switch (apiName){
            case AuditLogSave:
                addTask(auditLogDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindBeans:
                addTask(findAuditLogDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetIds:
                addTask(getAuditLogIDsDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(countAuditLogDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetLogRecord:
                addTask(getAuditLogDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
