package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.role.service.dispatcher.RoleFindBeansDispatcher;
import org.openiam.idm.srvc.role.service.dispatcher.RoleGetRoleLocalizedDispatcher;
import org.openiam.idm.srvc.role.service.dispatcher.RoleValidateDeleteDispatcher;
import org.openiam.idm.srvc.role.service.dispatcher.RoleValidateEditDispatcher;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.RoleAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class RoleListener extends AbstractRabbitMQListener<RoleAPI> {
    @Autowired
    private RoleFindBeansDispatcher roleFindBeansDispatcher;

    @Autowired
    private RoleValidateEditDispatcher roleValidateEditDispatcher;

    @Autowired
    private RoleValidateDeleteDispatcher roleValidateDeleteDispatcher;

    @Autowired
    private RoleGetRoleLocalizedDispatcher roleGetRoleLocalizedDispatcher;

    public RoleListener() {
        super(OpenIAMQueue.RoleQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, RoleAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        RoleAPI apiName = message.getRequestApi();
        switch (apiName) {
            case FindBeans:
                addTask(roleFindBeansDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ValidateEdit:
                addTask(roleValidateEditDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ValidateDelete:
                addTask(roleValidateDeleteDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetRoleLocalized:
                addTask(roleGetRoleLocalizedDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
