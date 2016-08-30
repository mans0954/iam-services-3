package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.idm.srvc.role.service.dispatcher.*;
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

    @Autowired
    private RoleGetAttributesDispatcher roleGetAttributesDispatcher;

    @Autowired
    private RoleCountBeansDispatcher roleCountBeansDispatcher;

    @Autowired
    private RoleAddGroupToRoleDispatcher roleAddGroupToRoleDispatcher;

    @Autowired
    private RoleSaveRoleDispatcher roleSaveRoleDispatcher;

    @Autowired
    private RoleRemoveRoleDispatcher roleRemoveRoleDispatcher;

    @Autowired
    private RoleValidateGroupToRoleDispatcher roleValidateGroupToRoleDispatcher;

    @Autowired
    private RoleRemoveGroupFromRoleDispatcher roleRemoveGroupFromRoleDispatcher;

    @Autowired
    private RoleAddUserToRoleDispatcher addUserToRoleDispatcher;

    @Autowired
    RoleRemoveUserFromRoleDispatcher roleRemoveUserFromRoleDispatcher;

    @Autowired
    RoleGetParentsDispatcher roleGetParentsDispatcher;

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
            case GetRoleAttributes:
                addTask(roleGetAttributesDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CountBeans:
                addTask(roleCountBeansDispatcher, correlationId, message, apiName, isAsync);
                break;
            case AddGroupToRole:
                addTask(roleAddGroupToRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case SaveRole:
                addTask(roleSaveRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case RemoveRole:
                addTask(roleRemoveRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ValidateGroup2RoleAddition:
                addTask(roleValidateGroupToRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case RemoveGroupFromRole:
                addTask(roleRemoveGroupFromRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case AddUserToRole:
                addTask(addUserToRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case RemoveUserFromRole:
                addTask(roleRemoveUserFromRoleDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetParentRoles:
                addTask(roleGetParentsDispatcher, correlationId, message, apiName, isAsync);
            default:
                break;
        }
    }
}
