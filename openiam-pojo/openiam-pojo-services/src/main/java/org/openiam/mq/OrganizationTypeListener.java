package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.org.service.dispatcher.*;
import org.openiam.mq.constants.queue.am.AMQueue;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class OrganizationTypeListener extends AbstractRabbitMQListener<OrganizationTypeAPI> {
    @Autowired
    private FindOrganizationTypeDispatcher findOrganizationTypeDispatcher;
    @Autowired
    private OrgTypeCountDispatcher orgTypeCountDispatcher;
    @Autowired
    private OrgTypeGetByIdDispatcher orgTypeGetByIdDispatcher;
    @Autowired
    private OrgTypeSaveDispatcher orgTypeSaveDispatcher;
    @Autowired
    private OrgTypeDeleteDispatcher orgTypeDeleteDispatcher;
    @Autowired
    private OrgTypeMembershipDispatcher orgTypeMembershipDispatcher;

    public OrganizationTypeListener() {
        super(AMQueue.OrganizationTypeQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, OrganizationTypeAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        OrganizationTypeAPI apiName = message.getRequestApi();
        switch (apiName) {
            case FindBeans:
            case FindAllowedChildren:
            case GetAllowedParents:
                addTask(findOrganizationTypeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(orgTypeCountDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetById:
                addTask(orgTypeGetByIdDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Save:
                addTask(orgTypeSaveDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Delete:
                addTask(orgTypeDeleteDispatcher, correlationId, message, apiName, isAsync);
                break;
            case AddChild:
            case RemoveChild:
                addTask(orgTypeMembershipDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                throw new RejectMessageException();
        }
    }
}
