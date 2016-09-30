package org.openiam.mq;

import org.openiam.authmanager.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AMManagerAPI;
import org.openiam.mq.constants.AMMenuAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class AMManagerQueueListener extends AbstractRabbitMQListener<AMManagerAPI> {
    @Autowired
    private IsMemberOfDispatcher isMemberOfDispatcher;
    @Autowired
    private GetGroupsForUserDispatcher getGroupsForUserDispatcher;
    @Autowired
    private GetRolesForUserDispatcher getRolesForUserDispatcher;
    @Autowired
    private GetResourcesForUserDispatcher getResourcesForUserDispatcher;
    @Autowired
    private GetOrganizationsForUserDispatcher getOrganizationsForUserDispatcher;


    public AMManagerQueueListener() {
        super(OpenIAMQueue.AMManagerQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AMManagerAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AMManagerAPI api = message.getRequestApi();
        switch (api){
            case IsMemberOfGroup:
            case IsMemberOfGroupWithRight:
            case IsMemberOfOrganization:
            case IsMemberOfOrganizationWithRight:
            case IsMemberOfRole:
            case IsMemberOfRoleWithRight:
            case IsUserEntitledToResource:
            case IsUserEntitledToResourceWithRight:
                addTask(isMemberOfDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case GetGroupsForUser:
                addTask(getGroupsForUserDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case GetRolesForUser:
                addTask(getRolesForUserDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case GetResourcesForUser:
                addTask(getResourcesForUserDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
            case GetOrganizationsForUser:
                addTask(getOrganizationsForUserDispatcher, correlationId, message, message.getRequestApi(), isAsync);
                break;
        }
    }
}
