package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.policy.service.dispatcher.*;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.PolicyAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class PolicyListener extends AbstractRabbitMQListener<PolicyAPI> {
    @Autowired
    private PolicyFindBeansDispatcher policyFindBeansDispatcher;

    @Autowired
    private PolicyGetPolicyDispatcher policyGetPolicyDispatcher;

    @Autowired
    private PolicyGetAllPolicyAttributesDispatcher policyGetAllPolicyAttributesDispatcher;

    @Autowired
    private PolicySaveOrUpdateITPolicyDispatcher policySaveOrUpdateITPolicyDispatcher;
    @Autowired
    private PolicyCountBeansDispatcher policyCountBeansDispatcher;
    @Autowired
    private PolicyFindItPolicyDispatcher policyFindItPolicyDispatcher;

    @Autowired
    private PolicyResetItPolicyDispatcher policyResetItPolicyDispatcher;

    @Autowired
    private PolicySavePolicyDispatcher policySavePolicyDispatcher;
    @Autowired
    private PolicyDeletePolicyDispatcher policyDeletePolicyDispatcher;

    public PolicyListener() {
        super(OpenIAMQueue.PolicyQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, PolicyAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        PolicyAPI apiName = message.getRequestApi();
        switch (apiName) {
            case FindBeans:
                addTask(policyFindBeansDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetPolicy:
                addTask(policyGetPolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetAllPolicyAttributes:
                addTask(policyGetAllPolicyAttributesDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveOrUpdateITPolicy:
                addTask(policySaveOrUpdateITPolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(policyCountBeansDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindITPolicy:
                addTask(policyFindItPolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ResetITPolicy:
                addTask(policyResetItPolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SavePolicy:
                addTask(policySavePolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            case DeletePolicy:
                addTask(policyDeletePolicyDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                throw new RejectMessageException();
        }
    }
}
