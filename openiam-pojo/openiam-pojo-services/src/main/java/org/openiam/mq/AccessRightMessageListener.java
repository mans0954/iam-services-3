package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.access.dispatcher.*;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 07/09/16.
 */
@Component
public class AccessRightMessageListener extends AbstractRabbitMQListener<AccessRightAPI> {
    @Autowired
    private GetAccessRightDispatcher getAccessRightDispatcher;
    @Autowired
    private GetAccessRightByIdsDispatcher getAccessRightByIdsDispatcher;
    @Autowired
    private FindAccessRightsDispatcher findAccessRightsDispatcher;
    @Autowired
    private CountAccessRightsDispatcher countAccessRightsDispatcher;
    @Autowired
    private SaveAccessRightDispatcher saveAccessRightDispatcher;
    @Autowired
    private DeleteAccessRightDispatcher deleteAccessRightDispatcher;


    public AccessRightMessageListener() {
        super(OpenIAMQueue.AccessRightQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AccessRightAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AccessRightAPI apiName = (AccessRightAPI)message.getRequestApi();
        switch (apiName){
            case GetAccessRight:
                addTask(getAccessRightDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetByIds:
                addTask(getAccessRightByIdsDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindBeans:
                addTask(findAccessRightsDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(countAccessRightsDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Save:
                addTask(saveAccessRightDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Delete:
                addTask(deleteAccessRightDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
