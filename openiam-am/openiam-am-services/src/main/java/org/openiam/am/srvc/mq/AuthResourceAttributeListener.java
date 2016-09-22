package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.*;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
public class AuthResourceAttributeListener  extends AbstractRabbitMQListener<AuthResourceAttributeAPI> {

    @Autowired
    private GetAmAttributeListDispatcher getAmAttributeListDispatcher;
    @Autowired
    private GetSSOAttributeListDispatcher getSSOAttributeListDispatcher;
    @Autowired
    private GetAttributeDispatcher getAttributeDispatcher;
    @Autowired
    private SaveAttributeMapDispatcher saveAttributeMapDispatcher;
    @Autowired
    private DeleteAttributeMapDispatcher deleteAttributeMapDispatcher;


    public AuthResourceAttributeListener() {
        super(OpenIAMQueue.AuthResourceAttributeQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AuthResourceAttributeAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        AuthResourceAttributeAPI apiName = message.getRequestApi();
        switch (apiName){
            case GetAmAttributeList:
                addTask(getAmAttributeListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetSSOAttributes:
                addTask(getSSOAttributeListDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAttribute:
                addTask(getAttributeDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveAttributeMap:
                addTask(saveAttributeMapDispatcher, correlationId, message, apiName, isAsync);
                break;
            case DeleteAttributeMap:
                addTask(deleteAttributeMapDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
